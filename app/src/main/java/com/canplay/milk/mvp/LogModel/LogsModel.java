package com.canplay.milk.mvp.LogModel;

import com.mykar.framework.activeandroid.query.Select;
import com.mykar.framework.activeandroid.query.Update;
import com.mykar.framework.commonlogic.model.BaseSingleModel;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import socket.Decode.SvrCommnuManager;
import socket.laserprotocol.entry.LaserLog;
import socket.laserprotocol.entry.LogProtocol;


public class LogsModel extends BaseSingleModel {
    public boolean hasNext;
    private int p;

    /**
     * 获取本地数据库中的操作日志
     *
     * @param delegate
     */
    public void getLog(final ILogsDelegate delegate, final int ps, final boolean isLoadMore) {

        Observable.create(new Observable.OnSubscribe<ArrayList<LaserLog>>() {
            @Override
            public void call(Subscriber<? super ArrayList<LaserLog>> subscriber) {
                Select select = new Select();
                ArrayList<LaserLog> laserLogs = new ArrayList<LaserLog>();
                int count = select.from(LogProtocol.class).where("devname = ?", SvrCommnuManager.shareInstance().getDeviceName()).count();
                if (!isLoadMore) {
                    p = 0;
                    laserLogs.clear();
                } else {
                    p++;
                }
                int start = p * ps;
                int end = (p + 1) * ps;
                if (end > count) {
                    end = count;
                    hasNext = false;
                } else {
                    hasNext = true;
                }
                //分页倒序
                List<LogProtocol> logProtocols = select.from(LogProtocol.class).where("devname = ?", SvrCommnuManager.shareInstance().getDeviceName()).orderBy("id DESC").limit(start + "," + end).execute();
                if (logProtocols.size() > 0) {
                    for (int i = 0; i < logProtocols.size(); i++) {
                        laserLogs.addAll(logProtocols.get(i).makeLog());
                    }
                }
                subscriber.onNext(laserLogs);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new Observer<ArrayList<LaserLog>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ArrayList<LaserLog> logs) {
                        delegate.getLogsSuccess(logs);
                    }
                });
    }


    /**
     * 获取本地数据库中的操作日志
     *
     * @param delegate
     */
    public void getFaultLog(final ILogsDelegate delegate, final int ps, final boolean isLoadMore) {
        Observable.create(new Observable.OnSubscribe<ArrayList<LaserLog>>() {
            @Override
            public void call(Subscriber<? super ArrayList<LaserLog>> subscriber) {
                ArrayList<LaserLog> laserLogs = new ArrayList<LaserLog>();
                Select select = new Select();
                int count = select.from(LogProtocol.class)
                        .where("devname = ?", SvrCommnuManager.shareInstance().getDeviceName())
                        .and("faultIndexs !=?", "")
                        .count();
                if (!isLoadMore) {
                    p = 0;
                    laserLogs.clear();
                } else {
                    p++;
                }
                int start = p * ps;
                int end = (p + 1) * ps;
                if (end > count) {
                    end = count;
                    hasNext = false;
                } else {
                    hasNext = true;
                }
                //分页倒序
                List<LogProtocol> logProtocols = select.from(LogProtocol.class)
                        .where("devname = ?", SvrCommnuManager.shareInstance().getDeviceName())
                        .and("faultIndexs !=?", "")
                        .orderBy("id DESC").limit(start + "," + end).execute();
                if (logProtocols.size() > 0) {
                    for (int i = 0; i < logProtocols.size(); i++) {
                        laserLogs.addAll(logProtocols.get(i).makeErrorLog());
                    }
                }
                subscriber.onNext(laserLogs);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io())
                .subscribe(new Observer<ArrayList<LaserLog>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ArrayList<LaserLog> logs) {
                        delegate.getLogsSuccess(logs);
                    }
                });
    }

    public static LaserLog getLastLog() {
        try {
            List<LogProtocol> logProtocols = new Select().from(LogProtocol.class).where("devname = ?", SvrCommnuManager.shareInstance().getDeviceName()).orderBy("id DESC").limit(0 + "," + 1).execute();
            return logProtocols.get(0).makeLog().get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public static LogProtocol getLaseRecord() {
        try {
            List<LogProtocol> logProtocols = new Select()
                    .from(LogProtocol.class)
                    .where("devname = ?", SvrCommnuManager.shareInstance().getDeviceName()).orderBy("id DESC").limit(0 + "," + 1).execute();
            return logProtocols.get(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void clearFaultLog(){
        new Update(LogProtocol.class)
                .set("faultIndexs = ?","")
                .where("devname = ?", SvrCommnuManager.shareInstance().getDeviceName())
                .execute();
    }
}
