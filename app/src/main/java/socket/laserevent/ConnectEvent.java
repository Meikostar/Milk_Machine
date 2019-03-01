package socket.laserevent;

import java.io.Serializable;

/**
 * Created by linquandong on 15/8/13.
 */
public class ConnectEvent implements Serializable {
    public int rescode = -1;
    public String msg = "";

    public boolean isConnected(){
        if(rescode == ConnectConstant.CONNNECT || rescode == ConnectConstant.DENLAY){
            return true;
        }else{
            return false;
        }
    }
}
