package com.snqu.shopping.ui.splash;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.anroid.base.BaseActivity;
import com.anroid.base.ui.StatusBar;
import com.snqu.shopping.R;
import com.snqu.shopping.util.video.MyMediaController;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 新人视频引导页面
 */
public class GuideVideoActivity extends BaseActivity {
    private VideoView textureView;
    private ProgressBar progressBar;
    private TextView tv_timer;
    private boolean isPaused;
    private boolean closeable;

    public static void start(Context ctx) {
        Intent intent = new Intent(ctx, GuideVideoActivity.class);
        ctx.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.guide_video_layout;
    }

    @Override
    public void init(Bundle savedInstanceState) {

        parseClipboard = false;
        textureView = findViewById(R.id.textureview);
        MyMediaController mc = new MyMediaController(this);
        textureView.setMediaController(mc);//设置VedioView与MediaController相关联
        progressBar = findViewById(R.id.loading_progressbar);
        tv_timer = findViewById(R.id.timer);
        tv_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
//                startMainActivity();
            }
        });
//        tv_timer.setEnabled(false);

        StatusBar.setStatusBar(this, false);

        try {
            Uri uri = Uri.parse("https://resources.xinletao.vip/p/static/video/common/start_page.mp4");
            textureView.setVideoURI(uri);

            textureView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    startMainActivity();
                }
            });
            textureView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    startMainActivity();
                    return false;
                }
            });
            textureView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    progressBar.setVisibility(View.GONE);
                }
            });
            textureView.requestFocus();
            textureView.start();
        } catch (Exception e) {
            e.printStackTrace();
            startMainActivity();
        }

//        timer();
    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (isPaused) {
                textureView.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
            startMainActivity();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        try {
            isPaused = true;
            textureView.pause();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressedSupport() {
        if (closeable) {
            startMainActivity();
        }
    }


    @SuppressLint("AutoDispose")
    private void timer() {
        Disposable subscribe = Observable.intervalRange(1, 10, 0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long value) throws Exception {
                        long left = 10 - value;
                        if (left == 0) {
                            closeable = true;
                            tv_timer.setText("跳过");
                            tv_timer.setEnabled(true);

                        } else {
                            tv_timer.setText(left + "S");
                        }

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    private void startMainActivity() {
//        UserClient.watchVideo();
//        MainActivity.start(this);
//        finish();
    }

}
