package net.csibio.aird.util;

import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.DDAMs;
import net.csibio.aird.bean.DDAPasefMs;

public class DDAUtil {

  public static void initFromIndex(DDAMs ms, BlockIndex index, Integer loc) {
    if (index.getNums() != null && index.getNums().size() > 0) {
      ms.setNum(index.getNums().get(loc));
    }
    if (index.getCvList() != null && index.getCvList().size() > 0) {
      ms.setCvList(index.getCvList().get(loc));
    }
    if (index.getTics() != null && index.getTics().size() > 0) {
      ms.setTic(index.getTics().get(loc));
    }
    if (index.getRangeList() != null && index.getRangeList().size() > 0) {
      ms.setRange(index.getRangeList().get(loc));
    }
  }

  public static void initFromIndex(DDAPasefMs ms, BlockIndex index, Integer loc) {
    if (index.getNums() != null && index.getNums().size() > 0) {
      ms.setNum(index.getNums().get(loc));
    }
    if (index.getCvList() != null && index.getCvList().size() > 0) {
      ms.setCvList(index.getCvList().get(loc));
    }
    if (index.getTics() != null && index.getTics().size() > 0) {
      ms.setTic(index.getTics().get(loc));
    }
    if (index.getRangeList() != null && index.getRangeList().size() > 0) {
      ms.setRange(index.getRangeList().get(loc));
    }
  }
}
