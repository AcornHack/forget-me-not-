package com.forgetmenot.forgetmenot;

        import android.app.Notification;
        import android.app.NotificationManager;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;

public class MemoNotificationReceiver extends BroadcastReceiver {

    public static String MEMO_ID = "memo-id";
    public static String MEMO = "memo";

    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = intent.getParcelableExtra(MEMO);
        int id = intent.getIntExtra(MEMO_ID, 0);
        notificationManager.notify(id, notification);

    }
}