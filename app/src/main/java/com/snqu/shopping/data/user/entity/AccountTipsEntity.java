package com.snqu.shopping.data.user.entity;


public class AccountTipsEntity {
    public AccountTip tips;

    public class AccountTip{
        public String title="";
        public String msg="";

        @Override
        public String toString() {
            return "AccountTipsEntity{" +
                    "title='" + title + '\'' +
                    ", msg='" + msg + '\'' +
                    '}';
        }
    }
}


