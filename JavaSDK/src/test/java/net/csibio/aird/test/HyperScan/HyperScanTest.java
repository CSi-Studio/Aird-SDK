package net.csibio.aird.test.HyperScan;

import com.alibaba.fastjson2.JSON;
import net.csibio.aird.bean.common.Xic;
import net.csibio.aird.parser.ColumnParser;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HyperScanTest {

    static String indexPath = "/Users/cicci/Documents/TestFile/negativeD24.cjson";

    @Test
    public void testEIC() throws IOException {
        long start = System.currentTimeMillis();
        double testMz = 192.1383;
        double[] testMzListForSampleA = {126.0544, 136.0616, 180.10170, 192.1383, 220.0984, 220.9531, 233.0922, 237.1024, 252.1022};
        ColumnParser parserNew = new ColumnParser(indexPath);

        System.out.println("索引初始化时间为:"+(System.currentTimeMillis() - start)+"毫秒");



        start = System.currentTimeMillis();
        Xic newXic = parserNew.calcXicByMz(testMz, 0.015);
        System.out.println("构建EIC单次的时间:"+(System.currentTimeMillis() - start)+"毫秒");

        start = System.currentTimeMillis();
        for (int i = 0; i < testMzListForSampleA.length; i++) {
            Xic xic = parserNew.calcXicByMz(testMzListForSampleA[i], 0.015);
        }
        System.out.println("构建EIC 10次的时间:"+(System.currentTimeMillis() - start)+"毫秒");

        start = System.currentTimeMillis();
        Xic newXicS = parserNew.calcXicArrayByMz(testMz, 0.015);
        System.out.println("Array构建EIC单次的时间:"+(System.currentTimeMillis() - start)+"毫秒");

        start = System.currentTimeMillis();
        for (int i = 0; i < testMzListForSampleA.length; i++) {
            Xic xic = parserNew.calcXicArrayByMz(testMzListForSampleA[i], 0.015);
        }
        System.out.println("Array构建EIC 10次的时间:"+(System.currentTimeMillis() - start)+"毫秒");

        validateResults(newXic, newXicS);
    }

    private void validateResults(Xic originalXic, Xic optimizedXic) {
        double[] originalRts = originalXic.getRts();
        double[] optimizedRts = optimizedXic.getRts();
        double[] originalIntensities = originalXic.getInts();
        double[] optimizedIntensities = optimizedXic.getInts();

        // 1. 基础验证
        boolean hasErrors = false;
        int rtLengthDiff = originalRts.length - optimizedRts.length;

        if (rtLengthDiff != 0) {
            System.err.println("❌ Critical: RT array length mismatch! Original: " +
                    originalRts.length + ", Optimized: " + optimizedRts.length);
            hasErrors = true;
        }

        // 2. RT值验证
        int minLength = Math.min(originalRts.length, optimizedRts.length);
        int rtMismatches = 0;
        double maxRtDiff = 0;

        for (int i = 0; i < minLength; i++) {
            double diff = Math.abs(originalRts[i] - optimizedRts[i]);
            if (diff > 0.001) {
                rtMismatches++;
                maxRtDiff = Math.max(maxRtDiff, diff);
                if (rtMismatches <= 5) {  // 只打印前5个错误
                    System.out.printf("⚠️ RT mismatch at index %d: Original=%.6f, Optimized=%.6f, Diff=%.6f%n",
                            i, originalRts[i], optimizedRts[i], diff);
                }
            }
        }

        // 3. 强度值验证
        int intensityMismatches = 0;
        double maxIntensityDiff = 0;
        double totalOriginalIntensity = 0;
        double totalOptimizedIntensity = 0;
        double maxIntensityValue = 0;

        for (int i = 0; i < minLength; i++) {
            double origIntensity = originalIntensities[i];
            double optIntensity = optimizedIntensities[i];
            double diff = Math.abs(origIntensity - optIntensity);

            totalOriginalIntensity += origIntensity;
            totalOptimizedIntensity += optIntensity;
            maxIntensityValue = Math.max(maxIntensityValue, Math.max(origIntensity, optIntensity));

            // 相对误差和绝对误差结合验证
            boolean isMismatch = false;
            if (diff > 1e-5) {  // 绝对误差阈值
                double relDiff = diff / Math.max(origIntensity, 1e-10);
                if (relDiff > 1e-4) {  // 相对误差阈值
                    isMismatch = true;
                }
            }

            if (isMismatch) {
                intensityMismatches++;
                maxIntensityDiff = Math.max(maxIntensityDiff, diff);
                if (intensityMismatches <= 5) {  // 只打印前5个错误
                    System.out.printf("⚠️ Intensity mismatch at index %d (RT=%.4f): Orig=%.6f, Opt=%.6f, Diff=%.6f%n",
                            i, originalRts[i], origIntensity, optIntensity, diff);
                }
            }
        }

        // 4. 汇总报告
        System.out.println("\n===== Validation Summary =====");
        System.out.printf("RT Points: Original=%d, Optimized=%d%n",
                originalRts.length, optimizedRts.length);

        if (rtMismatches > 0) {
            System.err.printf("❌ RT Mismatches: %d/%d (%.2f%%) | Max Diff: %.6f%n",
                    rtMismatches, minLength, 100.0 * rtMismatches / minLength, maxRtDiff);
            hasErrors = true;
        } else {
            System.out.println("✅ RT values match perfectly");
        }

        System.out.printf("Total Intensity: Original=%.6f, Optimized=%.6f, Diff=%.6f%n",
                totalOriginalIntensity, totalOptimizedIntensity,
                Math.abs(totalOriginalIntensity - totalOptimizedIntensity));

        if (intensityMismatches > 0) {
            System.err.printf("❌ Intensity Mismatches: %d/%d (%.2f%%) | Max Diff: %.6f | Max Value: %.6f%n",
                    intensityMismatches, minLength,
                    100.0 * intensityMismatches / minLength, maxIntensityDiff, maxIntensityValue);
            hasErrors = true;
        } else {
            System.out.println("✅ Intensity values match perfectly");
        }

        // 5. 最终结论
        if (!hasErrors) {
            System.out.println("✅ SUCCESS: Results are identical");
        } else {
            System.err.println("❌ FAILURE: Significant differences detected!");
        }

        // 6. 详细差异报告（可选）
        if (hasErrors && minLength > 0) {
            System.out.println("\nTop 5 RT points with largest intensity differences:");
            List<IntensityDiff> diffs = new ArrayList<>();
            for (int i = 0; i < minLength; i++) {
                double diff = Math.abs(originalIntensities[i] - optimizedIntensities[i]);
                if (diff > 1e-6) {
                    diffs.add(new IntensityDiff(i, originalRts[i], diff));
                }
            }

            diffs.sort((a, b) -> Double.compare(b.diff, a.diff));
            int count = Math.min(5, diffs.size());
            for (int i = 0; i < count; i++) {
                IntensityDiff d = diffs.get(i);
                System.out.printf("Index %d (RT=%.4f): Diff=%.8f%n",
                        d.index, d.rt, d.diff);
            }
        }
    }

    // 辅助类用于存储强度差异
    private static class IntensityDiff {
        int index;
        double rt;
        double diff;

        public IntensityDiff(int index, double rt, double diff) {
            this.index = index;
            this.rt = rt;
            this.diff = diff;
        }
    }

}
