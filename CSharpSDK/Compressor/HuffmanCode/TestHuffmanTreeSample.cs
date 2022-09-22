/*
 * Copyright (c) 2020 CSi Studio
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2. 
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2 
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.  
 * See the Mulan PSL v2 for more details.
 */

namespace AirdSDK.Compressor.HuffmanCode
{
    public class TestHuffmanTreeSample
    {
        //static void Main(string[] args)
        //{
        //    Random r1 = new Random();
        //    Random r2 = new Random();
        //    List<int[]> list = new List<int[]>();
        //    int[] array = new int[2000];
        //    int[] array2 = new int[1500];
        //    for (int i = 0; i < array.Length; i++)
        //    {
        //        array[i] = r1.Next(0, 909);
        //    }
        //    list.Add(array);
        //    for (int j = 0; j < array2.Length; j++)
        //    {
        //        array2[j] = r2.Next(0, 909);
        //    }
        //    list.Add(array2); //以上随机生成一个int数组的集合

        //    int[] mobiArray = HuffmanCoder.toIntArray(list); //将传入的int数组集合转换成int数组
        //    HuffmanTree tree = HuffmanCoder.buildTree(mobiArray); //将int数组传入构建霍夫曼树（原始）
        //    Dictionary<int, int> dic = HuffmanTree.toDictionary(tree); //将霍夫曼树（原始）转换成map用于本地存储
        //    HuffmanTree huffmanTree = HuffmanTree.fromDictionary(dic); //从本地map转换成霍夫曼树（重构）
        //    byte[] mobiByte = HuffmanCoder.encode(mobiArray, tree); //使用霍夫曼树（原始/重构）进行编码
        //    int[] decodeArray = HuffmanCoder.decode(mobiByte, huffmanTree); //使用霍夫曼树（原始/重构）进行解码
        //    string[] signTable = TreeList.getSignTable(); //获取所有数字的集合
        //    string[] keyTable = TreeList.getKeyTable(); //获取所有编码的集合
        //    for (int k = 0; k < decodeArray.Length; k++)
        //    {
        //        Console.WriteLine(decodeArray[k]);
        //    }

        //}
    }
}