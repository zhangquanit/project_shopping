package com.snqu.shopping.util.statistics.task;

public enum NewTaskType {
    CLIPBOARD("商品标题查询成功", "NewTaskTwo"),
    ME("佣金查看成功", "NewTaskThree"), //查看购物佣金
    BIND_WX("微信号填写成功", "NewTaskFour"); // 填写微信号

    public String type;
    public String title;

    private NewTaskType(String title, String type) {
        this.title = title;
        this.type = type;
    }

}