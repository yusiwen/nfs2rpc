package code.google.nfs.rpc.netty.server;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import code.google.nfs.rpc.NamedThreadFactory;
import code.google.nfs.rpc.ProtocolFactory;
import code.google.nfs.rpc.Service;
import code.google.nfs.rpc.netty.serialize.NettyProtocolDecoder;
import code.google.nfs.rpc.netty.serialize.NettyProtocolEncoder;
import code.google.nfs.rpc.server.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Netty Server
 *
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
@Component(name = "code.google.nfs.rpc.netty.server", service = Server.class, property = { "type=netty" })
public class NettyServer implements Server {

  private static final Log LOGGER = LogFactory.getLog(NettyServer.class);

  private ServerBootstrap bootstrap = null;

  private AtomicBoolean startFlag = new AtomicBoolean(false);

  private static final int PROCESSORS = Runtime.getRuntime().availableProcessors();

  public NettyServer() {
    ThreadFactory serverBossTF = new NamedThreadFactory("NETTYSERVER-BOSS-");
    ThreadFactory serverWorkerTF = new NamedThreadFactory("NETTYSERVER-WORKER-");
    EventLoopGroup bossGroup = new NioEventLoopGroup(PROCESSORS, serverBossTF);
    NioEventLoopGroup workerGroup = new NioEventLoopGroup(PROCESSORS * 2, serverWorkerTF);
    workerGroup.setIoRatio(Integer.parseInt(System.getProperty("nfs.rpc.io.ratio", "50")));
    bootstrap = new ServerBootstrap();
    bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
        .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
        .option(ChannelOption.SO_REUSEADDR,
            Boolean.parseBoolean(System.getProperty("nfs.rpc.tcp.reuseaddress", "true")))
        .option(ChannelOption.TCP_NODELAY, Boolean.parseBoolean(System.getProperty("nfs.rpc.tcp.nodelay", "true")));
  }

  @Override
  public void start(int listenPort, final ExecutorService threadPool) throws Exception {
    if (!startFlag.compareAndSet(false, true)) {
      return;
    }
    bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {

      protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("decoder", new NettyProtocolDecoder());
        pipeline.addLast("encoder", new NettyProtocolEncoder());
        pipeline.addLast("handler", new NettyServerHandler(threadPool));
      }

    });
    bootstrap.bind(new InetSocketAddress(listenPort)).sync();
    LOGGER.warn("Server started,listen at: " + listenPort);
  }

  @Override
  public void registerProcessor(int protocolType, String serviceName, Object serviceInstance) {
    ProtocolFactory.getServerHandler(protocolType).registerProcessor(serviceName, serviceInstance);
  }

  @Override
  public void stop() throws Exception {
    LOGGER.warn("Server stop!");
    startFlag.set(false);
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
