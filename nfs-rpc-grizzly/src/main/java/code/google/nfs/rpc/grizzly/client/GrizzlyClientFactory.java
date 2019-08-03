package code.google.nfs.rpc.grizzly.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.grizzly.CompletionHandler;
import org.glassfish.grizzly.Connection;
import org.glassfish.grizzly.GrizzlyFuture;
import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;
import org.glassfish.grizzly.strategies.SameThreadIOStrategy;

import code.google.nfs.rpc.client.AbstractClientFactory;
import code.google.nfs.rpc.client.Client;
import code.google.nfs.rpc.client.ClientFactory;
import code.google.nfs.rpc.grizzly.serialize.GrizzlyProtocolFilter;

/**
 * Grizzly Client Factory
 *
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class GrizzlyClientFactory extends AbstractClientFactory {

  private static Log LOGGER = LogFactory.getLog(GrizzlyClientFactory.class);

  private static final ClientFactory _self = new GrizzlyClientFactory();

  @SuppressWarnings("rawtypes")
  private static ConcurrentHashMap<String, List<Connection>> cns = new ConcurrentHashMap<>();

  private GrizzlyClientFactory() {
    ;
  }

  public static ClientFactory getInstance() {
    return _self;
  }

  @SuppressWarnings("rawtypes")
  @Override
  protected Client createClient(String targetIP, int targetPort, int connectTimeout, String key) throws Exception {
    Connection connection = null;
    GrizzlyClientHandler handler = new GrizzlyClientHandler();
    FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();
    filterChainBuilder.add(new TransportFilter());
    filterChainBuilder.add(new GrizzlyProtocolFilter());
    filterChainBuilder.add(handler);

    final TCPNIOTransportBuilder transportBuilder = TCPNIOTransportBuilder.newInstance();
    transportBuilder.setOptimizedForMultiplexing(true);

    transportBuilder.setIOStrategy(SameThreadIOStrategy.getInstance());

    final TCPNIOTransport transport = transportBuilder.build();
    transport.setTcpNoDelay(Boolean.parseBoolean(System.getProperty("nfs.rpc.tcp.nodelay", "true")));

    transport.setProcessor(filterChainBuilder.build());

    transport.start();
    Future<Connection> future = transport.connect(targetIP, targetPort);
    if (connectTimeout < 1000) {
      connectTimeout = 1000;
    }
    connection = future.get(connectTimeout, TimeUnit.MILLISECONDS);
    List<Connection> list = cns.get(key);
    if (list == null) {
      list = new ArrayList<Connection>();
      cns.put(key, list);
    }
    list.add(connection);

    @SuppressWarnings("unchecked")
    GrizzlyClient client = new GrizzlyClient(targetIP, targetPort, connectTimeout, connection, key);
    handler.setClient(client);
    return client;
  }

  @SuppressWarnings("rawtypes")
  @Override
  public void shutdown(boolean immediately) {

    for (Map.Entry<String, List<Connection>> entry : cns.entrySet()) {
      List<Connection> list = entry.getValue();
      for (Connection cf : list) {
        GrizzlyFuture closeFuture = cf.close();

        closeFuture.addCompletionHandler(new CompletionHandler() {

          @Override
          public void cancelled() {

          }

          @Override
          public void failed(Throwable throwable) {

          }

          @Override
          public void completed(Object result) {
            LOGGER.debug("session is now closed");
          }

          @Override
          public void updated(Object result) {

          }

        });
      }
    }
  }
}
