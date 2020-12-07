__kernel void lowerBound(__global const float* targets,
                  __global const float* array,
                  const int arrayLength,
                  __global int* results){
    int id = get_global_id(0);
    float target = targets[id];
    int rightIndex = arrayLength - 1;
    if(target <= array[0]){
        results[id]=0;
        return;
    }
    if(target >= array[rightIndex]){
        results[id]=-1;
        return;
    }
    int leftIndex = 0;
    while (leftIndex + 1 < rightIndex) {
        int tmp = (leftIndex + rightIndex)/2;
        if (target < array[tmp]) {
            rightIndex = tmp;
        } else if (target > array[tmp]) {
            leftIndex = tmp;
        } else {
            results[id]=tmp;
            return;
        }
    }
    results[id]=rightIndex;
    return;
}