package code.google.nfs.rpc.mina.client;

import java.net.InetSocketAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;

import code.google.nfs.rpc.RequestWrapper;
import code.google.nfs.rpc.ResponseWrapper;
import code.google.nfs.rpc.client.AbstractClient;
import code.google.nfs.rpc.client.Client;
import code.google.nfs.rpc.client.ClientFactory;

/**
 * Mina Client
 *
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class MinaClient extends AbstractClient {

  private static final Log LOGGER = LogFactory.getLog(MinaClient.class);

  private static final boolean isWarnEnabled = LOGGER.isWarnEnabled();
  private static final boolean isDebugEnabled = LOGGER.isDebugEnabled();

  private IoSession session;

  private String key;

  private int connectTimeout;

  public MinaClient(IoSession session, String key, int connectTimeout) {
    this.session = session;
    this.key = key;
    this.connectTimeout = connectTimeout;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public void sendRequest(final RequestWrapper wrapper, final int timeout) throws Exception {
    final long beginTime = System.currentTimeMillis();
    if (isDebugEnabled) {
      LOGGER.debug("session " + session.getId() + " start sending message: " + wrapper.getId());
    }
    WriteFuture writeFuture = session.write(wrapper);
    final Client self = this;
    writeFuture.addListener(new IoFutureListener() {
      public void operationComplete(IoFuture future) {
        WriteFuture wfuture = (WriteFuture) future;
        if (wfuture.isWritten()) {
          if (isDebugEnabled) {
            long elapsed = System.currentTimeMillis() - beginTime;
            LOGGER.debug(
                "session " + session.getId() + " finish sending message: " + wrapper.getId() + ",elasped: " + elapsed);
          }
          return;
        }
        String error = "send message to server: " + session.getRemoteAddress()
            + " error,maybe because sendbuffer is full or connection closed: " + !session.isConnected();
        if (System.currentTimeMillis() - beginTime >= timeout) {
          error = "write message to os send buffer timeout,consumetime is: " + (System.currentTimeMillis() - beginTime)
              + "ms,timeout is:" + timeout;
        }
        LOGGER.error(error);
        ResponseWrapper response = new ResponseWrapper(wrapper.getId(), wrapper.getCodecType(),
            wrapper.getProtocolType());
        response.setException(new Exception(error));
        try {
          putResponse(response);
        } catch (Exception e) {
          // IGNORE, should not happen
        }
        if (session.isConnected()) {
          if (isWarnEnabled) {
            LOGGER.warn("close the session because send request error,server:" + session.getRemoteAddress());
          }
          session.closeNow();
        } else {
          // TODO: exception handle
          MinaClientFactory.getInstance().removeClient(key, self);
        }
      }
    });
  }

  @Override
  public String getServerIP() {
    return ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress();
  }

  @Override
  public int getServerPort() {
    return ((InetSocketAddress) session.getRemoteAddress()).getPort();
  }

  @Override
  public int getConnectTimeout() {
    return connectTimeout;
  }

  @Override
  public long getSendingBytesSize() {
    return session.getScheduledWriteBytes();
  }

  @Override
  public ClientFactory getClientFactory() {
    return MinaClientFactory.getInstance();
  }
}
