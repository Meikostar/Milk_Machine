package socket.Decode;

import java.util.HashMap;

import socket.LaserConstant;
import socket.laserprotocol.AllargProtocol;
import socket.laserprotocol.Baseprotocol;
import socket.laserprotocol.ControlProtocol;
import socket.laserprotocol.ErrorProtocol;
import socket.laserprotocol.LoginProtocol;
import socket.laserprotocol.OperateProtocol;
import socket.laserprotocol.RailwayProtocol;
import socket.laserprotocol.SubscribeProtocol;
import socket.laserprotocol.device.DeviceProtocol;

/**
 * Created by linquandong on 16/12/9.
 */
public class CacheUtils {
    public HashMap<String, Baseprotocol> cache = new HashMap<>();
    public HashMap<String, String> protocolClassNames = new HashMap<>();

    private static CacheUtils instance;

    private CacheUtils(){
        //添加协议的配置
        protocolClassNames.put(LaserConstant.ERROR, ErrorProtocol.class.getName());
        protocolClassNames.put(LaserConstant.LOGIN, LoginProtocol.class.getName());
        protocolClassNames.put(LaserConstant.ALLARG_S, AllargProtocol.class.getName());
        protocolClassNames.put(LaserConstant.OPERATE, OperateProtocol.class.getName());
        protocolClassNames.put(LaserConstant.CONTROL, ControlProtocol.class.getName());
        protocolClassNames.put(LaserConstant.DEVICE, DeviceProtocol.class.getName());
        protocolClassNames.put(LaserConstant.SUBSCRIBE, SubscribeProtocol.class.getName());
        protocolClassNames.put(LaserConstant.RAILWAY, RailwayProtocol.class.getName());
    }

    public static CacheUtils shareInstance(){
        if(instance == null){
            instance = new CacheUtils();
        }
        return instance;
    }

    public <I extends Baseprotocol> I getProtocol(String key){
        if(cache.containsKey(key)){
            return (I) cache.get(key);
        }else{
            I pInstance = makeProtocol(key);
            if(pInstance != null){
                cache.put(key,pInstance);
            }
            return pInstance;
        }
    }
    public void removeProtocol(String key){
        cache.remove(key);
    }

    private <I extends Baseprotocol> I makeProtocol(String key) {
        String className = protocolClassNames.get(key);
        try {
            Class clz = Class.forName(className);
            return (I) clz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
