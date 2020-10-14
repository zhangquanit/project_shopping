package com.snqu.shopping.util.statistics;


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptUtils {
    public static String encryptDES(String encryptString, String encryptKey) throws Exception {
        //返回实现指定转换的Cipher对象 "算法/模式/填充"
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        //创建一个DESKeySpec对象，使用8个字节的key作为DES密钥的内容
        DESKeySpec desKeySpec = new DESKeySpec(encryptKey.getBytes("UTF-8"));
        //返回转换指定算法的秘密密钥的SecretKeyFactory对象
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        //根据提供的密钥生成SecretKey对象
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        //使用iv中的字节作为iv来构造一个iv ParameterSpec对象。复制该缓冲区的内容来防止后续修改
        IvParameterSpec iv = new IvParameterSpec(encryptKey.getBytes());
        //用密钥和一组算法参数初始化此 Cipher；Cipher：加密、解密、密钥包装或密钥解包，具体取决于 opmode 的值。
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        //加密同时解码成字符串返回
        return new String(encode(cipher.doFinal(encryptString.getBytes("UTF-8"))));
    }
    public static String decryptDES(String decodeString, String decodeKey) throws Exception {
        //使用指定密钥构造IV
        IvParameterSpec iv = new IvParameterSpec(decodeKey.getBytes());
        //根据给定的字节数组和指定算法构造一个密钥。
        SecretKeySpec skeySpec = new SecretKeySpec(decodeKey.getBytes(), "DES");
        //返回实现指定转换的 Cipher 对象
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        //解密初始化
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        //解码返回
        byte[] byteMi = decode(decodeString.toCharArray());
        byte decryptedData[] = cipher.doFinal(byteMi);
        return new String(decryptedData);
    }

    private static char[] alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/="
            .toCharArray();
    private static byte[] codes = new byte[256];

    public static char[] encode(byte[] data) {
        char[] out = new char[((data.length + 2) / 3) * 4];
        for (int i = 0, index = 0; i < data.length; i += 3, index += 4) {
            boolean quad = false;
            boolean trip = false;
            int val = (0xFF & (int) data[i]);
            val <<= 8;
            if ((i + 1) < data.length) {
                val |= (0xFF & (int) data[i + 1]);
                trip = true;
            }
            val <<= 8;
            if ((i + 2) < data.length) {
                val |= (0xFF & (int) data[i + 2]);
                quad = true;
            }
            out[index + 3] = alphabet[(quad ? (val & 0x3F) : 64)];
            val >>= 6;
            out[index + 2] = alphabet[(trip ? (val & 0x3F) : 64)];
            val >>= 6;
            out[index + 1] = alphabet[val & 0x3F];
            val >>= 6;
            out[index + 0] = alphabet[val & 0x3F];
        }
        return out;
    }

    public static byte[] decode(char[] data) {
        int len = ((data.length + 3) / 4) * 3;
        if (data.length > 0 && data[data.length - 1] == '=')
            --len;
        if (data.length > 1 && data[data.length - 2] == '=')
            --len;
        byte[] out = new byte[len];
        int shift = 0;
        int accum = 0;
        int index = 0;
        for (int ix = 0; ix < data.length; ix++) {
            int value = codes[data[ix] & 0xFF];
            if (value >= 0) {
                accum <<= 6;
                shift += 6;
                accum |= value;
                if (shift >= 8) {
                    shift -= 8;
                    out[index++] = (byte) ((accum >> shift) & 0xff);
                }
            }
        }
//        if (index != out.length)
//            throw new Error("miscalculated data length!");
        return out;
    }

    static {
        for (int i = 0; i < 256; i++)
            codes[i] = -1;
        for (int i = 'A'; i <= 'Z'; i++)
            codes[i] = (byte) (i - 'A');
        for (int i = 'a'; i <= 'z'; i++)
            codes[i] = (byte) (26 + i - 'a');
        for (int i = '0'; i <= '9'; i++)
            codes[i] = (byte) (52 + i - '0');
        codes['+'] = 62;
        codes['/'] = 63;
    }
}