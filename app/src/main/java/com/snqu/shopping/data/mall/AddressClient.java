package com.snqu.shopping.data.mall;

import com.android.util.db.EasyDB;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.base.ResponseDataObject;
import com.snqu.shopping.data.base.RestClient;
import com.snqu.shopping.data.mall.entity.address.AddressEntity;
import com.snqu.shopping.data.user.UserClient;

import java.util.List;

import io.reactivex.Observable;

/**
 * @author 张全
 */
public class AddressClient {

    /**
     * 新增地址
     *
     * @param addressEntity
     * @return
     */
    public static Observable<ResponseDataObject<Object>> doAddAddress(AddressEntity addressEntity) {
        return RestClient.getService(AddressApi.class)
                .addAddress(addressEntity);
    }

    /**
     * 修改地址
     *
     * @param addressEntity
     * @return
     */
    public static Observable<ResponseDataObject<Object>> doUpdateAddress(AddressEntity addressEntity) {
        return RestClient.getService(AddressApi.class)
                .updateAddress(addressEntity);
    }

    /**
     * 地址列表
     *
     * @return
     */
    public static Observable<ResponseDataArray<AddressEntity>> doGetAddress() {
        return RestClient.getService(AddressApi.class)
                .getAddress();
    }

    /**
     * 删除地址
     *
     * @return
     */
    public static Observable<ResponseDataObject<Object>> doDelAddress(String id) {
        return RestClient.getService(AddressApi.class)
                .deleteAddress(id);
    }

    /*
     ************************************** 定位&地址管理 ****************************************
     */

    public static void saveDefaultAddress(AddressEntity addressEntity) {
        EasyDB.with(AddressEntity.class, UserClient.getUser()._id).delete();
        if (null != addressEntity)
            EasyDB.with(AddressEntity.class, UserClient.getUser()._id).insert(addressEntity);
    }

    public static AddressEntity getDefaultAddress() {
        List<AddressEntity> addressList = EasyDB.with(AddressEntity.class, UserClient.getUser()._id).query();
        for (AddressEntity item : addressList) {
            if (item.is_default == 1) {
                return item;
            }
        }
        return null;
    }
}
