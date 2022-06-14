/*
 * (C) Copyright 2015-2016 by MSDK Development Team
 *
 * This software is dual-licensed under either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1 as published by the Free
 * Software Foundation
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by the Eclipse Foundation.
 */

package net.csibio.aird.test.mzml.data;

import java.util.ArrayList;

/**
 * <p>MzMLScanList class.</p>
 */
public class MzMLScanList extends MzMLCVGroup {

    private final ArrayList<MzMLScan> scans;

    /**
     * <p>Constructor for MzMLScanList.</p>
     */
    public MzMLScanList() {
        this.scans = new ArrayList<>();
    }

    /**
     * <p>Getter for the field <code>scans</code>.</p>
     *
     * @return a {@link ArrayList} object.
     */
    public ArrayList<MzMLScan> getScans() {
        return scans;
    }

    /**
     * <p>addScan.</p>
     *
     * @param scan a {@link io.github.msdk.io.mzml.data.MzMLScan} object.
     */
    public void addScan(MzMLScan scan) {
        scans.add(scan);
    }

}
