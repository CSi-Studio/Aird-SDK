namespace AirdSDK.Bean.Msi
{
    public class SpectraPosition
    {
        public int[] x {  get; set; }
        public int[] y { get; set; }
        public int[] z { get; set; }

        public SpectraPosition(int[] x, int[] y, int[] z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
