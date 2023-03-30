namespace AirdSDK.Beans.Common;

public class ByteColumn
{
    public byte[] indexIds;
    public byte[] intensities;

    public ByteColumn(byte[] indexIds, byte[] intensities)
    {
        this.indexIds = indexIds;
        this.intensities = intensities;
    }
}