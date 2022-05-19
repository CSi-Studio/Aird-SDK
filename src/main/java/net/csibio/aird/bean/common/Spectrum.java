/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.bean.common;

import lombok.Data;

@Data
public class Spectrum {

  private double[] mzs;
  private double[] ints;
  private double[] mobilities;

  public Spectrum(double[] mzs, double[] ints) {
    this.mzs = mzs;
    this.ints = ints;
  }

  public Spectrum(double[] mzs, double[] ints, double[] mobilities) {
    this.mzs = mzs;
    this.ints = ints;
    this.mobilities = mobilities;
  }
}
