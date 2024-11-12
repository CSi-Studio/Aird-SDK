package net.csibio.aird.test.airdslice;

import lombok.Data;

@Data
public class EChartsSeries {

    String name;
    double[] data;
    String type = "line";

    public EChartsSeries(String name, double[] data){
        this.name = name;
        this.data = data;
    }
}
