package code.google.nfs.rpc.mina2.client;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.SocketConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import code.google.nfs.rpc.client.AbstractClientFactory;
import code.google.nfs.rpc.client.Client;
import code.google.nfs.rpc.mina2.serialize.MinaProtocolCodecFilter;

/**
 * Mina Client Factory
 *
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class MinaClientFactory extends AbstractClientFactory {

  private static Log LOGGER = LogFactory.getLog(MinaClientFactory.class);

  private static final boolean isDebugEnabled = LOGGER.isDebugEnabled();

  private static final int processorCount = Runtime.getRuntime().availableProcessors() + 1;

  private static final AbstractClientFactory _self = new MinaClientFactory();

  private static ConcurrentHashMap<String, List<ConnectFuture>> cfs = new ConcurrentHashMap<>();
  private SocketConnector ioConnector;
  private AtomicBoolean isShutdown = new AtomicBoolean(false);

  private MinaClientFactory() {
    // only one ioConnector,avoid create too many io processor thread
    ioConnector = new NioSocketConnector(processorCount);
    // ioConnector.getFilterChain().addLast("executor", new
    // ExecutorFilter(Executors.newCachedThreadPool()));
    ioConnector.getSessionConfig()
        .setTcpNoDelay(Boolean.parseBoolean(System.getProperty("nfs.rpc.tcp.nodelay", "true")));
    ioConnector.getSessionConfig().setReuseAddress(true);
    ioConnector.getFilterChain().addLast("objectserialize", new MinaProtocolCodecFilter());
  }

  public AtomicBoolean isShutdown() {
    return isShutdown;
  }

  public static AbstractClientFactory getInstance() {
    return _self;
  }

  @Override
  protected Client createClient(String targetIP, int targetPort, int connectTimeout, String key) throws Exception {
    if (isDebugEnabled) {
      LOGGER.debug(
          "create connection to :" + targetIP + ":" + targetPort + ",timeout is:" + connectTimeout + ",key is:" + key);
    }
    if (connectTimeout > 1000) {
      ioConnector.setConnectTimeoutMillis((int) connectTimeout);
    } else {
      ioConnector.setConnectTimeoutMillis(1000);
    }
    SocketAddress targetAddress = new InetSocketAddress(targetIP, targetPort);
    MinaClientProcessor processor = new MinaClientProcessor(this, key);
    ioConnector.setHandler(processor);
    ConnectFuture connectFuture = ioConnector.connect(targetAddress);
    List<ConnectFuture> cfList = cfs.get(key);
    if (cfList == null) {
      cfList = new ArrayList<ConnectFuture>();
      cfs.put(key, cfList);
    }
    cfList.add(connectFuture);
    // wait for connection established
    connectFuture.awaitUninterruptibly();

    IoSession ioSession = connectFuture.getSession();
    if ((ioSession == null) || (!ioSession.isConnected())) {
      String targetUrl = targetIP + ":" + targetPort;
      LOGGER.error("create connection error,targetaddress is " + targetUrl);
      throw new Exception("create connection error,targetaddress is " + targetUrl);
    }
    if (isDebugEnabled) {
      LOGGER.debug("create connection to :" + targetIP + ":" + targetPort + ",timeout is:" + connectTimeout + ",key is:"
          + key + " successed");
    }
    MinaClient client = new MinaClient(ioSession, key, connectTimeout);
    processor.setClient(client);
    return client;
  }

  public void shutdown(boolean immediately) {
    isShutdown.set(true);
    for (Map.Entry<String, List<ConnectFuture>> entry : cfs.entrySet()) {
      List<ConnectFuture> list = entry.getValue();
      for (ConnectFuture cf : list) {
        CloseFuture closeFuture = cf.getSession().getCloseFuture();
        closeFuture.addListener((IoFutureListener<?>) new IoFutureListener<IoFuture>() {

          @Override
          public void operationComplete(IoFuture future) {
            LOGGER.debug("session " + future.getSession().getId() + " is now closed");
          }
        });
        if (immediately) {
          LOGGER.debug("session " + closeFuture.getSession().getId() + " is closing now");
          closeFuture.getSession().closeNow();
        } else {
          LOGGER.debug("session " + closeFuture.getSession().getId() + " is closing on flush");
          closeFuture.getSession().closeOnFlush();
        }
        closeFuture.awaitUninterruptibly();
      }
    }
    ioConnector.dispose();
  }

}
