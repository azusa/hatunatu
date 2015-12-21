package org.seasar.dao.exception;


public class ReflectiveOperationRuntimeException extends RuntimeException {

    public ReflectiveOperationRuntimeException(ReflectiveOperationException cause){
        super(cause);
    }
}
