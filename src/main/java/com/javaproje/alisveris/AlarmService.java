package com.javaproje.alisveris;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class AlarmService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle b = new Bundle();
            Intent newIntent = new Intent(context, AlarmManagerUyariActivity.class);
            b.putString("message", "alarm");
            newIntent.putExtras(b);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(newIntent);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}