package com.snqu.shopping.data.mall.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class MallOrderEntity implements Parcelable {

    /**
     * _id : 16815990261457684
     * goods_info : [{"name":"九阳面条机全自动智能自动加水多功能压面机","selling_price":144900,"original_price":189900,"inv":51,"inv_add_sub":1,"standard_name":"大号","serven_reason":1,"number":3,"total_price":434700,"shop_category":"食品、美妆","_id":"5f45d53e4a71d023df104f65","describe":"商品名称：九阳面条机全自动智能自动加水多功能压面机家用电动饺子皮机600g容量1-5人M6-L20商品编号：41967615966店铺： 九阳官方旗舰店商品毛重：6.7kg操控方式：按键产品类别：立式智能面条机清洗方式：可拆洗产品功能：称重，吹风，自动滴水螺杆材质：不锈钢","shop_way_id":"5f45ceee4a71d0789a32d572","shop_category_id":["5f45c3cf4a71d02e7819b055","5f45c6df4a71d01d4651b922"],"banner_img":["/test/static/images/20200826/1f6c329ec64249f9b21ef8e811302ee0.jpg","/test/static/images/20200902/271a1a5f690d17bfec80423ecb048f36.jpg","/test/static/images/20200902/8cfe5262364098c2be9e8f5193be2e7d.jpg","/test/static/images/20200902/f64454adf2d5aa0cd710b1002f40abe1.jpg"],"user_type_in":[{"name":"姓名","value":"苏林"},{"name":"qq号","value":123456}]}]
     * total_price : 434700
     * status : 10
     * total_goods : 1
     */

    public String _id; //订单编号
    public long total_price; //总价格
    public int status; //状态
    public int total_goods; //商品总件数
    public List<ShopGoodsEntity> goods_info;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._id);
        dest.writeLong(this.total_price);
        dest.writeInt(this.status);
        dest.writeInt(this.total_goods);
        dest.writeList(this.goods_info);
    }

    public MallOrderEntity() {
    }

    protected MallOrderEntity(Parcel in) {
        this._id = in.readString();
        this.total_price = in.readLong();
        this.status = in.readInt();
        this.total_goods = in.readInt();
        this.goods_info = new ArrayList<ShopGoodsEntity>();
        in.readList(this.goods_info, ShopGoodsEntity.class.getClassLoader());
    }

    public static final Parcelable.Creator<MallOrderEntity> CREATOR = new Parcelable.Creator<MallOrderEntity>() {
        @Override
        public MallOrderEntity createFromParcel(Parcel source) {
            return new MallOrderEntity(source);
        }

        @Override
        public MallOrderEntity[] newArray(int size) {
            return new MallOrderEntity[size];
        }
    };
}
