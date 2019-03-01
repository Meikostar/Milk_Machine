package socket.laserprotocol.entry;

import android.text.TextUtils;

import com.mykar.framework.activeandroid.Model;
import com.mykar.framework.activeandroid.annotation.Column;
import com.mykar.framework.activeandroid.annotation.Table;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import socket.util.HexUtil;

/**
 * Created by linquandong on 17/2/22.
 */
@Table(name = "LogProtocol")
public class LogProtocol extends Model implements Cloneable, Serializable {

    public LogProtocol() {
        super();
    }

    @Column(name = "devname")
    public String devname;

    @Column(name = "controlStr")
    public String controlStr;

    @Column(name = "faultStr")
    public String faultStr;

    @Column(name = "controlIdexs")
    public String controlIdexs;

    @Column(name = "faultIndexs")
    public String faultIndexs;

    @Column(name = "timeStr", unique = true, onUniqueConflict = Column.ConflictAction.IGNORE)
    public String timeStr;

    @Column(name = "state")
    public int state;  //作为清除故障的标记 1：代表被清除了

    @Override
    public LogProtocol clone() {
        try {
            LogProtocol protocol = new LogProtocol();
            protocol.devname = devname;
            protocol.controlStr = controlStr;
            protocol.faultStr = faultStr;
            protocol.controlIdexs = controlIdexs;
            protocol.faultIndexs = faultIndexs;
            protocol.timeStr = timeStr;
            protocol.state = state;
            return protocol;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public boolean edcode(String devStr, String control, String fault) {
        try {
            //如果完全一样返回
            if (devname != null && controlStr != null && faultStr != null) {
                if (devname.equals(devStr) && controlStr.equals(control) && faultStr.equals(fault)) {
                    return false;
                }
            }
            //否则进行比较
            if (TextUtils.isEmpty(faultStr)) {
                faultIndexs = "";
//                faultIndexs = makeAllChange(HexUtil.mutilHexStringToBitStr(fault).length());
            } else {
                faultIndexs = compareBit(HexUtil.mutilHexStringToBitStr(faultStr), HexUtil.mutilHexStringToBitStr(fault));
            }
            if (TextUtils.isEmpty(controlStr)) {
//                controlIdexs = makeAllChange(HexUtil.mutilHexStringToBitStr(fault).length());
                controlIdexs = "";
            } else {
                controlIdexs = compareBit(HexUtil.mutilHexStringToBitStr(controlStr), HexUtil.mutilHexStringToBitStr(control));
            }
            devname = devStr;
            controlStr = control;
            faultStr = fault;
            timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private String makeAllChange(int length) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            sb.append(i + ";");
        }
        return sb.toString();
    }

    private String compareBit(String s, String s1) {
        StringBuffer sb = new StringBuffer();
        char[] orgin = s.toCharArray();
        char[] desc = s1.toCharArray();
        for (int i = 0; i < orgin.length; i++) {
            if (orgin[i] != desc[i]) {
                sb.append(i + ";");
            }
        }
        return sb.toString();
    }

    public ArrayList<LaserLog> makeLog() {
        ArrayList<LaserLog> laserLogs = new ArrayList<>();
        String[] controls = controlIdexs.split(";");
        String[] faults = faultIndexs.split(";");

        for (int i = 0; i < controls.length; i++) {
            String indexStr = controls[i];
            if (indexStr.isEmpty()) {
                continue;
            }
            int index = Integer.parseInt(indexStr);
            if ((12 < index && index < 29) || index == 31) {
                //保留字段
            } else {
                LaserLog laserLog = new LaserLog();
                laserLog.id = ControlState.shareInstance().getInnerId(controlStr, index);
                laserLog.timeStr = timeStr;
                laserLog.name = ControlState.shareInstance().getState(controlStr, index);
                laserLogs.add(laserLog);
            }
        }
        for (int i = 0; i < faults.length; i++) {
            String indexStr = faults[i];
            if (indexStr.isEmpty()) {
                continue;
            }
            int index = Integer.parseInt(indexStr);
            if (index > 17) {
                //保留字段
            } else {
                LaserLog laserLog = new LaserLog();
                laserLog.id = FaultCode.shareInstance().getInnerId(faultStr, index);
                laserLog.timeStr = timeStr;
                laserLog.name = FaultCode.shareInstance().getState(faultStr, index);
                laserLogs.add(laserLog);
            }
        }
        return laserLogs;
    }

    public ArrayList<LaserLog> makeErrorLog() {
        ArrayList<LaserLog> laserLogs = new ArrayList<>();
        String[] faults = faultIndexs.split(";");
        for (int i = 0; i < faults.length; i++) {
            String indexStr = faults[i];
            if (indexStr.isEmpty()) {
                continue;
            }
            int index = Integer.parseInt(indexStr);
            if (index > 17) {
                //保留字段
            } else {
                LaserLog laserLog = new LaserLog();
                laserLog.id = FaultCode.shareInstance().getInnerId(faultStr, index);
                laserLog.timeStr = timeStr;
                laserLog.name = FaultCode.shareInstance().getState(faultStr, index);
                laserLogs.add(laserLog);
            }
//            //只显示故障码，不显示故障解除的的日志"X"
//            if(laserLog.id.startsWith("E")){
//                laserLogs.add(laserLog);
//            }
        }
        return laserLogs;
    }

    public ArrayList<LaserLog> getAllErrorLog() {
        ArrayList<LaserLog> laserLogs = new ArrayList<>();
        if (TextUtils.isEmpty(faultStr)) {
            return laserLogs;
        }

        String[] faults = makeAllChange(HexUtil.mutilHexStringToBitStr(faultStr).length()).split(";");
        for (int i = 0; i < faults.length; i++) {
            String indexStr = faults[i];
            if (indexStr.isEmpty()) {
                continue;
            }
            int index = Integer.parseInt(indexStr);
            if (index > 17) {
                //保留字段
            } else {
                LaserLog laserLog = new LaserLog();
                laserLog.id = FaultCode.shareInstance().getInnerId(faultStr, index);
                laserLog.timeStr = timeStr;
                laserLog.name = FaultCode.shareInstance().getState(faultStr, index);
//                只显示故障码，不显示故障解除的的日志"X"
                if (laserLog.id.startsWith("E")) {
                    laserLogs.add(laserLog);
                }
            }
//            //只显示故障码，不显示故障解除的的日志"X"
//            if(laserLog.id.startsWith("E")){
//                laserLogs.add(laserLog);
//            }
        }
        return laserLogs;
    }


    public boolean isWirable() {

        if (!controlIdexs.isEmpty() || !faultIndexs.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

}
