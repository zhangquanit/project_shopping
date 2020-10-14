package com.snqu.shopping.data.home.entity.artical;

import java.util.List;

/**
 * 文章
 */
public class ArticalEntity {
    public String _id;
    public String title;
    public String cover_image;
    public String description;
    public String type;
    public int share_wechat_open;
    public String jump_url;
    public long itime;
    public List<String> category;
    public List<String> tag;

}
