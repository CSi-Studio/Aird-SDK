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

public class MinHeap<Item extends Comparable>  {

    private Item[] data;
    private int count;
    private int capacity;

    // 构造函数, 构造一个空堆, 可容纳capacity个元素
    public MinHeap(int capacity){
        data = (Item[])new Comparable[capacity+1];
        count = 0;
        this.capacity = capacity;
    }

    // 返回堆中的元素个数
    public int size(){
        return count;
    }

    // 返回一个布尔值, 表示堆中是否为空
    public boolean isEmpty(){
        return count == 0;
    }


    // 像最大堆中插入一个新的元素 item
    public void insert(Item item){

        assert count + 1 <= capacity;
        data[count+1] = item;
        count ++;
        shiftUp(count);
    }

    // 交换堆中索引为i和j的两个元素
    private void swap(int i, int j){
        Item t = data[i];
        data[i] = data[j];
        data[j] = t;
    }

    private void show(){
        for (int i = 1; i <= count; i++) {
            System.out.println(data[i]);
        }
    }

    //********************
    //* 最大堆核心辅助函数
    //********************
    private void shiftUp(int k){

        while( k > 1 && data[k].compareTo(data[k/2])< 0 ){
            swap(k, k/2);
            k /= 2;
        }
    }
    private void shiftDown(int k){

        while( 2*k <= count ){
            int j = 2*k; // 在此轮循环中,data[k]和data[j]交换位置
            if( j+1 <= count && data[j+1].compareTo(data[j]) < 0 ) {
                j ++;
            }
            // data[j] 是 data[2*k]和data[2*k+1]中的最小值

            if( data[k].compareTo(data[j]) <= 0 ) {
                break;
            }
            swap(k, j);
            k = j;
        }
    }

    // 从最小堆中取出堆顶元素, 即堆中所存储的最小数据
    public Item extractMin(){
        assert count > 0;
        Item ret = data[1];

        swap( 1 , count );
        count --;
        shiftDown(1);

        return ret;
    }

    // 获取最小堆中的堆顶元素
    public Item getMax(){
        assert( count > 0 );
        return data[1];
    }

}