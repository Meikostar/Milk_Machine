package socket.util;

import android.content.Context;
import android.net.ConnectivityManager;

import com.canplay.milk.base.BaseApplication;
import com.mykar.framework.ApplicationHolder;

import org.litepal.LitePalApplication;

/**
 * Created by neal on 2014/12/2.
 */
public class NetworkUtil {
    public static boolean isNetworkConnect(){
        if(LitePalApplication.getContext()==null){
            return false;
        }
        ConnectivityManager connectivityManager= (ConnectivityManager) LitePalApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager!=null) {
            if(connectivityManager.getActiveNetworkInfo()!=null && connectivityManager.getActiveNetworkInfo().isConnected()){
                return true;
            }
        }
        return false;
    }
}
