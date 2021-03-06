package code.google.nfs.rpc.grizzly.client;

import org.glassfish.grizzly.CompletionHandler;
import org.glassfish.grizzly.Connection;

import code.google.nfs.rpc.RequestWrapper;
import code.google.nfs.rpc.client.AbstractClient;
import code.google.nfs.rpc.client.ClientFactory;

/**
 * Grizzly Client
 *
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class GrizzlyClient extends AbstractClient {

  private String targetIP;
  private int targetPort;
  private int connectTimeout;
  private Connection<Object> connection;
  private String clientKey;

  public GrizzlyClient(String targetIP, int targetPort, int connectTimeout, Connection<Object> connection,
      String clientKey) {
    this.targetIP = targetIP;
    this.targetPort = targetPort;
    this.connectTimeout = connectTimeout;
    this.connection = connection;
    this.clientKey = clientKey;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  @Override
  public void sendRequest(RequestWrapper wrapper, int timeout) throws Exception {
    connection.write(wrapper, new CompletionHandler() {

      public void cancelled() {
      }

      public void failed(Throwable throwable) {
        throwable.printStackTrace();
        GrizzlyClientFactory.getInstance().removeClient(clientKey, null);
      }

      public void completed(Object result) {
      }

      public void updated(Object result) {
      }
    });
  }

  @Override
  public String getServerIP() {
    return targetIP;
  }

  @Override
  public int getServerPort() {
    return targetPort;
  }

  @Override
  public int getConnectTimeout() {
    return connectTimeout;
  }

  @Override
  public long getSendingBytesSize() {
    return connection.getWriteBufferSize();
  }

  @Override
  public ClientFactory getClientFactory() {
    return GrizzlyClientFactory.getInstance();
  }
}
