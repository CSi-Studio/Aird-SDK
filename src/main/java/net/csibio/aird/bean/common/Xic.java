package net.csibio.aird.bean.common;

import lombok.Data;

@Data
public class Xic {

  public double[] rts;
  public double[] ints;

  public Xic(double[] rts, double[] ints) {
    this.rts = rts;
    this.ints = ints;
  }
}
