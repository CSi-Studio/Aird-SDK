__kernel void lowerBound(__global const float* targets,
                  __global const float* mzArray,
                  __global const float* intArray,
                  const int mzLength,
                  const float mzWindow,
                  __global float* results){
    int id = get_global_id(0);
    float target = targets[id];
    float targetStart = target - mzWindow/2;
    float targetEnd = target + mzWindow/2;
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
    return;
}