using System.Collections.Generic;
using System;
using AirdSDK.Beans.Common;
using AirdSDK.Beans;
using AirdSDK.Bean.Msi;

namespace AirdSDK.Parser;

public class MSIMaldiParser : DDAParser
{
    public List<DDAMs> msList;
    private List<ImageData> imageDataList;

    public MSIMaldiParser(string indexFilePath) : base(indexFilePath)
    {
        msList = ReadAllToMemory();
    }    

    public new List<DDAMs> ReadAllToMemory()
    {
        BlockIndex ms1Index = GetMs1Index(); //所有的ms1谱图都在第一个index中
        for (int i = 0; i < ms1Index.nums.Count; i++)
        {
            ms1Index.rts[i] = i + 1;
        }
        Dictionary<double, Spectrum> ms1Map = GetSpectra(ms1Index);
        List<double> ms1RtList = new(ms1Map.Keys);
        List<DDAMs> ms1List = BuildDdaMsList(ms1RtList, 0, ms1RtList.Count, ms1Index, ms1Map, false);
        return ms1List;
    }

    public List<ImageData> GetImageDataList(double mz, double tolerance)
    {
        imageDataList = [];
        int[] x = airdInfo.msiInfo.spectraPosition.x;
        int[] y = airdInfo.msiInfo.spectraPosition.y;
        
        for (int index = 0; index < x.Length; index++)
        {            
            double[] mzArray = msList[index].spectrum.mzs;
            double[] intArray = msList[index].spectrum.ints;
            double intensity = 0;
            for (int i = 0; i < mzArray.Length; i++)
            {
                if (Math.Abs(mzArray[i] - mz) <= tolerance)
                {
                    intensity += intArray[i];
                }                
            }
            imageDataList.Add(new ImageData()
            {
                X = x[index],
                Y = y[index],
                Intensity = intensity
            });

        }
        return imageDataList;
    }

}