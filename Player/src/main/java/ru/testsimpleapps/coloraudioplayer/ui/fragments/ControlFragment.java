package ru.testsimpleapps.coloraudioplayer.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ru.testsimpleapps.coloraudioplayer.R;
import ru.testsimpleapps.coloraudioplayer.managers.player.AudioPlayer;
import ru.testsimpleapps.coloraudioplayer.managers.player.data.PlayerConfig;
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.IPlaylist;
import ru.testsimpleapps.coloraudioplayer.managers.player.playlist.cursor.CursorFactory;
import ru.testsimpleapps.coloraudioplayer.managers.tools.PreferenceTool;
import ru.testsimpleapps.coloraudioplayer.managers.tools.TextTool;
import ru.testsimpleapps.coloraudioplayer.managers.tools.TimeTool;
import ru.testsimpleapps.coloraudioplayer.service.PlayerService;


public class ControlFragment extends BaseFragment implements SeekBar.OnSeekBarChangeListener {

    public static final String TAG = ControlFragment.class.getSimpleName();
    private static final String NOTES = " ♫ ♪ ♭ ♩";
    private static final int MAX_SEEK_POSITION = 1000;

    protected Unbinder mUnbinder;

    /*
    * Controls
    * */
    @BindView(R.id.control_play_pause)
    protected ImageButton mPlayPauseButton;
    @BindView(R.id.control_expand)
    protected ImageButton mExpandButton;
    @BindView(R.id.control_random)
    protected ImageButton mRandomButton;
    @BindView(R.id.control_previous)
    protected ImageButton mPreviousButton;
    @BindView(R.id.control_next)
    protected ImageButton mNextButton;
    @BindView(R.id.control_repeat)
    protected ImageButton mRepeatButton;

    /*
    * Seeker
    * */
    @BindView(R.id.control_seek_position)
    protected SeekBar mSeekBar;

    /*
    * Info
    * */
    @BindView(R.id.control_track_name)
    protected TextView mTrackNameTextView;
    @BindView(R.id.control_number_tracks)
    protected TextView mTrackNumberTextView;
    @BindView(R.id.control_position_time)
    protected TextView mCurrentTimeTextView;
    @BindView(R.id.control_total_time)
    protected TextView mTotalTimeTextView;
    @BindView(R.id.control_time_layout)
    protected LinearLayout mTimeLayout;

    private boolean mIsTouchSeekBar = false;
    private int mDuration = 0;

    public static ControlFragment newInstance() {
        ControlFragment fragment = new ControlFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_control, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        init(savedInstanceState);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver, getIntentFilter());
        return view;
    }

    @Override
    public void onDestroyView() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mIsTouchSeekBar = true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (mDuration > 0) {
            final int step = mDuration / MAX_SEEK_POSITION;
            mCurrentTimeTextView.setText(TimeTool.getDuration(step * seekBar.getProgress()));
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mIsTouchSeekBar = false;
        if (mDuration > 0) {
            final int step = mDuration / MAX_SEEK_POSITION;
            PlayerService.sendCommandSeek(step * seekBar.getProgress());
        }
    }

    @OnClick(R.id.control_expand)
    protected void onExpandClick() {
        final boolean isExpand = !(mTimeLayout.getVisibility() == View.VISIBLE);
        setPartPanelVisibility(isExpand);
        PreferenceTool.getInstance().setControlPanelExpand(isExpand);
    }

    @OnClick(R.id.control_play_pause)
    protected void onPlayPauseClick() {
        PlayerService.sendCommandPlayPause();
    }

    @OnClick(R.id.control_repeat)
    protected void onRepeatClick() {
        setRepeatButton(PlayerConfig.getInstance().setRepeat());
    }

    @OnClick(R.id.control_random)
    protected void onRandomClick() {
        setRandomButton(PlayerConfig.getInstance().setRandom());
    }

    @OnClick(R.id.control_next)
    protected void onNextClick() {
        PlayerService.sendCommandNext();
    }

    @OnClick(R.id.control_previous)
    protected void onPreviousClick() {
        PlayerService.sendCommandPrevious();
    }

    private void init(final Bundle savedInstanceState) {
        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBar.setMax(MAX_SEEK_POSITION);

        setPartPanelVisibility(PreferenceTool.getInstance().getControlPanelExpand());
        setRepeatButton(PlayerConfig.getInstance().getRepeat());
        setRandomButton(PlayerConfig.getInstance().isRandom());
        setTrackPosition(CursorFactory.getInstance().position() + 1, CursorFactory.getInstance().size());

        if (PlayerConfig.getInstance().getPlaylistId() == IPlaylist.ERROR_CODE) {
            setTrackName(getRunningString(NOTES, NOTES, mTrackNameTextView));
        } else {
            setTrackName(getRunningString(CursorFactory.getInstance().getTrackName(), " ", mTrackNameTextView));
        }

        PlayerService.sendCommandControlCheck();
    }

    private void setPartPanelVisibility(final boolean isVisible) {
        if (isVisible) {
            mExpandButton.setImageResource(R.drawable.image_expand_inactive);
            mTimeLayout.setVisibility(View.VISIBLE);
            mTrackNameTextView.setVisibility(View.VISIBLE);
        } else {
            mExpandButton.setImageResource(R.drawable.image_expand_active);
            mTimeLayout.setVisibility(View.GONE);
            mTrackNameTextView.setVisibility(View.GONE);
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                final String action = intent.getAction();
                if (action != null) {

                    // Update image_play/image_pause button
                    if (action.equals(PlayerService.RECEIVER_PLAY_PAUSE)) {
                        if (intent.hasExtra(PlayerService.EXTRA_PLAY_PAUSE)) {
                            setPlayPauseButton(intent.getBooleanExtra(PlayerService.EXTRA_PLAY_PAUSE, false));
                        }
                    }

                    // Update SeekBar
                    if (action.equals(PlayerService.RECEIVER_PLAY_PROGRESS)) {
                        if (intent.hasExtra(PlayerService.EXTRA_PLAY_PROGRESS) && intent.hasExtra(PlayerService.EXTRA_PLAY_DURATION)) {
                            final int progress = intent.getIntExtra(PlayerService.EXTRA_PLAY_PROGRESS, AudioPlayer.MIN_SEEK_POSITION);
                            final int duration = intent.getIntExtra(PlayerService.EXTRA_PLAY_DURATION, AudioPlayer.MIN_SEEK_POSITION);
                            setSeekBarProgress(progress, duration);
                            setTrackDuration(progress, duration);
                        }
                    }

                    // Update track position
                    if (action.equals(PlayerService.RECEIVER_PLAYLIST_TRACKS)) {
                        if (intent.hasExtra(PlayerService.EXTRA_PLAYLIST_POSITION) && intent.hasExtra(PlayerService.EXTRA_PLAYLIST_TOTAL)) {
                            setTrackPosition(intent.getLongExtra(PlayerService.EXTRA_PLAYLIST_POSITION, 0L),
                                    intent.getLongExtra(PlayerService.EXTRA_PLAYLIST_TOTAL, 0L));
                        }
                    }

                    // Update track name
                    if (action.equals(PlayerService.RECEIVER_PLAYLIST_NAME)) {
                        if (intent.hasExtra(PlayerService.EXTRA_PLAYLIST_NAME)) {
                            setTrackName(getRunningString(intent.getStringExtra(PlayerService.EXTRA_PLAYLIST_NAME), " ", mTrackNameTextView));
                        }
                    }
                }
            }
        }
    };

    private String getRunningString(final String text, final String symbols, final TextView textView) {
        final int widthScreen = getDisplayWidth();
        final int widthText = TextTool.measureTextWidth(text, textView);
        final int widthSymbols = TextTool.measureTextWidth(symbols, textView);
        final int delta = widthScreen - widthText;
        final StringBuilder runningString  = new StringBuilder(text);

        if (delta >= 0) {
            final int n = delta / widthSymbols + 5;
            for(int i = 0; i < n + 5; i++){
                runningString.append(symbols);
            }
        }

        return runningString.toString();
    }

    private void setTrackName(final String name) {
        if (mTrackNameTextView != null) {
            mTrackNameTextView.setText(name);
            mTrackNameTextView.setSelected(true);
            mTrackNameTextView.requestFocus();
        }
    }

    private void setTrackPosition(final long current, final long total) {
        if (mTrackNumberTextView != null) {
            mTrackNumberTextView.setText("" + (current > 0? current : 0) + "/" + total);
        }
    }

    private void setSeekBarProgress(final int progress, final int duration) {
        if (mSeekBar != null && mCurrentTimeTextView != null && !mIsTouchSeekBar) {
            mDuration = duration;
            final float part = (float) progress / (float) mDuration;
            mSeekBar.setProgress((int) (part * MAX_SEEK_POSITION));
            mCurrentTimeTextView.setText(TimeTool.getDuration(progress));
        }
    }

    private void setTrackDuration(final int progress, final int duration) {
        if (mCurrentTimeTextView != null && mTotalTimeTextView != null) {
            mTotalTimeTextView.setText(TimeTool.getDuration(duration));
            if (!mIsTouchSeekBar) {
                mCurrentTimeTextView.setText(TimeTool.getDuration(progress));
            }
        }
    }

    private void setPlayPauseButton(final boolean isPlay) {
        if (mPlayPauseButton != null) {
            if (isPlay) {
                mPlayPauseButton.setImageResource(R.drawable.image_pause);
            } else {
                mPlayPauseButton.setImageResource(R.drawable.image_play);
            }
        }
    }

    private void setRepeatButton(final PlayerConfig.Repeat repeat) {
        switch (repeat) {
            case ALL:
                mRepeatButton.setImageResource(R.drawable.image_repeat_active);
                break;
            case ONE:
                mRepeatButton.setImageResource(R.drawable.image_repeat_active_one);
                break;
            case NONE:
                mRepeatButton.setImageResource(R.drawable.image_repeat_inactive);
                break;
        }
    }

    private void setRandomButton(final boolean isRandom) {
        if (isRandom) {
            mRandomButton.setImageResource(R.drawable.image_shuffle_active);
        } else {
            mRandomButton.setImageResource(R.drawable.image_shuffle_inactive);
        }
    }

    private IntentFilter getIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(PlayerService.RECEIVER_PLAY_PAUSE);
        intentFilter.addAction(PlayerService.RECEIVER_PLAY_PROGRESS);
        intentFilter.addAction(PlayerService.RECEIVER_PLAYLIST_TRACKS);
        intentFilter.addAction(PlayerService.RECEIVER_PLAYLIST_NAME);
        return intentFilter;
    }

}
