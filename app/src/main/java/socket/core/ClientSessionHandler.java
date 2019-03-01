package socket.core;


import apache.mina.core.service.IoHandlerAdapter;
import apache.mina.core.session.IdleStatus;
import apache.mina.core.session.IoSession;

/**
 * Created by neal on 2014/12/2.
 */
public class ClientSessionHandler extends IoHandlerAdapter {
    private SocketEventListener socketEventListener;
    public SocketEventListener getSocketEventListener(){
        return socketEventListener;
    }
    public void setSocketEventListener(SocketEventListener socketEventListener) {
        this.socketEventListener = socketEventListener;
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        super.sessionCreated(session);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
        super.sessionOpened(session);
        if (socketEventListener != null) {
            socketEventListener.onPushConnected();
        }
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        super.sessionClosed(session);
        if (socketEventListener != null) {
            socketEventListener.onPushDisConnected();
        }
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        super.sessionIdle(session, status);
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        super.exceptionCaught(session, cause);
        if (socketEventListener != null) {
            socketEventListener.onPushExceptionCaught(cause);
        }
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        super.messageReceived(session, message);
        if (socketEventListener != null) {
            socketEventListener.onPushMessageReceived(session, message);
        }
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
        super.messageSent(session, message);
        if (socketEventListener != null) {
            socketEventListener.onPushMessageSent();
        }
    }

    @Override
    public void inputClosed(IoSession session) throws Exception {
        super.inputClosed(session);
    }
}
