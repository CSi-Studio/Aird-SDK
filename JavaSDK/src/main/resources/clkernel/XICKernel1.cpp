/*
 * Copyright (c) 2020 CSi Biotech
 * AirdSDK and AirdPro are licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

inline void acc(__global const float* mzArray,__global const float* intArray,int id, int mzLength, float targetStart, float targetEnd,__global float* results)
{
     int high = mzLength - 1;
        int index = 0;
        if(targetStart <= mzArray[0]){
            index=0;
        }else if(targetStart >= mzArray[high]){
            index=-1;
        }else{
            int low = 0;
            while (low + 1 < high) {
                int mid = (low + high)/2;
                if (targetStart < mzArray[mid]) {
                    high = mid;
                } else if (targetStart > mzArray[mid]) {
                    low = mid;
                } else {
                    high=mid;
                    break;
                }
            }
            index=high;
        }
        if(index == -1){
            results[id] = 0;
        }else{
            float intensitySum = 0;
            while(index < mzLength && mzArray[index] <= targetEnd){
                intensitySum += intArray[index];
                index++;
            }
            results[id] = intensitySum;
        }
}

__kernel void lowerBound(__global const float* targets,
                  const int targetLength,
                  __global const float* mzArray1,
                  __global const float* intArray1,
                  const int mzLength1,
                  const float mzWindow,
                  __global float* results){
    int id = get_global_id(0);
    float targetStart = targets[id] - mzWindow/2;
    float targetEnd = targets[id] + mzWindow/2;
    acc(mzArray1, intArray1, id, mzLength1, targetStart, targetEnd, results);
    return;
}
