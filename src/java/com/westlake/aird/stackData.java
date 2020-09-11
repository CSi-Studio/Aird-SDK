package com.westlake.aird;

import com.westlake.aird.util.CompressUtil;
import lombok.Data;

import java.util.*;

//不对数组的index进行移位缩短操作，直接使用zlib压缩
public class stackData {
    public static void main(String[] args) {
        for(int k=1;k<12;k++) {
            int arrNum = (int) Math.pow(2, k);
            //生成有序数组
            List<int[]> arrGroup = new LinkedList<>();
            for (int i = 0; i < arrNum; i++) {
                int[] arr = new int[300 + (int) (Math.random() * 100)];
                for (int j = 0; j < arr.length; j++) {
                    arr[j] = (j == 0 ? 0 : arr[j - 1]) + (int) (Math.random() * 10);
                }
                arrGroup.add(arr);
            }
            Stack stack = stackEncode(arrGroup);
            List<int[]> stackDecode = stackDecode(stack);
            Boolean a = Boolean.TRUE;
            for (int i = 0; i < arrGroup.size(); i++) {
                a = a & Arrays.equals(arrGroup.get(i), stackDecode.get(i));
            }
            System.out.println("对照压缩前后数组是否相同" + a);
        }
    }

    public static Stack stackEncode(List<int[]> arrGroup){
        int stackLen =0;
        for(int[] arr:arrGroup){
            stackLen+=arr.length;
        }
        int[][] stackSort = new int[stackLen][2];
        int index=0;
        int arrLen=0;
        for(int[] arr:arrGroup){
            for (int i = 0; i < arr.length; i++) {
                stackSort[i+arrLen][0] = arr[i];
                stackSort[i+arrLen][1] = index;
            }
            index++;
            arrLen+=arr.length;
        }
        Arrays.sort(stackSort,(a,b) -> a[0]-b[0]);
        int[] stackArr =new int[stackSort.length];
        int[] stackIndex =new int[stackSort.length];
        for(int i=0;i<stackSort.length;i++){
            stackArr[i]=stackSort[i][0];
            stackIndex[i]=stackSort[i][1];
        }
        byte[] comArr= CompressUtil.transToByte(CompressUtil.fastPforEncoder(stackArr));

//        byte[] byteIndex=new byte[stackLen*4];
//        for(int i=0;i<stackLen;i++){
//            for (int j = 0; j < 4; j++) {
//                byteIndex[4*i+j] = (byte) ((stackIndex[i] >> (8*j)) & 0xff);
//            }
//        }
//        byte[] comIndex= CompressUtil.zlibEncoder(byteIndex);
        byte[] comIndex=CompressUtil.transToByte(stackIndex);
        Stack stack = new Stack();
        stack.comArr=comArr;
        stack.comIndex=comIndex;
        return stack;
    }

    public static List<int[]> stackDecode(Stack stack){
        int[] stackArr=CompressUtil.fastPforDecoder(CompressUtil.transToInteger(stack.getComArr()));

//        byte[] byteIndex=CompressUtil.zlibDecoder(stack.getComIndex());
//        int[] stackIndex=new int[stackArr.length];
//        for(int i=0;i<stackArr.length;i++){
//            for(int j=0;j<4;j++){
//                stackIndex[i] += (byteIndex[4*i+j]& 0xff)<<(8*j);
//            }
//        }
        int[] stackIndex=CompressUtil.transToInteger(stack.getComIndex());
        //合并
        int[][] stackSort=new int[stackArr.length][2];
        for(int i=0;i<stackArr.length;i++){
            stackSort[i][0]=stackArr[i];
            stackSort[i][1]=stackIndex[i];
        }
        Arrays.sort(stackSort,(a,b) -> (a[1]==b[1]? a[0]-b[0]:a[1]-b[1]));

        //统计index数组中各个元素出现的次数，得到最大次数
        Map<Integer,Integer> map = new HashMap<Integer, Integer>();
        for (int i=0;i<stackIndex.length;i++){
            if(map.get(stackIndex[i])!=null){
                map.put(stackIndex[i],map.get(stackIndex[i])+1);
            }else{
                map.put(stackIndex[i],1);
            }
        }
        Collection<Integer> c = map.values();
        Object[] obj = c.toArray();
        Arrays.sort(obj);
        int maxArrLength = (int) obj[obj.length-1];
        int[] sortIndex = stackIndex.clone();
        Arrays.sort(sortIndex);
        int maxIndex=sortIndex[sortIndex.length-1];

        //根据index拆分stackArr
        List<int[]> arrGroup = new LinkedList<>();
        int temp =0;
        for(int i=0;i<maxIndex+1;i++){
            int[] arr= new int[map.get(i)];
            for(int j=0;j<map.get(i);j++){
                arr[j]=stackSort[temp+j][0];
            }
            temp+=map.get(i);
            arrGroup.add(arr);
        }
        return arrGroup;
    }

    @Data
    public static class Stack {
        private byte[] comArr;
        private byte[] comIndex;
    }

}
