package com.snqu.shopping.common.viewmodel;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.snqu.shopping.data.base.BaseResponseObserver;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class BaseAndroidViewModel extends AndroidViewModel {

    public CompositeDisposable compositeDisposable;

    public BaseAndroidViewModel(@NonNull Application application) {
        super(application);
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }


    @SuppressLint("AutoDispose")
    protected <T> void executeNoMapHttp(Observable<T> observable, @NonNull BaseResponseObserver<T> observer) {
        observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribeWith(observer);
        if (compositeDisposable != null) {
            compositeDisposable.add(observer);
        }
    }

    @SuppressLint({"AutoDispose", "CheckResult"})
    protected <T> void executeNoMapHttp(Observable<T> observable, Consumer<T> onNext, Consumer<Throwable> onError) {
        Disposable disposable = observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(onNext, onError);

        if (compositeDisposable != null) {
            compositeDisposable.add(disposable);
        }
    }

    protected void addDisposable(Disposable disposable) {
        if (compositeDisposable != null) {
            compositeDisposable.add(disposable);
        }
    }

}