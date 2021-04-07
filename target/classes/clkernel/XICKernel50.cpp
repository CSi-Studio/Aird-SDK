
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
                  const int length1,
                  __global const float* mzArray2,
                  __global const float* intArray2,
                  const int length2,
                  __global const float* mzArray3,
                  __global const float* intArray3,
                  const int length3,
                  __global const float* mzArray4,
                  __global const float* intArray4,
                  const int length4,
                  __global const float* mzArray5,
                  __global const float* intArray5,
                  const int length5,
                  __global const float* mzArray6,
                  __global const float* intArray6,
                  const int length6,
                  __global const float* mzArray7,
                  __global const float* intArray7,
                  const int length7,
                  __global const float* mzArray8,
                  __global const float* intArray8,
                  const int length8,
                  __global const float* mzArray9,
                  __global const float* intArray9,
                  const int length9,
                  __global const float* mzArray10,
                  __global const float* intArray10,
                  const int length10,

                  __global const float* mzArray11,
                  __global const float* intArray11,
                  const int length11,
                  __global const float* mzArray12,
                  __global const float* intArray12,
                  const int length12,
                  __global const float* mzArray13,
                  __global const float* intArray13,
                  const int length13,
                  __global const float* mzArray14,
                  __global const float* intArray14,
                  const int length14,
                  __global const float* mzArray15,
                  __global const float* intArray15,
                  const int length15,
                  __global const float* mzArray16,
                  __global const float* intArray16,
                  const int length16,
                  __global const float* mzArray17,
                  __global const float* intArray17,
                  const int length17,
                  __global const float* mzArray18,
                  __global const float* intArray18,
                  const int length18,
                  __global const float* mzArray19,
                  __global const float* intArray19,
                  const int length19,
                  __global const float* mzArray20,
                  __global const float* intArray20,
                  const int length20,

                  __global const float* mzArray21,
                  __global const float* intArray21,
                  const int length21,
                  __global const float* mzArray22,
                  __global const float* intArray22,
                  const int length22,
                  __global const float* mzArray23,
                  __global const float* intArray23,
                  const int length23,
                  __global const float* mzArray24,
                  __global const float* intArray24,
                  const int length24,
                  __global const float* mzArray25,
                  __global const float* intArray25,
                  const int length25,
                  __global const float* mzArray26,
                  __global const float* intArray26,
                  const int length26,
                  __global const float* mzArray27,
                  __global const float* intArray27,
                  const int length27,
                  __global const float* mzArray28,
                  __global const float* intArray28,
                  const int length28,
                  __global const float* mzArray29,
                  __global const float* intArray29,
                  const int length29,
                  __global const float* mzArray30,
                  __global const float* intArray30,
                  const int length30,

                  __global const float* mzArray31,
                  __global const float* intArray31,
                  const int length31,
                  __global const float* mzArray32,
                  __global const float* intArray32,
                  const int length32,
                  __global const float* mzArray33,
                  __global const float* intArray33,
                  const int length33,
                  __global const float* mzArray34,
                  __global const float* intArray34,
                  const int length34,
                  __global const float* mzArray35,
                  __global const float* intArray35,
                  const int length35,
                  __global const float* mzArray36,
                  __global const float* intArray36,
                  const int length36,
                  __global const float* mzArray37,
                  __global const float* intArray37,
                  const int length37,
                  __global const float* mzArray38,
                  __global const float* intArray38,
                  const int length38,
                  __global const float* mzArray39,
                  __global const float* intArray39,
                  const int length39,
                  __global const float* mzArray40,
                  __global const float* intArray40,
                  const int length40,

                  __global const float* mzArray41,
                  __global const float* intArray41,
                  const int length41,
                  __global const float* mzArray42,
                  __global const float* intArray42,
                  const int length42,
                  __global const float* mzArray43,
                  __global const float* intArray43,
                  const int length43,
                  __global const float* mzArray44,
                  __global const float* intArray44,
                  const int length44,
                  __global const float* mzArray45,
                  __global const float* intArray45,
                  const int length45,
                  __global const float* mzArray46,
                  __global const float* intArray46,
                  const int length46,
                  __global const float* mzArray47,
                  __global const float* intArray47,
                  const int length47,
                  __global const float* mzArray48,
                  __global const float* intArray48,
                  const int length48,
                  __global const float* mzArray49,
                  __global const float* intArray49,
                  const int length49,
                  __global const float* mzArray50,
                  __global const float* intArray50,
                  const int length50,
                  const float mzWindow,
                  __global float* results){
    int id = get_global_id(0);
    float targetStart = targets[id] - mzWindow/2;
    float targetEnd = targets[id] + mzWindow/2;
    if(length1 != 0){
        acc(mzArray1, intArray1, id+targetLength*0, length1, targetStart, targetEnd, results);
    }
    if(length2 != 0){
        acc(mzArray2, intArray2, id+targetLength*1, length2, targetStart, targetEnd, results);
    }
    if(length3 != 0){
        acc(mzArray3, intArray3, id+targetLength*2, length3, targetStart, targetEnd, results);
    }
    if(length4 != 0){
        acc(mzArray4, intArray4, id+targetLength*3, length4, targetStart, targetEnd, results);
    }
    if(length5 != 0){
        acc(mzArray5, intArray5, id+targetLength*4, length5, targetStart, targetEnd, results);
    }
    if(length6 != 0){
        acc(mzArray6, intArray6, id+targetLength*5, length6, targetStart, targetEnd, results);
    }
    if(length7 != 0){
        acc(mzArray7, intArray7, id+targetLength*6, length7, targetStart, targetEnd, results);
    }
    if(length8 != 0){
        acc(mzArray8, intArray8, id+targetLength*7, length8, targetStart, targetEnd, results);
    }
    if(length9 != 0){
        acc(mzArray9, intArray9, id+targetLength*8, length9, targetStart, targetEnd, results);
    }
    if(length10 != 0){
        acc(mzArray10, intArray10, id+targetLength*9, length10, targetStart, targetEnd, results);
    }

    if(length11 != 0){
        acc(mzArray11, intArray11, id+targetLength*10, length11, targetStart, targetEnd, results);
    }
    if(length12 != 0){
        acc(mzArray12, intArray12, id+targetLength*11, length12, targetStart, targetEnd, results);
    }
    if(length13 != 0){
        acc(mzArray13, intArray13, id+targetLength*12, length13, targetStart, targetEnd, results);
    }
    if(length14 != 0){
        acc(mzArray14, intArray14, id+targetLength*13, length14, targetStart, targetEnd, results);
    }
    if(length15 != 0){
        acc(mzArray15, intArray15, id+targetLength*14, length15, targetStart, targetEnd, results);
    }
    if(length16 != 0){
        acc(mzArray16, intArray16, id+targetLength*15, length16, targetStart, targetEnd, results);
    }
    if(length17 != 0){
        acc(mzArray17, intArray17, id+targetLength*16, length17, targetStart, targetEnd, results);
    }
    if(length18 != 0){
        acc(mzArray18, intArray18, id+targetLength*17, length18, targetStart, targetEnd, results);
    }
    if(length19 != 0){
        acc(mzArray19, intArray19, id+targetLength*18, length19, targetStart, targetEnd, results);
    }
    if(length20 != 0){
        acc(mzArray20, intArray20, id+targetLength*19, length20, targetStart, targetEnd, results);
    }

    if(length21 != 0){
        acc(mzArray21, intArray21, id+targetLength*20, length21, targetStart, targetEnd, results);
    }
    if(length22 != 0){
        acc(mzArray22, intArray22, id+targetLength*21, length22, targetStart, targetEnd, results);
    }
    if(length23 != 0){
        acc(mzArray23, intArray23, id+targetLength*22, length23, targetStart, targetEnd, results);
    }
    if(length24 != 0){
        acc(mzArray24, intArray24, id+targetLength*23, length24, targetStart, targetEnd, results);
    }
    if(length25 != 0){
        acc(mzArray25, intArray25, id+targetLength*24, length25, targetStart, targetEnd, results);
    }
    if(length26 != 0){
        acc(mzArray26, intArray26, id+targetLength*25, length26, targetStart, targetEnd, results);
    }
    if(length27 != 0){
        acc(mzArray27, intArray27, id+targetLength*26, length27, targetStart, targetEnd, results);
    }
    if(length28 != 0){
        acc(mzArray28, intArray28, id+targetLength*27, length28, targetStart, targetEnd, results);
    }
    if(length29 != 0){
        acc(mzArray29, intArray29, id+targetLength*28, length29, targetStart, targetEnd, results);
    }
    if(length30 != 0){
        acc(mzArray30, intArray30, id+targetLength*29, length30, targetStart, targetEnd, results);
    }

    if(length31 != 0){
        acc(mzArray31, intArray31, id+targetLength*30, length31, targetStart, targetEnd, results);
    }
    if(length32 != 0){
        acc(mzArray32, intArray32, id+targetLength*31, length32, targetStart, targetEnd, results);
    }
    if(length33 != 0){
        acc(mzArray33, intArray33, id+targetLength*32, length33, targetStart, targetEnd, results);
    }
    if(length34 != 0){
        acc(mzArray34, intArray34, id+targetLength*33, length34, targetStart, targetEnd, results);
    }
    if(length35 != 0){
        acc(mzArray35, intArray35, id+targetLength*34, length35, targetStart, targetEnd, results);
    }
    if(length36 != 0){
        acc(mzArray36, intArray36, id+targetLength*35, length36, targetStart, targetEnd, results);
    }
    if(length37 != 0){
        acc(mzArray37, intArray37, id+targetLength*36, length37, targetStart, targetEnd, results);
    }
    if(length38 != 0){
        acc(mzArray38, intArray38, id+targetLength*37, length38, targetStart, targetEnd, results);
    }
    if(length39 != 0){
        acc(mzArray39, intArray39, id+targetLength*38, length39, targetStart, targetEnd, results);
    }
    if(length40 != 0){
        acc(mzArray40, intArray40, id+targetLength*39, length40, targetStart, targetEnd, results);
    }

    if(length41 != 0){
        acc(mzArray41, intArray41, id+targetLength*40, length41, targetStart, targetEnd, results);
    }
    if(length42 != 0){
        acc(mzArray42, intArray42, id+targetLength*41, length42, targetStart, targetEnd, results);
    }
    if(length43 != 0){
        acc(mzArray43, intArray43, id+targetLength*42, length43, targetStart, targetEnd, results);
    }
    if(length44 != 0){
        acc(mzArray44, intArray44, id+targetLength*43, length44, targetStart, targetEnd, results);
    }
    if(length45 != 0){
        acc(mzArray45, intArray45, id+targetLength*44, length45, targetStart, targetEnd, results);
    }
    if(length46 != 0){
        acc(mzArray46, intArray46, id+targetLength*45, length46, targetStart, targetEnd, results);
    }
    if(length47 != 0){
        acc(mzArray47, intArray47, id+targetLength*46, length47, targetStart, targetEnd, results);
    }
    if(length48 != 0){
        acc(mzArray48, intArray48, id+targetLength*47, length48, targetStart, targetEnd, results);
    }
    if(length49 != 0){
        acc(mzArray49, intArray49, id+targetLength*48, length49, targetStart, targetEnd, results);
    }
    if(length50 != 0){
        acc(mzArray50, intArray50, id+targetLength*49, length50, targetStart, targetEnd, results);
    }
    return;
}
