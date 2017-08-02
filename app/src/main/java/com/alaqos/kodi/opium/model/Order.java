package com.alaqos.kodi.opium.model;

/**
 * Created by Dosso on 7/13/2017.
 */

public class Order {
    private int id;
    private String _id;
    private String from;
    private String reference;
    private int montant;
    private int paye;
    private int reste;
    private String timestamp;
    private String picture;
    private boolean status;

    public Order() {}

    public Order(int id, String _id, String from, String reference, int montant, int paye, int reste, String timestamp, String picture, boolean status) {
        this.id = id;
        this._id = _id;
        this.from = from;
        this.reference = reference;
        this.montant = montant;
        this.paye = paye;
        this.reste = reste;
        this.timestamp = timestamp;
        this.picture = picture;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public int getMontant() {
        return montant;
    }

    public void setMontant(int montant) {
        this.montant = montant;
    }

    public int getPaye() {
        return paye;
    }

    public void setPaye(int paye) {
        this.paye = paye;
    }

    public int getReste() {
        return reste;
    }

    public void setReste(int reste) {
        this.reste = reste;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}