package projects.etrkk5.todoapp.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.Settings;

import projects.etrkk5.todoapp.MainActivity;

public class myAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI);
        mediaPlayer.start();
        Intent i = new Intent(context, MainActivity.class);
        context.startActivity(i);
    }
}
