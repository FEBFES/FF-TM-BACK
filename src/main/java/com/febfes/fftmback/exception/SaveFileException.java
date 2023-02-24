package com.febfes.fftmback.exception;

public class SaveFileException extends RuntimeException {

    public SaveFileException(String fileName) {
        super("Error to save file with name " + fileName);
    }
}
