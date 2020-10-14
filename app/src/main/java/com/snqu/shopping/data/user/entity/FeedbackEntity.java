package com.snqu.shopping.data.user.entity;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Keep;

@Keep
public class FeedbackEntity implements Parcelable {
    public String _id; //":"5eba5b1e1d402680e8000d63",                //类型：String  必有字段  备注：无
    public String phone;//":"mock",                //类型：String  必有字段  备注：电话号码
    public String content;//":"我是反馈内容",                //类型：String  必有字段  备注：反馈内容
    public String[] enclosure;//
    public String reply_content;//":"mock",                //类型：String  必有字段  备注：回复内
    public int status; //":1,                //类型：Number  必有字段  备注：状态
    public String view_time;//":1589271745,                //类型：Number  必有字段  备注：查看时间
    public Long itime;//":1589271326                //类型：Number  必有字段  备注：提交时间
    public String file;//"/dev/static/images/20200512/8dc10c0eb8f0058a3de9498e8b34b078.png",                //类型：String  必有字段  备注：无
    public String ful_url;//
    public boolean local_show; //控制本地是否显示
    public Uri bitmap;

    public FeedbackEntity(){

    }

    protected FeedbackEntity(Parcel in) {
        _id = in.readString();
        phone = in.readString();
        content = in.readString();
        enclosure = in.createStringArray();
        reply_content = in.readString();
        status = in.readInt();
        view_time = in.readString();
        if (in.readByte() == 0) {
            itime = null;
        } else {
            itime = in.readLong();
        }
        file = in.readString();
        ful_url = in.readString();
    }

    public static final Creator<FeedbackEntity> CREATOR = new Creator<FeedbackEntity>() {
        @Override
        public FeedbackEntity createFromParcel(Parcel in) {
            return new FeedbackEntity(in);
        }

        @Override
        public FeedbackEntity[] newArray(int size) {
            return new FeedbackEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(_id);
        dest.writeString(phone);
        dest.writeString(content);
        dest.writeStringArray(enclosure);
        dest.writeString(reply_content);
        dest.writeInt(status);
        dest.writeString(view_time);
        if (itime == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(itime);
        }
        dest.writeString(file);
        dest.writeString(ful_url);
    }
}
