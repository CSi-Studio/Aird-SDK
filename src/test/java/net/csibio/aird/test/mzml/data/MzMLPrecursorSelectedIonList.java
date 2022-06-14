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
 * <p>MzMLPrecursorSelectedIonList class.</p>
 */
public class MzMLPrecursorSelectedIonList {

    private final ArrayList<MzMLPrecursorSelectedIon> selectedIonList;

    /**
     * <p>Constructor for MzMLPrecursorSelectedIonList.</p>
     */
    public MzMLPrecursorSelectedIonList() {
        this.selectedIonList = new ArrayList<>();
    }

    /**
     * <p>Getter for the field <code>selectedIonList</code>.</p>
     *
     * @return a {@link ArrayList} object.
     */
    public ArrayList<MzMLPrecursorSelectedIon> getSelectedIonList() {
        return selectedIonList;
    }

    /**
     * <p>addSelectedIon.</p>
     *
     * @param e a {@link MzMLPrecursorSelectedIon} object.
     */
    public void addSelectedIon(MzMLPrecursorSelectedIon e) {
        selectedIonList.add(e);
    }
}
