package net.csibio.aird.bean;

import lombok.Data;

@Data
public class SpectrumDetail {

  float rt;
  float[] mzs;
  float[] intensities;
  byte[] mzBytes;    //压缩前的数mz据流
  byte[] intensityBytes;   //压缩前的数intensity据流

}
