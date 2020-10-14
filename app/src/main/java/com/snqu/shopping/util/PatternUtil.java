package com.snqu.shopping.util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则验证
 *
 * @author 张全
 */
public class PatternUtil {


    /**
     * 验证手机号是否合法
     *
     * @param phone
     * @return
     */
    public static boolean isValidatePhone(String phone) {
        if (TextUtils.isEmpty(phone) || phone.length() != 11) {
            return false;
        }
        return Pattern.matches("^1(3[0-9]|4[0-9]|5[0-9]|7[0-9]|8[0-9]|9[0-9])\\d{8}$", phone);
    }


    /**
     * 不能包含特殊字符，只能是中文、字母、数字
     *
     * @param content
     * @return
     */
    public static boolean checkNonCharacters(String content) {
        String regex = "^[\\u4e00-\\u9fa5a-zA-Z0-9]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(content);
        return match.matches();
    }
}
