package com.budwk.starter.storage.utils;

/**
 * @author caoshi
 * @date 10点20分2021年4月25日
 */
public class StorageUtil {

    public static String prefixSeparator(String str){
        return str.startsWith("/")?str:"/"+str;
    }

    public static String cutPrefixSeparator(String str){
        return str.startsWith("/")?str.substring(1):str;
    }


}
