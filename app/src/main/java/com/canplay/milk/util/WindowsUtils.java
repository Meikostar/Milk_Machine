package com.canplay.milk.util;

import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by honghouyang on 2017/2/22.
 */

public class WindowsUtils {

    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }
}
