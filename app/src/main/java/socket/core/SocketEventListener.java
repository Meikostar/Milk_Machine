package socket.core;

import apache.mina.core.session.IoSession;

/**
 * Created by neal on 2014/12/7.
 */
public interface SocketEventListener {
    public abstract void onPushConnected();
    public abstract void onPushExceptionCaught(Throwable cause);
    public abstract void onPushMessageSent();
    public abstract void onPushMessageReceived(IoSession session, Object message);
    public abstract void onPushDisConnected();
}
