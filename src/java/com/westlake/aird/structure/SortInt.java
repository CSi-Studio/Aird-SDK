package com.westlake.aird.structure;

import lombok.Data;

@Data
public class SortInt implements Comparable{
    int number;
    Integer layer; //二进制表达,表示所在的原层数

    public SortInt(int number, Integer layer){
        this.number = number;
        this.layer = layer;
    }

    @Override
    public int compareTo(Object obj) {
        SortInt sortInt = (SortInt)obj;
        return this.number - sortInt.number;
    }
}
