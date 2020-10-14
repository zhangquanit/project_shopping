package com.snqu.shopping.data.mall.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.snqu.shopping.data.mall.entity.address.AddressEntity;
import com.snqu.shopping.util.NumberUtil;

import java.io.Serializable;
import java.util.List;

public class ShopGoodsEntity implements Serializable, Parcelable {


    /**
     * _id : 5f45d53e4a71d023df104f65
     * name : 九阳面条机全自动智能自动加水多功能压面机
     * describe : 商品名称：九阳面条机全自动智能自动加水多功能压面机家用电动饺子皮机600g容量1-5人M6-L20商品编号：41967615966店铺： 九阳官方旗舰店商品毛重：6.7kg操控方式：按键产品类别：立式智能面条机清洗方式：可拆洗产品功能：称重，吹风，自动滴水螺杆材质：不锈钢
     * shop_way_id : 5f45ceee4a71d0789a32d572
     * shop_category_id : ["5f45c3cf4a71d02e7819b055","5f45c6df4a71d01d4651b922"]
     * hit_start_time : 1598284800
     * hit_end_time : 1600271999
     * serven_reason : 1
     * not_flow : [["台湾省"],["香港特别行政区"],["澳门特别行政区"]]
     * shop_sales_address_id : 5f449dea4a71d036224f7722
     * sold : 69
     * banner_img : ["/test/static/images/20200826/1f6c329ec64249f9b21ef8e811302ee0.jpg"]
     * goods_img : ["/test/static/images/20200826/44fcc81c15ae5db4b7c588f10a822d79.jpg"]
     * standard : [{"name":"大号","selling_price":144900,"original_price":189900,"inv":42},{"name":"小号","selling_price":129900,"original_price":159900,"inv":30}]
     * status : 1
     * itime : 1598412094
     * utime : 1598425640
     * real_sold : 0
     * shop_goods_id : 1598412094
     * selling_price : 129900
     * original_price : 159900
     * inv : 72
     * banner_img_txt : ["https://resource-t.xin1.cn/test/static/images/20200826/1f6c329ec64249f9b21ef8e811302ee0.jpg"]
     * goods_img_txt : ["https://resource-t.xin1.cn/test/static/images/20200826/44fcc81c15ae5db4b7c588f10a822d79.jpg"]
     * shop_category_name : 食品、美妆
     * standard_txt : 多规格
     */

    public String _id;
    public String name;
    public String describe;
    public String shop_way_id;
    public int hit_start_time;
    public int hit_end_time;
    public int serven_reason;
    public String shop_sales_address_id;
    public int sold;
    public int status;
    public int itime;
    public int utime;
    public int real_sold;
    public int shop_goods_id;
    public Long selling_price;
    public Long original_price;
    public int inv;
    public String shop_category_name;
    public String standard_txt;
    public List<String> shop_category_id;
    public String not_flow;
    //    public List<String> banner_img;
//    public List<String> goods_img;
    public List<StandardBean> standard;
    public List<String> banner_img_txt;
    public List<String> goods_img_txt;
    public int inv_add_sub;
    public String standard_name;
    public int number;
    public int total_price;
    public String shop_category;
    public int is_address;
    public List<String> user_type_in;
    public AddressEntity addressEntity;

    public String getImage() {
        if (null == banner_img_txt || banner_img_txt.isEmpty()) {
            return null;
        }
        return banner_img_txt.get(banner_img_txt.size() - 1);
    }

    public String getReasonText() {
        if (serven_reason == -1) {
            return "支持7天无理由退货";
        } else if (serven_reason == 1) {
            return "不支持7天无理由退货";
        }
        return null;
    }

    /**
     * 原价
     *
     * @return
     */
    public String getOldPrice() {
        return NumberUtil.saveTwoPoint(original_price);
    }

    /**
     * @return
     */
    public String getNewPrice() {
        return NumberUtil.saveTwoPoint(selling_price);
    }

    public static class StandardBean implements Parcelable {
        /**
         * name : 大号
         * selling_price : 144900
         * original_price : 189900
         * inv : 42
         */

        public String name;
        public Long selling_price;
        public Long original_price;
        public int inv;

        @Override
        public String toString() {
            return "StandardBean{" +
                    "name='" + name + '\'' +
                    ", selling_price=" + selling_price +
                    ", original_price=" + original_price +
                    ", inv=" + inv +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.name);
            dest.writeValue(this.selling_price);
            dest.writeValue(this.original_price);
            dest.writeInt(this.inv);
        }

        public StandardBean() {
        }

        protected StandardBean(Parcel in) {
            this.name = in.readString();
            this.selling_price = (Long) in.readValue(Long.class.getClassLoader());
            this.original_price = (Long) in.readValue(Long.class.getClassLoader());
            this.inv = in.readInt();
        }

        public static final Parcelable.Creator<StandardBean> CREATOR = new Parcelable.Creator<StandardBean>() {
            @Override
            public StandardBean createFromParcel(Parcel source) {
                return new StandardBean(source);
            }

            @Override
            public StandardBean[] newArray(int size) {
                return new StandardBean[size];
            }
        };
    }

    @Override
    public String toString() {
        return "ShopGoodsEntity{" +
                "_id='" + _id + '\'' +
                ", name='" + name + '\'' +
                ", describe='" + describe + '\'' +
                ", shop_way_id='" + shop_way_id + '\'' +
                ", hit_start_time=" + hit_start_time +
                ", hit_end_time=" + hit_end_time +
                ", serven_reason=" + serven_reason +
                ", shop_sales_address_id='" + shop_sales_address_id + '\'' +
                ", sold='" + sold + '\'' +
                ", status=" + status +
                ", itime=" + itime +
                ", utime=" + utime +
                ", real_sold=" + real_sold +
                ", shop_goods_id=" + shop_goods_id +
                ", selling_price=" + selling_price +
                ", original_price=" + original_price +
                ", inv=" + inv +
                ", shop_category_name='" + shop_category_name + '\'' +
                ", standard_txt='" + standard_txt + '\'' +
                ", shop_category_id=" + shop_category_id +
                ", not_flow=" + not_flow +
                ", standard=" + standard +
                ", banner_img_txt=" + banner_img_txt +
                ", goods_img_txt=" + goods_img_txt +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._id);
        dest.writeString(this.name);
        dest.writeString(this.describe);
        dest.writeString(this.shop_way_id);
        dest.writeInt(this.hit_start_time);
        dest.writeInt(this.hit_end_time);
        dest.writeInt(this.serven_reason);
        dest.writeString(this.shop_sales_address_id);
        dest.writeInt(this.sold);
        dest.writeInt(this.status);
        dest.writeInt(this.itime);
        dest.writeInt(this.utime);
        dest.writeInt(this.real_sold);
        dest.writeInt(this.shop_goods_id);
        dest.writeValue(this.selling_price);
        dest.writeValue(this.original_price);
        dest.writeInt(this.inv);
        dest.writeString(this.shop_category_name);
        dest.writeString(this.standard_txt);
        dest.writeStringList(this.shop_category_id);
        dest.writeString(this.not_flow);
        dest.writeTypedList(this.standard);
        dest.writeStringList(this.banner_img_txt);
        dest.writeStringList(this.goods_img_txt);
        dest.writeInt(this.inv_add_sub);
        dest.writeString(this.standard_name);
        dest.writeInt(this.number);
        dest.writeInt(this.total_price);
        dest.writeString(this.shop_category);
        dest.writeInt(this.is_address);
        dest.writeStringList(this.user_type_in);
        dest.writeParcelable(this.addressEntity, flags);
    }

    public ShopGoodsEntity() {
    }

    protected ShopGoodsEntity(Parcel in) {
        this._id = in.readString();
        this.name = in.readString();
        this.describe = in.readString();
        this.shop_way_id = in.readString();
        this.hit_start_time = in.readInt();
        this.hit_end_time = in.readInt();
        this.serven_reason = in.readInt();
        this.shop_sales_address_id = in.readString();
        this.sold = in.readInt();
        this.status = in.readInt();
        this.itime = in.readInt();
        this.utime = in.readInt();
        this.real_sold = in.readInt();
        this.shop_goods_id = in.readInt();
        this.selling_price = (Long) in.readValue(Long.class.getClassLoader());
        this.original_price = (Long) in.readValue(Long.class.getClassLoader());
        this.inv = in.readInt();
        this.shop_category_name = in.readString();
        this.standard_txt = in.readString();
        this.shop_category_id = in.createStringArrayList();
        this.not_flow = in.readString();
        this.standard = in.createTypedArrayList(StandardBean.CREATOR);
        this.banner_img_txt = in.createStringArrayList();
        this.goods_img_txt = in.createStringArrayList();
        this.inv_add_sub = in.readInt();
        this.standard_name = in.readString();
        this.number = in.readInt();
        this.total_price = in.readInt();
        this.shop_category = in.readString();
        this.is_address = in.readInt();
        this.user_type_in = in.createStringArrayList();
        this.addressEntity = in.readParcelable(AddressEntity.class.getClassLoader());
    }

    public static final Creator<ShopGoodsEntity> CREATOR = new Creator<ShopGoodsEntity>() {
        @Override
        public ShopGoodsEntity createFromParcel(Parcel source) {
            return new ShopGoodsEntity(source);
        }

        @Override
        public ShopGoodsEntity[] newArray(int size) {
            return new ShopGoodsEntity[size];
        }
    };
}
