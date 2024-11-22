package com.slt.devops.netfacilitysales.models.networkfacility;


import java.util.List;

public class LandlineResponce {

    private boolean error;
    private List<LandLine> data;
    private String message;

    public LandlineResponce(boolean error, List<LandLine> data, String message) {
        this.error = error;
        this.data = data;
        this.message = message;
    }

    public boolean isError() {
        return error;
    }

    public List<LandLine> getNoList() {
        return data;
    }

    public LandLine[] getLandLineArray() {
        LandLine[] myArray = new LandLine[data.size()];
        data.toArray(myArray);
        return myArray;
    }

    public String getMessage() {
        return message;
    }
}
