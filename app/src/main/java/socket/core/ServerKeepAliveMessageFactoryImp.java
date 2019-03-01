package socket.core;

import apache.mina.core.session.IoSession;
import apache.mina.filter.keepalive.KeepAliveMessageFactory;

public class ServerKeepAliveMessageFactoryImp implements KeepAliveMessageFactory{

	@Override
	public boolean isRequest(IoSession session, Object message) {
		if(message instanceof String && message.equals(Config.PING_MESSAGE)){
			return true;
		}
		return false;
	}

	@Override
	public boolean isResponse(IoSession session, Object message) {
		if(message instanceof String && message.equals(Config.PONG_MESSAGE)){
			return true;
		}
		return false;
	}

	@Override
	public Object getRequest(IoSession session) {
		return null;
	}

	@Override
	public Object getResponse(IoSession session, Object request) {
		return Config.PONG_MESSAGE;
	}

}