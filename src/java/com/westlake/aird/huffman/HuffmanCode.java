/*
 * Copyright (c) 2020 Propro Studio
 * Aird and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.westlake.aird.huffman;

import java.util.*;

public class HuffmanCode{

    private MinHeap<Node> minNodesHeap;
    private Node huffmanTreeRoot;
//    private HashMap<Float, boolean[]> huffmanEncodeMap;
    private boolean treeIsBuilt;
    private boolean encodeMapIsBuilt;
    private HashMap<Float, Integer> huffmanEncodeMap;
    private HashMap<Integer, Float> huffmanDecodeMap;

    // 把所有节点放入一个最小堆，便于找到频数最小的节点
    public HuffmanCode(Node[] nodes) {
        minNodesHeap = new MinHeap<>(nodes.length);
        for (Node node : nodes) {
            minNodesHeap.insert(node);
        }
        treeIsBuilt = false;
        encodeMapIsBuilt = false;
    }

    // 生成huffman树
    public void createHuffmanTree() {

        if(!treeIsBuilt) {

            while (minNodesHeap.size() > 1) {
                // 取出最小的频数两个节点
                Node rightChild = minNodesHeap.extractMin();
                Node leftChild = minNodesHeap.extractMin();
                Node tempNode = new Node(-1, rightChild.getFreq() + leftChild.getFreq());
                rightChild.setParent(tempNode);
                leftChild.setParent(tempNode);
                tempNode.setLeftChild(leftChild);
                tempNode.setRightChild(rightChild);
                minNodesHeap.insert(tempNode);
            }

            huffmanTreeRoot = minNodesHeap.extractMin();
            treeIsBuilt = true;
        }
    }




    public void createHuffmanCodeMap() {

        if(!encodeMapIsBuilt) {
            huffmanEncodeMap = new HashMap<>();
            huffmanDecodeMap = new HashMap<>();

            Queue<Node> nodes = new LinkedList<>();
            nodes.offer(huffmanTreeRoot);
            while (!nodes.isEmpty()) {
                Node currentNode = nodes.poll();
                if (currentNode.isOrigin()) {
                    huffmanEncodeMap.put(currentNode.getData(), currentNode.getCode());
                    huffmanDecodeMap.put(currentNode.getCode(), currentNode.getData());
                } else {
                    if (currentNode.getLeftChild() != null) {
                        nodes.offer(currentNode.getLeftChild()
                                .setLayer(currentNode.getLayer() + 1)
                                .setCode(currentNode.getCode()));
                    }
                    if (currentNode.getRightChild() != null) {
                        int rightCode = currentNode.getCode() + (1 << currentNode.getLayer());
                        nodes.offer(currentNode.getRightChild()
                                .setLayer(currentNode.getLayer() + 1)
                                .setCode(rightCode));
                    }
                }
            }
            encodeMapIsBuilt = true;
        }

    }

    public byte[] huffmanCompress(float[] target){

        if(!encodeMapIsBuilt) {
            createHuffmanCodeMap();
            System.out.println("huffman code map built");
        }

        ArrayList<Byte> dbb = new ArrayList<>();
        for (float f :
                target) {
            long code = huffmanEncodeMap.get(f);

            do {
                byte b = (byte)(code & 0b01111111);
                code = code >>> 7 ;
                if(code == 0) b = (byte)(b | 0b10000000);
                dbb.add(b);
            }while(code != 0);

        }

        byte[] ret = new byte[dbb.size()];
        for (int i = 0; i < dbb.size(); i++) {
            ret[i] = dbb.get(i);
        }
        return  ret;
    }

    public float[] huffmanDecompress(byte[] target){
        ArrayList<Float> floats = new ArrayList<>();

        int buffer = 0;
        int byteNum = 0;
        for (byte b :
                target) {
            if((b >>> 7) == 0) {
                buffer = buffer + (b << (7 * byteNum++));
            }
            else{
                buffer = buffer + ((b & 0b01111111)<<(7 * byteNum));
                floats.add(huffmanDecodeMap.get(buffer));
                buffer = 0;
                byteNum = 0;
            }
        }

        float[] ret = new float[floats.size()];
        System.out.println(floats.size());

        for (int i = 0; i < floats.size(); i++) {
            ret[i] = floats.get(i);
        }

        return ret;

    }


/*
    public void createHuffmanCodeMap(){
        if(!encodeMapIsBuilt) {
            huffmanEncodeMap = new HashMap<>();
        }
        goNode(huffmanTreeRoot, new boolean[0]);

        encodeMapIsBuilt = true;
    }

    private void goNode(Node node, boolean[] path){
        if(node.isOrigin()) {
            huffmanEncodeMap.put(node.getData(), path);
        }{
            boolean[] leftPath = new boolean[path.length + 1];
            System.arraycopy(path, 0, leftPath, 0, path.length);
            if(node.getLeftChild() != null) {
                leftPath[leftPath.length - 1] = false;
                goNode(node.getLeftChild(), leftPath);
            }
            boolean[] rightPath = new boolean[path.length + 1];
            System.arraycopy(path, 0, rightPath, 0, path.length);
            if(node.getRightChild() != null) {
                rightPath[rightPath.length - 1] = true;
                goNode(node.getRightChild(), rightPath);
            }
        }
    }

    public byte[] huffmanCompress(float[] target){
        createHuffmanCodeMap();
        ArrayList<Byte> dbb = new ArrayList<>();
        int counter = 0;
        int codeBuffer = 0;
        for (float f :
                target) {
            boolean[] code = huffmanEncodeMap.get(f);
            for (boolean bit :
                    code) {
                codeBuffer = (codeBuffer<<1) + (bit? 1:0);
                counter ++;
                if(counter == 8){
                    dbb.add((byte)codeBuffer);
                    counter = 0;
                }
            }
        }
        dbb.add((byte)codeBuffer);
        dbb.add((byte)counter);

        byte[] ret = new byte[dbb.size()];
        for (int i = 0; i < dbb.size(); i++) {
            ret[i] = dbb.get(i);
        }
        return  ret;
    }

    public float[] huffmanDecompress(byte[] target){
        byte lastRest = target[target.length-1];
        ArrayList<Float> floats = new ArrayList<>();

        Node current = huffmanTreeRoot;
        for (int i = 0; i < target.length-2; i++) {
            for (int j = 0; j < 8; j++) {
                if(current.isOrigin()){
                    floats.add(current.getData());
                    current = huffmanTreeRoot;
                }

                boolean bit = (target[i] >> (7 - j) & 0x00000001) == 1;

                if(bit){
                    current = current.getRightChild();
                }else{
                    current = current.getLeftChild();
                }
            }
        }
        for (int i = 0; i < lastRest; i++) {
            if(current.isOrigin()){
                floats.add(current.getData());
                current = huffmanTreeRoot;
            }

            boolean bit = (target[target.length - 2] >> (lastRest -1 - i) & 0x00000001) == 1;

            if(bit){
                current = current.getRightChild();
            }else{
                current = current.getLeftChild();
            }
        }
        if(current.isOrigin()) floats.add(current.getData());

        float[] ret = new float[floats.size()];
        for (int i = 0; i < floats.size(); i++) {
            ret[i] = floats.get(i);
        }

        return ret;
    }
*/
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("HuffmanCode:\n");
        for (Map.Entry<Float, Integer> entry:
        huffmanEncodeMap.entrySet()){
            sb.append(String.format("%f -> ", entry.getKey()));
            sb.append(Integer.toBinaryString(entry.getValue()));
            sb.append("\n");
        }
        return sb.toString();
    }
}