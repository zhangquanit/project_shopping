package com.snqu.shopping.util.statistics.task;


import android.annotation.SuppressLint;
import android.app.Activity;

import com.android.util.log.LogUtil;
import com.snqu.shopping.data.base.ResponseDataObject;
import com.snqu.shopping.data.home.HomeClient;
import com.snqu.shopping.data.user.UserClient;
import com.snqu.shopping.data.user.entity.UserEntity;
import com.snqu.shopping.util.statistics.ui.TaskProgressView;
import com.snqu.shopping.util.statistics.ui.TaskRewardDialog;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 日常任务汇报
 *
 * @author 张全
 */
public class DayTaskReport {
    private static final String TAG = "TaskReport";
//    private static Map<String, Boolean> reportLog = new HashMap<>();

    /**
     * 分享汇报
     */
    @SuppressLint("CheckResult")
    public static void shareReport(Activity context, TaskInfo taskInfo) {
        if (null == taskInfo) return;
        UserEntity user = UserClient.getUser();
        if (user == null) {
            return;
        }

//        final String taskName = user._id + taskInfo.id;
//        if (reportLog.containsKey(taskName) && reportLog.get(taskName)) {
//            return;
//        }
        HomeClient.dayTaskReport(taskInfo.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseDataObject<NewTaskReportEntity>>() {
                    @Override
                    public void accept(ResponseDataObject<NewTaskReportEntity> responseDataObject) throws Exception {
                        LogUtil.e(TAG, "日常任务 分享汇报成功  ");

                        NewTaskReportEntity reportEntity = responseDataObject.data;
                        if (null == reportEntity) {
                            return;
                        }
                        if (reportEntity.status == 1) {
                            TaskRewardDialog.show(context, "分享成功", reportEntity.x_number);
                        } else {
                            // 因为有的任务可以做多次  如果返回重复汇报 下次不再汇报
//                            reportLog.put(taskName, true); //重复汇报 记录下来  下次不再汇报
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtil.e(TAG, "日常任务  分享汇报失败 error=" + throwable.getMessage());
                        throwable.printStackTrace();
                    }
                });
    }


    /**
     * 浏览商品汇报
     *
     * @param context
     * @param taskInfo
     * @param progressView
     */
    @SuppressLint("CheckResult")
    public static void watchGoodsReport(Activity context, TaskInfo taskInfo, TaskProgressView progressView) {

        if (null == taskInfo) return;
        UserEntity user = UserClient.getUser();
        if (user == null) {
            return;
        }

//        final String taskName = user._id + taskInfo.id;
//        if (reportLog.containsKey(taskName) && reportLog.get(taskName)) {
//            return;
//        }
        HomeClient.dayTaskReport(taskInfo.id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseDataObject<NewTaskReportEntity>>() {
                    @Override
                    public void accept(ResponseDataObject<NewTaskReportEntity> responseDataObject) throws Exception {
                        LogUtil.e(TAG, "日常任务 浏览商品汇报成功  ");

                        NewTaskReportEntity reportEntity = responseDataObject.data;
                        if (null == reportEntity) {
                            if (null != progressView) {
                                progressView.onFail();
                            }
                            return;
                        }

                        if (reportEntity.status == 1) {
                            if (null != progressView) {
                                progressView.onSuccess();
                            }
                        } else {
                            // 因为有的任务可以做多次  如果返回重复汇报 下次不再汇报
//                            reportLog.put(taskName, true);

                            if (null != progressView) {
                                progressView.onFail();
                            }
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtil.e(TAG, "日常任务  浏览商品汇报失败 error=" + throwable.getMessage());
                        throwable.printStackTrace();
                        if (null != progressView) {
                            progressView.onFail();
                        }
                    }
                });
    }
}
