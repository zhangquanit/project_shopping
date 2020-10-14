package com.snqu.shopping.data.mall.entity;

import com.snqu.shopping.data.mall.entity.address.AddressEntity;
import com.snqu.shopping.data.mall.entity.flow.FlowEntity;

import java.util.List;

/**
 * 订单详情
 */
public class MallOrderDetailEntity {

    public String _id; //订单号
    public String user_id;

    public AddressEntity address; //收货地址
    public List<ShopGoodsEntity> goods_info; //商品信息
    public long total_price; //总价
    public long pay_price;//支付金额
    public String pay_type;
    public long order_time; //下单时间
    public long timeout; //订单过期时间
    public long pay_time; //支付时间
    public int status; //0:已取消 10:已下单待支付 20:已支付待发货 30:已发货待确认收货 40已完成
    public List<ConvertEntity> admin_user_show_in;//兑换码
    public long deliver_time;//
    public String express_code;//：快递编码
    public String express_no;// 快递单号
    public CommentEntity comment;//评价
    public FlowEntity flow;//物流

    public String status_excuse;//


}
