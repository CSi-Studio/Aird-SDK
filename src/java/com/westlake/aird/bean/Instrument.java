package com.westlake.aird.bean;

import java.util.List;

public class Instrument {

    /**
     * 设备仪器厂商
     * Instrument manufacturer
     */
    public String manufacturer;

    /**
     * 设备类型
     * Instrument Model
     */
    public String model;


    public List<String> source;

    /**
     * 分析方式
     */
    public List<String> analyzer;

    /**
     * 探测器
     */
    public List<String> detector;

    /**
     * 其他特征,使用K:V;K:V;K:V;类似的格式进行存储
     */
    public String features;

}
