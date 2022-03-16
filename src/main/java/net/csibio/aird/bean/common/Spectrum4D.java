package net.csibio.aird.bean.common;

import lombok.Data;

@Data
public class Spectrum4D<T> {

  private T mzs;
  private float[] ints;
  private T mobilities;

  public Spectrum4D(T mzs, float[] ints, T mobilities) {
    this.mzs = mzs;
    this.ints = ints;
    this.mobilities = mobilities;
  }

}
