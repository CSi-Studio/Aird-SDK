package net.csibio.aird.test.thermo;

import com.sun.jna.Platform;
import io.github.msdk.datamodel.MsScan;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Logger;
import java.util.zip.ZipInputStream;
import net.csibio.aird.test.mzml.MzMLFileImportMethod;
import net.csibio.aird.test.util.ZipUtils;
import org.apache.commons.io.FileUtils;

public class RawFileReader {

  private Logger logger = Logger.getLogger(this.getClass().getName());

  public void parse(File fileToOpen) {

    Process dumper = null;
    try {
      final File thermoRawFileParserDir = unzipThermoRawFileParser();
      String thermoRawFileParserCommand;

      if (Platform.isWindows()) {
        thermoRawFileParserCommand =
            thermoRawFileParserDir + File.separator + "ThermoRawFileParser.exe";
      } else if (Platform.isLinux()) {
        thermoRawFileParserCommand =
            thermoRawFileParserDir + File.separator + "ThermoRawFileParserLinux";
      } else if (Platform.isMac()) {
        thermoRawFileParserCommand =
            thermoRawFileParserDir + File.separator + "ThermoRawFileParserMac";
      } else {
        logger.info("Unsupported platform: JNA ID " + Platform.getOSType());
        return;
      }

      final String cmdLine[] = new String[]{ //
          thermoRawFileParserCommand, // program to run
          "-s", // output mzML to stdout
          "-p", // no peak picking
          "-z", // no zlib compression (higher speed)
          "-f=1", // no index, https://github.com/compomics/ThermoRawFileParser/issues/118
          "-i", // input RAW file name coming next
          fileToOpen.getPath() // input RAW file name
      };

      // Create a separate process and execute ThermoRawFileParser.
      // Use thermoRawFileParserDir as working directory; this is essential, otherwise the process will fail.
      dumper = Runtime.getRuntime().exec(cmdLine, null, thermoRawFileParserDir);

      // Get the stdout of ThermoRawFileParser process as InputStream
      InputStream mzMLStream = dumper.getInputStream();
      BufferedInputStream bufStream = new BufferedInputStream(mzMLStream);

      MzMLFileImportMethod msdkTask = new MzMLFileImportMethod(bufStream);
      msdkTask.execute();
      io.github.msdk.datamodel.RawDataFile msdkFile = msdkTask.getResult();

      if (msdkFile == null) {
        logger.info("MSDK returned null");
        return;
      }
      int totalScans = msdkFile.getScans().size();

      for (MsScan scan : msdkFile.getScans()) {
        //TODO 解码scan
      }

      // Finish
      bufStream.close();
      dumper.destroy();

    } catch (Throwable e) {

      e.printStackTrace();

      if (dumper != null) {
        dumper.destroy();
      }
      return;
    }
  }

  private File unzipThermoRawFileParser() throws IOException {
    final String tmpPath = System.getProperty("java.io.tmpdir");
    File thermoRawFileParserFolder = new File(tmpPath, "mzmine_thermo_raw_parser");
    final File thermoRawFileParserExe = new File(thermoRawFileParserFolder,
        "ThermoRawFileParser.exe");

    // Check if it has already been unzipped
    if (thermoRawFileParserFolder.exists() && thermoRawFileParserFolder.isDirectory()
        && thermoRawFileParserFolder.canRead() && thermoRawFileParserExe.exists()
        && thermoRawFileParserExe.isFile() && thermoRawFileParserExe.canExecute()) {
      logger.finest("ThermoRawFileParser found in folder " + thermoRawFileParserFolder);
      return thermoRawFileParserFolder;
    }

    // In case the folder already exists, unzip to a different folder
    if (thermoRawFileParserFolder.exists()) {
      logger.finest("Folder " + thermoRawFileParserFolder + " exists, creating a new one");
      thermoRawFileParserFolder = Files.createTempDirectory("mzmine_thermo_raw_parser").toFile();
    }

    logger.finest("Unpacking ThermoRawFileParser to folder " + thermoRawFileParserFolder);
    InputStream zipStream = getClass()
        .getResourceAsStream("/vendorlib/thermo/ThermoRawFileParser.zip");
    if (zipStream == null) {
      throw new IOException(
          "Failed to open the resource /vendorlib/thermo/ThermoRawFileParser.zip");
    }
    ZipInputStream zipInputStream = new ZipInputStream(zipStream);
    ZipUtils.unzipStream(zipInputStream, thermoRawFileParserFolder);
    zipInputStream.close();

    // Delete the temporary folder on application exit
    FileUtils.forceDeleteOnExit(thermoRawFileParserFolder);

    return thermoRawFileParserFolder;
  }
}
