package com.ashish.volumecontrol;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    private AudioManager mAudioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get AudioManager Service to control device volumes
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // Initialize various SeekBar
        final SeekBar alarmSeekBar = (SeekBar) findViewById(R.id.alarmSeekBar);
        final SeekBar musicSeekBar = (SeekBar) findViewById(R.id.musicSeekBar);
        final SeekBar ringSeekBar = (SeekBar) findViewById(R.id.ringSeekBar);
        final SeekBar notificationSeekBar = (SeekBar) findViewById(R.id.notificationSeekBar);
        final SeekBar systemSeekBar = (SeekBar) findViewById(R.id.systemSeekBar);
        final SeekBar voiceSeekBar = (SeekBar) findViewById(R.id.voiceCallSeekBar);

        final TextView alarmTextView = (TextView) findViewById(R.id.textViewAlarm);
        final TextView musicTextView = (TextView) findViewById(R.id.textViewMusic);
        final TextView ringTextView = (TextView) findViewById(R.id.textViewRing);
        final TextView notificationTextView = (TextView) findViewById(R.id.textViewNotification);
        final TextView systemTextView = (TextView) findViewById(R.id.textViewSystem);
        final TextView voiceCallTextView = (TextView) findViewById(R.id.textViewVoiceCall);

        // Control various volumes and set data accordingly
        initControl(alarmSeekBar, AudioManager.STREAM_ALARM, alarmTextView, getString(R.string.alarm_volume_txt));
        initControl(musicSeekBar, AudioManager.STREAM_MUSIC, musicTextView, getString(R.string.music_volume_txt));
        initControl(ringSeekBar, AudioManager.STREAM_RING, ringTextView, getString(R.string.ringtone_volume_txt));
        initControl(notificationSeekBar, AudioManager.STREAM_NOTIFICATION, notificationTextView,
                getString(R.string.notification_volume_txt));
        initControl(systemSeekBar, AudioManager.STREAM_SYSTEM, systemTextView, getString(R.string.system_volume_txt));
        initControl(voiceSeekBar, AudioManager.STREAM_VOICE_CALL, voiceCallTextView, getString(R.string.call_volume_txt));
    }

    private void initControl(final SeekBar seekBar, final int stream, final TextView textView, final String text) {

        // Set maximum progress value for SeekBar
        seekBar.setMax(mAudioManager.getStreamMaxVolume(stream));

        // Get maximum seekbar value
        final int maxValue = seekBar.getMax();

        // Set current progress for SeekBar
        seekBar.setProgress(mAudioManager.getStreamVolume(stream));

        textView.setText(text + " (" + seekBar.getProgress() + "/" + maxValue + ")");

        // Handle SeekBar change event using listener and set progress
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                mAudioManager.setStreamVolume(stream, progress, AudioManager.FLAG_PLAY_SOUND);
                textView.setText(text + " (" + progress + "/" + maxValue + ")");
            }

            public void onStartTrackingTouch(SeekBar bar) {

            }

            public void onStopTrackingTouch(SeekBar bar) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }
}