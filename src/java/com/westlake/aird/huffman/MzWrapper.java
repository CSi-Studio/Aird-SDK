package com.westlake.aird.huffman;

import java.util.HashMap;

// 统计核质比的频数，并生成用于huffman编码的节点数组
public class MzWrapper {

    HashMap<Float, Node> mzMap = new HashMap<>();

    public MzWrapper(float[] mzs) {

        for (float mz : mzs) {
            if (mzMap.containsKey(mz)) {
                mzMap.get(mz).countMz();
            } else {
                mzMap.put(mz, new Node(mz, 1));
            }
        }
    }

    public Node[] getNodeArray() {
        Node[] nodes = new Node[mzMap.size()];
        return mzMap.values().toArray(nodes);
    }

}
