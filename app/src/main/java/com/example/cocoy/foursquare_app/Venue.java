package com.example.cocoy.foursquare_app;

/**
 * Created by cocoy on 20/09/2017.
 */

public class Venue {
    String id;
    String name;
    String address;
    String category;
    String phone;
    String checkins;
    String prefix;
    String sufix;
    String photo;

    public Venue(String id, String name, String address, String category, String checkins, String photo) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.category = category;
        //this.phone = phone;
        this.checkins = checkins;
        this.photo = photo;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCheckins() {
        return checkins;
    }

    public void setCheckins(String checkins) {
        this.checkins = checkins;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSufix() {
        return sufix;
    }

    public void setSufix(String sufix) {
        this.sufix = sufix;
    }
}
