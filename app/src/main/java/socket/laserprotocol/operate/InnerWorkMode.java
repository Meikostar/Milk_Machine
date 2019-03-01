package socket.laserprotocol.operate;

import com.canplay.medical.R;
import com.canplay.milk.util.FR;

import socket.LaserConstant;
import socket.util.HexUtil;

/**
 * Created by linquandong on 17/2/21.
 * 内控时的工作模式
 */
public class InnerWorkMode extends Operate{
    public String busId = LaserConstant.INNSERMODE;
    private int modeCode;//连续模式用00 PWM模式用02 RAMP模式用03
    public String nameDesc="";
    public int projectCode;//程序号 1~31

    public InnerWorkMode(){

    }
    public InnerWorkMode(int modeCode){
       setMode(modeCode);

    }
    public static String getDisPaly(int modeCode){
        switch (modeCode) {
            case 0:
                return "C";
            case 2:
                return "M";
            case 3:
                return "R";
        }
        return "";
    }
    public static String getDisPalyDesc(int modeCode){
        switch (modeCode) {
            case 0:
                return FR.str(R.string.laser_insermode_series);
            case 2:
                return FR.str(R.string.laser_insermode_pwm);
            case 3:
                return FR.str(R.string.laser_insermode_ramp);
        }
        return "";
    }
    public void setMode(int modeCode) {
        this.modeCode = modeCode;
        switch (modeCode){
            case 0:
                nameDesc = FR.str(R.string.laser_insermode_series);
                break;
            case 2:
                nameDesc = FR.str(R.string.laser_insermode_pwm);
                break;
            case 3:
                nameDesc = FR.str(R.string.laser_insermode_ramp);
                break;
        }
    }

    @Override
    public String makeCommunicateStr() {
        String hefix = HexUtil.or(busId,"80"); //切换工作模式，写
        String modeStr = String.format("%02x",modeCode);
        String proStr = String.format("%02x",projectCode);
        String data = modeStr+proStr;

        return makCommnuByHeaderData(hefix,data);
    }

    @Override
    public void encode(String laserData) {

    }
}
