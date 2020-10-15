package com.snqu.shopping.ui.goods.vm

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.LogUtils
import com.snqu.shopping.common.viewmodel.BaseAndroidViewModel
import com.snqu.shopping.data.ApiHost
import com.snqu.shopping.data.base.*
import com.snqu.shopping.data.goods.GoodsClient
import com.snqu.shopping.data.goods.bean.CollectionEntity
import com.snqu.shopping.data.goods.entity.*
import com.snqu.shopping.data.home.entity.CommunityEntity
import com.snqu.shopping.data.mall.MallClient
import com.snqu.shopping.data.user.UserClient
import com.snqu.shopping.ui.goods.GoodsDetailActivity
import com.snqu.shopping.util.log.LogClient
import io.reactivex.disposables.Disposable
import java.io.File

/**
 * desc:
 * time: 2019/8/28
 * @author 银进
 */
class GoodsViewModel : BaseAndroidViewModel {

    val dataResult = MutableLiveData<NetReqResult>()
    private val mPLinkDisposable: Disposable? = null

    constructor(application: Application) : super(application)

    /**
     * 获取我推荐的奖励信息
     */
    fun getMyGoodRecmInfo() {
        executeNoMapHttp(GoodsClient.getMyGoodRecmInfo(), object : BaseResponseObserver<ResponseDataObject<GoodRecmInfoEntity>>() {
            override fun onSuccess(value: ResponseDataObject<GoodRecmInfoEntity>?) {
                dataResult.value = NetReqResult(ApiHost.GET_MY_GOOD_RECM_INFO, null, true, value?.data)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.GET_MY_GOOD_RECM_INFO, e?.alert, false, null)
            }

            override fun onEnd() {
            }

        })
    }


    /**
     * 获取问题反馈列表
     *
     * @return
     */
    fun getMyGoodRecmList(queryParam: GoodsQueryParam) {
        executeNoMapHttp(GoodsClient.getMyGoodRecmList(queryParam), object : BaseResponseObserver<ResponseDataArray<CommunityEntity>>() {
            override fun onSuccess(value: ResponseDataArray<CommunityEntity>?) {
                dataResult.value = NetReqResult(ApiHost.GET_MY_GOOD_RECM_LIST, null, true, value?.data)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.GET_MY_GOOD_RECM_LIST, e?.alert, false, null)
            }

            override fun onEnd() {
            }
        })
    }


    /**
     * 提交好物推荐
     */
    fun sendGoodRecm(pathList: List<String>, share_content: String, goods_id: String, item_source: String, item_id: String,tomorrow:Int) {
        executeNoMapHttp(GoodsClient.submitGoodRecm(pathList, share_content, goods_id, item_source, item_id,tomorrow), object : BaseResponseObserver
        <ResponseDataObject<Any>>() {
            override fun onSuccess(value: ResponseDataObject<Any>?) {
                dataResult.value = NetReqResult(ApiHost.POST_SHARE_GOOD_RECM, null, true, null)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.POST_SHARE_GOOD_RECM, e?.alert, false, null)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 上传好物推荐图片
     */
    fun uploadGoodRecmFiles(file: File, type: String = "images") {
        executeNoMapHttp(GoodsClient.uploadGoodsRecm(file, type), object : BaseResponseObserver<ResponseDataArray<GoodsRecmEntity>>() {
            override fun onSuccess(value: ResponseDataArray<GoodsRecmEntity>?) {
                dataResult.value = NetReqResult(ApiHost.UPFILE_COMMUNITY_GOODS_RECM, null, true, value?.data)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.UPFILE_COMMUNITY_GOODS_RECM, e?.alert, false, null)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 判断商品是否可以推荐
     */
    fun clickCommunityGoodsRecm(goods_id: String, item_source: String) {
        if (UserClient.isLogin()) {
            executeNoMapHttp(GoodsClient.communityGoodsRecm(goods_id, item_source), object : BaseResponseObserver<ResponseDataObject<GoodRecmEntity>>() {
                override fun onSuccess(value: ResponseDataObject<GoodRecmEntity>?) {
                    dataResult.value = NetReqResult(ApiHost.COMMUNITY_GOODS_RECM_CLICK, value?.message, true, value?.data)
                }

                override fun onError(e: HttpResponseException?) {
                    dataResult.value = NetReqResult(ApiHost.COMMUNITY_GOODS_RECM_CLICK, e?.alert, false)
                }

                override fun onEnd() {
                }
            })
        }
    }

    /**
     * 判断商品是否可以推荐
     */
    fun communityGoodsRecm(goods_id: String, item_source: String) {
        if (UserClient.isLogin()) {
            executeNoMapHttp(GoodsClient.communityGoodsRecm(goods_id, item_source), object : BaseResponseObserver<ResponseDataObject<GoodRecmEntity>>() {
                override fun onSuccess(value: ResponseDataObject<GoodRecmEntity>?) {
                    dataResult.value = NetReqResult(ApiHost.COMMUNITY_GOODS_RECM, value?.message, true, value?.data)
                }

                override fun onError(e: HttpResponseException?) {
                    dataResult.value = NetReqResult(ApiHost.COMMUNITY_GOODS_RECM, e?.alert, false)
                }

                override fun onEnd() {
                }
            })
        }
    }

    /**
     * 删除我的推荐
     */
    fun delGoodRecm(recm_uid: String) {
        executeNoMapHttp(GoodsClient.delGoodRecm(recm_uid), object : BaseResponseObserver<ResponseDataObject<Any>>() {
            override fun onSuccess(value: ResponseDataObject<Any>?) {
                dataResult.value = NetReqResult(ApiHost.DEL_GOOD_RECM, null, true, null)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.DEL_GOOD_RECM, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 添加收藏
     */
    fun doAddCollectionGoods(id: String, item_source: String?) {
        executeNoMapHttp(GoodsClient.doAddCollectionGoods(id, item_source), object : BaseResponseObserver<ResponseDataObject<Any>>() {
            override fun onSuccess(value: ResponseDataObject<Any>?) {
                dataResult.value = NetReqResult(ApiHost.ADD_COLLECTION_GOODS, "收藏成功", true)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.ADD_COLLECTION_GOODS, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 删除收藏(ids:多个用,隔开1,2,3,4)
     */
    fun doDeleteCollectionGoods(ids: String) {
        executeNoMapHttp(GoodsClient.doDeleteCollectionGoods(ids), object : BaseResponseObserver<ResponseDataObject<Any>>() {
            override fun onSuccess(value: ResponseDataObject<Any>?) {
                dataResult.value = NetReqResult(ApiHost.DELETE_COLLECTION_GOODS, "删除成功", true)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.DELETE_COLLECTION_GOODS, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 删除收藏更具商品id
     */
    fun deleteCollectionGoodsItem(id: String, item_source: String?) {
        executeNoMapHttp(GoodsClient.deleteCollectionGoodsItem(id, item_source), object : BaseResponseObserver<ResponseDataObject<Any>>() {
            override fun onSuccess(value: ResponseDataObject<Any>?) {
                dataResult.value = NetReqResult(ApiHost.DELETE_COLLECTION_GOODS_ITEM, "取消成功", true)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.DELETE_COLLECTION_GOODS_ITEM, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 清除失效收藏
     */
    fun doClearFailCollectionGoods() {
        executeNoMapHttp(GoodsClient.doClearFailCollectionGoods(), object : BaseResponseObserver<ResponseDataObject<Any>>() {
            override fun onSuccess(value: ResponseDataObject<Any>?) {
                dataResult.value = NetReqResult(ApiHost.CLEAR_FAIL_COLLECTION_GOODS, "删除成功", true)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.CLEAR_FAIL_COLLECTION_GOODS, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 获取收藏列表
     */
    fun favList(){
        executeNoMapHttp(GoodsClient.favList(), object : BaseResponseObserver<ResponseDataObject<CollectionEntity>>() {
            override fun onSuccess(value: ResponseDataObject<CollectionEntity>?) {
                if (value?.data == null) {
                    dataResult.value = NetReqResult(ApiHost.FAV_LIST, value?.message, false)
                } else {
                    dataResult.value = NetReqResult(ApiHost.FAV_LIST, "", true, value.data)
                }

            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.FAV_LIST, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }

//    /**
//     * 获取收藏列表
//     */
//    fun doCollectionGoodsList() {
//        executeNoMapHttp(GoodsClient.doCollectionGoodsList(), object : BaseResponseObserver<ResponseDataObject<CollectionListGoodsEntity>>() {
//            override fun onSuccess(value: ResponseDataObject<CollectionListGoodsEntity>?) {
//                if (value?.data == null) {
//                    dataResult.value = NetReqResult(ApiHost.COLLECTION_GOODS_LIST, value?.message, false)
//                } else {
//                    dataResult.value = NetReqResult(ApiHost.COLLECTION_GOODS_LIST, "", true, value.data)
//                }
//
//            }
//
//            override fun onError(e: HttpResponseException?) {
//                dataResult.value = NetReqResult(ApiHost.COLLECTION_GOODS_LIST, e?.alert, false)
//            }
//
//            override fun onEnd() {
//            }
//        })
//    }


    /**
     * 获取商品基础详情数据
     */
    fun doGoodsDetail(id: String, item_source: String?, item_id: String) {
        executeNoMapHttp(GoodsClient.doGoodsDetail(id, item_source, item_id), object : BaseResponseObserver<ResponseDataObject<GoodsEntity?>>() {
            override fun onSuccess(value: ResponseDataObject<GoodsEntity?>) {
                dataResult.value = NetReqResult(ApiHost.GOODS_DETAIL, "", true, value.data)
            }

            override fun onError(e: HttpResponseException?) {
                LogClient.log(GoodsDetailActivity.TAG, "doGoodsDetail失败，参数=" + (e?.alert + "," + e?.msg))
                dataResult.value = NetReqResult(ApiHost.GOODS_DETAIL, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 更新|获取推荐语
     */
    fun getGoodsRecText(goods_id: String, item_source: String) {
        executeNoMapHttp(GoodsClient.getGoodsRecText(goods_id, item_source), object : BaseResponseObserver<ResponseDataObject<GoodsRecmText>>() {
            override fun onSuccess(value: ResponseDataObject<GoodsRecmText>) {
                dataResult.value = NetReqResult(ApiHost.GOODS_REC_TEXT, "", true, value?.data)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.GOODS_REC_TEXT, e?.msg, false, null)
            }

            override fun onEnd() {
            }

        })
    }

    /**
     * 获取商品详情数据
     */
    fun doGoodsDetailDesc(id: String, item_source: String?) {
        executeNoMapHttp(GoodsClient.doGoodsDetailDesc(id, item_source), object : BaseResponseObserver<ResponseDataObject<GoodsEntity?>>() {
            override fun onSuccess(value: ResponseDataObject<GoodsEntity?>) {
                dataResult.value = NetReqResult(ApiHost.GOODS_DETAIL_DESC, "", true, value.data)
            }

            override fun onError(e: HttpResponseException?) {
                LogClient.log(GoodsDetailActivity.TAG, "doGoodsDetailDesc失败，参数=" + (e?.alert + "," + e?.msg))
                dataResult.value = NetReqResult(ApiHost.GOODS_DETAIL_DESC, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 获取商品列表
     */
    fun getGoodsList(page: Int, source_type0: String, source_type1: String, tid: String,
                     item_source: String, category_id: String, item_category_id: String,
                     nine_cid: String) {
        val client = GoodsClient.getGoodsList(page, source_type0, source_type1, tid, item_source, category_id, item_category_id, nine_cid)
        executeNoMapHttp(client, object : BaseResponseObserver<ResponseDataArray<GoodsEntity?>>() {
            override fun onSuccess(value: ResponseDataArray<GoodsEntity?>?) {
                dataResult.value = NetReqResult(ApiHost.GET_GOODS_LIST, null, true, value)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.GET_GOODS_LIST, e?.msg, false, null)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 获取猜你喜欢商品列表
     */
    fun getLikeGoodsList(queryParam: GoodsQueryParam, type_1: String, type_2: String, liveData: MutableLiveData<NetReqResult>) {
        val client = GoodsClient.getLikeGoodsList(queryParam, type_1, type_2)
        executeNoMapHttp(client, object : BaseResponseObserver<ResponseDataArray<GoodsEntity?>>() {
            override fun onSuccess(value: ResponseDataArray<GoodsEntity?>?) {
                if (liveData != null) {
                    liveData.value = NetReqResult(ApiHost.GET_GOODS_LIST, null, true, value)
                } else {
                    dataResult.value = NetReqResult(ApiHost.GET_GOODS_LIST, null, true, value)
                }
            }

            override fun onError(e: HttpResponseException?) {
                if (liveData != null) {
                    liveData.value = NetReqResult(ApiHost.GET_GOODS_LIST, e?.msg, false, null)
                } else {
                    dataResult.value = NetReqResult(ApiHost.GET_GOODS_LIST, e?.msg, false, null)
                }
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 获取商品是否已经收藏
     */
    fun doGoodsFav(id: String, item_source: String?) {
        executeNoMapHttp(GoodsClient.doGoodsFav(id, item_source), object : BaseResponseObserver<ResponseDataObject<GoodsFavEntity?>>() {
            override fun onSuccess(value: ResponseDataObject<GoodsFavEntity?>) {
                dataResult.value = NetReqResult(ApiHost.GOODS_FAV, "", true, value.data)
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.GOODS_FAV, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 获取店铺推荐商品
     */
    fun doShopRecommend(id: String, goods_id: String, item_source: String?) {
        executeNoMapHttp(MallClient.doShopRecommend(id, goods_id, item_source), object : BaseResponseObserver<ResponseDataObject<List<GoodsEntity>>>() {
            override fun onSuccess(value: ResponseDataObject<List<GoodsEntity>>?) {
                dataResult.value = NetReqResult(ApiHost.SHOP_RECOMMEND, "", true, value?.data
                        ?: mutableListOf<GoodsEntity>())

            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.SHOP_RECOMMEND, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 获取商品评论
     */
    fun doGoodsRates(id: String, type: Int?, row: Int, page: Int, item_source: String?) {
        executeNoMapHttp(GoodsClient.doGoodsRates(id, type, row, page, item_source), object : BaseResponseObserver<ResponseDataObject<List<RateBase>>>() {
            override fun onSuccess(value: ResponseDataObject<List<RateBase>>?) {
                dataResult.value = NetReqResult(ApiHost.GOODS_RATES, "", true, value?.data
                        ?: mutableListOf<RateBase>())

            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.GOODS_RATES, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 获取商品评论数量
     */
    fun doGoodsRatesCount(id: String, item_source: String?) {
        executeNoMapHttp(GoodsClient.doGoodsRatesCount(id, item_source), object : BaseResponseObserver<ResponseDataObject<RateBaseCountEntity>>() {
            override fun onSuccess(value: ResponseDataObject<RateBaseCountEntity>?) {
                dataResult.value = NetReqResult(ApiHost.GOODS_RATES_COUNT, "", true, value?.data)

            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.GOODS_RATES_COUNT, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }

    /**
     * 获取商品详情页推荐商品
     */
    fun doGoodsDetailRecommend(id: String, row: Int, page: Int, item_source: String?) {
        executeNoMapHttp(GoodsClient.doGoodsDetailRecommend(id, row, page, item_source), object : BaseResponseObserver<ResponseDataObject<List<GoodsEntity>>>() {
            override fun onSuccess(value: ResponseDataObject<List<GoodsEntity>>?) {
                dataResult.value = NetReqResult(ApiHost.GOODS_DETAIL_RECOMMEND, "", true, value?.data
                        ?: mutableListOf<GoodsEntity>())
            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.GOODS_DETAIL_RECOMMEND, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }


    /**
     * 添加商品浏览记录
     */
    fun doAddGoodsRecode(id: String?, item_source: String?) {
        executeNoMapHttp(GoodsClient.doAddGoodsRecode(id, item_source), object : BaseResponseObserver<ResponseDataObject<Any>>() {
            override fun onSuccess(value: ResponseDataObject<Any>?) {
            }

            override fun onError(e: HttpResponseException?) {

            }

            override fun onEnd() {
            }
        })
    }


    /**
     * 转链商品链接
     */
    @SuppressLint("CheckResult", "AutoDispose")
    fun doPromotionLink(link_type: Array<String>, tid: String, item_source: String, link_url: String, need_code: String) {
        val promotionLinkBodyEntity = PromotionLinkBodyEntity(link_type, tid, link_url, item_source, need_code)
        executeNoMapHttp(GoodsClient.doPromotionLink(promotionLinkBodyEntity), object : BaseResponseObserver<ResponseDataObject<PromotionLinkEntity>>() {
            override fun onSuccess(value: ResponseDataObject<PromotionLinkEntity>?) {
                if (value?.data != null) {
                    dataResult.value = NetReqResult(ApiHost.PROMOTION_LINK, "", true, value.data).apply {
                        extra = true
                    }
                } else {
                    dataResult.value = NetReqResult(ApiHost.PROMOTION_LINK, "", false)
                }

            }

            override fun onError(e: HttpResponseException?) {
                LogClient.log(GoodsDetailActivity.TAG, "doPromotionLink失败，参数=" + (e?.alert + "," + e?.msg))
                //需要用户授权操作
                if (e?.resultCode == 423 || e?.resultCode == 500002) {
                    if ((e.data as ResponseDataObject<PromotionLinkEntity>).data == null) {
                        dataResult.value = NetReqResult(ApiHost.PROMOTION_LINK, e.alert, false)
                    } else {
                        dataResult.value = NetReqResult(ApiHost.PROMOTION_LINK, "", true, (e.data as ResponseDataObject<PromotionLinkEntity>).data).apply {
                            extra = false
                        }
                    }
                } else {
                    dataResult.value = NetReqResult(ApiHost.PROMOTION_LINK, e?.alert, false)
                }

            }

            override fun onEnd() {
            }
        })
    }


    //.........VIP商品...........
    /**
     * 获取商品基础详情数据
     */
    fun doVipGoodsDetail(id: String) {
        executeNoMapHttp(GoodsClient.doVipGoodsDetail(id), object : BaseResponseObserver<ResponseDataObject<VipGoodsEntity?>>() {
            override fun onSuccess(value: ResponseDataObject<VipGoodsEntity?>) {
                if (value.isSuccessful) {
                    dataResult.value = NetReqResult(ApiHost.VIP_GOODS_DETAIL, "", true, value.data)
                } else {
                    dataResult.value = NetReqResult(ApiHost.VIP_GOODS_DETAIL, "", false)
                }

            }

            override fun onError(e: HttpResponseException?) {
                dataResult.value = NetReqResult(ApiHost.VIP_GOODS_DETAIL, e?.alert, false)
            }

            override fun onEnd() {
            }
        })
    }


}