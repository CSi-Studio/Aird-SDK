package com.westlake.aird.huffman;

import java.util.*;

public class HuffmanCode{

    private MinHeap<Node> minNodesHeap;
    private Node huffmanTreeRoot;

    // 把所有节点放入一个最小堆，便于找到频数最小的节点
    public HuffmanCode(Node[] nodes) {
        minNodesHeap = new MinHeap<>(nodes.length);
        for (Node node : nodes) {
            minNodesHeap.insert(node);
        }
    }

    // 生成huffman树
    public void createHuffmanTree() {

        while(minNodesHeap.size() > 1){
            // 取出最小的频数两个节点
            Node rightChild = minNodesHeap.extractMin();
            Node leftChild = minNodesHeap.extractMin();
            Node tempNode = new Node(-1,rightChild.getFreq() + leftChild.getFreq());
            rightChild.setParent(tempNode);
            leftChild.setParent(tempNode);
            tempNode.setLeftChild(leftChild);
            tempNode.setRightChild(rightChild);
            minNodesHeap.insert(tempNode);
        }

        huffmanTreeRoot = minNodesHeap.extractMin();
    }

    public void showHuffmanTree(){
        Queue<Node> nodeQueue = new LinkedList<>() ;
        nodeQueue.offer(huffmanTreeRoot);
        while (!nodeQueue.isEmpty()){
            Node node = nodeQueue.poll();
            if(node.isOrigin()) {
                System.out.println(node);
            } else {
                System.out.println("parent: "+ node.getFreq());
            }
            if(null != node.getLeftChild()) {
                nodeQueue.offer(node.getLeftChild());
            }
            if(null != node.getRightChild()) {
                nodeQueue.offer(node.getRightChild());
            }
        }
    }

    // 通过huffman树生成编码并用一个Hashmap返回
    public HashMap<Float,String> outHuffmanCodeMap(){
        HashMap<Float,String> huffmanCodeMap = new HashMap<>();
        Queue<Node> nodeQueue = new LinkedList<>() ;
        nodeQueue.offer(huffmanTreeRoot);
        while (!nodeQueue.isEmpty()){
            Node node = nodeQueue.poll();
            if(node.isOrigin()) {
                huffmanCodeMap.put(node.getMz(), node.getCode());
            }
            if(null != node.getLeftChild()){
                nodeQueue.offer(node.getLeftChild().addCode("0").addCode(node.getCode()));
            }
            if(null != node.getRightChild()) {
                nodeQueue.offer(node.getRightChild().addCode("1").addCode(node.getCode()));
            }
        }
        return huffmanCodeMap;
    }
}