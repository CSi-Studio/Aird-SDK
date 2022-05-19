package net.csibio.aird.parser;

import net.csibio.aird.bean.AirdInfo;
import net.csibio.aird.bean.Compressor;
import net.csibio.aird.enums.AirdType;
import net.csibio.aird.exception.ScanException;

/**
 * DIA Parser
 */
public class DIAParser extends BaseParser {

  /**
   * 构造函数
   *
   * @param indexFilePath index file path
   * @throws ScanException scan exception
   */
  public DIAParser(String indexFilePath) throws Exception {
    super(indexFilePath);
  }

  public DIAParser(String indexFilePath, AirdInfo airdInfo) throws Exception {
    super(indexFilePath, airdInfo);
  }

  /**
   * 构造函数
   *
   * @param airdPath      aird file path
   * @param mzCompressor  mz compressor
   * @param intCompressor intensity compressor
   * @throws ScanException scan exception
   */
  public DIAParser(String airdPath, Compressor mzCompressor, Compressor intCompressor)
      throws Exception {
    super(airdPath, mzCompressor, intCompressor, null, AirdType.DIA.getName());
  }
}
