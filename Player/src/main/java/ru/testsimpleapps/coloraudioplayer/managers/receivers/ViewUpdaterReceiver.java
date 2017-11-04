package ru.testsimpleapps.coloraudioplayer.managers.receivers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import ru.testsimpleapps.coloraudioplayer.App;
import ru.testsimpleapps.coloraudioplayer.ui.activities.MainActivity;

public class ViewUpdaterReceiver extends BroadcastReceiver {

    public static final String UPDATE_ACTIVITY_EXIT = "UPDATE_ACTIVITY_EXIT";
    public static final String UPDATE_PLAY_BUTTON = "UPDATE_PLAY_BUTTON";
    public static final String UPDATE_SEEK_BAR = "UPDATE_SEEK_BAR";
    public static final String UPDATE_EQUALIZER_DIALOG = "UPDATE_EQUALIZER_DIALOG";

    private final MainActivity mAppCompatActivity;

    public ViewUpdaterReceiver(MainActivity appCompatActivity) {
        mAppCompatActivity = appCompatActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(App.TAG_APP, getClass().getSimpleName() + " - onReceive()");
        if (intent != null && intent.getAction() != null) {
            String command = intent.getAction();

            if (command.equals(UPDATE_ACTIVITY_EXIT)) {
                mAppCompatActivity.finish();
            }

            if (command.equals(UPDATE_PLAY_BUTTON)) {

            }

            if (command.equals(UPDATE_SEEK_BAR)) {

            }

            if (command.equals(UPDATE_EQUALIZER_DIALOG)) {
                if (intent.hasExtra(UPDATE_EQUALIZER_DIALOG)) {

                }
            }

        }
    }

    public static IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UPDATE_PLAY_BUTTON);
        intentFilter.addAction(UPDATE_SEEK_BAR);
        intentFilter.addAction(UPDATE_ACTIVITY_EXIT);
        intentFilter.addAction(UPDATE_EQUALIZER_DIALOG);
        return intentFilter;
    }
}