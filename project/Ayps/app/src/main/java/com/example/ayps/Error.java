package com.example.ayps;

public enum Error {

    EMAIL_SING_IN_FAILED(0, "A database error has occurred."),
    DUPLICATE_USER(1, "This user already exists.");

    private final int code;
    private final String description;

    private Error(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code + ": " + description;
    }

}
