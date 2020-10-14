package com.snqu.shopping.data.mall.entity;

import java.util.List;

public class MallRecommendEntity {

    /**
     * _id : 5f477f494a71d0525f734b33
     * images : /test/static/images/20200827/6d10ba1c7a1b1a7ae4a8f050621ff186.jpg
     * place : 2
     * activity_id :
     * goods_details : 5f45d53e4a71d023df104f65
     * goods : {"_id":"5f45d53e4a71d023df104f65","name":"九阳面条机全自动智能自动加水多功能压面机","describe":"商品名称：九阳面条机全自动智能自动加水多功能压面机家用电动饺子皮机600g容量1-5人M6-L20商品编号：41967615966店铺： 九阳官方旗舰店商品毛重：6.7kg操控方式：按键产品类别：立式智能面条机清洗方式：可拆洗产品功能：称重，吹风，自动滴水螺杆材质：不锈钢","sold":"69","real_sold":0,"selling_price":129900}
     * images_url : https://resource-t.xin1.cn/test/static/images/20200827/6d10ba1c7a1b1a7ae4a8f050621ff186.jpg
     */

    public String _id;
    public String images;
    public int place;
    public String activity_id;
    public String goods_details;
    public List<ShopGoodsEntity> goods;
    public String images_url;
    public String url; //活动地址
}
