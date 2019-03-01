package com.google.zxing.client.android.decode;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.text.TextUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.Hashtable;

public class CodeCreator {

    /*logo*/
    private static Bitmap logoBitmap;

    /*生成二维码*/
    public static Bitmap createQRCode(String content, int w, int h,Bitmap logo) throws WriterException {


        if (TextUtils.isEmpty(content)) {
            return null;
        }
        /*偏移量*/
        int offsetX = w / 2;
        int offsetY = h / 2;

        /*生成logo*/
        if (logo!=null){
            Matrix matrix = new Matrix();
            float scaleFactor = Math.min(w * 1.0f / 5 / logo.getWidth(), h * 1.0f / 5 /logo.getHeight());
            matrix.postScale(scaleFactor,scaleFactor);
            logoBitmap= Bitmap.createBitmap(logo, 0, 0, logo.getWidth(),   logo.getHeight(), matrix, true);
        }


        /*如果log不为null,重新计算偏移量*/
        int logoW = 0;
        int logoH = 0;
        if (logoBitmap != null) {
            logoW = logoBitmap.getWidth();
            logoH = logoBitmap.getHeight();
            offsetX = (w - logoW) / 2;
            offsetY = (h - logoH) / 2;
        }

        /*指定为UTF-8*/
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        //容错级别
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        //设置空白边距的宽度
        hints.put(EncodeHintType.MARGIN, 0);
        // 生成二维矩阵,编码时指定大小,不要生成了图片以后再进行缩放,这样会模糊导致识别失败
        BitMatrix matrix = new MultiFormatWriter().encode(content,
                BarcodeFormat.QR_CODE, w, h, hints);

        // 二维矩阵转为一维像素数组,也就是一直横着排了
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if(x >= offsetX && x < offsetX + logoW && y>= offsetY && y < offsetY + logoH){
                    int pixel = logoBitmap.getPixel(x-offsetX,y-offsetY);
                    if(pixel == 0){
                        if(matrix.get(x, y)){
                            pixel = 0xff000000;
                        }else{
                            pixel = 0xffffffff;
                        }
                    }
                    pixels[y * w + x] = pixel;
                }else{
                    if (matrix.get(x, y)) {
                        pixels[y * w + x] = 0xff000000;
                    } else {
                        pixels[y * w + x] = 0xffffffff;
                    }
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(w, h,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }
    /**
     * 生成二维码 要转换的地址或字符串,可以是中文
     *
     * @param url
     * @param width
     * @param height
     * @return
     */
    public static Bitmap createQRImage(String url, final int width, final int height) {
        try {
            // 判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1) {
                return null;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            // 图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url,
                    BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            // 下面这里按照二维码的算法，逐个生成二维码的图片，
            // 两个for循环是图片横列扫描的结果
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = 0xff000000;
                    } else {
                        pixels[y * width + x] = 0xffffffff;
                    }
                }
            }
            // 生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
}
