package com.snqu.shopping.data.mall.entity.flow;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class FlowDetailEntity implements Parcelable, Serializable {
    public String time;
    public String ftime;
    public String context; //快件离开 【天津中转部】 已发往 【成都中转】


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.time);
        dest.writeString(this.ftime);
        dest.writeString(this.context);
    }

    public FlowDetailEntity() {
    }

    protected FlowDetailEntity(Parcel in) {
        this.time = in.readString();
        this.ftime = in.readString();
        this.context = in.readString();
    }

    public static final Parcelable.Creator<FlowDetailEntity> CREATOR = new Parcelable.Creator<FlowDetailEntity>() {
        @Override
        public FlowDetailEntity createFromParcel(Parcel source) {
            return new FlowDetailEntity(source);
        }

        @Override
        public FlowDetailEntity[] newArray(int size) {
            return new FlowDetailEntity[size];
        }
    };
}
