package com.snqu.shopping.data.mall.entity;

import java.io.Serializable;
import java.util.List;

public class MallGoodShareInfoEntity implements Serializable {

    /**
     * _id : 5f45d53e4a71d023df104f65
     * name : 九阳面条机全自动智能自动加水多功能压面机
     * describe : 商品名称：九阳面条机全自动智能自动加水多功能压面机家用电动饺子皮机600g容量1-5人M6-L20商品编号：41967615966店铺： 九阳官方旗舰店商品毛重：6.7kg操控方式：按键产品类别：立式智能面条机清洗方式：可拆洗产品功能：称重，吹风，自动滴水螺杆材质：不锈钢
     * selling_price : 129900
     * banner_img_txt : ["https://resource-t.xin1.cn/test/static/images/20200826/1f6c329ec64249f9b21ef8e811302ee0.jpg","https://resource-t.xin1.cn/test/static/images/20200902/271a1a5f690d17bfec80423ecb048f36.jpg","https://resource-t.xin1.cn/test/static/images/20200902/8cfe5262364098c2be9e8f5193be2e7d.jpg","https://resource-t.xin1.cn/test/static/images/20200902/f64454adf2d5aa0cd710b1002f40abe1.jpg"]
     * url : http://www.baidu.com?_id=5f45d53e4a71d023df104f65
     */

    public String _id;
    public String name;
    public String describe;
    public int selling_price;
    public String url;
    public List<String> banner_img_txt;
}
