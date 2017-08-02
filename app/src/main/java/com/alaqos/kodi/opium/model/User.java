package com.alaqos.kodi.opium.model;

/**
 * Created by kodi on 6/16/2017.
 */

public class User {
    private String _id;
    private String pt_id;
    private String image;
    private String first_name;
    private String last_name;
    private String email;
    private String phone;
    private String password;

    public User() {
    }

    public User(String _id, String first_name, String last_name, String email, String phone, String image) {
        this._id = _id;
        this.image = image;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.phone = phone;
    }

    public User(String _id, String first_name, String last_name, String email, String phone, String password, String image) {
        this._id = _id;
        this.image = image;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public String getPtId() {
        return pt_id;
    }

    public void setPtId(String parc) {
        this.pt_id = pt_id;
    }

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFullname() {
        return first_name + " " + last_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
