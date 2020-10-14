package com.snqu.shopping.data.home.entity;

public class ShareEntity {

    public ShareBean C;
    public ShareBean P;
    public ShareBean D;
    public ShareBean V;
    public ShareBean B;

    @Override
    public String toString() {
        return "ShareEntity{" +
                "C=" + C +
                ", P=" + P +
                ", D=" + D +
                ", V=" + V +
                ", B=" + B +
                '}';
    }

    public static class ShareBean{

        public ShareBean(String code, String share) {
            this.code = code;
            this.share = share;
        }

        public String code;
        public String share;

        @Override
        public String toString() {
            return "ShareBean{" +
                    "code='" + code + '\'' +
                    ", share='" + share + '\'' +
                    '}';
        }
    }
}

