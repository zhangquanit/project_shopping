package com.snqu.shopping.util.statistics.task;


import android.annotation.SuppressLint;
import android.app.Activity;

import com.android.util.log.LogUtil;
import com.snqu.shopping.data.base.ResponseDataObject;
import com.snqu.shopping.data.home.HomeClient;
import com.snqu.shopping.data.user.UserClient;
import com.snqu.shopping.data.user.entity.UserEntity;
import com.snqu.shopping.util.statistics.ui.TaskRewardDialog;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * 新手任务汇报
 *
 * @author 张全
 */
public class TaskReport {
    private static final String TAG = "TaskReport";
    private static Map<String, Boolean> reportLog = new HashMap<>();

    @SuppressLint("CheckResult")
    public static void newTaskReport(Activity context, NewTaskType newTaskType) {
        UserEntity user = UserClient.getUser();
        if (user == null) {
            return;
        }

        final String taskName = user._id + "_task_" + newTaskType.type;
        if (reportLog.containsKey(taskName) && reportLog.get(taskName)) {
            return;
        }

        HomeClient.newTaskReport(newTaskType.type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseDataObject<NewTaskReportEntity>>() {
                    @Override
                    public void accept(ResponseDataObject<NewTaskReportEntity> responseDataObject) throws Exception {
                        LogUtil.e(TAG, "新手任务 汇报成功  ");

                        NewTaskReportEntity reportEntity = responseDataObject.data;
                        if (null == reportEntity) {
                            return;
                        }
                        if (reportEntity.status == 1) {
                            TaskRewardDialog.show(context, newTaskType.title, reportEntity.x_number);
                        } else {
                            // 因为有的任务可以做多次  如果返回重复汇报 下次不再汇报
                            reportLog.put(taskName, true);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        LogUtil.e(TAG, "新手任务 汇报错误  error=" + throwable.getMessage());
                        throwable.printStackTrace();
                    }
                });
    }
}
