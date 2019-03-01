package socket.laserprotocol.device;

import java.util.ArrayList;

import socket.LaserConstant;
import socket.laserprotocol.BaseLaserDate;
import socket.laserprotocol.Baseprotocol;

/**
 * Created by linquandong on 2017/5/24.
 */

public class DeviceProtocol extends Baseprotocol {
    public DeviceProtocol(){
        controlStr = LaserConstant.DEVICE;
    }

    @Override
    public String makeSvrCmdStr()
    {
        //查询所有客户端
        return "02";
    }

    @Override
    public void encode(BaseLaserDate laserDate) {
        //去除头获取数据正文
        busStr= laserDate.busStr.substring(2,laserDate.busStr.length());

    }

    public ArrayList<LaserDevice> getDeviceList(){
        ArrayList<LaserDevice> arrayList = new ArrayList<>();
        for (int i = 0;i<busStr.length();i+=18){
            try {
                String itemStr = busStr.substring(i,i+18);
                LaserDevice device = new LaserDevice(itemStr);
                arrayList.add(device);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return arrayList;
    }
}
