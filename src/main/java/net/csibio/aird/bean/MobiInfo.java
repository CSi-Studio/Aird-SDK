package net.csibio.aird.bean;

import lombok.Data;

@Data
public class MobiInfo {

  /**
   * start position in the aird for mobi array
   */
  long dictStart;

  /**
   * end position in the aird for mobi array
   */
  long dictEnd;

  /**
   * ion mobility unit
   */
  String unit;

  /**
   * ion mobility type, see MobilityType
   */
  String type;
}
