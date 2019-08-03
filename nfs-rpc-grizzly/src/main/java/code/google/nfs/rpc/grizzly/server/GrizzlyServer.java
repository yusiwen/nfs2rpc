package code.google.nfs.rpc.grizzly.server;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.glassfish.grizzly.filterchain.FilterChainBuilder;
import org.glassfish.grizzly.filterchain.TransportFilter;
import org.glassfish.grizzly.nio.transport.TCPNIOTransport;
import org.glassfish.grizzly.nio.transport.TCPNIOTransportBuilder;
import org.glassfish.grizzly.strategies.SameThreadIOStrategy;
import org.glassfish.grizzly.threadpool.GrizzlyExecutorService;
import org.glassfish.grizzly.threadpool.ThreadPoolConfig;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import code.google.nfs.rpc.ProtocolFactory;
import code.google.nfs.rpc.Service;
import code.google.nfs.rpc.grizzly.serialize.GrizzlyProtocolFilter;
import code.google.nfs.rpc.server.Server;

/**
 * Grizzly Server
 *
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
@Component(name = "code.google.nfs.rpc.grizzly.server", service = Server.class, property = { "type=mina" })
public class GrizzlyServer implements Server {

  private static final Log LOGGER = LogFactory.getLog(GrizzlyServer.class);
  private TCPNIOTransport transport = null;

  @Override
  public void start(int listenPort, ExecutorService threadpool) throws Exception {
    ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) threadpool;
    ThreadPoolConfig config = ThreadPoolConfig.defaultConfig().copy()
        .setCorePoolSize(threadPoolExecutor.getCorePoolSize()).setMaxPoolSize(threadPoolExecutor.getMaximumPoolSize())
        .setPoolName("GRIZZLY-SERVER");
    ExecutorService executorService = GrizzlyExecutorService.createInstance(config);

    FilterChainBuilder filterChainBuilder = FilterChainBuilder.stateless();
    filterChainBuilder.add(new TransportFilter());
    filterChainBuilder.add(new GrizzlyProtocolFilter());
    filterChainBuilder.add(new GrizzlyServerHandler(executorService));
    TCPNIOTransportBuilder builder = TCPNIOTransportBuilder.newInstance();
    builder.setOptimizedForMultiplexing(true);
    builder.setIOStrategy(SameThreadIOStrategy.getInstance());

    transport = builder.build();

    transport.setProcessor(filterChainBuilder.build());
    transport.bind(listenPort);

    transport.start();
    LOGGER.warn("server started,listen at: " + listenPort);

    Thread.currentThread().join();
  }

  @Override
  public void stop() throws Exception {
    if (transport != null) {
      transport.stop();
      LOGGER.warn("server stoped!");
    }
  }

  @Override
  public void registerProcessor(int protocolType, String serviceName, Object serviceInstance) {
    ProtocolFactory.getServerHandler(protocolType).registerProcessor(serviceName, serviceInstance);
  }

  @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
  public void registerProcessor(Service service, final Map<String, Object> props) {
    if (service != null) {
      if (props != null) {
        LOGGER.info(
            "Regisering service instance: " + service.getServiceName() + ", type: " + service.getServiceProtocolType());
        registerProcessor(service.getServiceProtocolType(), service.getServiceName(), service);
      }
    }
  }

  public void unregisterProcessor(Service service, final Map<String, Object> props) {
    if (service != null) {
      LOGGER.info(
          "Unregisering service instance: " + service.getServiceName() + ", type: " + service.getServiceProtocolType());
      ProtocolFactory.getServerHandler(service.getServiceProtocolType()).unregisterProcessor(service.getServiceName(),
          service);
    }
  }

}
