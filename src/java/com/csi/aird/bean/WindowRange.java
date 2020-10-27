/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.csi.aird.bean;

import lombok.Data;

/**
 * Created by James Lu MiaoShan
 * Time: 2018-07-26 23:32
 */
@Data
public class WindowRange {

    /**
     * precursor mz start
     */
    Double start;
    /**
     * precursor mz end
     */
    Double end;
    /**
     * precursor mz
     */
    Double mz;

    /**
     * extend features
     */
    String features;

    public WindowRange() {}

    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }

        if(obj instanceof WindowRange){
            WindowRange windowRange = (WindowRange) obj;
            if(start == null || end == null || windowRange.getStart() == null || windowRange.getEnd()==null){
                return false;
            }

            return (this.start.equals(windowRange.getStart()) && this.end.equals(windowRange.getEnd()));
        }else{
            return false;
        }
    }
}
