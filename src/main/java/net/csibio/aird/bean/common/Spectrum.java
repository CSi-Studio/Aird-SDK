package net.csibio.aird.bean.common;

import lombok.Data;

@Data
public class Spectrum<T> {

  public T mzs;
  public float[] ints;

  public Spectrum(T mzs, float[] ints) {
    this.mzs = mzs;
    this.ints = ints;
  }

}
