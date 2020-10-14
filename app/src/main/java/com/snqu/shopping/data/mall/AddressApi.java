package com.snqu.shopping.data.mall;

import com.snqu.shopping.data.ApiHost;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.base.ResponseDataObject;
import com.snqu.shopping.data.mall.entity.address.AddressEntity;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * 地址管理相关api
 *
 * @author 张全
 */
public interface AddressApi {
    /**
     * 新增地址
     */
    @POST(ApiHost.ADDRESS_ADD)
    Observable<ResponseDataObject<Object>> addAddress(@Body AddressEntity data);

    /**
     * 修改地址
     */
    @POST(ApiHost.ADDRESS_UPDATE)
    Observable<ResponseDataObject<Object>> updateAddress(@Body AddressEntity data);

    /**
     * 地址列表
     */
    @GET(ApiHost.ADDRESS_LIST)
    Observable<ResponseDataArray<AddressEntity>> getAddress();

    /**
     * 删除地址
     *
     * @return
     */
    @POST(ApiHost.ADDRESS_DEL)
    @FormUrlEncoded
    Observable<ResponseDataObject<Object>> deleteAddress(@Field("_id") String id);

}
