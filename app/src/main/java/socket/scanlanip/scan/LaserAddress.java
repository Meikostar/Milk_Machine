package socket.scanlanip.scan;

import android.text.TextUtils;

import com.mykar.framework.activeandroid.Model;
import com.mykar.framework.activeandroid.annotation.Column;
import com.mykar.framework.activeandroid.annotation.Table;
import com.mykar.framework.activeandroid.query.Select;

import java.io.Serializable;
import java.util.List;

/**
 * Created by linquandong on 2017/6/5.
 */
@Table(name = "LaserAddress")
public class LaserAddress extends Model implements Serializable {
    @Column(name = "hostName")
    public String hostName;

    @Column(name = "ip" ,unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String ip;

    @Column(name = "port")
    public int port = 8080;

    public LaserAddress(){
        super();
    }
    public LaserAddress(String name, String addr) {
        hostName = name;
        ip = addr;
    }

    public boolean isAvailable() {
        return !TextUtils.isEmpty(hostName);
    }

    @Override
    public boolean equals(Object o) {
        try {
            if (o instanceof LaserAddress) {
                LaserAddress temp = (LaserAddress) o;
                return temp.ip.equals(ip);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.equals(o);
    }

    public static List<LaserAddress> getAll() {
       return new Select().all().from(LaserAddress.class).execute();
    }
}
