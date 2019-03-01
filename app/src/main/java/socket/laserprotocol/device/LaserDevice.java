package socket.laserprotocol.device;



import com.canplay.medical.R;
import com.canplay.milk.util.FR;

import java.io.Serializable;
import java.util.HashMap;

import socket.util.HexUtil;

/**
 * Created by linquandong on 2017/5/24.
 */

public class LaserDevice implements Serializable {
    public String deviceid = "";
    public String state = "";
    public String equ_pwd = "87654321"; //默认密码
    /**
     * company_sn : 123456
     * equ_sn : 235324
     * equ_pwd : 123456
     * start_time : 0
     * stop_time : 0
     * user_mobile : 18570550240
     */
    //服务器数据
    public String id;
    public String company_sn;
    public String equ_type;
    public String equ_sn;
    public int service_id;
    public long start_time;
    public long stop_time;
    public String user_name;
    public String user_address;
    public String user_mobile;
    public String type_img;
    public String type_sn;
    public int status;
    public double lat;
    public double lon;
    public String ip;
    public boolean isLoacl;

    public LaserDevice(String hexStr) {
        if(hexStr.length() == 18){
            deviceid = HexUtil.hexStr2CN(hexStr.substring(0,16));
            /**
              * 8bit   ==> 0：无专线占用  1：有专线占用
                0~7 bit==>被订阅数量，

             */

            state = hexStr.substring(16,18);
        }
    }
    //订阅数量
    public int scrsubCount(){
        if(state == null || state.isEmpty()){
            return 0;
        }
        int count = Integer.parseInt(state,16);
        return 1<<count;
    }

    //是否被占用
    public boolean hasOccupy(){
        if(state == null || state.isEmpty()){
            return false;
        }
        int count = Integer.parseInt(state,16);
        int st = count>>7;
        return st==1;
    }

    public String getoccpy() {
        if(deviceid.isEmpty()){
            return FR.str(R.string.offline);
        }else{
            return  FR.str(R.string.online);
        }
        //暂时去掉是否被占用
//        if(hasOccupy()){
//            return FR.str(R.string.occupy);
//        }else{
//            return FR.str(R.string.no_occupy);
//        }
    }

    public LaserDevice(String reallyName, String equ_pwd){
        this.deviceid = reallyName;
        this.equ_pwd = equ_pwd;
    }



    public LaserDevice() {
    }


    public HashMap<String, Object> getParam(boolean isEdit) {
        HashMap<String, Object> params = new HashMap<>();
//        if (isEdit) {
            params.put("id",id);
//        }
//        params.put("company_sn",company_sn);
//        params.put("equ_type",equ_type);
//        params.put("equ_sn",equ_sn);
//        params.put("equ_pwd",equ_pwd);
//        params.put("user_name",user_name);
//        params.put("user_address",user_address);
//        params.put("company_sn",company_sn);
        return params;
    }

}
