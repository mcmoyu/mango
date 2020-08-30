package com.moyu.mango.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {

    public static String encrypt(String content) {
        byte[] a;
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        a = messageDigest.digest(content.getBytes());
//        System.out.println(byte2hex(a));
        return byte2hex(a);
    }

    private static String byte2hex(byte[] b) { // 二进制转字符串
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs;
    }
}
