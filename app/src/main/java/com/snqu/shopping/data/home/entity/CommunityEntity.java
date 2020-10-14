package com.snqu.shopping.data.home.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.google.gson.annotations.SerializedName;
import com.snqu.shopping.R;
import com.snqu.shopping.data.goods.entity.GoodsEntity;
import com.snqu.shopping.util.GlideUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 社区
 *
 * @author 张全
 */
public class CommunityEntity implements Parcelable {

    /**
     * _id : 5dd4dcf693869e75bb43cda2
     * plate_id : 5dd38d5193869e416734ad44
     * recom_id : 5dd3926593869e094b3cf3d2
     * item_id : 1
     * item_source : B
     * content :
     * images : ["https://resource-t.xin1.cn/static/images/20191120/4c99a0ed17382bb1ff1cce4ad2519483.jpg","https://resource-t.xin1.cn/static/images/20191120/cc4ad78d79894fc4b7483fa2f4924749.jpg"]
     * goods_id : d7a4ca8709b5f6aed7baf9a511ba2afa
     * plate_title : 爆款分享
     * recom_title : 飞天小女警
     * sort : 50
     * counts : 0
     * itime : 1574231286
     * avatar : https://resource-t.xin1.cn/static/images/20191119/95648ef4c895b5f7d2157deb2fe92077.jpg
     * goodBase : {"_id":"d7a4ca8709b5f6aed7baf9a511ba2afa","item_id":"1","item_source":"B","item_title":"九阳电饭煲锅5L升家用大容量智能柴火饭旗舰店官网正品3-4-6个人2","item_sell_count":5288,"item_price":26900,"item_min_price":34900,"seller_shop_id":"72119289","item_delivery_postage":0,"status":1,"item_image":"//avatar.alicdn.com/i3/897258160/O1CN01JGFT3F2A9KQd3qIOy_!!0-item_pic.jpg","coupon":{"start_time":1574179200,"end_time":1574438399,"start_fee":34900,"amount":8000,"rate":2292},"sync_status":1,"rebate":{"amount":5380,"xkd_rate":6600,"xkd_amount":3550},"sort":1822274,"seller_shop_name":"九阳合千润专卖店"}
     */

    public String _id;
    public String plate_id;
    public String recom_id;
    public String item_id;
    public String item_source;
    @SerializedName(value = "content", alternate = {"share_content"})
    public String content;
    public String user_id;
    public String goods_id;
    public String plate_title;
    public String recom_title;
    public int order_count;
    public int rank;
    public int sort;
    public int counts; //转发点击量
    public String avatar;
    @SerializedName(value = "goodBase")
    public GoodsEntity goodBase;
    public GoodsEntity goods_info;
    public UserInfoBean user_info;
    public List<String> images; //使用getImages();
    public long start_time; //
    public List<String> videos = new ArrayList<>();
    public String status;  //1 审核通过 2 审核失败
    public String examine_content;//审核记录
    public long itime;
    public String utime;
    public String es_rank;
    public String es_order_count;
    public List<String> images_url;
    public long reward_amount;
    public String settle_status;//结算状态 1 未结算，2 已结算 3 结算失败
    public String flag_txt;
    public String settle_type; // 1 正常结算  2 异步结算


    public CommunityEntity() {

    }

    protected CommunityEntity(Parcel in) {
        _id = in.readString();
        plate_id = in.readString();
        recom_id = in.readString();
        item_id = in.readString();
        item_source = in.readString();
        content = in.readString();
        user_id = in.readString();
        goods_id = in.readString();
        plate_title = in.readString();
        recom_title = in.readString();
        order_count = in.readInt();
        rank = in.readInt();
        sort = in.readInt();
        counts = in.readInt();
        avatar = in.readString();
        goodBase = in.readParcelable(GoodsEntity.class.getClassLoader());
        images = in.createStringArrayList();
        start_time = in.readLong();
        videos = in.createStringArrayList();
        status = in.readString();
        examine_content = in.readString();
        itime = in.readLong();
        utime = in.readString();
        es_rank = in.readString();
        es_order_count = in.readString();
        images_url = Arrays.asList(in.createStringArray());
        flag_txt = in.readString();
    }

    public static final Creator<CommunityEntity> CREATOR = new Creator<CommunityEntity>() {
        @Override
        public CommunityEntity createFromParcel(Parcel in) {
            return new CommunityEntity(in);
        }

        @Override
        public CommunityEntity[] newArray(int size) {
            return new CommunityEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(plate_id);
        dest.writeString(recom_id);
        dest.writeString(item_id);
        dest.writeString(item_source);
        dest.writeString(content);
        dest.writeString(user_id);
        dest.writeString(goods_id);
        dest.writeString(plate_title);
        dest.writeString(recom_title);
        dest.writeInt(order_count);
        dest.writeInt(rank);
        dest.writeInt(sort);
        dest.writeInt(counts);
        dest.writeString(avatar);
        dest.writeParcelable(goodBase, flags);
        dest.writeStringList(images);
        dest.writeLong(start_time);
        dest.writeStringList(videos);
        dest.writeString(status);
        dest.writeString(examine_content);
        dest.writeLong(itime);
        dest.writeString(utime);
        dest.writeString(es_rank);
        dest.writeString(es_order_count);
        dest.writeList(images_url);
        dest.writeString(flag_txt);
    }

    public static class UserInfoBean {
        /**
         * username : 135****9655
         * avatar :
         */

        public String username;
        public String avatar;
    }


    public boolean hasImgs() {
        return null != images && !images.isEmpty();
    }

    public boolean hasVideos() {
        return null != videos && !videos.isEmpty();
    }

    public List<String> getItemList() {
        if (null != videos && !videos.isEmpty()) {
            return videos;
        }
        return getImages();
    }

    public List<String> getImages() {
        if (null != images && !images.isEmpty()) {
            List<String> imgList = new ArrayList<>();
            for (String url : images) {
                imgList.add(GlideUtil.checkUrl(url));
            }
            return imgList;
        }
        return images;
    }

    public void setRankBg(ImageView imageView) {
        if (rank == 1) {
            imageView.setImageResource(R.drawable.community_rank1);
        } else if (rank == 2) {
            imageView.setImageResource(R.drawable.community_rank2);
        } else if (rank == 3) {
            imageView.setImageResource(R.drawable.community_rank3);
        } else {
            imageView.setImageBitmap(null);
        }
    }

    public GoodsEntity getGoods() {
        if (null != goods_info) {
            return goods_info;
        }
        if (null != goodBase && !TextUtils.isEmpty(goodBase.get_id())) {
            return goodBase;
        }
        return null;
    }

    @Override
    public String toString() {
        return "CommunityEntity{" +
                "_id='" + _id + '\'' +
                ", plate_id='" + plate_id + '\'' +
                ", recom_id='" + recom_id + '\'' +
                ", item_id='" + item_id + '\'' +
                ", item_source='" + item_source + '\'' +
                ", content='" + content + '\'' +
                ", user_id='" + user_id + '\'' +
                ", goods_id='" + goods_id + '\'' +
                ", plate_title='" + plate_title + '\'' +
                ", recom_title='" + recom_title + '\'' +
                ", order_count=" + order_count +
                ", rank=" + rank +
                ", sort=" + sort +
                ", counts=" + counts +
                ", avatar='" + avatar + '\'' +
                ", goodBase=" + goodBase +
                ", goods_info=" + goods_info +
                ", user_info=" + user_info +
                ", images=" + images +
                ", start_time=" + start_time +
                ", videos=" + videos +
                ", status='" + status + '\'' +
                ", examine_content='" + examine_content + '\'' +
                ", itime=" + itime +
                ", utime='" + utime + '\'' +
                ", es_rank='" + es_rank + '\'' +
                ", es_order_count='" + es_order_count + '\'' +
                ", images_url=" + images_url +
                ", reward_amount=" + reward_amount +
                '}';
    }
}
