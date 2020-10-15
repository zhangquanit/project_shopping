package com.snqu.shopping.data.goods.entity

import android.os.Parcelable
import android.text.TextUtils
import com.snqu.shopping.data.home.entity.ShopItemEntity
import com.snqu.shopping.util.NumberUtil
import kotlinx.android.parcel.Parcelize


/**
 * desc:
 * time: 2019/8/28
 * @author 银进
 */
@Parcelize
data class GoodsEntity(
        var _id: String? = null,
        val category_id: String? = null,//商品分类id
        val category_idpath: List<String>? = null,// xkd商品类目
        val consumer_protection: List<ConsumerProtection>? = null,//'商品服务'
        val coupon: Coupon? = null,//'优惠劵信息'
        val item_brand_value: String? = null,//'商品品牌'
        val item_brand_value_id: String? = null,
        val item_category: String? = null,//'商品分类文字'
        val item_category_id: String? = null,//商品顶级分类id'
        val item_delivery_from: String? = null,//'商品始发地'
        val item_delivery_postage: Long? = null,// '商品快递费用'
        val item_desc: ItemDesc? = null,//'商品内容 淘宝的为数组 pages'
        val item_fav_count: Int? = null,//商品收藏数
        val item_feed_count: Int? = null,//商品评论总数
        var item_id: String? = null,//商品id'
        var item_image: String? = null,//商品首图
        val item_images: List<String?>? = null,//商品图片列表'
        val item_max_price: Long? = null,// '商品最高价格'
        val item_min_price: Long? = null,//'商品最低价格 单个商品两个值相同'
        val item_price: Long? = null,//商品券后价
        val item_price_tag: String? = null,//'商品价格标签'
        val item_props: List<Map<String, String>>? = null,// '商品属性 规格
        val item_root_category_id: Int? = null,//xkd商品类目
        val item_sell_count: Int? = null,//商品月销量
//        val item_sku_base: String? = null,
//        val item_sku_core: String? = null,
//        val item_sku_props: String? = null,
        var item_source: String? = null,//商品来源'
        val item_sub_title: String? = null,//商品副标题
        val item_title: String? = null,//'商品标题'
        val item_url: String? = null,//'商品地址'
        val item_urls: String? = null,
        val item_video: List<VideoBean?>? = null,// '商品视频'
        val itime: Int? = null,//'收录时间'
        val rate_base: RateBase? = null,
        val rebate: Rebate? = null,//佣金返利比例
        var presale: Presale? = null,//预售
        val next_level: NextLevel? = null,//下一级，星乐桃返利信息
        val seller: ShopItemEntity? = null,
        val seller_shop_id: String? = null,//'商品店铺id'
        val seller_shop_name: String? = null,//'商品店铺名字'
        val seller_user_id: String? = null,//'卖家用户id'
        val seller_shop_url: String? = null, //店铺H5地址
        val sort: Int? = null,
        var double_rebate: Boolean = false,//是否双倍返利
        var fix_rebate_amount: Int = 0,//固定返利金额
        var status: Int? = null,//状态 0下架1上架
        val sync_status: Int? = null,//同步状态
        val flag: List<String> = arrayListOf<String>(),//1" // 淘宝聚划算"2" // 天猫超市 "4" // 京东自营"6" // 淘抢购 "7" // 天猫国际
        val jd_plus: Long = 0,//京东plus会员价格
        val utime: Int? = null,//'更新时间'
        val real_url: String? = null, //解析——返回的跳转url
        val goods_id: String? = null, //解析——返回的商品id
        val need_search: String? = null, //解析——是否跳转到搜索
        var desc_content: String? = null,
        var share_code: String? = null,
        var hight_rebate: String? = null,// 0代表不是高佣，1代表是
        var recommend_text: String? = null, //推荐语
        var reload_rec_text: String? = null //是否需要重新获取推荐语 如果为1 请求下面接口
) : Parcelable {
    fun getDoubleOtherPrice(): String {
        return NumberUtil.saveTwoPoint((rebate?.xkd_amount ?: 0) + (fix_rebate_amount))//双倍返利
    }

    fun hasCommissionPrice(): Boolean {
        if (rebate == null || rebate.xlt_commission == 0L) {
            return false
        }
        return true
    }

    /**
     * 产品要求只要显示的内容有一个为null的话就不显示店铺信息
     */
    fun isShowSeller(): Boolean {
        if (seller == null) {
            return false
        }
        if (TextUtils.isEmpty(seller.seller_shop_id)) {
            return false
        }
        if (TextUtils.isEmpty(seller.seller_shop_name)) {
            return false
        }
        if (TextUtils.isEmpty(seller.seller_shop_icon)) {
            return false
        }
        if (TextUtils.isEmpty(seller.scoreDesc) || TextUtils.isEmpty(seller.scoreServ) || TextUtils.isEmpty(seller.scorePost)) {
            return false
        }
        return true
    }

    fun getCommissionPrice(): String {
        if (rebate == null) {
            return ""
        }
        return NumberUtil.saveTwoPoint(rebate.xlt_commission)// 星乐桃返利
    }

    fun hasSubsidyPrice(): Boolean {
        if (rebate == null || rebate.xlt_subsidy == 0L) {
            return false
        }
        return true
    }

    fun getSubsidyPrice(): String {
        if (rebate == null) {
            return ""
        }
        return NumberUtil.saveTwoPoint(rebate.xlt_subsidy)// 补贴
    }

    fun getOld_price(): String {
        return NumberUtil.saveTwoPoint(item_min_price)//原价
    }

    fun getItemPrice(): String {
        return NumberUtil.saveTwoPoint(item_price)
    }

    fun getJDPlusPrice(): String {
        return NumberUtil.saveTwoPoint(jd_plus)//原价
    }

    /**
     * 是否是京东自营
     */
    fun isJDSelf(): Boolean {
        if (flag.contains("4")) {
            return true
        }
        return false
    }

    fun getNow_price(): String {
        return NumberUtil.saveTwoPoint(item_price)//券后价
    }

    fun getSell_count(): String {
        return NumberUtil.sellCount(item_sell_count)//销量
    }

    fun getDelivery_postage(): String {
        return NumberUtil.saveTwoPoint(item_delivery_postage)//运费
    }


    fun isSupportPostage(): Boolean {  //包邮
        return item_delivery_postage == 0L
    }

    /**
     * 优惠金额
     */
    fun getCouponPrice(): String {
        if (coupon == null) {
            return ""
        }
        if (coupon.isOverTime()) {
            return ""
        }
        return coupon.getPrice()
    }

    /**
     * 返利金
     */
    fun getRebatePrice(): String {
        if (rebate == null) {
            return ""
        }
        return rebate.getPrice()
    }

    /**
     * 省,优惠券+返利金
     */
    fun getEconomPrice(): String {
        var rebatePrice: Long? = 0
        if (rebate != null) {
            rebatePrice = rebate.xkd_amount
        }
        var couponPrice: Long? = 0
        couponPrice = if (coupon == null) {
            0
        } else {
            if (coupon.isOverTime()) {
                0
            } else {
                coupon.amount?.toLong()
            }
        }
        return NumberUtil.saveTwoPoint(rebatePrice!! + couponPrice!!)
    }

    fun getShopType(): String? {
        return item_source
    }

    fun getShopName(): String? {
        return seller_shop_name
    }


}

@Parcelize
data class Coupon(
        val amount: Int? = null,//优惠券信息-优惠券面额。
        val coupon_id: String? = null,//优惠券信息-优惠券id（淘宝）
        val end_time: Long? = null,//优惠券信息-优惠券结束时间
        val info: String? = null,//优惠券信息-优惠券满减信息
        val remain_count: Int? = null,//优惠券信息-优惠券剩余量
        val start_fee: Long? = null,//优惠券信息-优惠券起用门槛，满X元可用。如：满299元减20元
        val start_time: Long? = null,//优惠券信息-优惠券开始时间
        val total_count: Long? = null,//优惠券信息-优惠券总量
        val url: String? = null//优惠券地址（京东
) : Parcelable {
    fun getPrice(): String {
        if (amount == 0) {
            return ""
        }
        return NumberUtil.couponPrice(amount)//优惠券金额
    }

    fun isOverTime(): Boolean = System.currentTimeMillis() > (end_time ?: 0) * 1000L
}

@Parcelize
data class RateBase(
        val _id: String? = null,//优惠券信息-优惠券面额。如：满299元减20元
        val feed_id: String? = null,//评论id
        val item_id: String? = null,//商品id
        val item_source: String? = null,//商品数据源
        val feed_content: String? = null,//评论内容
        val user_nick: String? = null,//昵称
        val head_pic: String? = null,//头像（"京东没有）
        val images: List<String>? = null,//图片
        val sku_map: Map<String, String>? = null,//商品属性
        val sku_id: String? = null,//头像（"京东没有）
        val user_star: String? = null,//评分
        val date_time: Int? = null,//时间
        val append_feed: List<AppendFeed>? = null,//追加评价评分
        val interact_info: InteractInfo? = null//评价基础信息

) : Parcelable

@Parcelize
data class AppendFeed(
        val appendedFeedback: String? = null,//追加评论内容
        val intervalDay: String? = null//追加评论时间
) : Parcelable

@Parcelize
data class InteractInfo(
        val readCount: String? = null//预览数量
) : Parcelable

@Parcelize
data class VideoBean(
        val url: String? = null,//视频地址
        val thumbnail: String? = null//视频缩略图
) : Parcelable

@Parcelize
data class NextLevel(val level: Int? = null,
                     val level_txt: String? = null,
                     val rebate: Rebate? = null
) : Parcelable


@Parcelize
data class Rebate(
        val amount: Int? = null,//金额 分
        val rate: Int? = null,//比例
        val xlt_commission: Long? = null,// '星乐桃返利'
        val xlt_subsidy: Long? = null,// '补贴'
        val xkd_amount: Long? = 0,//星乐桃返利金额
        val xkd_rate: Int? = null//星乐桃返利比例
) : Parcelable {
    fun getPrice(): String {
        if (xkd_amount == 0L) {
            return ""
        }
        return NumberUtil.saveTwoPoint(xkd_amount)//返利
    }

    fun getDoublePrice(): String {

        return NumberUtil.saveTwoPoint((xkd_amount ?: 0) * 2)//双倍返利
    }

}

@Parcelize
data class ItemDesc(
        val content: List<String>? = null
) : Parcelable

@Parcelize
data class ConsumerProtection(
        val desc: String? = null,
        val title: String? = null
) : Parcelable

@Parcelize
data class Presale(
        val discount_fee_text: String? = null,  //类型：String  必有字段  备注：预付优惠提示
        val tail_end_time: Long? = null, // 预付尾款结束时间
        val tail_start_time: Long? = null, // 预付尾款开始时间
        var end_time: Long? = null, //预付定金结束时间
        val start_time: Long? = null, //预付定金开始时间
        val deposit: Long? = null  //类型：Number  必有字段  备注：定金
) : Parcelable {
    fun getDeposit(): String {
        if (deposit == 0L) {
            return ""
        }
        return NumberUtil.saveTwoPoint(deposit)//返利
    }
}