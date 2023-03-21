package net.csibio.aird.bean.common;

import lombok.Data;
import net.csibio.aird.bean.CV;
import net.csibio.aird.bean.WindowRange;

import java.util.List;

@Data
public class MrmPair {

    /**
     * order number for current spectrum
     */
    int num;

    String id;

    String key;

    WindowRange precursor;
    WindowRange product;
    String polarity;
    String activator;
    Float energy;

    /**
     * cvList for current scan
     */
    List<CV> cvList;

    double[] rts;
    double[] ints;
}
