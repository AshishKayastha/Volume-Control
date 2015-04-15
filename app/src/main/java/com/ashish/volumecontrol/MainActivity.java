package com.ashish.volumecontrol;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    private static final String SILENT_MODE = "SILENT MODE";
    private boolean mIsPhoneInSilent;
    private AudioManager mAudioManager;
    private SharedPreferences mSharedPrefs;
    private Switch mSilentSwitch;
    private ImageView mSilentIcon;
    private SeekBar mAlarmSeekBar, mMediaSeekBar, mRingSeekBar, mNotificationSeekBar, mSystemSeekBar, mVoiceSeekBar;
    private TextView mAlarmTextView, mMediaTextView, mRingTextView, mNotificationTextView, mSystemTextView,
            mVoiceCallTextView, mSilentModeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get AudioManager Service to control device volumes
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        // Initialize various SeekBar
        mAlarmSeekBar = (SeekBar) findViewById(R.id.alarmSeekBar);
        mMediaSeekBar = (SeekBar) findViewById(R.id.mediaSeekBar);
        mRingSeekBar = (SeekBar) findViewById(R.id.ringSeekBar);
        mNotificationSeekBar = (SeekBar) findViewById(R.id.notificationSeekBar);
        mSystemSeekBar = (SeekBar) findViewById(R.id.systemSeekBar);
        mVoiceSeekBar = (SeekBar) findViewById(R.id.voiceCallSeekBar);

        // Initialize TextViews to set seekbar progress
        mAlarmTextView = (TextView) findViewById(R.id.textViewAlarm);
        mMediaTextView = (TextView) findViewById(R.id.textViewMedia);
        mRingTextView = (TextView) findViewById(R.id.textViewRing);
        mNotificationTextView = (TextView) findViewById(R.id.textViewNotification);
        mSystemTextView = (TextView) findViewById(R.id.textViewSystem);
        mVoiceCallTextView = (TextView) findViewById(R.id.textViewVoiceCall);

        mSilentModeTextView = (TextView) findViewById(R.id.silentTextView);
        mSilentIcon = (ImageView) findViewById(R.id.silentIcon);

        // Initialize Switch buttons
        mSilentSwitch = (Switch) findViewById(R.id.silentSwitch);

        // To save and restore silent mode status
        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mIsPhoneInSilent = mSharedPrefs.getBoolean(SILENT_MODE, false);

        /*
         * Get silent status from SharedPreferences
         * and accordingly set the switch button status
         */
        mSilentSwitch.setChecked(mIsPhoneInSilent);
        if (mIsPhoneInSilent) {
            mSilentModeTextView.setText(getString(R.string.silent_mode_on_txt));
            mSilentIcon.setImageResource(R.drawable.btn_silent);
        } else {
            mSilentModeTextView.setText(getString(R.string.silent_mode_off_txt));
            mSilentIcon.setImageResource(R.drawable.btn_ring);
        }
        setControls();
        setSilentControls();

        // Handle silent switch button change events
        mSilentSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    //If button is on then set phone to silent mode
                    mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    mSilentModeTextView.setText(getString(R.string.silent_mode_on_txt));
                    mSilentIcon.setImageResource(R.drawable.btn_silent);
                    mSharedPrefs.edit().putBoolean(SILENT_MODE, true).apply();
                    setSilentControls();
                } else {
                    //If button is on then set phone to normal mode
                    mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    mSilentModeTextView.setText(getString(R.string.silent_mode_off_txt));
                    mSilentIcon.setImageResource(R.drawable.btn_ring);
                    mSharedPrefs.edit().putBoolean(SILENT_MODE, false).apply();
                    setSilentControls();
                }
            }
        });
    }

    // Control alarm, media, voice call volumes
    private void setControls() {
        initControl(mAlarmSeekBar, AudioManager.STREAM_ALARM, mAlarmTextView, getString(R.string.alarm_volume_txt));
        initControl(mMediaSeekBar, AudioManager.STREAM_MUSIC, mMediaTextView, getString(R.string.media_volume_txt));
        initControl(mVoiceSeekBar, AudioManager.STREAM_VOICE_CALL, mVoiceCallTextView, getString(R.string.call_volume_txt));
    }

    // Control ring, notification, system volumes differently, to work with silent mode
    private void setSilentControls() {
        initSilentControl(mRingSeekBar, AudioManager.STREAM_RING, mRingTextView, getString(R.string.ringtone_volume_txt));
        initSilentControl(mNotificationSeekBar, AudioManager.STREAM_NOTIFICATION, mNotificationTextView,
                getString(R.string.notification_volume_txt));
        initSilentControl(mSystemSeekBar, AudioManager.STREAM_SYSTEM, mSystemTextView, getString(R.string.system_volume_txt));
    }

    // Control various volumes and set data, progress accordingly
    private void initControl(final SeekBar seekBar, final int stream, final TextView textView, final String text) {

        // Set maximum progress value for SeekBar
        seekBar.setMax(mAudioManager.getStreamMaxVolume(stream));

        // Get maximum seekbar value
        final int maxValue = seekBar.getMax();

        // Set current progress for SeekBar & TextView
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

    // For Silent Mode
    private void initSilentControl(final SeekBar seekBar, final int stream, final TextView textView, final String text) {

        // Set maximum progress value for SeekBar
        seekBar.setMax(mAudioManager.getStreamMaxVolume(stream));

        // Get maximum seekbar value
        final int maxValue = seekBar.getMax();

        // Set current progress for SeekBar & TextView
        seekBar.setProgress(mAudioManager.getStreamVolume(stream));
        textView.setText(text + " (" + seekBar.getProgress() + "/" + maxValue + ")");

        // Handle SeekBar change event using listener and set progress
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                mAudioManager.setStreamVolume(stream, progress, AudioManager.FLAG_PLAY_SOUND);
                textView.setText(text + " (" + progress + "/" + maxValue + ")");
                if (progress > 0) {
                    mSharedPrefs.edit().putBoolean(SILENT_MODE, false).apply();
                    mSilentSwitch.setChecked(mIsPhoneInSilent);
                    mSilentModeTextView.setText(getString(R.string.silent_mode_off_txt));
                    mSilentIcon.setImageResource(R.drawable.btn_ring);
                } else {
                    mSharedPrefs.edit().putBoolean(SILENT_MODE, true).apply();
                    mSilentSwitch.setChecked(mIsPhoneInSilent);
                    mSilentModeTextView.setText(getString(R.string.silent_mode_on_txt));
                    mSilentIcon.setImageResource(R.drawable.btn_silent);
                }
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
        switch (id) {

            case R.id.action_about:
                showAboutDialog();
                return true;

            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAboutDialog() {
        final LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final View aboutView = inflater.inflate(R.layout.about_dialog, null);

        final TextView appDescriptionText = (TextView) aboutView.findViewById(R.id.appDescription);
        appDescriptionText.setText(getString(R.string.app_name) + " " + getAppVersion() + "\n\n" +
                getString(R.string.about_app_description));

        final TextView developedByText = (TextView) aboutView.findViewById(R.id.developedBy);
        developedByText.setText(Html.fromHtml("<u>" + getString(R.string.developed_by_text) + "</u>"));

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.about_txt))
                .setView(aboutView)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }

    private String getAppVersion() {
        final PackageManager pm = getPackageManager();
        final String packageName = getPackageName();
        String version = "";
        try {
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            version = pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }
}