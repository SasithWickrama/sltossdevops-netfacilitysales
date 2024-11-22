package com.slt.devops.netfacilitysales.models;

public class UpdateResponse {


    private boolean error;
    private String message;

    public UpdateResponse(boolean error, String message) {
        this.error = error;
        this.message = message;
    }

    public boolean isError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

}
