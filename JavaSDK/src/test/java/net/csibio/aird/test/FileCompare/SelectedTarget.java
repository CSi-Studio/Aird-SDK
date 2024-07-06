package net.csibio.aird.test.FileCompare;

import lombok.Data;

@Data
public class SelectedTarget {

    int fileNo;

    String type;

    double dtRatio;
    double sizeRatio;

    public SelectedTarget() {
    }

    public SelectedTarget(int fileNo) {
        this.fileNo = fileNo;
    }
}
