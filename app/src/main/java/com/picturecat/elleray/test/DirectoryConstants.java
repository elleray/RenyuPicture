/*
 * *
 *  Copyright (c) 2015. Dingtone, inc. All rights reserved.
 * /
 */

package com.picturecat.elleray.test;

import android.os.Environment;

import java.io.File;

/**
 * 管理本地文件目录类
 * Created by elleray on 16/7/28.
 */
public class DirectoryConstants {
    public static final String SDCARD_ROOT = Environment
            .getExternalStorageDirectory().getAbsolutePath();

    public static final String SYSTEM_DCIM = new StringBuffer(SDCARD_ROOT)
            .append(File.separator).append("DCIM").append(File.separator).toString();
    public static final String SYSTEM_DCIM_CAMERA = new StringBuffer(SDCARD_ROOT)
            .append(File.separator).append("DCIM").append(File.separator).append("Camera")
            .append(File.separator).toString();

    public static final String GODAP = "人鱼图库";

    public static final String HOME_ROOT = new StringBuffer(SDCARD_ROOT)
            .append(File.separator).append(GODAP).append(File.separator)
            .toString();

    //一级目录
    public static String TRANSFER = new StringBuffer(HOME_ROOT).append("transfer").append(File.separator).toString();
    public static String DATA = new StringBuffer(HOME_ROOT).append("data").append(File.separator).toString();
    public static String LOG = new StringBuffer(HOME_ROOT).append("log").append(File.separator).toString();
    public static String IMAGE = new StringBuffer(HOME_ROOT).append("picture").append(File.separator).toString();
    public static String VIDEO = new StringBuffer(HOME_ROOT).append("video").append(File.separator).toString();
    public static String APK = new StringBuffer(HOME_ROOT).append("apk").append(File.separator).toString();
    public static String DOCUMENT =  new StringBuffer(HOME_ROOT).append("document").append(File.separator).toString();
    public static String MUSIC =  new StringBuffer(HOME_ROOT).append("music").append(File.separator).toString();
    public static String DOWNLOAD =  new StringBuffer(HOME_ROOT).append("me/godap/download").append(File.separator).toString();
    public static String CACHE = new StringBuffer(HOME_ROOT).append("cache").append(File.separator).toString();
    public static String UPLOAD = new StringBuffer(HOME_ROOT).append("upload").append(File.separator).toString();

    //二级目录
    public static String DOWNLOAD_MUSIC =  new StringBuffer(DOWNLOAD).append("music").append(File.separator).toString();
    public static String DOWNLOAD_VIDEO =  new StringBuffer(DOWNLOAD).append("video").append(File.separator).toString();



    //three dir
    public static String TRANSFER_USER_AVATAR = new StringBuffer(TRANSFER).append("tmp_avatar").append(File.separator).toString();
}
