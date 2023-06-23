package net.csibio.aird.test;

import com.alibaba.fastjson2.JSON;
import net.csibio.aird.compressor.bytecomp.ZlibWrapper;
import net.csibio.aird.util.FileUtil;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

public class ReadMzML {

    @Test
    public void readBase64String() {
        String mzOriString = "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";
        String mz = "              <binary>eJwdkn9M1HUYx+MkNeUuNIvguCNhxlDoMiOlcm7Pc2OMyBi76ZdGXuuoXHcVyklrZkGF/NCLzjbnmkOzxrIxV4IMyI6ui4y55lrTtgzwgssuyBDJAMl60R+v3b6fz/t5v5/n+VxqYqlk3FoqqxaWyppFpbJxiUucS11SnOSSUrNLtlhcUnG7S55Jdsn2ZS55eblLdt3hkj19W6UpvFVavtpKrUGtIWsXG7L+NgMPAw8DDwMPAw8DDwMPAw8DDwMPQwoerJBN+RVS+FCFlKyvkLINFWIUVMi2h/ntccu2XrdUfu6WF067ZccXbnkl5CbXLW996SbbQ7ZHDkY8cvhrjxzr98jH33jkxBmPdH7rkd4Bj0zt8crs6165pdYrC+u8svwtr6S+7ZWMeq+s2uuVFff5xOrwycr7fZK91id5D/hk3Tofffnoy0dfPvry0ZePvnz0Na+pQlOFpor7Ku6ruK/ivkqCf1RL65/V8tFEtXxytVo+nayWrmvVcjTNL21Wv5y0+aXb7pdQhl/67/FT76feT71fmhJqJclUKy2wfEGtHITUxFo5DCcHGyR/qEG6ITWxkbNG3qxRjsGqhY3svlFOwNrFjdIJ629rlJsrmiXxrmZpgCUpzRKA5Lub5T2QSwEpigbkLGz+JSBtF1sk5+cWaYf8oSA5QXlkOIguiCaIJiiuEb7vOSBFKw/IWShOOijfwQ/mg+S18uatEgbn0lbmbZUsa6vkpLfKk6MfyCA8HftA5uLH5Y3fj4tp7Dj+HRICudRBLx3kdJDTIba0Lmq72FcXM4WYKYR/iL32ycqJPsm+2sdu+9htH35hvMJSD/vGw2L5I8wbhMX6ZxhtmHeYP4twFpFDVyKcRziPcB7BJyJ5kxF8IrxRBK9+vPrx6kfbj7afzH52MiCOwQH2MiAlfw/LOTg/PSzXf4vKq/Eo80SpG6FuhB5GyBuRFVf4zRuVIBy6b5T/2Cj/sVF2GJMfYXA0xj5i8tyvMYlD9tUYM8XoJSaF12PkxMiJkRMjf4z8MfLHZC5nXN5YPS71a8Zlce44GeNkzjDfDJkz1CRo8nSCvgeJ103aAEv+NmkJJE+bODdpyoxJJycX6K5rC7TrfxK1YCpRd0PWjaXaBjlzS/X0VJLehLq/kvBK0kIYmOcus5akmDUAk5NmXXfNjIcZDzMeZmrM1Jg1AoXXzdSY6QE9JE+btQzOT89rLWgtaC1oLWgtaC3alLBMz8Jm0zLdNpumUci6kcYMVj0HZdNW6q3MYdX3wTZr1aMQhcobVr0ML8xZ1ZhJ14tgm03nPp1d2OjDRh82erUzn103/WUn3858du7t+Nvxt+Nvp96Ov516O/Wc352p58FIzcQnk34y0WfS42qyV9PjavJy+c7lO5cd5mo7OP7J1YIVeXoaNt2Zh8aBxoHGQb8b6HcD/W7QlIKH9X24CFP/bNRXbm7UWdg9VcyeiumzGK/H9STk33xcX1z0hE5A9eIn1JZWrkchy1qubZCTXq7t4LCVa/Sncq28WK6XIX7vU/pi9lM6AYcsbrXe7tZWcAzu0pOQP7QLfQ36GvQ12v5zDXc13NWQ+Zp2wyP/vqZFO+q0Cc5C0s463bzzTf0UJmF8x16+92oLjNTv18K9+7UBBiDj1oA+DcegYWFA47BmUUALKo/obhiEjGePaB1I7Yf6EcSgs/EzTWn6TA34ceSUpo6e0t0wCBmxUxpu7tGsfT1aD2cgZ3+P7oMr4Aj0aBCmAvOaXjS9ehlM3i/UCW1QUBkiP0R+SLfvuMBcFzRv5wXd8s6QdoO1ZUj3wDCsfHcIr2Huhrkb1vivl7Tk8iU9AUnPRPVl+B7KfotrJ6TE4zrx/JiWbR/TTjgzNqfF43PaDue/TXCmDCQ4i2DLS0ucDdAN0eJU5/LHUp0uKIqnO5ugFwLZjzrPwX+5KWeb</binary>";
        String intensityOrigin = "eJytVL9Lw0AU/qCLm4PUOaO6WHAR7BA0dwERyejo4FDQqYogFA3oIJ1cLOogBREc619gRkGEDg4uQszkJE4ODuK7JBev1ySmrRce9+7y7v343seD17JgBJHI1Z5kqJ0xHJc4/EMWnttk579GIpd4K9dTjSMwbdQdGxd3HE6V422co1tm8Hwr2f1TK9x1Hx2KIXTVp0exag8M9+/0r8LxsUp5kMhlVhm6Exw33ShHcRZ5tlvpOUpdvfP3Gb6uOEq7Nh6vbZc+bE3bGJvlwAyHd8JglCNJ8KH3lSWG9SpLzn3xCE9RU14eufkFVqad+OdQbG/7tzeI+6fmIuKHOQZWKi49/pVck3cpsVWe6PmtNf+uV31vKDUOjI+ChawdgXYv41AtjUWOnT3i5C3D1BFDhXjkCs5l8LlH3yjWRx3TNE7LO/293ps87hg53FB10cMifcjjWRq2qo0p9XMGZ45j/pPjsslhvhAvD+gurkPYCd3UYwX9uGXpLulCGpscKwscz98MnU7URze2c1OwSvOV4K3WGN87kvtB9iwpgl8Rm//S1XkTzurl4WaP1IX49Wz+yLnyly9hV2QOSqyGnQPq3O3ZB/SZlZeuj+JnxJ7/AHnYUcQ=";
        String intensity = "              <binary>eJytVL9Lw0AU/sDFzUHqnFFdLLgIdgiau4iDZHR0cCi4iILoogEdpJOLpW4BERzrX2BGwSVjl0LM5CRO4iS+S3Lxek3StPXgkXcv796P7308+G0LRpSIPN4CQ7PDcD3DEV6y+O6RX/iWiDzirTy9JvmaNg4dG51nDqfB8T7HEdQY/NDKvuGtFX/1GF3KIXQ1pk+5mq8MLx/0r87xuUN1kMhjNhiCeY7HIKlR3EWdXju/RqmrtvCc4fueAyc2ggcbWy71sGRjdoVsyxz+DYNRSyTDh97XNxn2Giy7D+UjPEVPZXWU1hdZhX7in0O5/eO/2SCdn1qLyB/XGFm5uAzEV2rN3uXkVnmi17fbGt2v+t5QehwbHwUL2TsizS7zUC/uBsfpGXHyiWHxiqFOPHIF5wr4PKDvV5ujjmkep6VNf6/Ppow7Rgk3VF3MsMocyniWh63qY0r9jsFZ5Vj74vBaHGafeHlBtrQP4Sd0U88VDeNWpLukx3JA+2Wdo/fD0O0mc3RTPzcHq7xYGd5qj6ndkdyPindJFfyq+PyXru6beFdvT7Z7pC4kPCrmj9wro2IJvyp7UGI16R5Q9+7Ad8yYRXXp+jRxppz5L3GoUVc=</binary>";
        float[] mzsOrigin = parse(mzOriString);
        float[] mzs = parse(mz.trim().replace("<binary>", "").replace("</binary>", ""));
        float[] intsOrigin = parse(intensityOrigin);
        float[] ints = parse(intensity.trim().replace("<binary>", "").replace("</binary>", ""));
        System.out.println("HelloWorld");
    }

    @Test
    public void statForFileSize() {
        statMzFileSizes();
        statIntensityFileSizes();
    }

    public void statMzFileSizes() {
        String[] directories = new String[]{
                "E:\\PrecisionControl\\mzML\\mz3dp",
                "E:\\PrecisionControl\\mzML\\mz4dp",
                "E:\\PrecisionControl\\mzML\\mz5dp",
                "E:\\PrecisionControl\\mzML\\mz2x5dp",
                "E:\\PrecisionControl\\mzML\\mz5x5dp",
        };
        for (String directory : directories) {
            System.out.println("scanning" + directory + ":");
            TreeMap<String, Long> fileSizeMap = FileUtil.sumFileSizes(directory);
            long[] sizes = new long[fileSizeMap.size()];
            int i = 0;
            for (Long value : fileSizeMap.values()) {
                sizes[i] = value/1024/1024;
                i++;
            }
            System.out.println(JSON.toJSONString(sizes));
        }
    }

    public void statIntensityFileSizes() {
        String[] directories = new String[]{
                "E:\\PrecisionControl\\mzML\\int2",
                "E:\\PrecisionControl\\mzML\\int02",
                "E:\\PrecisionControl\\mzML\\int002",
                "E:\\PrecisionControl\\mzML\\int008",
                "E:\\PrecisionControl\\mzML\\int0002",
        };
        for (String directory : directories) {
            System.out.println("scanning" + directory + ":");
            TreeMap<String, Long> fileSizeMap = FileUtil.sumFileSizes(directory);
            long[] sizes = new long[fileSizeMap.size()];
            int i = 0;
            for (Long value : fileSizeMap.values()) {
                sizes[i] = value/1024/1024;
                i++;
            }
            System.out.println(JSON.toJSONString(sizes));
        }
    }

    public float[] parse(String base64) {
        byte[] bytes = Base64.getDecoder().decode(base64);
        ByteBuffer byteBuffer = ByteBuffer.wrap(new ZlibWrapper().decode(bytes));
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        FloatBuffer fb = byteBuffer.asFloatBuffer();
        float[] fs = new float[fb.capacity()];
        for (int i = 0; i < fb.capacity(); i++) {
            fs[i] = fb.get(i);
        }
        return fs;
    }
}
