package socket.laserprotocol.entry;



import com.canplay.medical.R;
import com.canplay.milk.util.FR;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * Ramp字段
 */
public class RampField implements Serializable {
    public int index;
    public float value;//反馈电流
    public long time;//反馈电压

    public static final float UNINT = 0.01f;

    public RampField(String dataStr, int index) {
        decode(dataStr, index);
    }
    public RampField(){}

    public void decode(String dataStr, int index) {
        this.index = index;
        if (dataStr.length() == 12) {
            value = Integer.parseInt(dataStr.substring(0, 4), 16)*UNINT;
            time = Long.parseLong(dataStr.substring(4, 12), 16);
        }
    }

    public String getIdDesc() {
        return FR.str(R.string.rampindex)+index;
    }

    public String getValueDesc() {
        return FR.str(R.string.rampvalue)+getValue();
    }
    public String getTimeDesc(){
        return FR.str(R.string.ramptime)+time;
    }

    public String getValue() {
        DecimalFormat format = new DecimalFormat("0.00");
        return format.format(value);
    }

    public long getTime() {
        return time;
    }

    public String makeCmdStr() {
        String valueStr = String.format("%04x",(int)(value/UNINT));
        String timeStr = String.format("%08x",time);
        return valueStr+timeStr;
    }
}