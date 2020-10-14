package com.snqu.shopping.data.mall;

import android.text.TextUtils;

import com.android.util.ext.SPUtil;
import com.snqu.shopping.data.ApiHost;
import com.snqu.shopping.data.DataConfig;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.base.ResponseDataObject;
import com.snqu.shopping.data.base.RestClient;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.data.home.entity.SearchSlugEntity;
import com.snqu.shopping.data.mall.entity.CommentEntity;
import com.snqu.shopping.data.mall.entity.MallBannerEntity;
import com.snqu.shopping.data.mall.entity.MallCategoryEntity;
import com.snqu.shopping.data.mall.entity.MallGoodShareInfoEntity;
import com.snqu.shopping.data.mall.entity.MallOrderDetailEntity;
import com.snqu.shopping.data.mall.entity.MallOrderEntity;
import com.snqu.shopping.data.mall.entity.MallRecommendEntity;
import com.snqu.shopping.data.mall.entity.PayDataEntity;
import com.snqu.shopping.data.mall.entity.PayResultDataEntity;
import com.snqu.shopping.data.mall.entity.ShopGoodsEntity;
import com.snqu.shopping.data.mall.entity.address.AddressEntity;
import com.snqu.shopping.ui.mall.order.helper.MallOrderType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Query;

public class MallClient {

    private static MallApi getApi() {
        return RestClient.getService(MallApi.class);
    }

    /**
     * 获取banner
     *
     * @return
     */
    public static Observable<ResponseDataObject<MallBannerEntity>> getBanner() {
        return getApi().getBanner();
    }

    /**
     * banner点击汇报
     *
     * @param _id
     * @return
     */
    public static Observable<ResponseDataObject<Object>> bannerReport(String _id) {
        return getApi().bannerReport(_id);
    }

    /**
     * 获取分类
     */
    public static Observable<ResponseDataArray<MallCategoryEntity>> getCategory() {
        return getApi().getCategory();
    }

    /**
     * 获取分类
     *
     * @return
     */
    public static Observable<ResponseDataArray<MallRecommendEntity>> getRecommend(int page) {
        return getApi().getRecommend(page, 10);
    }

    /**
     * 获取分类商品
     *
     * @return
     */
    public static Observable<ResponseDataArray<ShopGoodsEntity>> getCategeoryGoods(String _id, String search, int page) {
        return getApi().getCategeoryGoods(_id, search, page, 10);
    }

    /**
     * 获取商品详情
     *
     * @param _id
     * @return
     */
    public static Observable<ResponseDataObject<ShopGoodsEntity>> getGoodDetail(String _id) {
        return getApi().getGoodDetail(_id);
    }

    /**
     * 订单支付
     *
     * @param payDataEntity
     * @return
     */
    public static Observable<ResponseDataObject<PayResultDataEntity>> goPay(PayDataEntity payDataEntity) {
        return getApi().goPay(payDataEntity);
    }

    /**
     * 订单再次支付
     *
     * @return
     */
    public static Observable<ResponseDataObject<PayResultDataEntity>> goRePay(String _id, String addressId) {
        if (TextUtils.isEmpty(addressId)) {
            return getApi().goRePay(_id);
        } else {
            return getApi().goRePay(_id, addressId);
        }
    }

    /**
     * 分享商品
     *
     * @param _id
     * @return
     */
    public static Observable<ResponseDataObject<MallGoodShareInfoEntity>> getShareInfo(@Query("_id") String _id) {
        return getApi().getShareInfo(_id);
    }


    /**
     * 商品立即购买
     *
     * @param _id           商品ID
     * @param standard_name 规则名称
     * @param number        购买数量
     * @return
     */
    public static Observable<ResponseDataObject<ShopGoodsEntity>> orderBuy(String _id, String standard_name, int number) {
        return getApi().orderBuy(_id, standard_name, number);
    }

    /**
     * 地址列表
     *
     * @return
     */
    public static Observable<ResponseDataArray<AddressEntity>> doGetAddress() {
        return getApi().getAddress();
    }


    /**
     * 搜索关键词
     *
     * @param search
     * @return
     */
    public static Observable<ResponseDataArray<SearchSlugEntity>> searchSlugList(String search) {
        return getApi().searchSlugList(search);
    }

    /**
     * 订单列表
     *
     * @param status
     * @param page
     * @return
     */
    public static Observable<ResponseDataArray<MallOrderEntity>> orderList(int status, int page) {
        StringBuffer sb = new StringBuffer(DataConfig.API_HOST + ApiHost.MALL_ORDER_LIST).append("?");
        sb.append("page=" + page);
        sb.append("&row=" + 10);
        if (status != MallOrderType.ALL.status) {
            sb.append("&status=" + status);
        }
        return getApi().orderList(sb.toString());
    }

    public static Observable<ResponseDataObject<Object>> orderCancel(String _id) {
        return getApi().orderCancel(_id);
    }

    /**
     * 订单详情
     */
    public static Observable<ResponseDataObject<MallOrderDetailEntity>> orderDetail(String _id) {
        return getApi().orderDetail(_id);
    }

    /**
     * 订单评价
     *
     * @param commentEntity
     * @return
     */
    public static Observable<ResponseDataObject<Object>> orderComment(CommentEntity commentEntity) {
        return getApi().orderComment(commentEntity.id, commentEntity.goods, commentEntity.flow, commentEntity.service, commentEntity.content);
    }

    /**
     * 订单-确认收货
     *
     * @return
     */
    public static Observable<ResponseDataObject<Object>> orderReceipt(String _id) {
        return getApi().orderReceipt(_id);
    }

    /**
     * 获取店铺推荐商品
     */
    public static Observable<ResponseDataObject<List<GoodsEntity>>> doShopRecommend(String id, String goods_id, String item_source) {
        return getApi().shopRecommend(id, goods_id, item_source);
    }


    //----------------------------------本地缓存---------
    private static final String MALL_SEARCH_HISTORY = "MALL_SEARCH_HISTORY";

    /**
     * 获取历史搜索
     *
     * @return
     */
    public static List<String> getSearchHistory() {
        List<String> searchList = new ArrayList<>();
        String flightHistory = SPUtil.getString(MALL_SEARCH_HISTORY, null);
        if (TextUtils.isEmpty(flightHistory)) {
            return searchList;
        }
        String[] items = flightHistory.split(",");
        searchList = Arrays.asList(items);

        //倒序
        Collections.reverse(searchList);
        //去重
        List listTemp = new ArrayList();
        for (int i = 0; i < searchList.size(); i++) {
            if (!listTemp.contains(searchList.get(i))) {
                listTemp.add(searchList.get(i));
            }
        }
        //最多10个
        if (listTemp.size() > 10) {
            listTemp = listTemp.subList(0, 10);

            //只保留10个
            List<String> savedList = new ArrayList<>(listTemp);
            Collections.reverse(savedList);
            StringBuffer stringBuffer = new StringBuffer();
            for (String item : savedList) {
                stringBuffer.append(item).append(",");
            }
            stringBuffer = stringBuffer.deleteCharAt(stringBuffer.length() - 1);
            SPUtil.setString(MALL_SEARCH_HISTORY, stringBuffer.toString());
        }

        return listTemp;
    }

    /**
     * 保存搜索历史
     */
    public static void addSearchHistory(String keyword) {
        String searchHistory = SPUtil.getString(MALL_SEARCH_HISTORY, null);
        if (!TextUtils.isEmpty(searchHistory)) {
            searchHistory += "," + keyword;
        } else {
            searchHistory = keyword;
        }
        SPUtil.setString(MALL_SEARCH_HISTORY, searchHistory);
    }

    /**
     * 清除搜索历史
     */
    public static void clearSearchHistory() {
        SPUtil.setString(MALL_SEARCH_HISTORY, null);
    }


}
