package net.csibio.aird.bean.common;

import lombok.Data;

@Data
public class Spectrum<T, D, F> {

    private T mzs;
    private D ints;
    private F mobilities;

    public Spectrum(T mzs, D ints) {
        this.mzs = mzs;
        this.ints = ints;
    }

    public Spectrum(T mzs, D ints, F mobilities) {
        this.mzs = mzs;
        this.ints = ints;
        this.mobilities = mobilities;
    }
}
