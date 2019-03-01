package socket.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;


import com.canplay.milk.receiver.LaserReceiver;

import socket.core.ConnectManager;


public class SocketService extends Service {

    ConnectManager connectManager = ConnectManager.getInstance();
    Intent boradIntent;
    public static void startMyself(Context context) {
        Intent intent = new Intent(context, SocketService.class);
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        boradIntent = new Intent(this, LaserReceiver.class);
        System.out.println("service create" + android.os.Process.myPid() + '-' + android.os.Process.myTid());
        connectService();
    }

    private void connectService() {
        connectManager.startConnect();
//        connectManager.setPushEventListener(new SocketEventListener() {
//            @Override
//            public void onPushConnected() {
//                System.out.println("service push open" + android.os.Process.myPid() + '-' + android.os.Process.myTid());
//                sendSocketBroadCast(ConnectConstant.CONNNECT,"连接成功");
//            }
//
//            @Override
//            public void onPushExceptionCaught(Throwable cause) {
//
//                System.out.println("service push exception" + android.os.Process.myPid() + '-' + android.os.Process.myTid());
//               sendSocketBroadCast(ConnectConstant.CAUGHT,cause.getMessage());
//            }
//
//            @Override
//            public void onPushMessageSent() {
//                System.out.println("service push sent" + android.os.Process.myPid() + '-' + android.os.Process.myTid());
//            }
//
//            @Override
//            public void onPushMessageReceived(IoSession session, Object message) {
//                String msg = "";
//                if (message instanceof String) {
//                    msg = (String) message;
//                    if(!msg.startsWith("FFFFFFFFF")){
//                        sendSocketBroadCast(ConnectConstant.MSGRECEI_BUS,msg);
//                    }else {
//                        //广播帧
//                        sendSocketBroadCast(ConnectConstant.MSGRECEI_STATE,msg);
//                    }
//                }
//                Log.i("service push Received", "Threaid:" + Thread.currentThread().getId() + Thread.currentThread().getName() + "message:" + msg);
//
//            }
//
//            @Override
//            public void onPushDisConnected() {
//                System.out.println("service push close" + android.os.Process.myPid() + '-' + android.os.Process.myTid());
//                sendSocketBroadCast(ConnectConstant.DISCONNECT,"连接断开");
//            }
//        });
//        connectManager.startConnect();

    }

    private void sendSocketBroadCast(int res, String message) {
        boradIntent.putExtra("res", res);
        boradIntent.putExtra("msg",message);
        sendBroadcast(boradIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("service start command" + android.os.Process.myPid() + '-' + android.os.Process.myTid());
        super.onStartCommand(intent, flags, startId);
        if (!connectManager.isConnect()) {
            connectService();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        connectManager.disConnect();
    }
}
