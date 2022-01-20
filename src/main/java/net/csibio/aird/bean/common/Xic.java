package net.csibio.aird.bean.common;

import lombok.Data;

@Data
public class Xic<T> {

  public T rts;
  public float[] ints;

  public Xic(T rts, float[] ints) {
    this.rts = rts;
    this.ints = ints;
  }

}
