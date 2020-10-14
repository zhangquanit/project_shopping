package com.snqu.shopping.data.home.entity;

import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import com.android.util.LContext;
import com.android.util.os.DeviceUtil;
import com.blankj.utilcode.util.SpanUtils;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.util.NumberUtil;

import java.io.Serializable;
import java.util.List;

import kotlinx.android.parcel.Parcelize;

@Parcelize
public class ShopItemEntity implements Serializable {
    /**
     * _id : 4097f5c6fb7d149dab6a30cb845c32e3
     * seller_fans : 513000
     * seller_shop_id : 72119289
     * seller_shop_icon : //avatar.alicdn.com/imgextra//46/11/TB1V6emqhGYBuNjy0FnSut5lpXa.jpg
     * seller_shop_name : 九阳合千润专卖店
     * seller_type : B
     * score_desc : 0
     * score_serv : 0
     * score_post : 0
     */

    public String _id;
    public String seller_fans;
    public String seller_shop_id;

    @Override
    public String toString() {
        return "ShopItemEntity{" +
                "_id='" + _id + '\'' +
                ", seller_fans='" + seller_fans + '\'' +
                ", seller_shop_id='" + seller_shop_id + '\'' +
                ", seller_shop_icon='" + seller_shop_icon + '\'' +
                ", seller_shop_name='" + seller_shop_name + '\'' +
                ", seller_type='" + seller_type + '\'' +
                ", score_desc=" + score_desc +
                ", score_serv=" + score_serv +
                ", score_post=" + score_post +
                ", score_desc_level=" + score_desc_level +
                ", score_serv_level=" + score_serv_level +
                ", score_post_level=" + score_post_level +
                ", sell_count=" + sell_count +
                ", levelColor=" + levelColor +
                ", levelText='" + levelText + '\'' +
                ", scroll=" + scroll +
                ", goods_count=" + goods_count +
                ", goods=" + goods +
                ", credit_level=" + credit_level +
                ", jd_self=" + jd_self +
                '}';
    }

    public String seller_shop_icon;
    public String seller_shop_name;
    public String seller_type;//item_type
    public float score_desc; //宝贝描述
    public float score_serv;//卖家服务
    public float score_post; //物流服务
    public int score_desc_level;//-1 0 1 低中高
    public int score_serv_level; //-1 0 1 低中高
    public int score_post_level; ////-1 0 1 低中高
    public String sell_count; //销量
    public String seller_shop_url;

    public int levelColor;
    public String levelText;

    public List<ScorllItem> scroll;//

    public int goods_count; //商品总数
    public List<GoodsEntity> goods;

    public float credit_level; //淘宝 1-20 京东 0-5.0
    public int jd_self; //1 标记京东自营店


    public String getFans() {
        int fansCount = TextUtils.isEmpty(seller_fans) ? 0 : Integer.valueOf(seller_fans);
        return NumberUtil.sellCount(fansCount);
    }

    /**
     * 宝贝描述
     *
     * @return
     */
    public SpannableStringBuilder getScoreDesc() {
        if (score_desc > 0) {
            String str = score_desc + getLevelStr(score_desc_level);
            return getText("宝贝描述", str, levelColor);
        }
        return null;
    }

    /**
     * 卖家服务
     *
     * @return
     */
    public SpannableStringBuilder getScoreServ() {
        if (score_serv > 0) {
            String str = score_serv + getLevelStr(score_serv_level);
            return getText("卖家服务", str, levelColor);
        }
        return null;
    }

    /**
     * 物流服务
     *
     * @return
     */
    public SpannableStringBuilder getScorePost() {
        if (score_post > 0) {
            String str = score_post + getLevelStr(score_post_level);
            return getText("物流服务", str, levelColor);
        }
        return null;
    }

    private String getLevelStr(int level) {
        switch (level) {
            case -1:
                levelColor = Color.parseColor("#08B12B");
                levelText = "低";
                break;
            case 0:
                levelColor = Color.parseColor("#1393FF");
                levelText = "中";
                break;
            case 1:
                levelColor = Color.parseColor("#FFFF8202");
                levelText = "高";
                break;
        }
        return levelText;
    }

    public SpannableStringBuilder getText(String text, String value, int color) {
        return new SpanUtils()
                .append(text).setForegroundColor(Color.parseColor("#848487")).setFontSize(13, true)
                .appendSpace(DeviceUtil.dip2px(LContext.getContext(), 2))
                .append(value).setForegroundColor(color).setFontSize(13, true)
                .create();
    }


    public String getSellCount() {
        int count = TextUtils.isEmpty(sell_count) ? 0 : Integer.valueOf(sell_count);
        return NumberUtil.sellCount(count);//销量
    }

    public static class ScorllItem {
        public String img_url;
        public String content;
    }
}
