package com.snqu.shopping.data.user.entity;

import androidx.annotation.Keep;

@Keep
public class FeedbackBody {
    public String phone;
    public String log_url;
    public String content;
    public String[] enclosure;

    public FeedbackBody(String phone, String log_url, String content, String[] enclosure) {
        this.phone = phone;
        this.log_url = log_url;
        this.content = content;
        this.enclosure = enclosure;
    }
}
