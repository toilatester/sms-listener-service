package com.toilatester.sms.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import com.toilatester.sms.models.SMSData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReadSMS {

    ContentResolver content;
    Context context;

    public ReadSMS(Context context, ContentResolver content) {
        this.content = content;
        this.context = context;
    }

    public List<SMSData> getAllSMSMessages() {
        List<SMSData> smsMessages = new ArrayList<>();
        Cursor c = this.querySmsContent();
        if (c == null || c.getCount() == 0) {
            return smsMessages;
        }
        c.moveToFirst();
        smsMessages.add(getCurrentSmsMessageData(c));
        while (c.moveToNext()) {
            smsMessages.add(getCurrentSmsMessageData(c));
        }
        c.close();
        return smsMessages;
    }

    public SMSData getLatestSMSMessage() {
        Cursor c = this.querySmsContent();
        if (c == null || c.getCount() == 0) {
            return null;
        }
        c.moveToFirst();
        SMSData smsData = getCurrentSmsMessageData(c);
        c.close();
        return smsData;
    }

    public List<SMSData> getSMSByPhoneNumber(String phoneNumber, String limit) {
        List<SMSData> smsMessages = new ArrayList<>();
        List<SMSData> allMessages = getAllSMSMessages();
        int fetchSMSLimit = isNumeric(limit) ? Integer.parseInt(limit) : 10;
        for (SMSData sms : allMessages) {
            if (sms.getMobile().equalsIgnoreCase(phoneNumber)) {
                smsMessages.add(sms);
            }
            if (fetchSMSLimit-- <= 1)
                return smsMessages;
        }
        return smsMessages;
    }

    private Cursor querySmsContent() {
        Uri uri = Uri.parse("content://sms/inbox");
        return this.content.query(uri, null, null, null, null);
    }

    private SMSData getCurrentSmsMessageData(Cursor c) {
        String smsDate = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.DATE));
        String number = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
        String body = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY));
        Date date = new Date(Long.valueOf(smsDate));
        switch (Integer.parseInt(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.TYPE)))) {
            case Telephony.Sms.MESSAGE_TYPE_INBOX:
                return new SMSData(number, body, date);
            case Telephony.Sms.MESSAGE_TYPE_SENT:
            case Telephony.Sms.MESSAGE_TYPE_OUTBOX:
            default:
                return null;
        }
    }

    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            int i = Integer.parseInt(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
