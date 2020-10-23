package com.herewhite.demo.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.sdk.android.httpdns.HttpDns;
import com.alibaba.sdk.android.httpdns.HttpDnsService;
import com.google.gson.Gson;
import com.herewhite.demo.DemoAPI;
import com.herewhite.demo.DemoAPIv5;
import com.herewhite.demo.R;
import com.herewhite.demo.StartActivity;
import com.herewhite.demo.WhiteWebViewClient;
import com.herewhite.demo.adapter.PageSelectAdapter;
import com.herewhite.demo.adapter.SpeedSelectAdapter;
import com.herewhite.demo.utils.ColorUtil;
import com.herewhite.demo.utils.CommonUtil;
import com.herewhite.demo.utils.SelectUtil;
import com.herewhite.demo.widget.NoScrollGridView;
import com.herewhite.sdk.Logger;
import com.herewhite.sdk.Player;
import com.herewhite.sdk.PlayerEventListener;
import com.herewhite.sdk.WhiteSdk;
import com.herewhite.sdk.WhiteSdkConfiguration;
import com.herewhite.sdk.WhiteboardView;
import com.herewhite.sdk.domain.Appliance;
import com.herewhite.sdk.domain.MemberState;
import com.herewhite.sdk.domain.PlayerConfiguration;
import com.herewhite.sdk.domain.PlayerPhase;
import com.herewhite.sdk.domain.PlayerState;
import com.herewhite.sdk.domain.PlayerTimeInfo;
import com.herewhite.sdk.domain.Promise;
import com.herewhite.sdk.domain.SDKError;
import com.herewhite.sdk.domain.Scene;
import com.herewhite.sdk.domain.UrlInterrupter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class WhiteReplayActivity extends AppCompatActivity implements PlayerEventListener, View.OnClickListener {

    protected WhiteboardView whiteboardView;
    @Nullable
    protected Player player;
    Gson gson;
    protected boolean mUserIsSeeking;
    protected SeekBar mSeekBar;
    private TextView mTvTime;
    private TextView mTvSpeed;
    private ImageView mPlayAction;
    private View mClose;
    private LinearLayout mShowViewRe;

    private final String TAG = "player";
    private PlayerPhase mLastPlayerPhase = PlayerPhase.waitingFirstFrame;
    private float mLastSpeed = 1f;
    private DemoAPIv5 demoAPI = DemoAPIv5.get();

    public WhiteReplayActivity() {
        mUserIsSeeking = false;
        gson = new Gson();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_white_replay);
        mSeekBar = findViewById(R.id.player_seek_bar);
        whiteboardView = findViewById(R.id.white);
        mTvTime = findViewById(R.id.time);
        mTvSpeed = findViewById(R.id.play_speed);
        mPlayAction = findViewById(R.id.play_action);
        mClose = findViewById(R.id.close);
        mShowViewRe = findViewById(R.id.showview_group);
        SelectUtil.setSelect(mClose);
        SelectUtil.setSelect(mTvSpeed);
        SelectUtil.setSelect(mPlayAction);
        mClose.setOnClickListener(this);
        mTvSpeed.setOnClickListener(this);
        mPlayAction.setOnClickListener(this);



        WebView.setWebContentsDebuggingEnabled(true);
        whiteboardView.setBackgroundColor(0);
        whiteboardView.getBackground().setAlpha(0);
        //是否开启 httpDns
        useHttpDnsService(false);
        setupPlayer();
    }

    protected void setupPlayer() {
        Intent intent = getIntent();
        final String uuid = intent.getStringExtra(JoinRoomActivity.EXTRA_MESSAGE);
        final String roomToken = intent.getStringExtra(JoinRoomActivity.EXTRA_MESSAGE_ROOMTOKEN);
        initPlayer(uuid, roomToken);
    }

    protected void useHttpDnsService(boolean use) {
        if (use) {
            // 阿里云 httpDns 替换
            HttpDnsService httpDns = HttpDns.getService(getApplicationContext(), "188301");
            httpDns.setPreResolveHosts(new ArrayList<>(
                    Arrays.asList("expresscloudharestoragev2.herewhite.com", "cloudharev2.herewhite.com",
                            "scdncloudharestoragev3.herewhite.com", "cloudcapiv4.herewhite.com")));
            whiteboardView.setWebViewClient(new WhiteWebViewClient(httpDns));
        }
    }

    //region Menu Item
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.replayer_command, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
    }

    @SuppressLint("SourceLockedOrientationActivity")
    public void orientation(MenuItem item) {
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            WhiteReplayActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            WhiteReplayActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    public void getTimeInfo(MenuItem item) {
        Log.i(TAG, gson.toJson(player.getPlayerTimeInfo()));
    }

    public void getPlayState(MenuItem item) {
        Log.i(TAG, gson.toJson(player.getPlayerState()));
    }

    public void getPhase(MenuItem item) {
        Log.i(TAG, gson.toJson(player.getPlayerPhase()));
    }

    //endregion

    //region Play Action
    protected void play() {
        if (player != null) {
            player.play();
            mSeekBarUpdateHandler.removeCallbacks(mUpdateSeekBar);
            mSeekBarUpdateHandler.postDelayed(mUpdateSeekBar, 100);
        }
    }

    protected void pause() {
        if (player != null) {
            player.pause();
            mSeekBarUpdateHandler.removeCallbacks(mUpdateSeekBar);
        }
    }

    protected void seek(Long time, TimeUnit timeUnit) {
        if (player != null) {
            long scheduleTime = TimeUnit.MILLISECONDS.convert(time, timeUnit);
            player.seekToScheduleTime(scheduleTime);
        }
    }

    protected void seek(float progress) {
        if (player != null && player.getPlayerPhase() != PlayerPhase.waitingFirstFrame) {
            PlayerTimeInfo timeInfo = player.getPlayerTimeInfo();
            long time = (long) (progress * timeInfo.getTimeDuration());
            seek(time, TimeUnit.MILLISECONDS);
            Log.i(TAG, "seek: " + time + " progress: " + playerProgress());
            mSeekBar.setProgress((int) playerProgress());
            mSeekBarUpdateHandler.removeCallbacks(mUpdateSeekBar);
            mSeekBarUpdateHandler.postDelayed(mUpdateSeekBar, 100);
        }
    }
    //endregion

    //region button action

    void enableBtn() {
        findViewById(R.id.button_play).setEnabled(true);
        findViewById(R.id.button_pause).setEnabled(true);
        findViewById(R.id.button_reset).setEnabled(true);
    }

    public void play(android.view.View button) {
        play();
    }

    public void pause(android.view.View button) {
        pause();
    }

    public void reset(android.view.View button) {
        seek(0L);
    }

    //endregion

    //region seekBar
    protected Handler mSeekBarUpdateHandler = new Handler();
    protected Runnable mUpdateSeekBar = new Runnable() {
        @Override
        public void run() {
            if (mUserIsSeeking) {
                return;
            }
            float progress = playerProgress();
            if (player.getPlayerPhase() == PlayerPhase.playing) {
                Log.v(TAG, "progress: " + progress);
                mSeekBar.setProgress((int) progress);
                updateTime();
            }

            mSeekBarUpdateHandler.postDelayed(this, 100);
        }
    };

    private void updateTime() {
        if (player == null || player.getPlayerPhase() == PlayerPhase.waitingFirstFrame) {
            return;
        }
        PlayerTimeInfo timeInfo = player.getPlayerTimeInfo();
        float progress = Float.valueOf(timeInfo.getScheduleTime()) / timeInfo.getTimeDuration() * 100.f;
        Log.d(TAG, "updateTime:" + timeInfo.getScheduleTime() + "," + timeInfo.getTimeDuration());
        String time = timeParse(timeInfo.getScheduleTime()) + "/" + timeParse(timeInfo.getTimeDuration());
        mTvTime.post(new Runnable() {
            @Override
            public void run() {
                mTvTime.setText(time);
            }
        });
    }

    private String timeParse(long duration) {
        if (duration % 1000 > 500) {
            duration = (duration/1000 + 1) * 1000;
        } else {
            duration = duration/1000 * 1000;
        }

        String time = "" ;
        long minute = duration / 60000 ;
        long seconds = duration % 60000 ;
        long second = Math.round((float)seconds/1000) ;
        if( minute < 10 ){
            time += "0" ;
        }
        time += minute+":" ;
        if( second < 10 ){
            time += "0" ;
        }
        time += second ;
        return time ;
    }

    protected void setupSeekBar() {
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int userSelectedPosition = 0;

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mUserIsSeeking = true;
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    userSelectedPosition = progress;
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mUserIsSeeking = false;
                seek(userSelectedPosition / 100f);
            }
        });
    }

    //endregion

    protected void initPlayer(String uuid, String roomToken) {
        WhiteSdk whiteSdk = new WhiteSdk(whiteboardView, WhiteReplayActivity.this,
                new WhiteSdkConfiguration(demoAPI.getAppIdentifier(), true),
                new UrlInterrupter() {
            @Override
            public String urlInterrupter(String sourceUrl) {
                return sourceUrl;
            }
        });

        PlayerConfiguration playerConfiguration = new PlayerConfiguration(uuid, roomToken);
        // 只回放 60 秒。如果时间太长，seek bar 进度条移动不明显。
//        playerConfiguration.setDuration(60000L);

        // 如果只想实现部分 PlayerEventListener 可以使用 AbstractPlayerEventListener，替换其中想实现的方法
        whiteSdk.createPlayer(playerConfiguration, this, new Promise<Player>() {
            @Override
            public void then(Player wPlayer) {
                player = wPlayer;
                setupSeekBar();
                wPlayer.seekToScheduleTime(0);
                wPlayer.play();
                mSeekBarUpdateHandler.postDelayed(mUpdateSeekBar, 100);
                enableBtn();
            }

            @Override
            public void catchEx(SDKError t) {
                Logger.error("create player error, ", t);
                alert("创建回放失败", t.getJsStack());
            }
        });
    }

    //region PlayerEventListener
    @Override
    public void onPhaseChanged(PlayerPhase phase) {
        Log.i(TAG, "onPhaseChanged: " + phase);
        mLastPlayerPhase = phase;
        if (mLastPlayerPhase == PlayerPhase.playing) {
            mPlayAction.setImageDrawable(getResources().getDrawable(R.mipmap.video_pause));
        } else {
            mPlayAction.setImageDrawable(getResources().getDrawable(R.mipmap.video_play));
        }
        showToast(gson.toJson(phase));
    }

    @Override
    public void onLoadFirstFrame() {
        Log.i(TAG, "onLoadFirstFrame: ");
        showToast("onLoadFirstFrame");
    }

    @Override
    public void onSliceChanged(String slice) {
        //一般不需要实现
    }

    @Override
    public void onPlayerStateChanged(PlayerState modifyState) {
        Log.i(TAG, "onPlayerStateChanged: " + gson.toJson(modifyState));
    }

    @Override
    public void onStoppedWithError(SDKError error) {
        Log.d(TAG, "onStoppedWithError: " + error.getJsStack());
        showToast(error.getJsStack());
    }

    @Override
    public void onScheduleTimeChanged(long time) {
        Log.v(TAG, "onScheduleTimeChanged: " + time);
    }

    @Override
    public void onCatchErrorWhenAppendFrame(SDKError error) {
        showToast(error.getJsStack());
    }

    @Override
    public void onCatchErrorWhenRender(SDKError error) {
        showToast(error.getJsStack());
    }
    //endregion

    //region private

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (player == null) {
            return;
        }
        // 横竖屏等，引起白板大小变化时，需要手动调用该 API
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                player.refreshViewSize();
            }
        }, 1000);
    }

    void showToast(Object o) {
        Toast.makeText(this, o.toString(), Toast.LENGTH_SHORT).show();
    }

    public void alert(final String title, final String detail) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = new AlertDialog.Builder(WhiteReplayActivity.this).create();
                alertDialog.setTitle(title);
                alertDialog.setMessage(detail);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
                alertDialog.show();
            }
        });
    }

    float playerProgress() {
        if (player == null || player.getPlayerPhase() == PlayerPhase.waitingFirstFrame) {
            return 0;
        }
        PlayerTimeInfo timeInfo = player.getPlayerTimeInfo();
        float progress = Float.valueOf(timeInfo.getScheduleTime()) / timeInfo.getTimeDuration() * 100.f;
        return progress;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.play_action:
                if (player == null) {
                    return;
                }
                if (mLastPlayerPhase == PlayerPhase.playing) {
                    pause();
                } else {
                    play();
                }
                break;
            case R.id.play_speed:
                if (player == null) {
                    return;
                }
                showViewSpeed();
                break;
            case R.id.close:
                finish();
                break;
        }
    }

    private void showViewSpeed() {
        mShowViewRe.removeAllViews();
        View view = LayoutInflater.from(this).inflate(R.layout.layout_showview_speed, null);

        View viewBg = view.findViewById(R.id.showview_bg);
        viewBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mShowViewRe.removeAllViews();
            }
        });

        NoScrollGridView speedList = view.findViewById(R.id.showview_speed_list);
        SpeedSelectAdapter speedSelectAdapter = new SpeedSelectAdapter(this);
        speedSelectAdapter.setListener(new SpeedSelectAdapter.OnSpeedItemClick() {
            @Override
            public void onSpeedClick(int position) {
                //设置播放速度
                mLastSpeed = chagePosition2speed(position);
                if (player != null) {
                    player.setPlaybackSpeed(mLastSpeed);
                    mTvSpeed.setText(getResources().getStringArray(R.array.speed_arr)[position]);
                }
                mShowViewRe.removeAllViews();
            }
        });
        speedSelectAdapter.setList(getResources().getStringArray(R.array.speed_arr));
        speedSelectAdapter.setIndex(chageSpeed2position(mLastSpeed));
        speedList.setAdapter(speedSelectAdapter);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mShowViewRe.addView(view, params);
    }


    private int chageSpeed2position(float speed) {
        float[] speedlist = new float[] {2.0f,1.5f,1.25f,1.0f,0.75f,0.5f};
        for (int i = 0; i < speedlist.length; ++i) {
            if (speed == speedlist[i]) {
                return i;
            }
        }
        return 3;
    }
    private float chagePosition2speed(int position) {
        float[] speedlist = new float[] {2.0f,1.5f,1.25f,1.0f,0.75f,0.5f};
        if (position >=0 && position <speedlist.length){
            return speedlist[position];
        }
        return 1.0f;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        whiteboardView.removeAllViews();
        whiteboardView.destroy();
    }
}
