package com.snqu.shopping.data.home.entity;

import java.util.List;

public class HomeLayoutEntity {

    /**
     * 首页分类页
     */
    public Page page;
    /**
     * 首页分类列表
     */
    public List<CategoryEntity> category;


    public class Page{
        public List<Data> top;
        public List<Data> middle;

        @Override
        public String toString() {
            return "Page{" +
                    "top=" + top +
                    ", middle=" + middle +
                    '}';
        }
    }


    public class Data {
        public String type;
        public String bgImage;
        public String bgColor;
        public String bgImageUrl;
        public List<AdvertistEntity> data;
        public int lineHeight ; //每个item之间的间距，不同类型type大间距，同样类型小间距
        public String margin;//是否有间隙:为null无间隔/有值则有间隔',
        @Override
        public String toString() {
            return "Page{" +
                    "type=" + type +
                    ", bgImage='" + bgImage + '\'' +
                    ", bgColor='" + bgColor + '\'' +
                    ", data=" + data +
                    '}';
        }
    }




//
//    public static class TopBean {
//        /**
//         * type : 1001
//         * bgImage : https://resource.xin1.cn/p/static/images/20200630/80c5c753da8633fcf1cf1ef649fb0718.png
//         * bgColor : #FF8202
//         * data : [{"_id":"5efed8430a6c8ef7840010f7","bgImage":"https://resource.xin1.cn/p/static/images/20200630/80c5c753da8633fcf1cf1ef649fb0718.png","bgColor":"#FF8202","show_check_enable":1,"show_end_time":1,"show_start_time":1,"name":"京东充值话费优惠劵","image":"https://resource-t.xin1.cn/dev/static/images/20200702/7eae7ee9f3967b2a08d04ed927167c88.png","attribute":null,"authPlatform":null,"link_type":["2","2"],"needLogin":1,"needAuth":0,"open_third_app":0,"platform":"D","type":"image","direct":"2","protocol":"2","direct_protocal":"2","link_url":"","tid":"https://wqs.jd.com/wxsq_project/recharge/pingou/pingou.html","item_source":"D"},{"_id":"5efed8430a6c8ef7840010f7","bgImage":"https://resource.xin1.cn/p/static/images/20200630/80c5c753da8633fcf1cf1ef649fb0718.png","bgColor":"#FF8202","show_check_enable":1,"show_start_time":1594026237,"show_end_time":1596704636,"name":"京东充值话费优惠劵","image":"https://resource-t.xin1.cn/dev/static/images/20200702/7eae7ee9f3967b2a08d04ed927167c88.png","attribute":null,"authPlatform":null,"link_type":["2","2"],"needLogin":1,"needAuth":0,"open_third_app":0,"platform":"D","type":"image","direct":"2","protocol":"2","direct_protocal":"2","link_url":"","tid":"https://wqs.jd.com/wxsq_project/recharge/pingou/pingou.html","item_source":"D"}]
//         */
//
//        public int type;
//        public String bgImage;
//        public String bgColor;
//        public List<DataBean> data;
//
//        public static class DataBean {
//            /**
//             * _id : 5efed8430a6c8ef7840010f7
//             * bgImage : https://resource.xin1.cn/p/static/images/20200630/80c5c753da8633fcf1cf1ef649fb0718.png
//             * bgColor : #FF8202
//             * show_check_enable : 1
//             * show_end_time : 1
//             * show_start_time : 1
//             * name : 京东充值话费优惠劵
//             * image : https://resource-t.xin1.cn/dev/static/images/20200702/7eae7ee9f3967b2a08d04ed927167c88.png
//             * attribute : null
//             * authPlatform : null
//             * link_type : ["2","2"]
//             * needLogin : 1
//             * needAuth : 0
//             * open_third_app : 0
//             * platform : D
//             * type : image
//             * direct : 2
//             * protocol : 2
//             * direct_protocal : 2
//             * link_url :
//             * tid : https://wqs.jd.com/wxsq_project/recharge/pingou/pingou.html
//             * item_source : D
//             */
//
//            public String _id;
//            public String bgImage;
//            public String bgColor;
//            public int show_check_enable;
//            public int show_end_time;
//            public int show_start_time;
//            public String name;
//            public String image;
//            public Object attribute;
//            public Object authPlatform;
//            public int needLogin;
//            public int needAuth;
//            public int open_third_app;
//            public String platform;
//            public String type;
//            public String direct;
//            public String protocol;
//            public String direct_protocal;
//            public String link_url;
//            public String tid;
//            public String item_source;
//            public List<String> link_type;
//        }
//    }


    @Override
    public String toString() {
        return "HomeLayoutEntity{" +
                "page=" + page +
                ", category=" + category +
                '}';
    }
}
