package ru.testsimpleapps.coloraudioplayer;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import ru.testsimpleapps.coloraudioplayer.managers.player.data.PlayerConfig;
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.cursor.CursorFactory;
import ru.testsimpleapps.coloraudioplayer.managers.tools.FileTool;
import ru.testsimpleapps.coloraudioplayer.managers.tools.SerializableTool;
import ru.testsimpleapps.coloraudioplayer.ui.activities.MainActivity;

import static org.acra.ReportField.ANDROID_VERSION;
import static org.acra.ReportField.APP_VERSION_CODE;
import static org.acra.ReportField.PHONE_MODEL;
import static org.acra.ReportField.STACK_TRACE;

@ReportsCrashes(customReportContent = {APP_VERSION_CODE, ANDROID_VERSION, PHONE_MODEL, STACK_TRACE},
        mailTo = "testsimpleapps@gmail.com",
        mode = ReportingInteractionMode.DIALOG,
        resToastText = R.string.acra_crush_title,
        resDialogText = R.string.acra_crush_message_text,
        resDialogIcon = android.R.drawable.ic_dialog_info,
        resDialogTitle = R.string.acra_crush_title,
        resDialogCommentPrompt = R.string.acra_crush_message_comment,
        resDialogOkToast = R.string.acra_crush_ok)

public class App extends Application {

    public static final String TAG_APP = "TAG_APP";
    public static final String PLAYER_SETTINGS = "PLAYER_SETTINGS";

    public static final int THEME_BLUE = 0;
    public static final int THEME_GREEN = 1;
    public static final int THEME_RED = 2;

    public static App sPlayerApplication;
    public static int SELECTION_ITEM_COLOR = Color.WHITE;

    private int numberTheme = 0;
    private int numberVisualizer = 0;

    private PlayerConfig mPlayerConfig;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG_APP, getClass().getSimpleName() + " - onCreate()");
//        ACRA.init(this);

        sPlayerApplication = this;
//        loadSettings();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG_APP, getClass().getSimpleName() + " - onTerminate()");
        CursorFactory.closeCursorsPlaylist();
    }

    public static App getAppContext() {
        return sPlayerApplication;
    }

    public PlayerConfig getPlayerConfig() {
        return mPlayerConfig;
    }

    public void setCustomTheme(Context context) {
        switch (App.getAppContext().getNumberTheme()) {
            case App.THEME_BLUE:
                context.setTheme(R.style.BlueTheme);
                SELECTION_ITEM_COLOR = MainActivity.getColor(this, R.color.blueHints);
                break;
            case App.THEME_GREEN:
                context.setTheme(R.style.GreenTheme);
                SELECTION_ITEM_COLOR = MainActivity.getColor(this, R.color.greenHints);
                break;
            case App.THEME_RED:
                context.setTheme(R.style.RedTheme);
                SELECTION_ITEM_COLOR = MainActivity.getColor(this, R.color.redHints);
                break;
        }
    }

    public int getColorTheme(int alpha, int red, int green, int blue) {
        switch (App.getAppContext().getNumberTheme()) {
            case App.THEME_BLUE:
                return Color.argb(alpha, red, green, 255);
            case App.THEME_GREEN:
                return Color.argb(alpha, red, 255, blue);
            case App.THEME_RED:
                return Color.argb(alpha, 255, green, blue);
            default:
                return Color.argb(alpha, red, 255, blue);
        }
    }

    public boolean saveSettings() {
        return SerializableTool.objectToFile(FileTool.getAppPath(this) + PLAYER_SETTINGS, mPlayerConfig);
    }

    public void loadSettings() {
        mPlayerConfig = (PlayerConfig) SerializableTool.fileToObject(FileTool.getAppPath(this) + PLAYER_SETTINGS);
        if (mPlayerConfig == null)
            mPlayerConfig = new PlayerConfig(false, PlayerConfig.Repeat.ALL, PlayerConfig.DEFAULT_SEEK_POSITION, null, null, (short) 0, null, (short) 0, null);
        mPlayerConfig.setPlaylist(CursorFactory.setCursorPlaylist(this, mPlayerConfig.getPlaylistId(), mPlayerConfig.getPlaylistSort()));
    }

    public int getNumberTheme() {
        return numberTheme;
    }

    public int setNumberTheme(int numberTheme) {
        return this.numberTheme = numberTheme;
    }

    public int getNumberVisualizer() {
        return numberVisualizer;
    }

    public void setNumberVisualizer(int numberVisualizer) {
        this.numberVisualizer = numberVisualizer;
    }

    public static int getSelectionItemColor() {
        return SELECTION_ITEM_COLOR;
    }
}