package com.snqu.shopping.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.android.util.ext.ToastUtil;
import com.android.util.log.LogUtil;
import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import com.kd.charge.entrance.KdCharge;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.data.base.ResponseDataObject;
import com.snqu.shopping.data.goods.GoodsClient;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.home.entity.PlateCategoryEntity;
import com.snqu.shopping.data.home.entity.PlateCode;
import com.snqu.shopping.data.user.UserClient;
import com.snqu.shopping.data.user.entity.UserEntity;
import com.snqu.shopping.ui.bringgood.frag.BringGoodsFrag;
import com.snqu.shopping.ui.goods.GoodsDetailActivity;
import com.snqu.shopping.ui.goods.fragment.GoodRecmMySelfFrag;
import com.snqu.shopping.ui.login.LoginFragment;
import com.snqu.shopping.ui.main.MainActivity;
import com.snqu.shopping.ui.main.frag.ActivityDetailFrag;
import com.snqu.shopping.ui.main.frag.FreeShippingFrag;
import com.snqu.shopping.ui.main.frag.MarketDetailFrag;
import com.snqu.shopping.ui.main.frag.WebViewFrag;
import com.snqu.shopping.ui.main.frag.channel.ChannelListFrag;
import com.snqu.shopping.ui.main.frag.channel.plate.CommonPlateFrag;
import com.snqu.shopping.ui.main.frag.channel.reds.frag.RedsFrag;
import com.snqu.shopping.ui.main.frag.collection.CollectionFrag;
import com.snqu.shopping.ui.main.frag.search.SearchFrag;
import com.snqu.shopping.ui.main.scan.ScanActivity;
import com.snqu.shopping.ui.mall.goods.ShopGoodsDetailActivity;
import com.snqu.shopping.ui.mine.fragment.InvitateFrag;
import com.snqu.shopping.ui.mine.fragment.MeWechatFragment;
import com.snqu.shopping.ui.mine.fragment.MyTeamFragment;
import com.snqu.shopping.ui.mine.fragment.SelfBalanceFragment;
import com.snqu.shopping.ui.mine.fragment.WithdrawalFragment;
import com.snqu.shopping.ui.order.OrderActivity;
import com.snqu.shopping.ui.order.fragment.FindOrderFragment;
import com.snqu.shopping.ui.video.PlayerVideoActivity;
import com.snqu.shopping.util.location.LocationEntity;
import com.snqu.shopping.util.location.LocationUtil;
import com.snqu.shopping.util.statistics.task.TaskInfo;

import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import common.widget.dialog.loading.LoadingDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author 张全
 */
public class DispatchUtil {
    public static TaskInfo taskInfo;

    /**
     * 跳转页面
     */
    public static void goToPage(Activity ctx, String json) throws Exception {
        LogUtil.d("DispatchUtil", "请求参数 json=" + json);
        JSONObject jsonObject = new JSONObject(json);
        String page = jsonObject.optString("page");
        JSONObject dataJson = jsonObject.optJSONObject("data");
        //任务
        try {
            if (null != dataJson) {
                JSONObject taskInfoStr = dataJson.optJSONObject("taskInfo");
                if (null != taskInfoStr) {
                    TaskInfo info = new TaskInfo();
                    info.id = taskInfoStr.optString("id");
                    info.countDown = taskInfoStr.optLong("countDown");
                    info.reward = taskInfoStr.optString("reward");
                    info.type = taskInfoStr.optString("type");
                    taskInfo = info;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        UserEntity user = UserClient.getUser();
        switch (page) {
//            case "kuandian": //快电
//
//                break;
            case "HomePage": //首页
                MainActivity.start(ctx);
                break;
            case "WebViewPage":
                String url = dataJson.optString("url");
                if (!TextUtils.isEmpty(url)) {
                    WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
                    webViewParam.url = url;
                    webViewParam.taskInfo = taskInfo;
                    WebViewFrag.start(ctx, webViewParam);
                }
                break;
            case "GoodsDetailPage": //商品详情页
                String goodId = dataJson.optString("goods_id");
                String item_source = dataJson.optString("item_source");
                String item_id = dataJson.optString("item_id");
                if (!TextUtils.isEmpty(goodId)) {
                    GoodsDetailActivity.Companion.start(ctx, goodId, item_source, item_id);
                }
                break;
//            case "activity": //淘宝、天猫、京东活动
//                AdvertistEntity advertistEntity = new AdvertistEntity();
//                advertistEntity.platform = dataJson.optString("platform").toUpperCase();
//                advertistEntity.url = dataJson.optString("url");
//                advertistEntity.isAuth = dataJson.optInt("isAuth", -1);
//                CommonUtil.startWebFrag(ctx, advertistEntity);
//                break;
            case "VipPage": //会员页面
                MainActivity.startForPage(ctx, 2, taskInfo);
                break;
            case "InvitatePage": //邀请页面
                if (null == user) { //先登录
                    LoginFragment.Companion.start(ctx);
                } else {
                    InvitateFrag.start(ctx, taskInfo);
                }
                break;
            case "me": //个人中心
                MainActivity.startForPage(ctx, 4, taskInfo);
                break;
            case "earning": //收益报表
                CommonUtil.jumpToEarningPage(ctx);
                break;
            case "teamOrder": //团队订单
                if (null == user) { //先登录
                    LoginFragment.Companion.start(ctx);
                } else {
                    OrderActivity.Companion.start(ctx, 0, false);
                }
                break;
            case "selfOrder": //我的订单
                if (null == user) { //先登录
                    LoginFragment.Companion.start(ctx);
                } else {
                    OrderActivity.Companion.start(ctx, 0, true);
                }
                break;
            case "myteam": //我的团队
                if (null == user) { //先登录
                    LoginFragment.Companion.start(ctx);
                } else {
                    MyTeamFragment.start(ctx);
                }
                break;
            case "balance": //我的余额
                if (null == user) { //先登录
                    LoginFragment.Companion.start(ctx);
                } else {
                    SelfBalanceFragment.Companion.start(ctx);
                }
                break;
            case "withdraw": //提现
                if (null == user) { //先登录
                    LoginFragment.Companion.start(ctx);
                } else {
                    WithdrawalFragment.Companion.start(ctx);
                }
                break;
            case "findOrder"://找回订单
                if (null == user) { //先登录
                    LoginFragment.Companion.start(ctx);
                } else {
                    FindOrderFragment.Companion.start(ctx);
                }
                break;
            case "sendCircle": //发圈
                MainActivity.startForPage(ctx, 3, taskInfo);
                break;
            case "searchPage": //搜索页面
                String itemSource = null;
                String search = null;
                if (null != dataJson) {
                    itemSource = dataJson.optString("item_source");
                    search = dataJson.optString("search");
                }
                SearchFrag.startFromSearch(ctx, search, itemSource);
                break;
            case "scan": //扫一扫
                ScanActivity.Companion.start(ctx);
                break;
            case "collection": //收藏页面
                if (null == user) { //先登录
                    LoginFragment.Companion.start(ctx);
                } else {
                    CollectionFrag.start(ctx);
                }
                break;
            case "platePage": //跳转到板块页面
                PlateCategoryEntity plateInfo = new Gson().fromJson(dataJson.toString(), PlateCategoryEntity.class);
                String dev_code = plateInfo.dev_code;
                if (TextUtils.equals(dev_code, PlateCode.RED)) { //红人街
                    RedsFrag.start(ctx);
                    return;
                }
                int type = 0;
                if (TextUtils.equals(dev_code, PlateCode.COUPON)) { //大额券
                    type = 3;
                } else if (TextUtils.equals(dev_code, PlateCode.HOT_STYLE)) { //实时爆款
                    type = 2;
                } else if (TextUtils.equals(dev_code, PlateCode.P_99)) //9.9
                {
                    type = 1;
                } else if (TextUtils.equals(dev_code, PlateCode.N_99)) {
                    type = 10;
                }
                if (type > 0) {
                    if (type == 10) {
                        FreeShippingFrag.start(ctx);
                    } else {
                        ChannelListFrag.start(ctx, plateInfo, type); //跳转到大额券、实时爆款、9.9
                    }
                } else {
                    if (plateInfo.is_dev == 0) { //非人工编辑板块  统一模板
                        if (plateInfo.ch_set_icon == 1) { //图片分类，用全球购页面样式
                            MarketDetailFrag.start(ctx, plateInfo);
                        } else {  //文字分类，列表用首页列表样式
                            CommonPlateFrag.start(ctx, plateInfo);
                        }
                    }
                }
//                LogUtils.e("platePage="+dataJson);
//                try {
//                    AdvertistEntity advertistEntity = new Gson().fromJson(dataJson.toString(), AdvertistEntity.class);
//                    RecommendFrag.jumpPlatePage(ctx, advertistEntity);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                break;
            case "wechatPage": //绑定微信页面
                if (null == user) { //先登录
                    LoginFragment.Companion.start(ctx);
                } else {
                    MeWechatFragment.start(ctx);
                }
                break;
            case "DYTV":
                BringGoodsFrag.start(ctx);
                break;
            case "dyPage":  //抖音券
                BringGoodsFrag.start(ctx);
                break;
            case "VIDEO":
                PlayerVideoActivity.Companion.start(ctx, dataJson.optString("url"));
                break;
            case "G":
                GoodsDetailActivity.Companion.start(ctx, dataJson.optString("url"), dataJson.optString("item_source"), "");
                break;
            case "ACT":
                ActivityDetailFrag.start(ctx, dataJson.optString("name"), dataJson.optString("url"));
                break;
            case "videoPlayerPage": //播放页面
                String playUrl = dataJson.optString("url");
                if (!TextUtils.isEmpty(playUrl)) {
                    PlayerVideoActivity.Companion.start(ctx, playUrl);
                } else {
                    ToastUtil.show("播放url为空");
                }
                break;
            case "actDetail": //活动详情
                String code = dataJson.optString("code");
                if (null == user) { //先登录
                    LoginFragment.Companion.start(ctx);
                } else {
                    ActivityDetailFrag.start(ctx, null, code);
                }
                break;
            case "openApplication": //第三方app
                String nativeUrl = dataJson.optString("nativeUrl");
                String H5Url = dataJson.optString("H5Url");
                if (!TextUtils.isEmpty(nativeUrl)) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.setData(Uri.parse(nativeUrl));
                        ctx.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (!TextUtils.isEmpty(H5Url)) {
                            WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
                            webViewParam.url = H5Url;
                            WebViewFrag.start(ctx, webViewParam);
                        }
                    }
                } else if (!TextUtils.isEmpty(H5Url)) {
                    WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
                    webViewParam.url = H5Url;
                    WebViewFrag.start(ctx, webViewParam);
                }
                break;
            case "CommunityGoodsRecm": //我的推荐
                if (null == user) { //先登录
                    LoginFragment.Companion.start(ctx);
                } else {
                    GoodRecmMySelfFrag.start(ctx);
                }
                break;
            case "kuaidian": //快电
                if (null == user) { //先登录
                    LoginFragment.Companion.start(ctx);
                } else {
                    //泛型限定
                    LocationEntity location = LocationUtil.getLocation();
                    String latitude = "";
                    String longitude = "";
                    if (null != location) {
                        latitude = location.latitude;
                        longitude = location.longitude;
                    }
                    HashMap<String, String> hashMap = new HashMap<>(16);
                    hashMap.put("platformType", "9000106");//9000106  920072701
                    LogUtils.e("phone=" + user.phone);
                    hashMap.put("platformCode", user.phone);
                    hashMap.put("latitude", latitude);
                    hashMap.put("longitude", longitude);
                    //打开SDK主⻚
                    KdCharge.getInstance().startService(hashMap);
                }
                break;
            case "privilegePage": //特权页面
                MainActivity.startForPage(ctx, 1);
                break;
            case "mallPage": //直营页面
                MainActivity.startForPage(ctx, 2);
                break;
            case "mallGoodDetailPage"://直营商品详情
                if (null != dataJson) {
                    goodId = dataJson.optString("id");
                    ShopGoodsDetailActivity.start(ctx, goodId);
                }
                break;
            default:
                break;
        }
    }

    public static boolean parseGoodUrl(Context ctx, String path) {
        try {
            return parseJDGoods(ctx, path) || parseTBGoods(ctx, path) || parseTmallGoods(ctx, path) || parsePddGoods(ctx, path) || parseVipGoods(ctx, path) || parseSuningGoods(ctx, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean parseSuningGoods(Context ctx, String path) {
        String id = null;

        //
        //苏宁商品
        if (path.contains("m.suning.com/product/")) {
            id = parseSuningData(path);
            if (!TextUtils.isEmpty(id)) {
                LogUtil.d("WebViewFrag", "苏宁->解析商品item_id=" + id);
                getGoodsId(ctx, path, id, Constant.BusinessType.S);
            }
        }
        if (path.contains("product.suning.com/")) {
            id = parseSuningData(path);
            if (!TextUtils.isEmpty(id)) {
                LogUtil.d("WebViewFrag", "苏宁->解析商品item_id=" + id);
                getGoodsId(ctx, path, id, Constant.BusinessType.S);
            }
        }
        if (path.contains("sumfs.suning.com/sumis-web/staticRes/web/pgWelfare/index.html")) {
            Uri uri = Uri.parse(path);
            String vendorCode = uri.getQueryParameter("supplierCode");
            String commodityCode = uri.getQueryParameter("commodityCode");
            if (!TextUtils.isEmpty(vendorCode) || !TextUtils.isEmpty(commodityCode)) {
                id = vendorCode + "-" + commodityCode;
                LogUtil.d("WebViewFrag", "苏宁->解析商品item_id=" + id);
                getGoodsId(ctx, path, id, Constant.BusinessType.S);
            }
        }
        return !TextUtils.isEmpty(id);
    }


    private static String parseSuningData(String url) {
        if (!url.contains(".html")) {
            return null;
        }
        String id = null;
        String[] split = url.split(".html");
        if (split.length < 1) {
            return null;
        }
        String[] split2 = split[0].split("/");
        if (split2.length < 1) {
            return null;
        }
        String str2 = split2[split2.length - 1];
        String str3 = split2[split2.length - 2];
        String goods_id = str3 + "-" + str2;
        return goods_id;
    }

    /**
     * 京东商品
     */
    private static boolean parseJDGoods(Context ctx, String path) {
        String id = null;
        //京东商品
        if (path.contains("item.m.jd.com/product")
                || path.contains("item.jd.com")) {
            String subPath = path;
            if (subPath.contains("?")) {
                subPath = subPath.substring(0, subPath.indexOf("?"));
            }
            String reg = "[^0-9]";
            Pattern compile = Pattern.compile(reg);
            Matcher matcher = compile.matcher(subPath);
            id = matcher.replaceAll(" ").trim();
        } else if (path.contains("item.m.jd.com")) {
            id = Uri.parse(path).getQueryParameter("wareId");
        }

        if (!TextUtils.isEmpty(id)) {
            LogUtil.d("WebViewFrag", "京东->解析商品item_id=" + id);
            getGoodsId(ctx, path, id, Constant.BusinessType.JD);
        }
        return !TextUtils.isEmpty(id);
    }

    /**
     * 淘宝商品
     */
    private static boolean parseTBGoods(Context ctx, String path) {
        String id = null;
        //天猫
        if (path.contains("m.tb.cn")
                || path.contains("item.taobao.com")
                || path.contains("h5.m.taobao.com/awp/core/detail.htm")
                || path.contains("traveldetail.fliggy.com/item.htm")
        ) {
            id = Uri.parse(path).getQueryParameter("id");
        }

        if (path.contains("nmi.juhuasuan.com/market/ju/detail_wap.php")) {
            id = Uri.parse(path).getQueryParameter("item_id");
        }
        if (path.contains("ju.taobao.com/m/jusp/alone/detailwap/mtp.htm")) {
            id = Uri.parse(path).getQueryParameter("item_id");
        }

        if (path.contains("a.m.taobao.com/i")) {
            //a.m.taobao.com/i1234.htm
            String str = "a.m.taobao.com/i";
            id = path.substring(path.indexOf(str) + str.length(), path.indexOf(".htm"));
        }

        if (!TextUtils.isEmpty(id)) {
            LogUtil.d("WebViewFrag", "淘宝->解析商品item_id=" + id);
            getGoodsId(ctx, path, id, Constant.BusinessType.TB);
        }

        return !TextUtils.isEmpty(id);
    }

    /**
     * 天猫商品
     */
    private static boolean parseTmallGoods(Context ctx, String path) {
        String id = null;
        //天猫
        if (path.contains("detail.tmall.com/item.htm")
                || path.contains("detail.m.tmall.com/item.htm")
                || path.contains("detail.tmall.hk/hk/item.htm")
        ) {
            id = Uri.parse(path).getQueryParameter("id");
        }

        if (!TextUtils.isEmpty(id)) {
            LogUtil.d("WebViewFrag", "天猫->解析商品item_id=" + id);
            getGoodsId(ctx, path, id, Constant.BusinessType.TM);
        }
        return !TextUtils.isEmpty(id);
    }

    /**
     * 拼多多商品
     */
    private static boolean parsePddGoods(Context ctx, String path) {
        String id = null;
        if (path.contains("mobile.yangkeduo.com/duo_coupon_landing.html")
                || path.contains("mobile.yangkeduo.com/goods")
        ) {
            id = Uri.parse(path).getQueryParameter("goods_id");
        } else if (path.contains("mobile.yangkeduo.com/app.html")) {
            String launch_url = Uri.parse(path).getQueryParameter("launch_url");
            if (!TextUtils.isEmpty(launch_url)) {
                launch_url = URLDecoder.decode(launch_url);
                id = Uri.parse(launch_url).getQueryParameter("goods_id");
            }
        }

        if (!TextUtils.isEmpty(id)) {
            LogUtil.d("WebViewFrag", "拼多多->解析商品item_id=" + id);
            getGoodsId(ctx, path, id, Constant.BusinessType.PDD);
        }
        return !TextUtils.isEmpty(id);
    }

    /**
     * 唯品会商品
     */
    private static boolean parseVipGoods(Context ctx, String path) {
        String id = null;
        if (path.contains("click.union.vip.com/deeplink/showGoodsDetail")) {
            id = Uri.parse(path).getQueryParameter("pid");
        } else if (path.contains("m.vip.com/product")) {
            String reg = "(\\d+)-(\\d+)";
            Pattern compile = Pattern.compile(reg);
            Matcher matcher = compile.matcher(path);
            if (matcher.find()) {
                String value = matcher.group();
                id = value.split("-")[1];
            }
        } else if (path.contains("ms.vipstatic.com/union/deeplink/deeplink.html")) {
            String dest_url = Uri.parse(path).getQueryParameter("dest_url");
            if (!TextUtils.isEmpty(dest_url)) {
                dest_url = URLDecoder.decode(dest_url);
                String reg = "(\\d+)-(\\d+)";
                Pattern compile = Pattern.compile(reg);
                Matcher matcher = compile.matcher(dest_url);
                if (matcher.find()) {
                    String value = matcher.group();
                    id = value.split("-")[1];
                }
            }
        }

        if (!TextUtils.isEmpty(id)) {
            LogUtil.d("WebViewFrag", "唯品会->解析商品item_id=" + id);
            getGoodsId(ctx, path, id, Constant.BusinessType.V);
        }
        return !TextUtils.isEmpty(id);
    }

    @SuppressLint("CheckResult")
    private static void getGoodsId(Context ctx, String path, String itemId, String itemSource) {
        LoadingDialog loadingDialog = LoadingDialog.showDialog(ctx, "请求中...");
        GoodsClient.INSTANCE.doGoodsDetailByItemId(itemId, itemSource)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseDataObject<GoodsEntity>>() {
                    @Override
                    public void accept(ResponseDataObject<GoodsEntity> responseDataObject) throws Exception {
                        loadingDialog.dismiss();
                        if (responseDataObject.isSuccessful() && null != responseDataObject.data) {
                            GoodsEntity data = responseDataObject.data;
                            LogUtil.d("WebViewFrag", "跳转商品详情 商品id=" + data.get_id());
                            GoodsDetailActivity.start(ctx, data.get_id(), data.getItem_source(), data.getItem_id());
                        } else {
                            LogUtil.d("WebViewFrag", "商品未找到");
//                                CommonUtil.
//                            ToastUtil.show(responseDataObject.message);
                            //获取商品id失败  打开webview
                            WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
                            webViewParam.url = path;
                            webViewParam.isDetailJump = true;
                            WebViewFrag.start(ctx, webViewParam);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        loadingDialog.dismiss();
                        ToastUtil.show(R.string.net_noconnection);
                        //获取商品id失败  打开webview
//                        WebViewFrag.WebViewParam webViewParam = new WebViewFrag.WebViewParam();
//                        webViewParam.url = path;
//                        WebViewFrag.start(ctx, webViewParam);
                    }
                });
    }

}
