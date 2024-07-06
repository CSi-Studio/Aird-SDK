package net.csibio.aird.test.FileCompare;

import lombok.Data;

@Data
public class MsFile {

    int fileNo;
    String name;
    String manufacturer;
    String acquisitionMethod;
    long vendor;
    long zdpd;
    long comboComp;
    long mspack;
    long mzML;
    long mzML_Numpress;
    long mzMLb;
    long mzMLb_Numpress;

    double zdpdJsonSize;
    double ccJsonSize;
    double ccJsonCompressedSize;
    double ccProtoSize;

    String mzCC;
    String intensityCC;
    String mobiCC;
    String rtCC;
    String compressor;

    Double jsonZdpdVsCC;
    Double jsonVsProto;
    Double dtZdpdVsCC;
    Double dtJsonVsProto;

    Long spectraCount;

    //压缩时间的比较
    Integer ctMzML;
    Integer ctMzMLb;
    Integer ctMzMLNum;
    Integer ctMzMLbNum;
    Integer ctMspack;
    Integer ctZdpd;
    Integer ctCC;

    Integer dtMzML;
    Integer dtVendor;
    Integer dtZdpd;
    Integer dtCC;

    //索引文件的读取速度
    long dtZdpdJson;
    long dtJson;
    long dtProto;

    String mzHighComp;
    String intensityHighComp;
    String mobiHighComp;
    long sizeHighComp;
    long dtHighComp;

    String mzHighDT;
    String intensityHighDT;
    String mobiHighDT;
    long sizeHighDT;
    long dtHighDT;

   String mzBalance;
    String intensityBalance;
    String mobiBalance;
    long sizeBalance;
    long dtBalance;

    double sizeUp;
    double dtUp;

    String tag;

    public MsFile() {
    }

    public MsFile(int fileNo) {
        this.fileNo = fileNo;
        this.name = "File"+fileNo;
    }
}
