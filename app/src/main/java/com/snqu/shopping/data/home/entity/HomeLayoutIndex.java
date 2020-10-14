package com.snqu.shopping.data.home.entity;

import java.util.List;

/**
 * @author 张全
 */
public class HomeLayoutIndex {

    /**
     * plate : {"1-2":[{"_id":"5d6610951d40261858000696","username":"77","position":"1-2","plate_id":"5d65eecc1d402618240030f2","sort":1,"goods_count":4,"itime":1566970005,"utime":1566970005,"sub_title":"12","icon":"/static/images/20190828/accfb3f3544b75436d353baaed849f8d.jpg","is_dev":0,"dev_code":"3","goods":{"count":0,"data":[],"pageSize":10,"pageCount":0},"ader":[]}],"1-1":[{"_id":"5d6610951d40261858000697","username":"33","position":"1-1","plate_id":"5d65eecc1d402618240030f2","sort":1,"goods_count":0,"itime":1566970005,"utime":1566970005,"sub_title":"12","icon":"/static/images/20190828/accfb3f3544b75436d353baaed849f8d.jpg","is_dev":0,"dev_code":"3","goods":[],"ader":[]},{"_id":"5d6610951d40261858000698","username":"11","position":"1-1","plate_id":"5d65eeb91d40260f4c005e03","sort":2,"goods_count":0,"itime":1566970005,"utime":1566970005,"sub_title":"12","icon":"/static/images/20190828/accfb3f3544b75436d353baaed849f8d.jpg","is_dev":0,"dev_code":"3","goods":[],"ader":[]}]}
     * category : [{"_id":"5d5cdd8a1d402683a80041c3","pid":null,"username":"女警","icon":"","sort":50,"level":1,"idpath":["5d5cdd8a1d402683a80041c3"]},{"_id":"5d5cdd9a1d402683a80041c4","pid":"5d5cdd8a1d402683a80041c3","username":"飞天女警","icon":"","sort":50,"level":2,"idpath":["5d5cdd8a1d402683a80041c3","5d5cdd9a1d402683a80041c4"]}]
     */

    public PlateBean plate;
    public List<CategoryEntity> category;

    public static class PlateBean {
//        @SerializedName("1-2")
//        public List<PlateInfo> _$12;
//        @SerializedName("1-1")
//        public List<PlateInfo> _$11;

    }
}
