/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package apache.mina.transport.socket.nio;

import apache.mina.core.polling.AbstractPollingIoConnector;
import apache.mina.core.service.IoConnector;
import apache.mina.core.service.IoProcessor;
import apache.mina.core.service.SimpleIoProcessorPool;
import apache.mina.core.service.TransportMetadata;
import apache.mina.transport.socket.DatagramConnector;
import apache.mina.transport.socket.DatagramSessionConfig;
import apache.mina.transport.socket.DefaultDatagramSessionConfig;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.Executor;

/**
 * {@link IoConnector} for datagram transport (UDP/IP).
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public final class NioDatagramConnector extends AbstractPollingIoConnector<NioSession, DatagramChannel> implements
DatagramConnector {

    /**
     * Creates a new instance.
     */
    public NioDatagramConnector() {
        super(new DefaultDatagramSessionConfig(), NioProcessor.class);
    }

    /**
     * Creates a new instance.
     */
    public NioDatagramConnector(int processorCount) {
        super(new DefaultDatagramSessionConfig(), NioProcessor.class, processorCount);
    }

    /**
     * Creates a new instance.
     */
    public NioDatagramConnector(IoProcessor<NioSession> processor) {
        super(new DefaultDatagramSessionConfig(), processor);
    }

    /**
     * Constructor for {@link NioDatagramConnector} with default configuration which will use a built-in
     * thread pool executor to manage the given number of processor instances. The processor class must have
     * a constructor that accepts ExecutorService or Executor as its single argument, or, failing that, a
     * no-arg constructor.
     * 
     * @param processorClass the processor class.
     * @param processorCount the number of processors to instantiate.
     * @see SimpleIoProcessorPool#SimpleIoProcessorPool(Class, Executor, int, java.nio.channels.spi.SelectorProvider)
     * @since 2.0.0-M4
     */
    public NioDatagramConnector(Class<? extends IoProcessor<NioSession>> processorClass, int processorCount) {
        super(new DefaultDatagramSessionConfig(), processorClass, processorCount);
    }

    /**
     * Constructor for {@link NioDatagramConnector} with default configuration with default configuration which will use a built-in
     * thread pool executor to manage the default number of processor instances. The processor class must have
     * a constructor that accepts ExecutorService or Executor as its single argument, or, failing that, a
     * no-arg constructor. The default number of instances is equal to the number of processor cores
     * in the system, plus one.
     * 
     * @param processorClass the processor class.
     * @see SimpleIoProcessorPool#SimpleIoProcessorPool(Class, Executor, int, java.nio.channels.spi.SelectorProvider)
     * @see SimpleIoProcessorPool#DEFAULT_SIZE
     * @since 2.0.0-M4
     */
    public NioDatagramConnector(Class<? extends IoProcessor<NioSession>> processorClass) {
        super(new DefaultDatagramSessionConfig(), processorClass);
    }

    public TransportMetadata getTransportMetadata() {
        return NioDatagramSession.METADATA;
    }

    public DatagramSessionConfig getSessionConfig() {
        return (DatagramSessionConfig) sessionConfig;
    }

    @Override
    public InetSocketAddress getDefaultRemoteAddress() {
        return (InetSocketAddress) super.getDefaultRemoteAddress();
    }

    public void setDefaultRemoteAddress(InetSocketAddress defaultRemoteAddress) {
        super.setDefaultRemoteAddress(defaultRemoteAddress);
    }

    @Override
    protected void init() throws Exception {
        // Do nothing
    }

    @Override
    protected DatagramChannel newHandle(SocketAddress localAddress) throws Exception {
        DatagramChannel ch = DatagramChannel.open();

        try {
            if (localAddress != null) {
                try {
                    ch.socket().bind(localAddress);
                    setDefaultLocalAddress(localAddress);
                } catch (IOException ioe) {
                    // Add some info regarding the address we try to bind to the
                    // message
                    String newMessage = "Error while binding on " + localAddress + "\n" + "original message : "
                            + ioe.getMessage();
                    Exception e = new IOException(newMessage);
                    e.initCause(ioe.getCause());

                    // and close the channel
                    ch.close();

                    throw e;
                }
            }

            return ch;
        } catch (Exception e) {
            // If we got an exception while binding the datagram,
            // we have to close it otherwise we will loose an handle
            ch.close();
            throw e;
        }
    }

    @Override
    protected boolean connect(DatagramChannel handle, SocketAddress remoteAddress) throws Exception {
        handle.connect(remoteAddress);
        return true;
    }

    @Override
    protected NioSession newSession(IoProcessor<NioSession> processor, DatagramChannel handle) {
        NioSession session = new NioDatagramSession(this, handle, processor);
        session.getConfig().setAll(getSessionConfig());
        return session;
    }

    @Override
    protected void close(DatagramChannel handle) throws Exception {
        handle.disconnect();
        handle.close();
    }

    // Unused extension points.
    @Override
    @SuppressWarnings("unchecked")
    protected Iterator<DatagramChannel> allHandles() {
        return Collections.EMPTY_LIST.iterator();
    }

    @Override
    protected ConnectionRequest getConnectionRequest(DatagramChannel handle) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void destroy() throws Exception {
        // Do nothing
    }

    @Override
    protected boolean finishConnect(DatagramChannel handle) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void register(DatagramChannel handle, ConnectionRequest request) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected int select(int timeout) throws Exception {
        return 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Iterator<DatagramChannel> selectedHandles() {
        return Collections.EMPTY_LIST.iterator();
    }

    @Override
    protected void wakeup() {
        // Do nothing
    }
}
