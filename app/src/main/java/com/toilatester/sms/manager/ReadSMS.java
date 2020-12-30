package com.toilatester.sms.manager;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;

import com.toilatester.sms.models.SMSData;
import com.toilatester.sms.server.ServiceCallbacks;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReadSMS {

    ContentResolver content;
    Context context;
    ServiceCallbacks serviceCallbacks;

    public ReadSMS(Context context, ContentResolver content, ServiceCallbacks serviceCallbacks) {
        this.content = content;
        this.context = context;
        this.serviceCallbacks = serviceCallbacks;
    }

    public List<SMSData> getAllSMSMessages() {
        List<SMSData> smsMessages = new ArrayList<>();
        Uri uri = Uri.parse("content://sms/inbox");
        Cursor c = this.content.query(uri, null, null, null, null);
        if (c == null) {
            this.serviceCallbacks.showToast("No message in SMS box");
            return smsMessages;
        }
        int totalSMS = c.getCount();
        System.out.println("============= Send Toast " + this.serviceCallbacks);
        this.serviceCallbacks.showToast(String.format("Total %d message(s) in SMS box", totalSMS));
        if (c.moveToFirst()) {
            for (int j = 0; j < totalSMS; j++) {
                String smsDate = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.DATE));
                String number = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                String body = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY));
                Date date = new Date(Long.valueOf(smsDate));
                switch (Integer.parseInt(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.TYPE)))) {
                    case Telephony.Sms.MESSAGE_TYPE_INBOX:
                        smsMessages.add(new SMSData(number, body, date));
                        this.serviceCallbacks.showToast(String.format("Phone number %s [%s]", number, body));
                        break;
                    case Telephony.Sms.MESSAGE_TYPE_SENT:
                    case Telephony.Sms.MESSAGE_TYPE_OUTBOX:
                    default:
                        break;
                }
                c.moveToNext();
            }
        }
        return smsMessages;
    }
}
