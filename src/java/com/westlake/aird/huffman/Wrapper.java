package com.westlake.aird.huffman;

import java.util.*;

// 统计频数，并生成用于huffman编码的节点数组
public class Wrapper {

    HashMap<Float, Integer> freqMap;
    private final static Object MUTEX = new Object();

    public Wrapper() {
        this.freqMap = new HashMap<>();
    }

    public void offer(float[] floats) {

        for (float f : floats) {
            synchronized (MUTEX) {
                if (freqMap.containsKey(f)) {
                    freqMap.put(f, freqMap.get(f) + 1);
                } else {
                    freqMap.put(f, 1);
                }
            }
        }
    }


    public Node[] getNodeArray() {
        Node[] nodes = new Node[freqMap.size()];
        int i = 0;
        for (Map.Entry<Float, Integer> entry : freqMap.entrySet()) {
            nodes[i++] = new Node(entry.getKey(), entry.getValue());
        }
        return nodes;
    }

    @Override
    public String toString() {

        List<Map.Entry<Float, Integer>> list = new ArrayList<Map.Entry<Float, Integer>>(freqMap.entrySet());
        list.sort(new Comparator<Map.Entry<Float, Integer>>() {
            @Override
            public int compare(Map.Entry<Float, Integer> o1, Map.Entry<Float, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        StringBuilder str = new StringBuilder();
        for (Map.Entry<Float, Integer> entry : list) {
            str.append(String.format("%f : %d times\n", entry.getKey(), entry.getValue()));
        }
        str.append(String.format("%d items in total", freqMap.size()));
        return str.toString();
    }
}
