/*
 * Copyright (c) 2020 CSi Biotech
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class WindowRange implements Serializable {

    private static final long serialVersionUID = -1232223122L;
    /**
     * 前体质量起始数值 precursor mz start
     */
    Double start;
    /**
     * 前体质量结束数值 precursor mz end
     */
    Double end;
    /**
     * 前体质量精准数值 precursor mz
     */
    Double mz;

    /**
     * 前体带电量,通常为空,0也表示为空
     */
    Integer charge;

    /**
     * extend features
     */
    String features;

    public WindowRange() {
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof WindowRange) {
            WindowRange windowRange = (WindowRange) obj;
            if (start == null || end == null || windowRange.getStart() == null || windowRange.getEnd() == null) {
                return false;
            }

            return (this.start.equals(windowRange.getStart()) && this.end.equals(windowRange.getEnd()));
        } else {
            return false;
        }
    }

    public static WindowRange fromProto(net.csibio.aird.bean.proto.WindowRange.WindowRangeProto proto) {
        if (proto == null) {
            return null;
        }

        WindowRange windowRange = new WindowRange();
        windowRange.setStart(proto.getStart());
        windowRange.setEnd(proto.getEnd());
        windowRange.setMz(proto.getMz());
        windowRange.setCharge(proto.getCharge());
        windowRange.setFeatures(proto.getFeatures());

        return windowRange;
    }
}
