package com.slt.devops.netfacilitysales.models;

public class User {

    private String SID,NAME,CONTACTNO,REGDATE,RTOM;

    public  User(String sid, String name, String contact , String regdate,String rtom) {
        this.SID= sid;
        this.NAME = name;
        this.CONTACTNO = contact;
        this.REGDATE = regdate;
        this.RTOM = rtom;

    }

    public String getId() {
        return SID;
    }

    public String getName() {
        return NAME;
    }

    public String getContact() {
        return CONTACTNO;
    }

    public String getRegdate() {
        return REGDATE;
    }

    public String getRtom() {
        return RTOM;
    }

}
