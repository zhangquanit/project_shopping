package com.snqu.shopping.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.util.LContext
import com.android.util.log.LogUtil
import com.blankj.utilcode.util.*
import com.google.gson.Gson
import com.snqu.shopping.R
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.DataConfig
import com.snqu.shopping.data.base.RestClient
import com.snqu.shopping.data.user.UserClient
import com.snqu.shopping.data.user.entity.PushEntity
import com.snqu.shopping.ui.main.MainActivity
import com.umeng.socialize.sina.helper.Base64
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException


object PushUtil {

    const val CHANNEL_ID = "channel_id_xlt"
    const val CHANNEL_NAME = "channel_name_xlt"
    var NOTIFICATION_ID = 100
    var mNotificationManager: NotificationManager? = null
    var mNotification: Notification? = null

    const val TYPE_OPENAPP = "openApp" // 打开App
    const val TYPE_OPENCATEGORY = "openCategory" //打开分类界面
    const val TYPE_OPENVIPTAB = "openVipTab" // 打开会员界面
    const val TYPE_OPENCOMMUNITY = "openCommunity" // 打开发圈页面
    const val TYPE_OPENUSER = "openUser" // 打开个人中心

    const val TYPE_OPENWEBVIEW = "openWebview" //打开webview网页
    const val TYPE_OPENGOODSDETAIL = "openGoodsDetail" //打开商品详情
    const val TYPE_OPENSELFORDER = "openSelfOrder" //打开我的订单
    const val TYPE_OPENGROUPORDER = "openGroupOrder" //打开团队订单页面
    const val TYPE_OPENMYCOLLECTION = "openMyCollection" //打开我的收藏页面
    const val TYPE_OPENMYTEAM = "openMyTeam" //打开我的团队页面
    const val TYPE_OPENINCOMEREPORT = "openIncomeReport" //打开收益报表页面
    const val TYPE_OPENINVITATE = "openInvitate" //打开邀请好友界面
    const val TYPE_OPENSELFBALANCE = "openSelfBalance" //打开我的余额界面
    const val TYPE_OPENBINDALIPAY = "openBindAlipay" //打开绑定支付宝界面
    const val TYPE_OPENWITHDRAW = "openWithDraw" //打开提现页面
    const val TYPE_OPENACTIVITYDETAIL = "openActivityDetail";//打开活动详情


    @JvmStatic
    fun parseMessage(message: String) {
        LogUtils.d("推送消息=" + message)
        if (message.isNotEmpty()) {
            var pushEntity: PushEntity? = null
            try {
                pushEntity = Gson().fromJson(message, PushEntity::class.java)
            } catch (e: Exception) {
                LogUtils.e("推送消息解析异常=" + e.message)
            }
//            LogUtils.d("push=" + pushEntity.toString())
            if (pushEntity != null) {
                //TODO 暂时屏蔽id
                pushEntity._id = pushEntity.id
//                if (!PushClient.repeatMessage(pushEntity._id)) {
                if (mNotificationManager == null) {
                    mNotificationManager = LContext.getContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                }
                initNotification(pushEntity)
//                    PushClient.updateMessage(pushEntity)
//                }
            }

            // 单独理出来解析，是因为担心服务器的参数传输有问题，导致json解析失败，这里只需要消息id
            umengDataAdd(message)
        }
    }

    @JvmStatic
    fun umengDataAdd(message: String) {
        if (!TextUtils.isEmpty(message)) {
            var jsonObject: JSONObject? = null
            try {
                jsonObject = JSONObject(message)
                val id = jsonObject.optString("id")
                if (!TextUtils.isEmpty(id)) {
                    val userId = if (UserClient.isLogin()) UserClient.getUser()._id else ""
                    jsonObject = JSONObject()
                    jsonObject.put("isOpenNotification", NotificationManagerCompat.from(LContext.getContext()).areNotificationsEnabled())
                    jsonObject.put("device_model", DeviceUtils.getModel())
                    jsonObject.put("sdk_version", DeviceUtils.getSDKVersionCode())
                    jsonObject.put("app_version", AppUtils.getAppVersionName())
                    jsonObject.put("networkType", NetworkUtils.getNetworkType())
                    jsonObject.put("isMobileData", NetworkUtils.isMobileData())
                    jsonObject.put("local_time", TimeUtils.getNowString())
                    val content = String(Base64.encodebyte(jsonObject.toString().toByteArray()))

                    jsonObject = JSONObject()
                    jsonObject.put("id", id)
                    jsonObject.put("userId", userId)
                    jsonObject.put("content", content)

                    val requestBody: RequestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString())
                    val request: Request = Request.Builder().url(DataConfig.API_HOST + ApiHost.UMENG_DATA_REPORT)
                            .post(requestBody)
                            .build()
                    val call: Call = RestClient.getHttpClient().newCall(request)
                    call.enqueue(object : Callback {
                        override fun onFailure(call: Call?, e: IOException) {
                            e.printStackTrace()
                            LogUtil.d("日志发送失败")
                        }

                        @Throws(IOException::class)
                        override fun onResponse(call: Call?, response: Response?) {
                            LogUtil.d("日志发送成功")
                        }
                    })
                }
            } catch (e: JSONException) {
                LogUtil.e("推送日志上传解析失败")
            }
        }
    }

    private fun initNotification(pushEntity: PushEntity) {
        NOTIFICATION_ID++
        var context = LContext.getContext()
        //再构建Notification，默认跳转到首页
        var intent = Intent(context, MainActivity::class.java)
        intent.action = "push"
        intent.putExtra("push_data", pushEntity)
        val pendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        //适配安卓8.0的消息渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            channel.enableLights(false)
            channel.enableVibration(false)
            mNotificationManager!!.createNotificationChannel(channel)
        }
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, CHANNEL_ID).setContentIntent(
                pendingIntent)
                .setSmallIcon(R.drawable.umeng_push_notification_default_small_icon)
                .setContentTitle(pushEntity.title)
                .setContentText(pushEntity.content)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
        mNotification = builder.build()
        mNotificationManager!!.notify(NOTIFICATION_ID, mNotification)
    }


}