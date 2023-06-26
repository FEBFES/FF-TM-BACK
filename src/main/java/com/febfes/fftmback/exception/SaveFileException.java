package com.febfes.fftmback.exception;

import java.io.Serial;

public class SaveFileException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -8903589762357312661L;

    public SaveFileException(String fileName) {
        super("Error to save file with name " + fileName);
    }
}
