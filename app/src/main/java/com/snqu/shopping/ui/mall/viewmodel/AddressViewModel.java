package com.snqu.shopping.ui.mall.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.event.PushEvent;
import com.snqu.shopping.common.viewmodel.BaseAndroidViewModel;
import com.snqu.shopping.data.base.BaseResponseObserver;
import com.snqu.shopping.data.base.HttpResponseException;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.base.ResponseDataArray;
import com.snqu.shopping.data.base.ResponseDataObject;
import com.snqu.shopping.data.mall.AddressClient;
import com.snqu.shopping.data.mall.entity.address.AddressEntity;
import com.snqu.shopping.data.user.UserClient;
import com.snqu.shopping.data.user.entity.UserEntity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * 地址
 *
 * @author 张全
 */
public class AddressViewModel extends BaseAndroidViewModel {
    public static final String TAG_ADD = "TAG_ADD";
    public static final String TAG_UPDATE = "TAG_UPDATE";
    public static final String TAG_DEL = "TAG_DEL";
    public static final String TAG_LIST = "TAG_LIST";
    public MutableLiveData<NetReqResult> mNetReqResultLiveData = new MutableLiveData<>();

    public AddressViewModel(@NonNull Application application) {
        super(application);
    }


    /**
     * 添加地址
     *
     * @param addressEntity
     */
    public void addAddress(AddressEntity addressEntity) {
        executeNoMapHttp(AddressClient.doAddAddress(addressEntity), new BaseResponseObserver<ResponseDataObject<Object>>() {
            @Override
            public void onSuccess(ResponseDataObject<Object> value) {
                EventBus.getDefault().post(new PushEvent(Constant.Event.ADDRESS_UPDATE, addressEntity));
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_ADD, null, true));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_ADD, e.alert, false));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    /**
     * 更新地址
     *
     * @param addressEntity
     */
    public void updateAddress(AddressEntity addressEntity) {
        executeNoMapHttp(AddressClient.doUpdateAddress(addressEntity), new BaseResponseObserver<ResponseDataObject<Object>>() {
            @Override
            public void onSuccess(ResponseDataObject<Object> value) {
                EventBus.getDefault().post(new PushEvent(Constant.Event.ADDRESS_UPDATE, addressEntity));
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_UPDATE, null, true, addressEntity));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_UPDATE, e.alert, false));
            }

            @Override
            public void onEnd() {

            }
        });
    }

    public void updateUserAddress(List<AddressEntity> addressEntities) {
        if (null == addressEntities) addressEntities = new ArrayList<>();

        UserEntity user = UserClient.getUser();
        //更新用户对象中的地址
        AddressEntity defaultAddress = null;
        for (AddressEntity item : addressEntities) {
            if (item.is_default == 1) {
                defaultAddress = item;
                defaultAddress.user_id = user._id;
                break;
            }
        }

        //更新默认地址
        AddressClient.saveDefaultAddress(defaultAddress);
    }

    public void getAddressList() {
        if(UserClient.isLogin()) {
            executeNoMapHttp(AddressClient.doGetAddress(), new BaseResponseObserver<ResponseDataArray<AddressEntity>>() {
                @Override
                public void onSuccess(ResponseDataArray<AddressEntity> value) {
                    updateUserAddress(value.getDataList());
                    mNetReqResultLiveData.setValue(new NetReqResult(TAG_LIST, null, true, value.getDataList()));
                }

                @Override
                public void onError(HttpResponseException e) {
                    mNetReqResultLiveData.setValue(new NetReqResult(TAG_LIST, e.alert, false));
                }

                @Override
                public void onEnd() {

                }
            });
        }
    }

    public void delAddress(AddressEntity addressEntity) {
        executeNoMapHttp(AddressClient.doDelAddress(addressEntity._id), new BaseResponseObserver<ResponseDataObject<Object>>() {
            @Override
            public void onSuccess(ResponseDataObject<Object> value) {
                EventBus.getDefault().post(new PushEvent(Constant.Event.ADDRESS_UPDATE, addressEntity));
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_DEL, null, true, addressEntity));
            }

            @Override
            public void onError(HttpResponseException e) {
                mNetReqResultLiveData.setValue(new NetReqResult(TAG_DEL, e.alert, false));
            }

            @Override
            public void onEnd() {

            }
        });
    }

}
