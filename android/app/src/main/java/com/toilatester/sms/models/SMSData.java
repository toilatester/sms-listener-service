package com.toilatester.sms.models;

import java.util.Date;

public class SMSData {
    String mobile, message;

    Date receiveDate;

    public SMSData(String mobile, String message, Date receiveDate) {
        this.mobile = mobile;
        this.message = message;
        this.receiveDate = receiveDate;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
    }

}
