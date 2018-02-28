package com.github.eletransactionviewer.model;

/**
 * Created by AT-Praveen on 20/02/18.
 */

public class Data_Model {
    private String bankName;
    private String transAmountString;
    private String transDate;
    private String smsDate;
    private String cardNumber;
    private Double transAmount;

    public Data_Model() {
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getTransAmountString() {
        return transAmountString;
    }

    public void setTransAmountString(String transAmountString) {
        this.transAmountString = transAmountString;
    }

    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    public String getSmsDate() {
        return smsDate;
    }

    public void setSmsDate(String smsDate) {
        this.smsDate = smsDate;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Double getTransAmount() {
        return transAmount;
    }

    public void setTransAmount(Double transAmount) {
        this.transAmount = transAmount;
    }
}
