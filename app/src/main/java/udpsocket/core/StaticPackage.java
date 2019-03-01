package udpsocket.core;

import android.text.TextUtils;

import com.blanke.xsocket.tcp.client.helper.stickpackage.AbsStickPackageHelper;
import com.blanke.xsocket.tcp.client.helper.stickpackage.BaseStickPackageHelper;
import com.blanke.xsocket.tcp.client.helper.stickpackage.StaticLenStickPackageHelper;

/**
 * Created by Administrator on 2018/6/21.
 */

public class StaticPackage {
    public static  int selectPosition=0;

    public static AbsStickPackageHelper getStickPackageHelper() {
        AbsStickPackageHelper stickPackageHelper = null;
        switch (selectPosition) {
            case 0:
                stickPackageHelper = null;
                stickPackageHelper = new BaseStickPackageHelper();
                break;
            case 2:
                String lenStr = "";
                if (!TextUtils.isEmpty(lenStr)) {
                    int len = Integer.parseInt(lenStr);
                    stickPackageHelper = new StaticLenStickPackageHelper(len);
                }
                break;

        }
        return stickPackageHelper;
    }
}
