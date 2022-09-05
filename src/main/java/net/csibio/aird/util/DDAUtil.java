/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.util;

import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.DDAMs;
import net.csibio.aird.bean.DDAPasefMs;

/**
 * DDA Util
 */
public class DDAUtil {

    /**
     * Init a DDAMs instance from index instance
     *
     * @param ms    DDAMs instance
     * @param index index information
     * @param loc   index location
     */
    public static void initFromIndex(DDAMs ms, BlockIndex index, Integer loc) {
        if (index.getNums() != null && index.getNums().size() > 0) {
            ms.setNum(index.getNums().get(loc));
        }
        if (index.getCvList() != null && index.getCvList().size() > 0) {
            ms.setCvList(index.getCvList().get(loc));
        }
        if (index.getTics() != null && index.getTics().size() > 0) {
            ms.setTic(index.getTics().get(loc));
        }
        if (index.getRangeList() != null && index.getRangeList().size() > 0) {
            ms.setRange(index.getRangeList().get(loc));
        }
    }

    /**
     * Init a DDAPasefMs instance from index instance
     *
     * @param ms    DDAPasefMs instance
     * @param index index information
     * @param loc   index location
     */
    public static void initFromIndex(DDAPasefMs ms, BlockIndex index, Integer loc) {
        if (index.getNums() != null && index.getNums().size() > 0) {
            ms.setNum(index.getNums().get(loc));
        }
        if (index.getCvList() != null && index.getCvList().size() > 0) {
            ms.setCvList(index.getCvList().get(loc));
        }
        if (index.getTics() != null && index.getTics().size() > 0) {
            ms.setTic(index.getTics().get(loc));
        }
        if (index.getRangeList() != null && index.getRangeList().size() > 0) {
            ms.setRange(index.getRangeList().get(loc));
        }
    }
}
