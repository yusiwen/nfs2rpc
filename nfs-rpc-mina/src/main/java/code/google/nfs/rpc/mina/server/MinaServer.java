package code.google.nfs.rpc.mina.server;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import code.google.nfs.rpc.ProtocolFactory;
import code.google.nfs.rpc.Service;
import code.google.nfs.rpc.mina.serialize.MinaProtocolCodecFilter;
import code.google.nfs.rpc.server.Server;

/**
 * Mina2 Server
 *
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
@Component(name = "code.google.nfs.rpc.mina.server", service = Server.class, property = { "type=mina" })
public class MinaServer implements Server {

  private static final Log LOGGER = LogFactory.getLog(MinaServer.class);

  private IoAcceptor acceptor;

  private AtomicBoolean startFlag = new AtomicBoolean();

  private MinaServerHandler serverHandler = null;

  public MinaServer() {
    acceptor = new NioSocketAcceptor(Runtime.getRuntime().availableProcessors() + 1);
    ((NioSocketAcceptor) acceptor).setReuseAddress(true);
    ((NioSocketAcceptor) acceptor).getSessionConfig().setTcpNoDelay(true);
    // acceptor.getFilterChain().addLast("executor", new ExecutorFilter(5, 10));
    acceptor.getFilterChain().addLast("objectserialize", new MinaProtocolCodecFilter());
  }

  @Override
  public void start(int listenPort, ExecutorService businessThreadPool) throws Exception {
    if (!startFlag.compareAndSet(false, true)) {
      return;
    }
    try {
      serverHandler = new MinaServerHandler(businessThreadPool);
      acceptor.setHandler(serverHandler);
      acceptor.bind(new InetSocketAddress(listenPort));
      LOGGER.warn("Server started,listen at: " + listenPort);
    } catch (Exception e) {
      startFlag.set(false);
      LOGGER.error("Server start failed", e);
      throw new Exception("start server error", e);
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

  @Override
  public void stop() throws Exception {
    serverHandler = null;
    LOGGER.warn("Server stoped");
    acceptor.dispose();
    startFlag.set(false);
  }
}
