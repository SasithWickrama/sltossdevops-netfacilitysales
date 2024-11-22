package com.slt.devops.netfacilitysales.models;

import java.util.List;

public class LoginResponse {

    private boolean error;
    private String message;
    private List<User> data;


    public LoginResponse(boolean error, String message, List<User> data) {
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

    public List<User> getUser() { return data; }

    public User[] getUserArray() {
        User[] myArray = new User[data.size()];
        data.toArray(myArray);
        return myArray;
    }

}

