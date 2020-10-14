package com.anroid.base;

import android.view.View;

import androidx.core.app.ActivityCompat;

import com.anroid.base.ui.TitleBarView;

import me.yokeyword.fragmentation.ISupportFragment;

/**
 * @author 张全
 */
public abstract class SimpleFrag extends BaseFragment {
    private TitleBarView mToolbar;

    public void setToolbar(TitleBarView toolbar) {
        this.mToolbar = toolbar;
        toolbar.setOnLeftBtnClickListener(v -> {
            assert getFragmentManager() != null;
            if (getFragmentManager().getBackStackEntryCount() > 1) {
                pop();
            } else {
                ActivityCompat.finishAfterTransition(_mActivity);
            }
        });
    }

    protected void hideToolbar() {
        TitleBarView titleBar = getTitleBar();
        if (null != titleBar) {
            titleBar.setVisibility(View.GONE);
        }
    }

    protected TitleBarView getTitleBar() {
        if (null != mToolbar) {
            return mToolbar;
        }
        ISupportFragment preFragment = getPreFragment();
        if (null != preFragment && preFragment instanceof SimpleFrag) {
            SimpleFrag simpleFrag = (SimpleFrag) preFragment;
            mToolbar = simpleFrag.getTitleBar();
        }
        return mToolbar;
    }

    @Override
    public void onResume() {
        super.onResume();
//        MobclickAgent.onPageStart(pageName); //友盟统计
    }

    @Override
    public void onPause() {
        super.onPause();
//        MobclickAgent.onPageEnd(pageName); //友盟统计
    }

    @Override
    public void onStop() {
        super.onStop();
    }



}
