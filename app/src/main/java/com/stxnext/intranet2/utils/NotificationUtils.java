package com.stxnext.intranet2.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.stxnext.intranet2.broadcast.AlarmManagerService;

import java.util.Calendar;

/**
 * Created by Lukasz on 19.10.2015.
 */
public class NotificationUtils {

    public static void setTimeReportAlarmManagerIfNeeded(Context context) {
        Session session = Session.getInstance(context);
        // Delete previous one if there was a such.
        disableTimeReportAlarmManager(context);
        if (session.isTimeReportNotification()) {
            Calendar calendar = Calendar.getInstance();
            String notificationHourString = session.getTimeReportNotificationHour();
            String[] hourSplitted = notificationHourString.split(":");
            int hour = 17;
            int minute = 0;
            if (hourSplitted != null && hourSplitted.length == 2) {
                String hourString = hourSplitted[0];
                hour = Integer.valueOf(hourString).intValue();
                String minuteString = hourSplitted[1];
                minute = Integer.valueOf(minuteString).intValue();
            }
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            Calendar currentTime = Calendar.getInstance();
            if (currentTime.getTimeInMillis() > calendar.getTimeInMillis()) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            Intent intent = new Intent(context, AlarmManagerService.class);
//            intent.putExtra("timeOfNotification", String.format("%02d:%02d", hour, minute));
//            Calendar timeOfSettingNotification = Calendar.getInstance();
//            intent.putExtra("timeOfSetting", DateFormat.format("kk:mm dd.MM.yyyy", timeOfSettingNotification));
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            PendingIntent pi = PendingIntent.getService(context, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pi);
        }
    }

    public static void disableTimeReportAlarmManager(Context context) {
        Intent intent = new Intent(context, AlarmManagerService.class);
        PendingIntent pi = PendingIntent.getService(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi);
    }
}
