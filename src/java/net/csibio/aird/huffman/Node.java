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

/**
 * 构建huffman树的节点
 */
public class Node implements Comparable<Node>{
    private float data; //数据
    private int freq; //频数
    private Node parent; //父节点
    private Node leftChild; //左子节点
    private Node rightChild; //右子节点
    private int code; //编码
    private int layer;

    public Node(float data, int freq, Node parent, Node leftChild, Node rightChild) {
        this.data = data;
        this.freq = freq;
        this.parent = parent;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.code = 0;
        this.layer = 0;
    }
    public Node(float data, int freq){
        this(data,freq, null, null, null);
    }

    public Node(float data){
        this(data,0, null, null, null);
    }

    public Node(){
        this(-1,0, null, null, null);
    }

    public int getLayer() {
        return layer;
    }

    public Node setLayer(int layer) {
        this.layer = layer;
        return this;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public void count(){
        this.freq++;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(Node leftChild) {
        this.leftChild = leftChild;
    }

    public Node getRightChild() {
        return rightChild;
    }

    public void setRightChild(Node rightChild) {
        this.rightChild = rightChild;
    }

    public float getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    // 是否为表示核质比的节点
    public boolean isOrigin(){
        return data != -1;
    }

    public int getCode() {

        return code;
    }
    public Node setCode(int c){
        code = c;
        return this;
    }


    @Override
    public String toString() {
        return "Node{" +
                "data=" + data +
                ", freq=" + freq +
                '}';
    }

    @Override
    public int compareTo(Node node) {
        return this.freq - node.freq;
    }

}
