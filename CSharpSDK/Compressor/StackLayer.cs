/*
 * Copyright (c) 2020 CSi Studio
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2. 
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2 
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.  
 * See the Mulan PSL v2 for more details.
 */

using System;
using System.Collections;
using System.Collections.Generic;
using AirdSDK.Beans;
using CSharpFastPFOR.Port;

namespace AirdSDK.Compressor
{
    public class StackLayer
    {
        /**
         * compress the data with stack-ZDPD algorithm
         *
         * @param arrGroup mzArray to be compressed
         * @param pair sorting method of mzArray
         * @return compressed mzArray
         */
        public static Layers encode(List<int[]> arrGroup, SortedIntComp intComp, ByteComp byteComp)
        {
            int stackLen = 0; //记录堆叠数总长度
            foreach (int[] arr in arrGroup)
            {
                stackLen += arr.Length;
            }

            //合并排序数组 pair：默认采取pair成对排序，True也采取pair排序；false时采用QueueSort
            int[,] stackSort;
            stackSort = getPairSortArray(arrGroup);
            //if (pair)
            //{
            //    stackSort = getPairSortArray(arrGroup);
            //}
            //else
            //{
            //    stackSort = getQueueSortArray(arrGroup);
            //}
            //取出stack数组和index数组
            int[] stackArr = new int[stackLen];
            int[] stackIndex = new int[stackLen];
            for (int i = 0; i < stackLen; i++)
            {
                stackArr[i] = stackSort[i, 0];
                stackIndex[i] = stackSort[i, 1];
            }

            //index移位存储
            int digit = (int) Math.Ceiling(Math.Log(arrGroup.Count) / Math.Log(2));
            int indexLen = (stackLen * digit - 1) / 8 + 1;
            byte[] value = new byte[8 * indexLen];
            for (int i = 0; i < stackLen; i++)
            {
                int fromIndex = digit * i;
                for (int j = 0; j < digit; j++)
                {
                    value[fromIndex + j] = (byte) ((stackIndex[i] >> j) & 1);
                }
            }

            //把8个byte并为1个byte，用byte数组存是因为zlib压缩的是byte
            byte[] indexShift = new byte[indexLen];
            for (int i = 0; i < indexLen; i++)
            {
                int temp = 0;
                for (int j = 0; j < 8; j++)
                {
                    temp += value[8 * i + j] << j;
                    indexShift[i] = (byte) temp;
                }
            }

            //数组用fastPFor压缩，index用zlib压缩，并记录层数
            Layers layers = new Layers();
            layers.mzArray = byteComp.encode(ByteTrans.intToByte(intComp.encode(stackArr)));
            layers.tagArray = byteComp.encode(indexShift);
            layers.digit = digit;

            return layers;
        }

        /**
         * decompress the data with stack-ZDPD algorithm
         *
         * @param layers compressed mzArray
         * @return decompressed mzArray
         */
        public static List<int[]> decode(Layers layers, SortedIntComp intComp, ByteComp byteComp)
        {
            int[] stackArr = intComp.decode(ByteTrans.byteToInt(byteComp.decode(layers.mzArray)));
            int[] stackIndex = new int[stackArr.Length];
            byte[] tagShift = byteComp.decode(layers.tagArray);
            int digit = layers.digit;
            //拆分byte为8个bit，并分别存储
            byte[] value = new byte[8 * tagShift.Length];
            for (int i = 0; i < tagShift.Length; i++)
            {
                for (int j = 0; j < 8; j++)
                {
                    value[8 * i + j] = (byte) (((tagShift[i] & 0xff) >> j) & 1);
                }
            }

            //还原为int类型的index
            for (int i = 0; i < stackIndex.Length; i++)
            {
                for (int j = 0; j < digit; j++)
                {
                    stackIndex[i] += value[digit * i + j] << j;
                }
            }

            //统计index数组中各个元素出现的次数
            Hashtable table = new Hashtable();

            foreach (int index in stackIndex)
            {
                if (table.ContainsKey(index))
                {
                    table[index] = (int) table[index] + 1;
                }
                else
                {
                    table[index] = 1;
                }
            }

            //根据index拆分stackArr,还原数组
            List<int[]> arrGroup = new List<int[]>();
            int arrNum = table.Keys.Count;
            for (int i = 0; i < arrNum; i++)
            {
                arrGroup.Add(new int[(int) table[i]]);
            }

            int[] p = new int[arrNum];
            for (int i = 0; i < stackIndex.Length; i++)
            {
                arrGroup[stackIndex[i]][p[stackIndex[i]]++] = stackArr[i];
            }

            return arrGroup;
        }

        /**
         * sort mzArray with ArraySort method
         *
         * @param arrGroup mzArray to be sorted
         * @return sorted mzArray-Index pair
         */
        private static int[][] getFullSortArray(List<int[]> arrGroup)
        {
            int stackLen = 0; //记录堆叠数总长度
            foreach (int[] arr in arrGroup)
            {
                stackLen += arr.Length;
            }

            int[][] stackSort = new int[stackLen][]; //二维数组分别存储堆叠数字和层号

            int index = 0;
            int arrLen = 0;
            foreach (int[] arr in arrGroup)
            {
                for (int i = 0; i < arr.Length; i++)
                {
                    stackSort[i + arrLen] = new[] {arr[i], index};
                }

                index++;
                arrLen += arr.Length;
            }

            Array.Sort(stackSort, delegate(int[] t1, int[] t2) { return t1[0].CompareTo(t2[0]); }); //根据堆叠数对二维数组升序排列
            return stackSort;
        }

        /**
         * sort mzArray with pairSort method
         *
         * @param arrGroup mzArray to be sorted
         * @return sorted mzArray-Index pair
         */
        private static int[,] getPairSortArray(List<int[]> arrGroup)
        {
            List<int[]> indexGroup = new List<int[]>();
            indexGroup.Add(new int[arrGroup[0].Length]);
            for (int i = 1; i < arrGroup.Count; i++)
            {
                int[] indexes = new int[arrGroup[i].Length];
                Arrays.fill(indexes, i);
                indexGroup.Add(indexes);
            }

            int mergeTimes = (int) Math.Log(arrGroup.Count, 2);
            for (int i = 1; i <= mergeTimes; i++)
            {
                int stepWidth = (int) Math.Pow(2, i);
                int tempMergeTime = arrGroup.Count / stepWidth;

                for (int j = 0; j < tempMergeTime; j++)
                {
                    int leftIndex = j * stepWidth;
                    int rightIndex = leftIndex + stepWidth / 2;
                    int[] dataArr1 = arrGroup[leftIndex];
                    int[] dataArr2 = arrGroup[rightIndex];
                    int[] indexArr1 = indexGroup[leftIndex];
                    int[] indexArr2 = indexGroup[rightIndex];
                    int[] dataArr = new int[dataArr1.Length + dataArr2.Length];
                    int[] indexArr = new int[dataArr.Length];
                    int index1 = 0, index2 = 0, index = 0;
                    for (int k = 0; k < dataArr.Length; k++)
                    {
                        if (index1 >= dataArr1.Length)
                        {
                            dataArr[index] = dataArr2[index2];
                            indexArr[index] = indexArr2[index2];
                            index2++;
                            index++;
                            continue;
                        }

                        if (index2 >= dataArr2.Length)
                        {
                            dataArr[index] = dataArr1[index1];
                            indexArr[index] = indexArr1[index1];
                            index1++;
                            index++;
                            continue;
                        }

                        if (dataArr1[index1] <= dataArr2[index2])
                        {
                            dataArr[index] = dataArr1[index1];
                            indexArr[index] = indexArr1[index1];
                            index1++;
                        }
                        else
                        {
                            dataArr[index] = dataArr2[index2];
                            indexArr[index] = indexArr2[index2];
                            index2++;
                        }

                        index++;
                    }

                    indexGroup[leftIndex] = indexArr;
                    arrGroup[leftIndex] = dataArr;
                } //single thread
                //            });//multi threads
            }

            int[] arr = arrGroup[0];
            int[] indexArray = indexGroup[0];
            int[,] resultArr = new int[arr.Length, 2];
            for (int i = 0; i < arr.Length; i++)
            {
                resultArr[i, 0] = arr[i];
                resultArr[i, 1] = indexArray[i];
            }

            return resultArr;
        }

        //
        /**
         * sort mzArray with queueSort method
         *
         * @param arrGroup mzArray to be sorted
         * @return sorted mzArray-Index pair
         */
        //private static int[][] getQueueSortArray(List<int[]> arrGroup)
        //{
        //    int stackLen = 0;//记录堆叠数总长度
        //    foreach (int[] arr in arrGroup)
        //    {
        //        stackLen += arr.Length;
        //    }
        //    int[][] resultArr = new int[stackLen][];
        //    int[] indexes = new int[arrGroup.Count];
        //    Arrays.fill(indexes, 1);
        //    SimplePriorityQueue<int[]> queue = new SimplePriorityQueue<int[]>();
        //    for (int i = 0; i<arrGroup.Count; i++) 
        //    {
        //        queue.Enqueue(new int[] { arrGroup[i][0], i }, arrGroup[i][0]);
        //    }
        //    for (int i = 0; i < stackLen; i++)
        //    {
        //        int[] node = queue.Dequeue();
        //        resultArr[i] = node;
        //        int groupIndex = node[1];
        //        int index = indexes[groupIndex];
        //        if (index < arrGroup[groupIndex].Length)
        //        {
        //            queue.Enqueue(new int[] { arrGroup[groupIndex][index], groupIndex }, arrGroup[groupIndex][index]);
        //            indexes[groupIndex]++;
        //        }
        //    }
        //    return resultArr;
        //} 
    }
}