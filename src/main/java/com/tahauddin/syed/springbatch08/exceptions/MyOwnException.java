package com.tahauddin.syed.springbatch08.exceptions;

public class MyOwnException extends RuntimeException {

    public MyOwnException(String message) {
        super(message);
    }

    public MyOwnException(){

    }
}
