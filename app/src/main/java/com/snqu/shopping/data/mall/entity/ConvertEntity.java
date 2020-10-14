package com.snqu.shopping.data.mall.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 兑换码
 */
public class ConvertEntity implements Serializable, Parcelable {
    public String name;
    public String value;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.value);
    }

    public ConvertEntity() {
    }

    protected ConvertEntity(Parcel in) {
        this.name = in.readString();
        this.value = in.readString();
    }

    public static final Creator<ConvertEntity> CREATOR = new Creator<ConvertEntity>() {
        @Override
        public ConvertEntity createFromParcel(Parcel source) {
            return new ConvertEntity(source);
        }

        @Override
        public ConvertEntity[] newArray(int size) {
            return new ConvertEntity[size];
        }
    };
}
