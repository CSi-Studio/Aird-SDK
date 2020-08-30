package com.westlake.aird;

import com.westlake.aird.util.CompressUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class stackData {
    public static void main(String[] args) {
        //生成两个有序数组
        int[] arr1 =new int[40000];
        int[] arr2 =new int[40000];
        for (int i = 0; i < arr1.length; i++) {
            arr1[i] = (i == 0 ? 0 : arr1[i - 1]) + (int)(Math.random()*10);
        }
        for (int i = 0; i < arr2.length; i++) {
            arr2[i] = (i == 0 ? 0 : arr2[i - 1]) + (int)(Math.random()*10);
        }

        //生成堆叠数组和堆叠index
        int[][] stackSort = stackEncode(arr1,arr2);
        int[] stackArr=new int[stackSort.length];
        int[] stackIndex=new int[stackSort.length];
        for(int i=0;i<stackSort.length;i++){
            stackArr[i]=stackSort[i][0];
            stackIndex[i]=stackSort[i][1];
        }
        int[][] stackDecode =stackDecode(stackArr,stackIndex);
        System.out.println("对照数组1是否相同"+Arrays.equals(arr1,stackDecode[0]));
        System.out.println("对照数组2是否相同"+Arrays.equals(arr2,stackDecode[1]));

        //比较堆叠前后fastPfor压缩的长度
        int[] comArr1 = CompressUtil.fastPforEncoder(arr1);
        int[] comArr2 = CompressUtil.fastPforEncoder(arr2);
        int[] comStackArr = CompressUtil.fastPforEncoder(stackArr);
        byte[] comIndex =CompressUtil.transToByte(stackIndex);
        System.out.println(comArr1.length+comArr2.length);
        System.out.println(comStackArr.length);
    }

    public static int[][] stackEncode(int[]... arrGroup){
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
        return stackSort;
    }

    public static int[][] stackDecode(int[] stackArr,int[] stackIndex){
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
        int[][] arrGroup = new int[maxIndex+1][maxArrLength];
        int temp =0;
        for(int i=0;i<maxIndex+1;i++){
            for(int j=0;j<map.get(i);j++){
                arrGroup[i][j]=stackSort[temp+j][0];
            }
            temp+=map.get(i);
        }
        return arrGroup;
    }


}
