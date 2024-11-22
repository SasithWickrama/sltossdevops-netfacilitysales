package com.slt.devops.netfacilitysales.models;

public class Equipment {

    private String RTOM, LOCATION, TYPE, LATITUDE, LONGITUDE,DISCRIPTION,DISTANCE,OCCUPIED,FREE,INDEX,LEA,STATUS;

    public Equipment(String rtom, String location, String type, String lat, String lon,
                     String discription, String distance, String occupied, String free, String index, String lea, String status) {
        this.RTOM = rtom;
        this.LOCATION = location;
        this.TYPE = type;
        this.LATITUDE = lat;
        this.LONGITUDE = lon;
        this.DISTANCE = distance;
        this.OCCUPIED = occupied;
        this.FREE = free;
        this.DISCRIPTION =discription;
        this.INDEX =  index;
        this.LEA = lea;
        this.STATUS = status;
    }

    public String getRtom() {
        return RTOM;
    }

    public String getLocation() {
        return LOCATION;
    }

    public String getType() {
        return TYPE;
    }

    public String getLatitude() {
        return LATITUDE;
    }

    public String getLontitude() {
        return LONGITUDE;
    }

    public String getDISCRIPTION() { return DISCRIPTION; }

    public String getDISTANCE() { return DISTANCE;  }

    public String getOCCUPIED() {return OCCUPIED; }

    public String getFREE() { return FREE; }

    public String getINDEX() { return INDEX; }

    public String getLEA() {  return LEA;   }

    public String getSTATUS() {  return STATUS;   }

    public void setLEA(String LEA) { this.LEA = LEA;    }
}
