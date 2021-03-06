package code.google.nfs.rpc.netty4.client;



import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import code.google.nfs.rpc.client.AbstractClientFactory;
import code.google.nfs.rpc.client.Client;
import code.google.nfs.rpc.netty4.serialize.Netty4ProtocolDecoder;
import code.google.nfs.rpc.netty4.serialize.Netty4ProtocolEncoder;

/**
 * Netty4 Client Factory,to create client based on netty API
 *
 * @author <a href="mailto:coderplay@gmail.com">Min Zhou</a>
 */
public class Netty4ClientFactory extends AbstractClientFactory {

	private static final Log LOGGER = LogFactory
			.getLog(Netty4ClientFactory.class);

	private static AbstractClientFactory _self = new Netty4ClientFactory();

	private Netty4ClientFactory() {
	}

	public static AbstractClientFactory getInstance() {
		return _self;
	}

	protected Client createClient(String targetIP, int targetPort,
			int connectTimeout, String key) throws Exception {
		final Netty4ClientHandler handler = new Netty4ClientHandler(this, key);

		EventLoopGroup group = new NioEventLoopGroup(1);
		Bootstrap b = new Bootstrap();
		b.group(group)
		 .channel(NioSocketChannel.class)
		 .option(
		 		ChannelOption.TCP_NODELAY,
		 		Boolean.parseBoolean(System.getProperty(
		 				"nfs.rpc.tcp.nodelay", "true")))
		 .option(
		 		ChannelOption.SO_REUSEADDR,
		 		Boolean.parseBoolean(System.getProperty(
		 				"nfs.rpc.tcp.reuseaddress", "true")))
	     .option(
		 		ChannelOption.CONNECT_TIMEOUT_MILLIS,
		 		connectTimeout < 1000 ? 1000 : connectTimeout)
         .handler(new ChannelInitializer<SocketChannel>() {
             @Override
             public void initChannel(SocketChannel ch) throws Exception {
                 ch.pipeline().addLast("decoder", new Netty4ProtocolDecoder());
                 ch.pipeline().addLast("encoder", new Netty4ProtocolEncoder());
                 ch.pipeline().addLast("handler", handler);
             }
         });

		ChannelFuture future = b.connect(targetIP, targetPort);

		future.awaitUninterruptibly(connectTimeout);
		if (!future.isDone()) {
			LOGGER.error("Create connection to " + targetIP + ":" + targetPort
					+ " timeout!");
			throw new Exception("Create connection to " + targetIP + ":"
					+ targetPort + " timeout!");
		}
		if (future.isCancelled()) {
			LOGGER.error("Create connection to " + targetIP + ":" + targetPort
					+ " cancelled by user!");
			throw new Exception("Create connection to " + targetIP + ":"
					+ targetPort + " cancelled by user!");
		}
		if (!future.isSuccess()) {
			LOGGER.error("Create connection to " + targetIP + ":" + targetPort
					+ " error", future.cause());
			throw new Exception("Create connection to " + targetIP + ":"
					+ targetPort + " error", future.cause());
		}
		Netty4Client client = new Netty4Client(future, key, connectTimeout);
		handler.setClient(client);
		return client;
	}

}
