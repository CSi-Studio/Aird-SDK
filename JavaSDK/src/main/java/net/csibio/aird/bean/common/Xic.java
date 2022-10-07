/*
 * Copyright (c) 2020 CSi Biotech
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.bean.common;

import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

@Data
public class Xic {

    public double[] rts;
    public double[] ints;

    //用于存储每一张光谱图中计算XIC时最大的mz
    public double[] mzs;

    public Xic() {
    }

    public Xic(double[] rts, double[] ints) {
        this.rts = rts;
        this.ints = ints;
    }

    public Xic(List<Double> rtList, List<Double> intensityList, List<Double> mzList) {
        this.setRts(ArrayUtils.toPrimitive(rtList.toArray(new Double[0])));
        this.setInts(ArrayUtils.toPrimitive(intensityList.toArray(new Double[0])));

        if (mzList == null) {
            this.setMzs(null);
        } else {
            this.setMzs(ArrayUtils.toPrimitive(mzList.toArray(new Double[0])));
        }
    }

    public Xic(double[] rts, double[] ints, double[] mzs) {
        this.rts = rts;
        this.ints = ints;
        this.mzs = mzs;
    }
}
