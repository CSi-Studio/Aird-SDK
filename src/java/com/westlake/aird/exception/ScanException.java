package com.westlake.aird.exception;

import com.westlake.aird.enums.ResultCodeEnum;

public class ScanException extends RuntimeException {

    private static final long serialVersionUID = 4564124491192825748L;

    public ResultCodeEnum resultCode;

    public ScanException() {
        super();
    }

    public ScanException(ResultCodeEnum resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
    }
}
