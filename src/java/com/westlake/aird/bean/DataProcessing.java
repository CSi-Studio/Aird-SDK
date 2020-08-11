package com.westlake.aird.bean;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DataProcessing {

    /**
     * Any additional manipulation not included elsewhere in the dataProcessing element.
     */
    List<String> processingOperations;

    public void addProcessingOperation(String processingOperation)
    {
        if (processingOperations == null)
        {
            processingOperations = new ArrayList<String>();
        }
        processingOperations.add(processingOperation);
    }
}
