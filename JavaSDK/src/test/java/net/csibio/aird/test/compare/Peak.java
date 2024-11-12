package net.csibio.aird.test.compare;

import lombok.Data;

@Data
public class Peak {

    String group;
    float rt;
    float rtStart;
    float rtEnd;

    float mz;
    float mzStart;
    float mzEnd;

    float height;
    float area;

    float intMin;
    float intMax;

    @Override
    public boolean equals(Object arg0) {
        Peak o = (Peak) arg0;
      return Math.abs(this.mz - o.mz) <= 0.001 && Math.abs(this.rt - o.rt) <= 0.1;
    }
}
