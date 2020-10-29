/*
 * Copyright (c) 2020 CSi Biotech
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package net.csibio.aird.huffman;

public class DecompressTest {

    public void myTest(int dataSize, int dataRange, boolean detailPrint) {
        float[] data = new float[dataSize];
        for (int i = 0; i < dataSize; i++) {
            int randNum = (int) (Math.pow(Math.random(), 1.5) * dataRange);
            data[i] = randNum;
        }

        long rawSize, compressedSize;
        rawSize = data.length * 4;

        long start = System.currentTimeMillis();
        Wrapper wrapper = new Wrapper();
        wrapper.offer(data);
        HuffmanCode huffmanCode = new HuffmanCode(wrapper.getNodeArray());
        huffmanCode.createHuffmanTree();
        byte[] encoded =  huffmanCode.huffmanCompress(data);
        compressedSize = encoded.length;
        long end = System.currentTimeMillis();
        double compressTime = (end - start)/1000.0;

        start = System.currentTimeMillis();
        float[] decoded = huffmanCode.huffmanDecompress(encoded);
        end = System.currentTimeMillis();
        double decompressTime = (end - start)/1000.0;

        if(detailPrint) {
            for (float f :
                    data) {
                System.out.print(f + " ");
            }
            System.out.println();
            for (float f :
                    decoded) {
                System.out.print(f + " ");
            }
            System.out.println();
            System.out.println(wrapper);
            System.out.println(huffmanCode);
        }
        System.out.println(String.format("huffman compress for %d data(%d bytes) of %d range use %f s to %d bytes, decompress use %f s " ,
                dataSize,
                rawSize,
                dataRange,
                compressTime,
                compressedSize,
                decompressTime));
    }

    public static void main(String[] args) {
        int dataSize = 100;
        int dataRange = 5;
        new DecompressTest().myTest(dataSize, dataRange, true);

        /*
        byte[] target = new byte[2];
        int index = 0;
        int code = 0b10101010101010;
        do {
            byte b = (byte)(code & 0b01111111);
            code = code >>> 7 ;
            if(code == 0)  b = (byte)(b | 0b10000000);
//            System.out.println(Integer.toBinaryString(b>0?b:-b));
            target[index++] = b;
        }while(code != 0);

        int buffer = 0;
        int byteNum = 0;
        for (byte b :
                target) {
            if((b >>> 7) == 0) {
                buffer = buffer + (b << (7 * byteNum++));
            }
            else{
                buffer = buffer + ((b & 0b01111111)<<(7 * byteNum));
                System.out.println(Integer.toBinaryString(buffer));

                buffer = 0;
                byteNum = 0;
            }


        }
        */

    }
}
