package com.sephia.sephia_databaseapiproject.model;

public class Handphone {
    private Integer id;
    private String phone_name;
    private String price;
    public Handphone() {
        super();
    }
    public Handphone(Integer id, String phone_name, String price) {
        super();
        this.id = id;
        this.phone_name = phone_name;
        this.price = price;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getPhone_name() {
        return phone_name;
    }
    public void setPhone_name(String phone_name) {
        this.phone_name = phone_name;
    }
    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }
}