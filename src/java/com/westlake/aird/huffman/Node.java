package com.westlake.aird.huffman;

/**
 * 构建huffman树的节点
 */
public class Node implements Comparable<Node>{
    private float mz; //核质比
    private int freq; //频数
    private Node parent; //父节点
    private Node leftChild; //左子节点
    private Node rightChild; //右子节点
    private String code; //编码

    public Node(float mz, int freq, Node parent, Node leftChild, Node rightChild) {
        this.mz = mz;
        this.freq = freq;
        this.parent = parent;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.code = "";
    }
    public Node(float mz, int freq){
        this(mz,freq, null, null, null);
    }

    public Node(float mz){
        this(mz,0, null, null, null);
    }

    public Node(){
        this(-1,0, null, null, null);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public void countMz(){
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

    public float getMz() {
        return mz;
    }

    public void setMz(int mz) {
        this.mz = mz;
    }

    // 是否为表示核质比的节点
    public boolean isOrigin(){
        return mz != -1;
    }

    public Node addCode(String c){
        setCode(getCode()+c);
        return this;
    }
    @Override
    public String toString() {
        return "Node{" +
                "mz=" + mz +
                ", freq=" + freq +
                '}';
    }

    @Override
    public int compareTo(Node node) {
        return this.freq - node.freq;
    }

}
