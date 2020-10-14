package com.snqu.shopping.data.home.entity;

//public class PlateInfo implements Serializable {
//    /**
//     * _id : 5d6610951d40261858000696
//     * username : 77
//     * position : 1-2
//     * plate_id : 5d65eecc1d402618240030f2
//     * sort : 1
//     * goods_count : 4
//     * itime : 1566970005
//     * utime : 1566970005
//     * sub_title : 12
//     * icon : /static/images/20190828/accfb3f3544b75436d353baaed849f8d.jpg
//     * is_dev : 0
//     * dev_code : 3
//     * goods : {"count":0,"data":[],"pageSize":10,"pageCount":0}
//     * ader : []
//     */
//
//    public String _id;
//    public String position;
//    public String plate_id;
//    public int sort;
//    public int goods_count; //2双行 4单行
//    public long itime;
//    public long utime;
//    public String title;//在超市详情中使用
//    public String sub_title;
//    public int ch_set_icon;//是否设置二级图标
//    public String icon;
//    public int is_dev; //is_dev = 1 就是专属人工编辑板块  比如红人街
//    public String dev_code;
//    public PlateInfoGoods goods;
//    public ImageInfo show_image;
//
//    @Override
//    public String toString() {
//        return "PlateInfo{" +
//                "_id='" + _id + '\'' +
//                ", position='" + position + '\'' +
//                ", plate_id='" + plate_id + '\'' +
//                ", sort=" + sort +
//                ", goods_count=" + goods_count +
//                ", itime=" + itime +
//                ", utime=" + utime +
//                ", title='" + title + '\'' +
//                ", sub_title='" + sub_title + '\'' +
//                ", ch_set_icon=" + ch_set_icon +
//                ", icon='" + icon + '\'' +
//                ", is_dev=" + is_dev +
//                ", dev_code='" + dev_code + '\'' +
//                ", goods=" + goods +
//                ", show_image=" + show_image +
//                '}';
//    }
//
//    /**
//     * 广告
//     *
//     * @return
//     */
//    public boolean isAd() {
//        if (null != show_image && show_image.status == 1) {
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * 单行
//     *
//     * @return
//     */
//    public boolean isSingleLine() {
//        return goods_count == 4;
//    }
//
//    public PlateCategoryEntity toChannelEntity() {
//        PlateCategoryEntity plateCategoryEntity = new PlateCategoryEntity();
//        plateCategoryEntity.id = plate_id;
//        plateCategoryEntity.name = title;
//        plateCategoryEntity.icon = icon;
//        plateCategoryEntity.dev_code = dev_code;
//        return plateCategoryEntity;
//    }
//
//
//    public static class ImageInfo implements Serializable {
//        public int status; //1 是否开启广告
//        public String image; //广告图片
//        public String url;
//        public String platform;
//        public int needLogin;//需要登录
//        public int needAuth;// 需要授权
//        public List<String> authPlatform;//授权平台
//        public String end_time;
//        public String start_time;
//        public String[] link_type;
//        public String direct;
//        public String direct_protocal;
//        public String tid;
//        public String item_source;
//        public String link_url;
//        public int open_third_app = -1;
//
//        @Override
//        public String toString() {
//            return "ImageInfo{" +
//                    "status=" + status +
//                    ", image='" + image + '\'' +
//                    ", url='" + url + '\'' +
//                    ", platform='" + platform + '\'' +
//                    ", needLogin=" + needLogin +
//                    ", needAuth=" + needAuth +
//                    ", authPlatform=" + authPlatform +
//                    ", end_time='" + end_time + '\'' +
//                    ", start_time='" + start_time + '\'' +
//                    ", link_type=" + Arrays.toString(link_type) +
//                    ", direct='" + direct + '\'' +
//                    ", direct_protocal='" + direct_protocal + '\'' +
//                    ", tid='" + tid + '\'' +
//                    ", item_source='" + item_source + '\'' +
//                    ", link_url='" + link_url + '\'' +
//                    '}';
//        }
//    }
//
//    public static class PlateInfoGoods implements Serializable {
//        /**
//         * count : 0
//         * data : []
//         * pageSize : 10
//         * pageCount : 0
//         */
//
//        public int count;
//        public int pageSize;
//        public int pageCount;
//        public List<GoodsEntity> data;
//
//
//    }
//
//
//}