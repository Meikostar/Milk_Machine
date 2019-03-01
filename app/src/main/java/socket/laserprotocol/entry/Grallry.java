package socket.laserprotocol.entry;



import com.canplay.milk.util.FR;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * 通道实体
 * 15、16、17、18通道反馈电压分别代表1#、2#、3#、4#板的电源电压。
 */
public class Grallry implements Serializable {
    public int index;
    public int akw; //设定电流
    public int lfk;//反馈电流
    public int vfk;//反馈电压
    public int tm;//驱动温度
    public int td;//二极管温度
    public String state;// 通道状态

    public float pd;//电功率
//    public String centigrade = FR.str(R.string.centigrade);

    public Grallry(String dataStr, int index) {
        decode(dataStr, index);
    }

    public void decode(String dataStr, int index) {
        this.index = index;
        if (dataStr.length() == 12) {
            lfk = Integer.parseInt(dataStr.substring(0, 2), 16);
            vfk = Integer.parseInt(dataStr.substring(2, 6), 16);
            tm = Integer.parseInt(dataStr.substring(6, 8), 16);
            td = Integer.parseInt(dataStr.substring(8, 10), 16);
            state = dataStr.substring(10, 12);
            pd = lfk * vfk/100.0f;
        }
    }

    //从id 从1开始
    public String getIdDesc() {
        return String.format("%02d",index+1);
    }

    public String getlsDesc() {
        return new DecimalFormat("0.0").format(akw/10.f);
    }

    public String getlfDesc() {
        return new DecimalFormat("0.0").format(lfk/10.f);
    }

    public String getvfDesc() {
        return new DecimalFormat("0.0").format(vfk/10.f);
    }

    public String gettdDesc() {
        return new DecimalFormat("0").format(tm);
    }

    public String getteDesc(){
        return new DecimalFormat("0").format(td);
    }

    public String getstateDesc(){
        return ""+state;
    }

    public boolean isSn(){
        if(state.endsWith("0")){
            return true;
        }else {
            return false;
        }

    }

}