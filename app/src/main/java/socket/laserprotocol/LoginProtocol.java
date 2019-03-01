package socket.laserprotocol;

import com.canplay.milk.util.ConfigUtils;
import com.canplay.milk.util.DeviceUtils;
import com.canplay.milk.util.StringUtils;
import com.canplay.milk.util.UpLoadModel;
import com.mykar.framework.ApplicationHolder;

import org.litepal.LitePalApplication;

import java.io.UnsupportedEncodingException;
import java.util.Random;

import socket.Decode.SvrCommnuManager;
import socket.LaserConstant;

import socket.util.HexUtil;
import socket.util.WifiUtils;

/**
 * Created by linquandong on 16/12/5.
 */
public class LoginProtocol extends Baseprotocol {
    public String pwd;
    public String loginStatus = "";
    private static String netrandom; //保存服务器返回的随机数

    public LoginProtocol() {
        controlStr = LaserConstant.LOGIN;
    }

    public LoginProtocol(String pwd) {
        this();
        this.pwd = pwd;
    }
    /**
     * 局域网登录协议
     * @return
     */
    @Override
    public String makeCommunicateStr() {
        String hefix = controlStr;   //控制命令
        long random = getRandom(8);   //8位的随机数
        String randomStr = HexUtil.encodeHexStr((random + "").getBytes());
        String data = randomStr + getPwd(random);
        return makCommnuByHeaderData(hefix, data);
    }

    /**
     * 互联网登录协议
     * @return
     */
    @Override
    public String makeSvrCmdStr() {
        //设备名称--指的是APP的序列号（唯一性）8byte
        String macAddrssHex = HexUtil.cn2Hex("AP" + WifiUtils.shareInstance().getMacAddress().replaceAll(":", ""));
        String macAddrss = StringUtils.adapterRight(macAddrssHex, 8 * 2, "20");
        ConfigUtils.setNetMacId(macAddrss);
//       String sncode = HexUtil.cn2Hex(DeviceUtils.getDeviceSerial());

        //设备型号：可以是CPU序列号  32byte
        String cupHex = HexUtil.cn2Hex(DeviceUtils.getCpuName());
        String cup = StringUtils.adapterRight(cupHex, 32 * 2, "20");


        //设备识别码：设备型号 16byte
        String modelHex = HexUtil.cn2Hex(DeviceUtils.getDeviceModel());
        String model = StringUtils.adapterRight(modelHex, 16 * 2, "20");

        //设备版本：app软件版本 4byte
        String versionhex = HexUtil.cn2Hex(UpLoadModel.getVersionName(LitePalApplication.getContext()));
        String version = StringUtils.adapterRight(versionhex, 4 * 2, "20");

        //设备连接认证吗(87654321)
        String devicepw = HexUtil.encodeHexStr("87654321".getBytes());

        //服务器认证吗 (netrandom:连接时从服务器解析得到)
//        String randomStr = HexUtil.encodeHexStr((netrandom).getBytes());
        String md5 = StringUtils.getMD5(netrandom + pwd);  //随机数加上密码生产md5
//        String svrpw = randomStr+md5;

        //设备频道：用于设备类型在服务上的分类管理，目前我们用的是1
        String channel = "01";

        //设备IP：指的是APP所在内网的IP
        String ip = HexUtil.cn2Hex(WifiUtils.shareInstance().getLocalIpAddress());

        //端口8080 =》5050（16进制)
        String port = HexUtil.cn2Hex(":8080");

        return macAddrss + cup + model + version + devicepw + md5 + channel + ip + port;
    }

    public static void setRadomStr(String bustr) {
        try {
            String dataStr = new String(HexUtil.hexStringToBytes(bustr), "GBK");
            //需要发送登录请求
            if (dataStr.contains("Code")) {
                netrandom = dataStr.substring(dataStr.length() - 8);
                SvrCommnuManager.shareInstance().sendProtolRequest(new LoginProtocol("12345678"));
            } else if (dataStr.contains("Success")) {//登录成功 订阅全部
//                SvrCommnuManager.shareInstance().sendProtolRequest(new SubscribeProtocol());
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static boolean isLogining(String bustr){
        String dataStr = HexUtil.hexStr2CN(bustr);
        //需要发送登录请求
        if (dataStr.contains("Code")) {
            return true;
        }else{
            return false;
        }
    }

    private String getPwd(long random) {
        String md5 = StringUtils.getMD5(random + pwd);  //随机数加上密码生产md5
        return md5;
    }

    private int getRandom(int num) {
        StringBuilder str=new StringBuilder();//定义变长字符串
        Random random=new Random();
        for(int i=0;i<num;i++){
            int value = random.nextInt(10);
            if(i == 0 && value ==0){ //杜绝第一位为0导致少一位数
                value = 1;
            }
            str.append(value);
        }
        return Integer.parseInt(str.toString());
    }

    @Override
    public void encode(BaseLaserDate laserDate) {
        super.encode(laserDate);
        if (busStr.equals("00")) {
            loginStatus = "登陆成功";
        }
        if (busStr.equals("01")) {
            loginStatus = "登陆密码错误";
        }
        if (busStr.equals("10")) {
            loginStatus = "用户已登陆";
        }
    }
}
