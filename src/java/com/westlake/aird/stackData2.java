package com.westlake.aird;

import com.westlake.aird.util.CompressUtil;
import lombok.Data;

import java.util.*;

//对数组的index进行移位缩短操作后，使用zlib压缩
public class stackData2 {
    public static void main(String[] args) {
        for(int k=0;k<12;k++) {
            int arrNum= (int)Math.pow(2,k);
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
        for(int i=0;i<stackSort.length;i++){
            stackArr[i]=stackSort[i][0];
        }

        //index移位存储
        int digit = (int)Math.ceil(Math.log(arrGroup.size()) / Math.log(2));
        int indexLen = (stackLen*digit-1)/8+1;
        byte[] value = new byte[8*indexLen];
        for(int i=0;i<stackLen;i++){
            for(int j=0;j<digit;j++){
                value[j+digit*i]=(byte)((stackSort[i][1] >> j)&1);
            }
        }

        byte[] indexShift = new byte[indexLen];
        for(int i=0;i<indexLen;i++){
            int temp=0;
            for(int j=0;j<8;j++){
                temp+=value[8*i+j]<<j;
                indexShift[i]=(byte)temp;
            }
        }

        Stack stack = new Stack();
        stack.comArr= CompressUtil.transToByte(CompressUtil.fastPforEncoder(stackArr));
        stack.comIndex=CompressUtil.zlibEncoder(indexShift);
        stack.digit=digit;
        return stack;
    }

    public static List<int[]> stackDecode(Stack stack){
        int[] stackArr = CompressUtil.fastPforDecoder(CompressUtil.transToInteger(stack.getComArr()));
        int[] stackIndex= new int[stackArr.length];
        byte[] indexShift = CompressUtil.zlibDecoder(stack.getComIndex());
        int digit =stack.getDigit();

        byte[] value = new byte[8*indexShift.length];
        for(int i=0;i<indexShift.length;i++){
            for(int j=0;j<8;j++){
                value[8*i+j]=(byte)(((indexShift[i]&0xff)>>j)&1);
            }
        }
        for(int i=0;i<stackIndex.length;i++){
            for(int j =0;j<digit;j++){
                stackIndex[i] += value[digit*i+j]<<j;
            }
        }

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
        private int digit;
    }

}
