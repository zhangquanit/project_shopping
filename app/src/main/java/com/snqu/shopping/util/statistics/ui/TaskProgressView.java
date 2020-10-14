package com.snqu.shopping.util.statistics.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.SpanUtils;
import com.snqu.shopping.R;
import com.snqu.shopping.data.base.ResponseDataObject;
import com.snqu.shopping.data.home.HomeClient;
import com.snqu.shopping.util.statistics.task.DayTaskReport;
import com.snqu.shopping.util.statistics.task.NewTaskReportEntity;
import com.snqu.shopping.util.statistics.task.TaskInfo;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 任务进度
 *
 * @author 张全
 */
public class TaskProgressView extends RelativeLayout {
    private CircularProgressView progressBar;
    private TextView tv_reward;
    private TaskInfo taskInfo;

    public TaskProgressView(Context context) {
        super(context);
        init(context);
    }

    public TaskProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TaskProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context ctx) {
        LayoutInflater.from(ctx).inflate(R.layout.task_progress, this);
        progressBar = findViewById(R.id.progressbar);
        tv_reward = findViewById(R.id.reward);
    }

    @SuppressLint("CheckResult")
    public void setTaskInfo(TaskInfo info) {
        if (null == info || info.countDown <= 0 || !TextUtils.equals(info.type, "1")) {
            setVisibility(View.GONE);
            return;
        }
        taskInfo = info;
        HomeClient.getTaskXNumber(info.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseDataObject<NewTaskReportEntity>>() {
                    @Override
                    public void accept(ResponseDataObject<NewTaskReportEntity> dataObject) throws Exception {
                        if (null != dataObject.data && dataObject.data.user_status == 3) {
                            show();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    private void show() {
        setVisibility(View.VISIBLE);
        progressBar.setMax(taskInfo.countDown);

        SpannableStringBuilder stringBuilder = new SpanUtils()
                .setVerticalAlign(SpanUtils.ALIGN_BOTTOM)
                .append("+")
                .append(taskInfo.reward)
                .create();
        tv_reward.setText(stringBuilder);

        timer(taskInfo);
    }

    @SuppressLint("AutoDispose")
    private void timer(TaskInfo taskInfo) {
        disposable = Observable.intervalRange(1, taskInfo.countDown, 1, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long value) throws Exception {
                        if (stopFlag || getVisibility() != View.VISIBLE) { //不可见
                            return;
                        }
                        long left = taskInfo.countDown - value;
                        progressBar.setProgress(value);
                        if (left == 0) {
                            //接口汇报
                            DayTaskReport.watchGoodsReport((Activity) getContext(), taskInfo, TaskProgressView.this);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }

    public void onSuccess() {
        SpannableStringBuilder stringBuilder = new SpanUtils()
                .append("已获得\n").setFontSize(12, true)
                .append("+" + taskInfo.reward).setFontSize(22, true)
                .create();
        tv_reward.setText(stringBuilder);
    }

    public void onFail() {
        setVisibility(View.GONE);
    }

    public void stop() {
        stopFlag = true;
        try {
            if (null != disposable && !disposable.isDisposed()) {
                disposable.dispose();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean stopFlag;
    private Disposable disposable;
}
