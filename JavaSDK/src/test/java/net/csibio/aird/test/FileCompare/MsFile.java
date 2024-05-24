package net.csibio.aird.test.FileCompare;

import lombok.Data;

@Data
public class MsFile {

    int fileNo;
    String name;
    String manufacturer;
    String acquisitionMethod;
    Double vendorSize;
    Double zdpdSize;
    Double ccSize;
    Double mzMLSize;
    Double mzMLNumSize;
    Double mzMLbSize;
    Double mzMLbNumSize;
    Double zdpdJsonSize;
    Double ccJsonSize;
    Double ccProtoSize;

    String mzCC;
    String intensityCC;
    String mobiCC;
    String rtCC;

    Long spectraCount;

    Double ctZdpd;
    Double ctCC;
    Double dtZdpd;
    Double dtCC;

    long dtZdpdJson;
    long dtJson;
    long dtProto;

    public MsFile() {
    }

    public MsFile(int fileNo) {
        this.fileNo = fileNo;
        this.name = "File"+fileNo;
    }
}
