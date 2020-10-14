package com.snqu.shopping.ui.splash;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.anroid.base.BaseActivity;
import com.snqu.shopping.R;
import com.snqu.shopping.data.user.UserClient;
import com.snqu.shopping.ui.main.MainActivity;

/**
 * 欢迎页面
 *
 * @author 张全
 */
public class SplashTwoAct extends BaseActivity {

    public static void start(Context ctx) {
        Intent intent = new Intent(ctx, SplashTwoAct.class);
        ctx.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.splash_two_layout;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        parseClipboard = false;
        if (UserClient.hasShowGuide()) {
            MainActivity.start(this);
        } else { //跳转到引导界面
            GuideActivity.start(this);
        }
        finish();
    }


}
