package com.snqu.shopping.ui.mall.address;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.android.util.ext.ToastUtil;
import com.android.util.text.StringUtil;
import com.anroid.base.SimpleFrag;
import com.anroid.base.SimpleFragAct;
import com.anroid.base.ui.StatusBar;
import com.snqu.shopping.R;
import com.snqu.shopping.common.Constant;
import com.snqu.shopping.common.event.PushEvent;
import com.snqu.shopping.data.base.NetReqResult;
import com.snqu.shopping.data.mall.entity.address.AddressEntity;
import com.snqu.shopping.data.mall.entity.address.AreaEntity;
import com.snqu.shopping.data.mall.entity.address.PoiEntity;
import com.snqu.shopping.ui.mall.address.helper.SelectCityCallBack;
import com.snqu.shopping.ui.mall.address.helper.SelectCityDialog;
import com.snqu.shopping.ui.mall.viewmodel.AddressViewModel;
import com.snqu.shopping.util.PatternUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 新增地址
 *
 * @author 张全
 */
public class AddressAddFrag extends SimpleFrag {
    @BindView(R.id.et_user)
    EditText et_user; //收货人
    @BindView(R.id.address_et_phone)
    EditText et_phone; //电话号码
    @BindView(R.id.address_detail)
    TextView et_area;//所在地区
    @BindView(R.id.address_door)
    EditText et_address;//详细地址
    @BindView(R.id.address_hint)
    ImageView iv_default; //设为默认


    private static final String PARAM = "AddressEntity";
    private AddressEntity mAddressEntity;
    private String mProvince;
    private String mCity;
    private PoiEntity mPoiEntity;
    private AddressViewModel mAddressViewModel;
    private boolean isReq;
    private AreaEntity areaEntity;


    public static void start(Context ctx) {
        SimpleFragAct.start(ctx, new SimpleFragAct.SimpleFragParam("新增地址", AddressAddFrag.class));
    }

    public static void start(Context ctx, AddressEntity addressEntity) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(PARAM, addressEntity);
        SimpleFragAct.start(ctx, new SimpleFragAct.SimpleFragParam("编辑地址", AddressAddFrag.class, bundle));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.address_add_fragment;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        ButterKnife.bind(this, mView);
        StatusBar.setStatusBar(mContext, true, getTitleBar());
        addAction(Constant.Event.ADDRESS_CHOOSE_POI);
        Bundle arguments = getArguments();
        if (null != arguments) {
            mAddressEntity = (AddressEntity) arguments.getSerializable(PARAM);
        }
        initView();
        initData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PushEvent event) {
        if (TextUtils.equals(event.getAction(), Constant.Event.ADDRESS_CHOOSE_POI)) {  //选择地址
            mPoiEntity = (PoiEntity) event.getData();
            mCity = mPoiEntity.city;
            mProvince = mPoiEntity.province;
            et_area.setText(mPoiEntity.name);
        }
    }

    private void initView() {
        getTitleBar().setBackgroundColor(Color.WHITE);
        findViewById(R.id.address_btn_del).setVisibility(null != mAddressEntity ? View.VISIBLE : View.GONE);

        if (null != mAddressEntity) {
            areaEntity = mAddressEntity.getAreaEntity();
            et_user.setText(mAddressEntity.name);
            if (!TextUtils.isEmpty(mAddressEntity.name)) {
                et_user.setSelection(mAddressEntity.name.length());//将光标移至文字末尾
            }

            et_phone.setText(mAddressEntity.phone);
            et_area.setText(mAddressEntity.getAreaText());
            et_address.setText(mAddressEntity.address);
            iv_default.setSelected(mAddressEntity.is_default == 1);
        }
    }

    private void initData() {
        mAddressViewModel = ViewModelProviders.of(this).get(AddressViewModel.class);
        mAddressViewModel.mNetReqResultLiveData.observe(getLifecycleOwner(), new Observer<NetReqResult>() {
            @Override
            public void onChanged(@Nullable NetReqResult netReqResult) {
                isReq = false;
                if (TextUtils.equals(netReqResult.tag, AddressViewModel.TAG_ADD)) {
                    ToastUtil.show(netReqResult.successful ? "地址已保存" : "地址保存失败，请重新设置");
                } else if (TextUtils.equals(netReqResult.tag, AddressViewModel.TAG_UPDATE)) {
                    ToastUtil.show(netReqResult.successful ? "地址已保存" : "地址保存失败，请重新设置");
                } else if (TextUtils.equals(netReqResult.tag, AddressViewModel.TAG_DEL)) {
                    ToastUtil.show(netReqResult.successful ? "地址已删除" : "删除失败，请重试");
                }
                if (netReqResult.successful) {
//                    EventBus.getDefault().post(new PushEvent(Constant.Event.SAVE_ADDRESS));
                    finish();
                }
            }
        });
    }

    @OnClick({R.id.address_hint, R.id.address_btn_save, R.id.address_detail, R.id.address_detail_bar, R.id.address_btn_del})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.address_hint:   //默认地址
                v.setSelected(!v.isSelected());
                if (v.isSelected()) {
                    ToastUtil.show("已设为默认地址");
                }
                break;
            case R.id.address_detail:
            case R.id.address_detail_bar:  //选择地区
                showCitySelectDialog();
                break;
            case R.id.address_btn_del:  //删除
                mAddressViewModel.delAddress(mAddressEntity);
                break;
            case R.id.address_btn_save: //保存地址
                if (isReq) return;
                String user = StringUtil.trim(et_user);
                if (TextUtils.isEmpty(user)) {
                    ToastUtil.show("请填写收件人姓名");
                    return;
                }
                if (!PatternUtil.checkNonCharacters(user)) {
                    ToastUtil.show("收货人姓名填写错误，请输入20个以下的汉字、数字和英文");
                    return;
                }

                String phone = StringUtil.trim(et_phone);
                if (TextUtils.isEmpty(phone)) {
                    ToastUtil.show("请填写手机号码");
                    return;
                }
                if (!PatternUtil.isValidatePhone(phone)) {
                    ToastUtil.show("请输入11位手机号码");
                    return;
                }

                String sketch = StringUtil.trim(et_area);
                if (TextUtils.isEmpty(sketch)) {
                    ToastUtil.show("请选择所在地区");
                    return;
                }
                String address = StringUtil.trim(et_address);
                if (TextUtils.isEmpty(address)) {
                    ToastUtil.show("请填写详细地址");
                    return;
                }
                if (address.length() > 30) {
                    ToastUtil.show("详细地址不超过30个字符");
                    return;
                }
                int isDefault = iv_default.isSelected() ? 1 : -1;

                AddressEntity entity = new AddressEntity();
                if (null != mAddressEntity) entity._id = mAddressEntity._id;
                entity.name = user;
                entity.phone = phone;

                if (null != areaEntity) {
                    if (null != areaEntity.getProvinceEntity())
                        entity.province = areaEntity.getProvinceEntity().getName();
                    if (null != areaEntity.getCityEntity())
                        entity.city = areaEntity.getCityEntity().getName();
                    if (null != areaEntity.getCountyEntity())
                        entity.area = areaEntity.getCountyEntity().getName();
                }
                entity.address = address;
                entity.is_default = isDefault;


                isReq = true;
                if (null == mAddressEntity) { //新增
                    mAddressViewModel.addAddress(entity);
                } else {   //编辑
                    mAddressViewModel.updateAddress(entity);
                }

                break;
        }
    }

    private void showCitySelectDialog() {
        SelectCityDialog selectCityDialog = new SelectCityDialog();
        Bundle bundle = new Bundle();
        bundle.putParcelable(SelectCityDialog.EXTRA_ADDRESS_ENTITY, areaEntity);
        selectCityDialog.setArguments(bundle);
        selectCityDialog.setSelectCityCallBack(new SelectCityCallBack() {
            @Override
            public void selected(AreaEntity area) {
                areaEntity = area;
                StringBuffer stringBuffer = new StringBuffer();
                if (null != areaEntity.getProvinceEntity())
                    stringBuffer.append(areaEntity.getProvinceEntity().getName());
                if (null != areaEntity.getCityEntity())
                    stringBuffer.append(areaEntity.getCityEntity().getName());
                if (null != areaEntity.getCountyEntity())
                    stringBuffer.append(areaEntity.getCountyEntity().getName());
                et_area.setText(stringBuffer.toString());
            }
        });
        selectCityDialog.show(getChildFragmentManager(), "SelectCityDialog");
    }


    @Override
    public String getPageName() {
        return null == mAddressEntity ? "AddressAdd" : "AddressEdit";
    }
}
