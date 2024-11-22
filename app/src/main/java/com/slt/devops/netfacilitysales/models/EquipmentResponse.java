package com.slt.devops.netfacilitysales.models;

import java.util.List;

public class EquipmentResponse {

    private boolean error;
    private List<Equipment> data;
    private String message;

    public EquipmentResponse(boolean error, List<Equipment> data, String message) {
        this.error = error;
        this.data = data;
        this.message = message;
    }

    public boolean isError() {
        return error;
    }

    public List<Equipment> getEquipment() {
        return data;
    }

    public Equipment[] getEquipmentArray() {
        Equipment[] myArray = new Equipment[data.size()];
        data.toArray(myArray);
        return myArray;
    }

    public String getMessage() {
        return message;
    }
}
