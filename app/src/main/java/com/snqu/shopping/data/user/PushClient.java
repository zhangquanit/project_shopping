package com.snqu.shopping.data.user;

import com.android.util.db.EasyDB;
import com.snqu.shopping.data.user.entity.PushEntity;

import java.util.List;

public class PushClient {

    public static void updateMessage(final PushEntity pushEntity) {
        EasyDB.with(PushEntity.class).insert(pushEntity);
    }

    public static boolean repeatMessage(String id) {
        boolean flag = false;
        List<PushEntity> pushEntities = EasyDB.with(PushEntity.class).query();
        for (PushEntity pushEntity : pushEntities) {
            if (pushEntity._id != null) {
                if (pushEntity._id.equals(id)) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

}
