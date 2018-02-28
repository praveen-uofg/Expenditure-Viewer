package com.github.eletransactionviewer.model;

import android.support.annotation.NonNull;

import java.util.Objects;

/**
 * Created by AT-Praveen on 27/02/18.
 */

public class MonthWiseModel implements Comparable <MonthWiseModel>{
    private String bankName;
    private Double totalAmount;
    private String totalAmountString;
    private String cardnumber;

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }


    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCardnumber() {
        return cardnumber;
    }

    public void setCardnumber(String cardnumber) {
        this.cardnumber = cardnumber;
    }

    public String getTotalAmountString() {
        return totalAmountString;
    }

    public void setTotalAmountString(String totalAmountString) {
        this.totalAmountString = totalAmountString;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MonthWiseModel)) return false;

        MonthWiseModel model  = (MonthWiseModel) obj;
        return Objects.equals(cardnumber, model.cardnumber);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return  this.bankName + "|" + this.cardnumber + "|" + this.totalAmount;
    }

    @Override
    public int compareTo(@NonNull MonthWiseModel model) {
        if (model.getTotalAmount() > this.getTotalAmount())
            return 1;
        if (model.getTotalAmount() < this.getTotalAmount()) return -1;


        return 0;
    }
}
