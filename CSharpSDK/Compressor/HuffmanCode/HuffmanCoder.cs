/*
 * Copyright (c) 2020 CSi Studio
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2. 
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2 
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.  
 * See the Mulan PSL v2 for more details.
 */

using System.Collections.Generic;

namespace AirdSDK.Compressor;

public class HuffmanCoder
{
    static int arrayNumCount;
    static HuffmanTree codeTree = null;

    //该函数传入int数组集合，返回int数组，并在构建霍夫曼树（buildTree）函数中作为传入参数使用
    public static int[] toIntArray(List<int[]> mobiList)
    {
        for (int i = 0; i < mobiList.Count; i++)
        {
            arrayNumCount += mobiList[i].Length;
        }

        List<int> tmpList = new List<int>();
        for (int j = 0; j < mobiList.Count; j++)
        {
            for (int k = 0; k < mobiList[j].Length; k++)
            {
                tmpList.Add(mobiList[j][k]);
            }
        }

        int[] mobiArray = new int[arrayNumCount];
        for (int l = 0; l < arrayNumCount; l++)
        {
            mobiArray[l] = tmpList[l];
        }

        return mobiArray;
    }

    //直接调用此函数，可以建立霍夫曼树，该树用于编码和解码
    public static HuffmanTree buildTree(int[] mobiIntArray)
    {
        TreeList treeList = new TreeList(mobiIntArray);
        for (int k = 0; k < mobiIntArray.Length; k++)
        {
            treeList.addNum(mobiIntArray[k]); //提取int数组中出现的数字，并计算其权重（出现次数），除去重复出现的数字
        }

        treeList.sortTree(); //按权重从小到大排列
        while (treeList.length() > 1)
        {
            codeTree = treeList.mergeTree(); //合并最小权重的两个数，生成霍夫曼树
        }

        return codeTree;
    }

    //调用此函数，用于霍夫曼编码
    public static byte[] encode(int[] target, HuffmanTree tree)
    {
        TreeList.makeKey(tree, ""); //获取每个数对应的编码表
        List<byte> resultByte = TreeList.translate(target); //对应编码表转换成bit操作，用byte类型存储
        byte[] tmpByte = new byte[resultByte.Count];
        for (int i = 0; i < resultByte.Count; i++)
        {
            tmpByte[i] = resultByte[i];
        }

        return tmpByte;
    }

    //调用此函数，进行霍夫曼解码
    public static int[] decode(byte[] target, HuffmanTree tree)
    {
        int[] decodeInt = TreeList.readHuffmanCode(arrayNumCount, target, tree); //利用构建的霍夫曼树进行解码
        return decodeInt;
    }
}