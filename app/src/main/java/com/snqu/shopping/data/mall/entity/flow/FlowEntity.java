package com.snqu.shopping.data.mall.entity.flow;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FlowEntity implements Parcelable, Serializable {
    public String name;
    public String no;
    public List<FlowDetailEntity> detail;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.no);
        dest.writeList(this.detail);
    }

    public FlowEntity() {
    }

    protected FlowEntity(Parcel in) {
        this.name = in.readString();
        this.no = in.readString();
        this.detail = new ArrayList<FlowDetailEntity>();
        in.readList(this.detail, FlowDetailEntity.class.getClassLoader());
    }

    public static final Parcelable.Creator<FlowEntity> CREATOR = new Parcelable.Creator<FlowEntity>() {
        @Override
        public FlowEntity createFromParcel(Parcel source) {
            return new FlowEntity(source);
        }

        @Override
        public FlowEntity[] newArray(int size) {
            return new FlowEntity[size];
        }
    };
}