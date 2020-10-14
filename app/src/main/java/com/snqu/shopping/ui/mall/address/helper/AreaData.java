package com.snqu.shopping.ui.mall.address.helper;

import android.annotation.SuppressLint;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.snqu.shopping.data.mall.entity.address.ProvinceEntity;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.operators.observable.ObservableCreate;
import io.reactivex.schedulers.Schedulers;

public class AreaData {
    //省数据
    public static List<ProvinceEntity> provinceEntities;
    //市数据
    public static Map<String, List<ProvinceEntity>> cityListMap;
    //区数据
    public static Map<String, List<ProvinceEntity>> countyListMap;


    @SuppressLint("CheckResult")
    public static void parseData(Context context) {
        Observable<List<ProvinceEntity>> provinceObservable = ObservableCreate.create(emitter -> {
            provinceEntities = new Gson().fromJson(new InputStreamReader(
                    context.getAssets().open("province.json")), new TypeToken<List<ProvinceEntity>>() {
            }.getType());
            emitter.onNext(provinceEntities);
        });
        Observable<Map<String, List<ProvinceEntity>>> cityObservable = ObservableCreate.create(emitter -> {
            cityListMap = new Gson().fromJson(new InputStreamReader(
                    context.getAssets().open("city.json")), new TypeToken<Map<String, List<ProvinceEntity>>>() {
            }.getType());
            emitter.onNext(cityListMap);
        });
        Observable<Map<String, List<ProvinceEntity>>> countyObservable = ObservableCreate.create(emitter -> {
            countyListMap = new Gson().fromJson(new InputStreamReader(
                    context.getAssets().open("county.json")), new TypeToken<Map<String, List<ProvinceEntity>>>() {
            }.getType());
            emitter.onNext(countyListMap);
        });
        ObservableCreate.zip(provinceObservable, cityObservable, countyObservable, (stringProvinceEntityMap, stringCityEntityMap, stringCountyEntityMap) -> "1").subscribeOn(Schedulers.io())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
