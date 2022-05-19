package net.csibio.aird.bean.common;

import lombok.Data;

@Data
public class AnyPair<T, R> {

  T left;
  R right;

  public AnyPair() {
  }

  public AnyPair(T l, R r) {
    this.left = l;
    this.right = r;
  }
}
