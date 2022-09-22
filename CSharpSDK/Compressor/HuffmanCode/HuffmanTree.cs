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
using System.Linq;

namespace AirdSDK.Compressor
{
    public class HuffmanTree
    {
        private HuffmanTree lChild;
        private HuffmanTree rChild;
        private int num;
        private int freq;
        static Dictionary<int, int> hTreeDic = new Dictionary<int, int>();

        public HuffmanTree(int num)
        {
            this.num = num;
            this.freq = 1;
        }

        public HuffmanTree LChild
        {
            get { return lChild; }
            set { lChild = value; }
        }

        public HuffmanTree RChild
        {
            get { return rChild; }
            set { rChild = value; }
        }

        public int Num
        {
            get { return num; }
            set { num = value; }
        }

        public int Freq
        {
            get { return freq; }
            set { freq = value; }
        }

        public void incFreq()
        {
            freq++;
        }

        //将霍夫曼树转换成map进行本地存储
        public static Dictionary<int, int> toDictionary(HuffmanTree hTreeByte)
        {
            if (hTreeByte.LChild == null)
            {
                hTreeDic.Add(hTreeByte.Num, hTreeByte.Freq);
            }
            else
            {
                toDictionary(hTreeByte.LChild);
                toDictionary(hTreeByte.RChild);
            }

            return hTreeDic;
        }

        //将本地存储的map转换成霍夫曼树进行编码和解码
        public static HuffmanTree fromDictionary(Dictionary<int, int> dicTree)
        {
            HuffmanTree codeTree = new HuffmanTree(0);
            HuffmanTree[] huffmanTrees = new HuffmanTree[dicTree.Count];
            for (int i = 0; i < dicTree.Count; i++)
            {
                HuffmanTree tmpTree = new HuffmanTree(dicTree.ElementAt(i).Key);
                tmpTree.freq = dicTree.ElementAt(i).Value;
                huffmanTrees[i] = tmpTree;
            }

            Node first = new Node(huffmanTrees[0]);
            for (int k = 1; k < huffmanTrees.Length; k++)
            {
                Node tmpNode = new Node(huffmanTrees[k]);
                tmpNode.link = first;
                first = tmpNode;
            }

            TreeList treeList = new TreeList(first, dicTree.Count);
            treeList.sortTree();
            while (treeList.length() > 1)
            {
                codeTree = treeList.mergeTree();
            }

            return codeTree;
        }
    }
}