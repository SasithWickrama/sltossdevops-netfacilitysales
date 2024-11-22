package com.slt.devops.netfacilitysales.models;

public class VersionResponse
{


    private boolean error;
    private String message;
    private String data;

    public VersionResponse(boolean error, String message, String data) {
        this.error = error;
        this.message = message;
        this.data = data;
    }

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getData() {
        return data;
    }

}