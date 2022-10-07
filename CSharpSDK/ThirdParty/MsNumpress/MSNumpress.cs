/*
	MSNumpress.cs
	rfellers@gmail.com
	Copyright 2017 Ryan Fellers

    Based on:
    MSNumpress.java and IntDecoder.java
	johan.teleman@immun.lth.se

	Copyright 2013 Johan Teleman
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

using System;
using System.Diagnostics;

/// <summary>
/// Implementations of two compression schemes for numeric data from mass spectrometers.
/// </summary>
public class MSNumpress
{
    /// <summary>
    /// MS Numpress linear prediction compression
    /// </summary>
    public const string ACC_NUMPRESS_LINEAR = "MS:1002312";

    /// <summary>
    /// MS Numpress positive integer compression
    /// </summary>
    public const string ACC_NUMPRESS_PIC = "MS:1002313";

    /// <summary>
    /// MS Numpress short logged float compression
    /// </summary>
    public const string ACC_NUMPRESS_SLOF = "MS:1002314";

    /// <summary>
    /// Convenience function for decoding binary data encoded by MSNumpress.
    /// </summary>
    /// <param name="cvAccession">The PSI-MS obo CV accession of the encoded data.</param>
    /// <param name="data">array of double to be encoded.</param>
    /// <param name="dataSize">number of doubles from data to encode.</param>
    /// <returns>The decoded doubles</returns>
    /// <exception cref="ArgumentException">
    /// Cannot decode numLin data, need at least 8 initial bytes for fixed point.
    /// or
    /// Corrupt numLin data!
    /// or
    /// Cannot decode numPic data, need at least 8 initial bytes for fixed point.
    /// or
    /// Corrupt numPic data!
    /// or
    /// '" + cvAccession + "' is not a numpress compression term
    /// </exception>
    /// <remarks>
    /// If the passed cvAccession is one of
    /// 
    ///    ACC_NUMPRESS_LINEAR = "MS:1002312"
    ///    ACC_NUMPRESS_PIC    = "MS:1002313"
    ///    ACC_NUMPRESS_SLOF   = "MS:1002314"
    /// 
    /// the corresponding decode function will be called.
    /// </remarks>
    public static double[] decode(string cvAccession, byte[] data, int dataSize)
    {
        if (cvAccession == ACC_NUMPRESS_LINEAR)
        {
            if (dataSize < 8 || data.Length < 8)
                throw new ArgumentException("Cannot decode numLin data, need at least 8 initial bytes for fixed point.");

            double[] buffer = new double[dataSize * 2];
            int nbrOfDoubles = MSNumpress.decodeLinear(data, dataSize, buffer);
            if (nbrOfDoubles < 0)
                throw new ArgumentException("Corrupt numLin data!");

            double[] result = new double[nbrOfDoubles];
            Array.Copy(buffer, 0, result, 0, nbrOfDoubles);

            return result;
        }

        if (cvAccession == ACC_NUMPRESS_SLOF)
        {
            double[] result = new double[(dataSize - 8) / 2];
            MSNumpress.decodeSlof(data, dataSize, result);

            return result;
        }

        if (cvAccession == ACC_NUMPRESS_PIC)
        {
            if (dataSize < 8 || data.Length < 8)
                throw new ArgumentException("Cannot decode numPic data, need at least 8 initial bytes for fixed point.");

            double[] buffer = new double[dataSize * 2];
            int nbrOfDoubles = MSNumpress.decodePic(data, dataSize, buffer);
            if (nbrOfDoubles < 0)
                throw new ArgumentException("Corrupt numPic data!");

            double[] result = new double[nbrOfDoubles];
            Array.Copy(buffer, 0, result, 0, nbrOfDoubles);
            return result;

        }

        throw new ArgumentException("'" + cvAccession + "' is not a numpress compression term");
    }

    /// <summary>
    /// This encoding works on a 4 byte integer, by truncating initial zeros or ones.
    /// </summary>
    /// <param name="x">the int to be encoded</param>
    /// <param name="res">the byte array were halfbytes are stored</param>
    /// <param name="resOffset">position in res were halfbytes are written</param>
    /// <returns>the number of resulting halfbytes</returns>
    /// <remarks>
    /// If the initial (most significant) half byte is 0x0 or 0xf, the number of such 
    /// halfbytes starting from the most significant is stored in a halfbyte. This initial 
    /// count is then followed by the rest of the ints halfbytes, in little-endian order. 
    /// A count halfbyte c of
    /// 
    /// 		0 &lt;= c &lt;= 8 		is interpreted as an initial c 		0x0 halfbytes 
    /// 		9 &lt;= c &lt;= 15		is interpreted as an initial (c-8) 	0xf halfbytes
    /// 
    /// Ex:
    /// 	int		c		rest
    /// 	0 	=> 	0x8
    /// 	-1	=>	0xf		0xf
    /// 	23	=>	0x6 	0x7	0x1
    /// 
    /// 	@x			the int to be encoded
    ///	@res		the byte array were halfbytes are stored
    ///	@resOffset	position in res were halfbytes are written
    ///	@return		the number of resulting halfbytes
    /// </remarks>
    public static int encodeInt(long x, byte[] res, int resOffset)
    {
        byte i, l;
        long m;
        long mask = 0xf0000000;
        long init = x & mask;

        if (init == 0)
        {
            l = 8;
            for (i = 0; i < 8; i++)
            {
                m = mask >> (4 * i);
                if ((x & m) != 0)
                {
                    l = i;
                    break;
                }
            }
            res[resOffset] = l;
            for (i = l; i < 8; i++)
                res[resOffset + 1 + i - l] = (byte)(0xf & (x >> (4 * (i - l))));

            return 1 + 8 - l;

        }
        else if (init == mask)
        {
            l = 7;
            for (i = 0; i < 8; i++)
            {
                m = mask >> (4 * i);
                if ((x & m) != m)
                {
                    l = i;
                    break;
                }
            }
            res[resOffset] = (byte)(l | 8);
            for (i = l; i < 8; i++)
                res[resOffset + 1 + i - l] = (byte)(0xf & (x >> (4 * (i - l))));

            return 1 + 8 - l;

        }
        else
        {
            res[resOffset] = 0;
            for (i = 0; i < 8; i++)
                res[resOffset + 1 + i] = (byte)(0xf & (x >> (4 * i)));

            return 9;

        }
    }

    public static void encodeFixedPoint(double fixedPoint, byte[] result)
    {
        //long fp = double.doubleToLongBits(fixedPoint);
        long fp = BitConverter.DoubleToInt64Bits(fixedPoint); // RTF

        for (int i = 0; i < 8; i++)
        {
            result[7 - i] = (byte)((fp >> (8 * i)) & 0xff);
        }
    }

    public static double decodeFixedPoint(byte[] data)
    {
        long fp = 0;
        for (int i = 0; i < 8; i++)
        {
            fp = fp | ((0xFFL & data[7 - i]) << (8 * i));
        }

        //return double.longBitsToDouble(fp);
        return BitConverter.Int64BitsToDouble(fp);
    }

    /////////////////////////////////////////////////////////////////////////////////

    public static double optimalLinearFixedPoint(double[] data, int dataSize)
    {
        if (dataSize == 0) return 0;
        if (dataSize == 1) return Math.Floor(0xFFFFFFFFL / data[0]);
        double maxDouble = Math.Max(data[0], data[1]);

        for (int i = 2; i < dataSize; i++)
        {
            double extrapol = data[i - 1] + (data[i - 1] - data[i - 2]);
            double diff = data[i] - extrapol;
            maxDouble = Math.Max(maxDouble, Math.Ceiling(Math.Abs(diff) + 1));
        }

        return Math.Floor(0x7FFFFFFFL / maxDouble);
    }

    /// <summary>
    /// Encodes data using MS Numpress linear prediction compression.
    /// </summary>
    /// <param name="data">array of doubles to be encoded</param>
    /// <param name="dataSize">number of doubles from data to encode</param>
    /// <param name="result">array were resulting bytes should be stored</param>
    /// <param name="fixedPoint">the scaling factor used for getting the fixed point repr. This is stored in the binary and automatically extracted on decoding.</param>
    /// <returns>the number of encoded bytes</returns>
    /// <remarks>
    /// Encodes the doubles in data by first using a 
    ///   - lossy conversion to a 4 byte 5 decimal fixed point repressentation
    ///   - storing the residuals from a linear prediction after first two values
    ///   - encoding by encodeInt (see above) 
    /// 
    /// The resulting binary is maximally 8 + dataSize * 5 bytes, but much less if the 
    /// data is reasonably smooth on the first order.
    ///
    /// This encoding is suitable for typical m/z or retention time binary arrays. 
    /// On a test set, the encoding was empirically show to be accurate to at least 0.002 ppm.
    /// </remarks>
    public static int encodeLinear(double[] data, int dataSize, byte[] result, double fixedPoint)
    {
        long[] ints = new long[3];
        int i;
        int ri = 16;
        byte[] halfBytes = new byte[10];
        int halfByteCount = 0;
        int hbi;
        long extrapol;
        long diff;

        encodeFixedPoint(fixedPoint, result);

        if (dataSize == 0) return 8;

        ints[1] = (long)(data[0] * fixedPoint + 0.5);
        for (i = 0; i < 4; i++)
        {
            result[8 + i] = (byte)((ints[1] >> (i * 8)) & 0xff);
        }

        if (dataSize == 1) return 12;

        ints[2] = (long)(data[1] * fixedPoint + 0.5);
        for (i = 0; i < 4; i++)
        {
            result[12 + i] = (byte)((ints[2] >> (i * 8)) & 0xff);
        }

        halfByteCount = 0;
        ri = 16;

        for (i = 2; i < dataSize; i++)
        {
            ints[0] = ints[1];
            ints[1] = ints[2];
            ints[2] = (long)(data[i] * fixedPoint + 0.5);
            extrapol = ints[1] + (ints[1] - ints[0]);
            diff = ints[2] - extrapol;
            halfByteCount += encodeInt(diff, halfBytes, halfByteCount);

            for (hbi = 1; hbi < halfByteCount; hbi += 2)
                result[ri++] = (byte)((halfBytes[hbi - 1] << 4) | (halfBytes[hbi] & 0xf));

            if (halfByteCount % 2 != 0)
            {
                halfBytes[0] = halfBytes[halfByteCount - 1];
                halfByteCount = 1;
            }
            else
                halfByteCount = 0;
        }

        if (halfByteCount == 1)
            result[ri++] = (byte)(halfBytes[0] << 4);

        return ri;
    }

    /// <summary>
    /// Decodes data using MS Numpress linear prediction compression.
    /// </summary>
    /// <param name="data">array of bytes to be decoded</param>
    /// <param name="dataSize">number of bytes from data to decode</param>
    /// <param name="result">array were resulting doubles should be stored</param>
    /// <returns>the number of decoded doubles, or -1 if dataSize &lt; 4 or 4 &lt; dataSize &lt; 8</returns>
    /// <remarks>
    /// Result vector guaranteed to be shorter or equal to (|data| - 8) * 2
    ///
    /// Note that this method may throw a ArrayIndexOutOfBoundsException if it deems the input data to 
    /// be corrupt, i.e. that the last encoded int does not use the last byte in the data. In addition 
    /// the last encoded int need to use either the last halfbyte, or the second last followed by a 
    /// 0x0 halfbyte. 
    /// </remarks>
    public static int decodeLinear(byte[] data, int dataSize, double[] result)
    {
        int ri = 2;
        long[] ints = new long[3];
        long extrapol = 0;
        long y;
        var dec = new IntDecoder(data, 16);

        if (dataSize == 8) return 0;
        if (dataSize < 8) return -1;
        double fixedPoint = decodeFixedPoint(data);
        if (dataSize < 12) return -1;

        ints[1] = 0;
        for (int i = 0; i < 4; i++)
        {
            ints[1] = ints[1] | ((0xFFL & data[8 + i]) << (i * 8));
        }
        result[0] = ints[1] / fixedPoint;

        if (dataSize == 12) return 1;
        if (dataSize < 16) return -1;

        ints[2] = 0;
        for (int i = 0; i < 4; i++)
        {
            ints[2] = ints[2] | ((0xFFL & data[12 + i]) << (i * 8));
        }
        result[1] = ints[2] / fixedPoint;

        while (dec.pos < dataSize)
        {
            if (dec.pos == (dataSize - 1) && dec.half)
                if ((data[dec.pos] & 0xf) != 0x8)
                    break;

            ints[0] = ints[1];
            ints[1] = ints[2];
            ints[2] = dec.next();

            extrapol = ints[1] + (ints[1] - ints[0]);
            y = extrapol + ints[2];
            result[ri++] = y / fixedPoint;
            ints[2] = y;
        }

        return ri;
    }

    /////////////////////////////////////////////////////////////////////////////////

    /// <summary>
    /// Encodes ion counts by simply rounding to the nearest 4 byte integer, and compressing each integer with encodeInt.
    /// </summary>
    /// <param name="data">array of doubles to be encoded</param>
    /// <param name="dataSize">number of doubles from data to encode</param>
    /// <param name="result">array were resulting bytes should be stored</param>
    /// <returns>the number of encoded bytes</returns>
    /// <remarks>
    /// The handleable range is therefore 0 -> 4294967294.
    /// The resulting binary is maximally dataSize * 5 bytes, but much less if the 
    /// data is close to 0 on average.
    /// </remarks>
    public static int encodePic(double[] data, int dataSize, byte[] result)
    {
        long count;
        int ri = 0;
        int hbi = 0;
        byte[] halfBytes = new byte[10];
        int halfByteCount = 0;

        for (int i = 0; i < dataSize; i++)
        {
            count = (long)(data[i] + 0.5);
            halfByteCount += encodeInt(count, halfBytes, halfByteCount);

            for (hbi = 1; hbi < halfByteCount; hbi += 2)
                result[ri++] = (byte)((halfBytes[hbi - 1] << 4) | (halfBytes[hbi] & 0xf));

            if (halfByteCount % 2 != 0)
            {
                halfBytes[0] = halfBytes[halfByteCount - 1];
                halfByteCount = 1;
            }
            else
                halfByteCount = 0;

        }
        if (halfByteCount == 1)
            result[ri++] = (byte)(halfBytes[0] << 4);

        return ri;
    }

    /// <summary>
    /// Decodes data encoded by encodePic.
    /// </summary>
    /// <param name="data">array of bytes to be decoded (need memorycont. repr.)</param>
    /// <param name="dataSize">number of bytes from data to decode</param>
    /// <param name="result">array were resulting doubles should be stored</param>
    /// <returns>the number of decoded doubles</returns>
    /// <remarks>
    /// Result vector guaranteed to be shorter of equal to |data| * 2
    ///
    /// Note that this method may throw a ArrayIndexOutOfBoundsException if it deems the input data to 
    /// be corrupt, i.e. that the last encoded int does not use the last byte in the data. In addition 
    /// the last encoded int need to use either the last halfbyte, or the second last followed by a 
    /// 0x0 halfbyte. 
    /// </remarks>
    public static int decodePic(byte[] data, int dataSize, double[] result)
    {
        int ri = 0;
        long count;
        IntDecoder dec = new IntDecoder(data, 0);

        while (dec.pos < dataSize)
        {
            if (dec.pos == (dataSize - 1) && dec.half)
                if ((data[dec.pos] & 0xf) != 0x8)
                    break;

            count = dec.next();
            result[ri++] = count;
        }
        return ri;
    }

    /////////////////////////////////////////////////////////////////////////////////

    public static double optimalSlofFixedPoint(double[] data, int dataSize)
    {
        if (dataSize == 0) return 0;

        double maxDouble = 1;
        double x;
        double fp;

        for (int i = 0; i < dataSize; i++)
        {
            x = Math.Log(data[i] + 1);
            maxDouble = Math.Max(maxDouble, x);
        }

        fp = Math.Floor(0xFFFF / maxDouble);

        return fp;
    }

    /// <summary>
    /// Encodes ion counts by taking the natural logarithm, and storing a fixed point representation of this.
    /// </summary>
    /// <param name="data">array of doubles to be encoded</param>
    /// <param name="dataSize">number of doubles from data to encode</param>
    /// <param name="result">array were resulting bytes should be stored</param>
    /// <param name="fixedPoint">the scaling factor used for getting the fixed point repr. This is stored in the binary and automatically extracted on decoding.</param>
    /// <returns>the number of encoded bytes</returns>
    /// <remarks>
    /// Encodes ion counts by taking the natural logarithm, and storing a
    /// fixed point representation of this. This is calculated as
    /// 
    /// unsigned short fp = log(d+1) * fixedPoint + 0.5
    ///
    /// the result vector is exactly |data| * 2 + 8 bytes long
    /// </remarks>
    public static int encodeSlof(double[] data, int dataSize, byte[] result, double fixedPoint)
    {
        int x;
        int ri = 8;

        encodeFixedPoint(fixedPoint, result);

        for (int i = 0; i < dataSize; i++)
        {
            x = (int)(Math.Log(data[i] + 1) * fixedPoint + 0.5);

            result[ri++] = (byte)(0xff & x);
            result[ri++] = (byte)(x >> 8);
        }
        return ri;
    }

    /// <summary>
    /// Decodes data encoded by encodeSlof.
    /// </summary>
    /// <param name="data">array of bytes to be decoded (need memorycont. repr.)</param>
    /// <param name="dataSize">number of bytes from data to decode</param>
    /// <param name="result">array were resulting doubles should be stored</param>
    /// <returns>the number of decoded doubles</returns>
    /// <remarks>
    /// The result vector will be exactly (|data| - 8) / 2 doubles.
    /// returns the number of doubles read, or -1 is there is a problem decoding.
    /// </remarks>
    public static int decodeSlof(byte[] data, int dataSize, double[] result)
    {
        int x;
        int ri = 0;

        if (dataSize < 8) return -1;
        double fixedPoint = decodeFixedPoint(data);

        if (dataSize % 2 != 0) return -1;

        for (int i = 8; i < dataSize; i += 2)
        {
            x = (0xff & data[i]) | ((0xff & data[i + 1]) << 8);
            result[ri++] = Math.Exp((0xffff & x) / fixedPoint) - 1;
        }
        return ri;
    }

    /// <summary>
    /// Decodes ints from the half bytes in bytes. Lossless reverse of encodeInt, although not symmetrical in input arguments.
    /// </summary>
    public class IntDecoder
    {
        public int pos = 0;
        public bool half = false;
        public byte[] bytes;

        public IntDecoder(byte[] _bytes, int _pos)
        {
            bytes = _bytes;
            pos = _pos;
        }

        public long next()
        {
            int head;
            int i, n;
            long res = 0;
            long mask, m;
            int hb;

            if (!half)
                head = (0xff & bytes[pos]) >> 4;
            else
                head = 0xf & bytes[pos++];

            half = !half;

            if (head <= 8)
                n = head;
            else
            {
                // leading ones, fill in res
                n = head - 8;
                mask = unchecked((int)0xF0000000);

                for (i = 0; i < n; i++)
                {
                    m = mask >> (4 * i);
                    res = res | m;
                }
            }

            if (n == 8) return 0;

            for (i = n; i < 8; i++)
            {
                if (!half)
                    hb = (0xff & bytes[pos]) >> 4;
                else
                    hb = 0xf & bytes[pos++];

                res = (int)res | (hb << ((i - n) * 4));
                half = !half;
            }

            return res;
        }
    }
}