package com.mykar.framework.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by linquandong on 16/8/3.
 * 获取拍摄视频的路径
 */
public class ViedoUtils {

    private static String VIDEO_SUFFIX = ".mp4";
    private String videoPath;
    private Activity activity;

    public ViedoUtils(Activity activity) {
        this.activity = activity;
    }

    public void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        // set video quality
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mkLocalDir(FrameworkConstant.VIDEO_PATH)); // set the image file
        // name
        // start the video capture Intent
        activity.startActivityForResult(intent, FrameworkConstant.REQUESTCODE_VIDEO);
    }

    public String getVideoPath(int requestCode, int resultCode, Intent data) {
        if (resultCode == activity.RESULT_OK) {
            if (requestCode == FrameworkConstant.REQUESTCODE_VIDEO) {
                return videoPath;
            }
        }
        return null;
    }

    private Uri mkLocalDir(String fileName) {
        File dir = new File(fileName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 为以时间为视频命名
        File imageFile = new File(dir,
                new SimpleDateFormat("yyMMddHHmmss").format(new Date())
                        + VIDEO_SUFFIX);
        videoPath = imageFile.getAbsolutePath();
        Uri imgUri = Uri.fromFile(imageFile);
        return imgUri;
    }
}
