package com.snqu.shopping.data.red.entity;

import com.snqu.shopping.R;

import java.io.Serializable;

/**
 * @author 张全
 */
public class BigVInfo implements Serializable {

    /**
     * _id : 5d6e18a493869e2b9254c2ab
     * username : 虫虫Chonney
     * avatar : /static/images/20190903/ca31db1f497383325fc41f94050f7adc.jpg
     * source : 2
     * source_text : 小红书
     * good_info :
     */

    public String _id;
    public String name;
    public String avatar;
    public int source; //1 新浪微博  2 小红书 3 腾讯微博
    public String source_text;

    public int getSourceDrawable() {
        if (source == 1) {
            return R.drawable.reds_bigv_weibo;
        } else if (source == 2) {
            return R.drawable.reds_bigv_xhs;
        } else if (source == 3) {
            return R.drawable.reds_bigv_tx;
        }
        return -1;
    }

    /**
     * 大V主页
     *
     * @return
     */
    public int getDetailSourceDrawable() {
        if (source == 1) {
            return R.drawable.reds_bigv_detail_weibo;
        } else if (source == 2) {
            return R.drawable.reds_bigv_detail_xhs;
        } else if (source == 3) {
            return R.drawable.reds_bigv_detail_tx;
        }
        return -1;
    }

    public String getSourceText() {
        return source_text + "知名博主";
    }
}
