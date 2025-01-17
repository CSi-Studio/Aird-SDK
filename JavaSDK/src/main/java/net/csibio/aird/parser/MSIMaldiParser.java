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

import net.csibio.aird.bean.BlockIndex;
import net.csibio.aird.bean.common.Spectrum;
import net.csibio.aird.bean.msi.ImageData;
import net.csibio.aird.bean.msi.SpectraPosition;

import java.util.ArrayList;
import java.util.List;

public class MSIMaldiParser extends BaseParser{
    public List<Spectrum> msList;
    private List<ImageData> imageDataList;



    public MSIMaldiParser(String indexFilePath) throws Exception {
        super(indexFilePath);
    }

    /**
     * DDA只有一个MS1 BlockIndex,因此是归属于DDAParser的特殊算法
     *
     * @return the index of all the ms1
     */
    public BlockIndex getMs1Index() {
        if (airdInfo != null && airdInfo.getIndexList() != null && airdInfo.getIndexList().size() > 0) {
            return airdInfo.getIndexList().get(0);
        }
        return null;
    }

    public List<Spectrum> readAllToMemory(){
        BlockIndex ms1Index = getMs1Index(); //所有的ms1谱图都在第一个index中
        long start = System.currentTimeMillis();
        List<Spectrum> msList = getSpectraList(ms1Index);
        System.out.println("Read ms1 spectra time: " + (System.currentTimeMillis() - start)/1000 + " s");
        return msList;
    }

    public List<ImageData> getImageDataList(double mz, double tolerance){
        imageDataList = new ArrayList<>();
        int[] x = airdInfo.getMsiInfo().getSpectraPosition().getX();
        int[] y = airdInfo.getMsiInfo().getSpectraPosition().getY();
        
        for (int index = 0; index < x.length; index++)
        {            
            double[] mzArray = msList.get(index).getMzs();
            double[] intArray = msList.get(index).getInts();
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

    public SpectraPosition getSpectraPosition()
    {
        var x = airdInfo.getMsiInfo().getSpectraPosition().getX();
        var y = airdInfo.getMsiInfo().getSpectraPosition().getY();
        var z = airdInfo.getMsiInfo().getSpectraPosition().getZ();
        SpectraPosition sp = new SpectraPosition(x,y,z);
        return  sp;
    }

}