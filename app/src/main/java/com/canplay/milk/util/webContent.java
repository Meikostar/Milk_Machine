package com.canplay.milk.util;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/3/20.
 */

public class webContent {
    public static byte[] readStream(InputStream inputStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        while ((len = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, len);
        }

        inputStream.close();
        byteArrayOutputStream.close();
        return byteArrayOutputStream.toByteArray();
    }

    public static   String getHtml(String urlpath) throws Exception {
        URL url = new URL(urlpath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(6 * 1000);

//   StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
//        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectLeakedSqlLiteObjects().detectLeakedClosableObjects().penaltyLog().penaltyDeath().build());
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() == 200) {
            InputStream inputStream = conn.getInputStream();
            byte[] data = readStream(inputStream);
            Log.e("字节数组长度", data.length+"个字节");
            String html = new String(data);
            return html;
        }
        return null;
    }
    public static String getUrl(String s) {
        String regex;
        String title = "";
        ArrayList<String> list = new ArrayList<String>();
        regex = "(?<=<img src=\").*(?=\" class=\"head-img\"/>)";
        Pattern pa = Pattern.compile(regex);
        Matcher ma = pa.matcher(s);
        while (ma.find()) {
            list.add(ma.group());
        }
        for (int i = 0; i < list.size(); i++) {
            title = title + list.get(i);
        }
        return (title);
    }

//    public ImageLoader loadImage(String  url,Context context){
//        RequestQueue requestQueue = Volley.newRequestQueue(context);
//        final LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(50);
//        ImageCache imageCache = new ImageCache() {
//
//            @Override
//<span style="white-space:pre">        </span>public void putBitmap(String key, Bitmap value) {
//<span style="white-space:pre">        </span>lruCache.put(key, value);
//<span style="white-space:pre">        </span>}
//
//<span style="white-space:pre">        </span>@Override
//<span style="white-space:pre">        </span>public Bitmap getBitmap(String key) {
//<span style="white-space:pre">        </span>return lruCache.get(key);
//<span style="white-space:pre">        </span>}
//<span style="white-space:pre">        </span>};
//<span style="white-space:pre">        </span>ImageLoader imageLoader = new ImageLoader(requestQueue, imageCache);
//<span style="white-space:pre">        </span>return imageLoader;
//
//<span style="white-space:pre">        </span>}
}