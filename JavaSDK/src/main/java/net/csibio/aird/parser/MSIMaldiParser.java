/*
 * Copyright (c) 2020 CSi Biotech
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.parser;

import net.csibio.aird.bean.DDAMs;
import net.csibio.aird.bean.common.Spectrum;
import net.csibio.aird.bean.msi.ImageData;
import net.csibio.aird.bean.BlockIndex;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class MSIMaldiParser extends DDAParser{
    public List<DDAMs> msList;
    private List<ImageData> imageDataList;

    public MSIMaldiParser(String indexFilePath) throws Exception {
        super(indexFilePath);
        msList = ReadAllToMemory();
    }

    public List<DDAMs> ReadAllToMemory(){
        BlockIndex ms1Index = getMs1Index(); //所有的ms1谱图都在第一个index中
        List<Double> rts = new ArrayList<>();
        for (int i = 0; i < ms1Index.getNums().size(); i++){
            double rt = i + 1;
            rts.add(rt);
        }
        ms1Index.setRts(rts);
        TreeMap<Double, Spectrum> ms1Map = getSpectra(ms1Index);
        List<Double> ms1RtList = new ArrayList<>(ms1Map.keySet());
        return buildDDAMsList(ms1RtList, 0, ms1RtList.size(), ms1Index, ms1Map, false);
    }

    public List<ImageData> GetImageDataList(double mz, double tolerance){
        imageDataList = new ArrayList<>();
        int[] x = airdInfo.getMsiInfo().getSpectraPosition().getX();
        int[] y = airdInfo.getMsiInfo().getSpectraPosition().getY();
        
        for (int index = 0; index < x.length; index++)
        {            
            double[] mzArray = msList.get(index).getSpectrum().getMzs();
            double[] intArray = msList.get(index).getSpectrum().getInts();
            double intensity = 0;
            for (int i = 0; i < mzArray.length; i++)
            {
                if (Math.abs(mzArray[i] - mz) <= tolerance)
                {
                    intensity += intArray[i];
                }                
            }
            imageDataList.add(new ImageData(x[index], y[index], intensity));
        }
        return imageDataList;
    }

}