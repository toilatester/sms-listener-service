package com.toilatester.sms.manager;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.widget.Toast;

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
        Uri uri = Uri.parse("content://sms/inbox");
        Cursor c = this.content.query(uri, null, null, null, null);
        if (c == null) {
            Toast.makeText(this.context, "No message in SMS box", Toast.LENGTH_SHORT).show();
            return smsMessages;
        }
        int totalSMS = c.getCount();
        Toast.makeText(this.context, String.format("Total %d message(s) in SMS box", totalSMS), Toast.LENGTH_SHORT).show();
        if (c.moveToFirst()) {
            for (int j = 0; j < totalSMS; j++) {
                String smsDate = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.DATE));
                String number = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                String body = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY));
                Date date = new Date(Long.valueOf(smsDate));
                switch (Integer.parseInt(c.getString(c.getColumnIndexOrThrow(Telephony.Sms.TYPE)))) {
                    case Telephony.Sms.MESSAGE_TYPE_INBOX:
                        smsMessages.add(new SMSData(number, body, date));
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
