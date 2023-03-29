namespace AirdSDK.Beans.Common;

public class IntSpectrum
{
    public int[] mzs;
    public double[] intensities;

    public IntSpectrum(int[] mzs, double[] intensities)
    {
        this.mzs = mzs;
        this.intensities = intensities;
    }
}