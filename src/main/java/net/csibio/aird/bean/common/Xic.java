package net.csibio.aird.bean.common;

import lombok.Data;

@Data
public class Xic<T, D> {

  public T rts;
  public D ints;

  public Xic(T rts, D ints) {
    this.rts = rts;
    this.ints = ints;
  }
}
