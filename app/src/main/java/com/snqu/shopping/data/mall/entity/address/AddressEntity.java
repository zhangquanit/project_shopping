package com.snqu.shopping.data.mall.entity.address;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

import com.android.util.db.Key;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * 收货地址信息
 *
 * @author 张全
 */
@Keep
public class AddressEntity implements Serializable, Parcelable {
    @Key
    public String _id;
    public String user_id; //用户id
    public String name;
    public String phone; //原始手机号
    public String phone_txt;//显示的带*手机号

    public String province; //省
    public String city; //市
    public String area; //区
    public List<String> provinces; //省-市-区
    public String address;
    public String address_txt; //详细地址

    @SerializedName("default")
    public int is_default;//默认  是否默认 1-是 -1 否

    public String getAreaText() {
        StringBuffer sb = new StringBuffer();
        if (null != provinces) {
            for (String str : provinces) {
                sb.append(str);
            }
        }
        return sb.toString();
    }

    public AreaEntity getAreaEntity() {
        AreaEntity areaEntity = new AreaEntity();
        if (null != provinces) {
            if (provinces.size() >= 1) {
                areaEntity.setProvinceEntity(new ProvinceEntity(provinces.get(0), ""));
            }
            if (provinces.size() >= 2) {
                areaEntity.setCityEntity(new ProvinceEntity(provinces.get(1), ""));
            }
            if (provinces.size() >= 3) {
                areaEntity.setCountyEntity(new ProvinceEntity(provinces.get(2), ""));
            }
        }
        return areaEntity;
    }

    /**
     * - {
     * "_id":"5f45fb3668640000c1000b64",  //类型：String  必有字段  备注：无
     * "name":"苏林是",                //类型：String  必有字段  备注：收货人
     * "phone":"173****1300",          //类型：String  必有字段  备注：电话号码
     * "provinces": - [               //类型：Array  必有字段  备注：无
     * "四川省"                //类型：String  必有字段  备注：无
     * ],
     * "address":"府城大道江湖选哪个是48号",    //类型：String  必有字段  备注：无
     * "default":"1",                //类型：String  必有字段  备注：是否默认 1-是 -1 否
     * "address_txt":"四川省成都市高新区府城大道江湖选哪个是48号"   //类型：String  必有字段  备注：详细地址
     * }
     */

    @Override
    public String toString() {
        return "AddressEntity{" +
                "_id='" + _id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", phone_txt='" + phone_txt + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", area='" + area + '\'' +
                ", provinces=" + provinces +
                ", address='" + address + '\'' +
                ", address_txt='" + address_txt + '\'' +
                ", is_default=" + is_default +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this._id);
        dest.writeString(this.user_id);
        dest.writeString(this.name);
        dest.writeString(this.phone);
        dest.writeString(this.phone_txt);
        dest.writeString(this.province);
        dest.writeString(this.city);
        dest.writeString(this.area);
        dest.writeStringList(this.provinces);
        dest.writeString(this.address);
        dest.writeString(this.address_txt);
        dest.writeInt(this.is_default);
    }

    public AddressEntity() {
    }

    protected AddressEntity(Parcel in) {
        this._id = in.readString();
        this.user_id = in.readString();
        this.name = in.readString();
        this.phone = in.readString();
        this.phone_txt = in.readString();
        this.province = in.readString();
        this.city = in.readString();
        this.area = in.readString();
        this.provinces = in.createStringArrayList();
        this.address = in.readString();
        this.address_txt = in.readString();
        this.is_default = in.readInt();
    }

    public static final Parcelable.Creator<AddressEntity> CREATOR = new Parcelable.Creator<AddressEntity>() {
        @Override
        public AddressEntity createFromParcel(Parcel source) {
            return new AddressEntity(source);
        }

        @Override
        public AddressEntity[] newArray(int size) {
            return new AddressEntity[size];
        }
    };
}
