package com.snqu.shopping.ui.mall.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.MultipleItemRvAdapter;
import com.snqu.shopping.data.mall.entity.MallRecommendEntity;

import java.util.List;

public class MallRecommendAdapter extends MultipleItemRvAdapter<MallRecommendEntity, BaseViewHolder> {

    public static final int TYPE_ACT = 1; //活动
    public static final int TYPE_GOOD = 2; //商品

    public MallRecommendAdapter(@Nullable List<MallRecommendEntity> data) {
        super(data);

        //构造函数若有传其他参数可以在调用finishInitialize()之前进行赋值，赋值给全局变量
        //这样getViewType()和registerItemProvider()方法中可以获取到传过来的值
        //getViewType()中可能因为某些业务逻辑，需要将某个值传递过来进行判断，返回对应的viewType
        //registerItemProvider()中可以将值传递给ItemProvider

        //If the constructor has other parameters, it needs to be assigned before calling finishInitialize() and assigned to the global variable
        // This getViewType () and registerItemProvider () method can get the value passed over
        // getViewType () may be due to some business logic, you need to pass a value to judge, return the corresponding viewType
        //RegisterItemProvider() can pass value to ItemProvider

        finishInitialize();
    }

    @Override
    protected int getViewType(MallRecommendEntity entity) {
        if (entity.place == 1) {
            return TYPE_ACT;
        }
        return TYPE_GOOD;
    }

    @Override
    public void registerItemProvider() {
        mProviderDelegate.registerProvider(new MallRecommendActivityProvider());
        mProviderDelegate.registerProvider(new MallRecommendGoodProvider());
    }

}