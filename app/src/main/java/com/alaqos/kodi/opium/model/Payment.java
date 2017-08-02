package com.alaqos.kodi.opium.model;

/**
 * Created by Dosso on 7/24/2017.
 */

public class Payment {
    private int id;
    private String orderId;
    private int montant;
    private String paiementModeId;
    private String libelle;
    private String lieu;
    private String reference;
    private String createdAt;
    private boolean status;

    public Payment() {
    }

    public Payment(int id, String orderId, int montant, String paiementModeId, String libelle, String lieu, String reference, String createdAt, boolean status) {
        this.id = id;
        this.orderId = orderId;
        this.montant = montant;
        this.paiementModeId = paiementModeId;
        this.libelle = libelle;
        this.lieu = lieu;
        this.reference = reference;
        this.createdAt = createdAt;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getMontant() {
        return montant;
    }

    public void setMontant(int montant) {
        this.montant = montant;
    }

    public String getPaiementModeId() {
        return paiementModeId;
    }

    public void setPaiementModeId(String paiementModeId) {
        this.paiementModeId = paiementModeId;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
