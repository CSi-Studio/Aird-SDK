package net.csibio.aird.bean.common;

import java.io.Serializable;
import lombok.Data;

@Data
public class AnyPairs<T, D> implements Serializable {

  T[] x;
  D[] y;

  public AnyPairs() {
  }

  public AnyPairs(T[] x, D[] y) {
    this.x = x;
    this.y = y;
  }

  public int length() {
    if (x == null) {
      return 0;
    }
    return x.length;
  }

  public T[] getX() {
    return x;
  }

  public D[] getY() {
    return y;
  }
}
