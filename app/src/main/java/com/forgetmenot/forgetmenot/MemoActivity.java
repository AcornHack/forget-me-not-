package com.forgetmenot.forgetmenot;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

public class MemoActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "Katiefile";
    TextToSpeech textToSpeech;
    EditText editText;
    Button button;
    TimePicker timePicker;
    public static final String memos = "memos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SharedPreferences savedmemos = getSharedPreferences(memos, 0);
        setContentView(R.layout.activity_memo);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        SharedPreferences.Editor editor = settings.edit();
        editor.putString("name", "Katie");
        editor.commit();
        String anything = settings.getString("name", "default");

        editText = (EditText) findViewById(R.id.editText);
        editText.setText(anything);
        button = (Button) findViewById(R.id.enterMemoButton);
        timePicker = (TimePicker) findViewById(R.id.timePicker);

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String memo = editText.getText().toString();
                Editor editor = savedmemos.edit();
                editor.putString(UUID.randomUUID().toString(), memo).apply();
                textToSpeech.speak(memo, TextToSpeech.QUEUE_FLUSH, null);
                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();
                Notification testnotification = getNotification(memo);
                long time = getMilliseconds(hour, minute);
                System.out.println(timePicker.getCurrentHour());
                System.out.println(timePicker.getCurrentMinute());
                System.out.println(time);
                scheduleNotification(getNotification(memo), getMilliseconds(hour, minute));
                Toast.makeText(getApplicationContext(), "Memo Saved", Toast.LENGTH_SHORT).show();
            }
        });
    }
    protected long getMilliseconds(int hours, int minutes) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hours);
        cal.set(Calendar.MINUTE, minutes);
        cal.set(Calendar.SECOND, 0);
        return cal.getTimeInMillis();
    }

    public void onPause(){
        if(textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

private void scheduleNotification(Notification notification, long time) {

    Intent notificationIntent = new Intent(this, MemoNotificationReceiver.class);
    notificationIntent.putExtra(MemoNotificationReceiver.MEMO_ID, 1);
    notificationIntent.putExtra(MemoNotificationReceiver.MEMO, notification);
    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


    AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
    alarmManager.set(AlarmManager.RTC, time, pendingIntent);
}

    private Notification getNotification(String content) {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Scheduled Notification");
        builder.setContentText(content);
       builder.setSmallIcon(R.drawable.forgetlogo);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            return builder.build();
        }
        return builder.getNotification();
    }

}
