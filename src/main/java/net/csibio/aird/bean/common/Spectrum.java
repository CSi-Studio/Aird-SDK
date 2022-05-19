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
