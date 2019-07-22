package code.google.nfs.rpc.netty.client;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import code.google.nfs.rpc.client.AbstractClientProxy;
import code.google.nfs.rpc.client.Client;
import code.google.nfs.rpc.protocol.RPCProtocol;

public class NettyClientProxy extends AbstractClientProxy {

  private NettyClientInvocationHandler handler = null;

  @Override
  public Client getClient() throws Exception {
    return NettyClientFactory.getInstance().get(serverIp, serverPort, connectTimeout, clientNums);
  }

  @Override
  public Object getServiceProxy(String instanceName, int codecType, Class<?> clazz) {

    if (methodTimeouts.size() == 0) {
      methodTimeouts.put("*", 500);
    }

    List<InetSocketAddress> servers = new ArrayList<>();
    servers.add(new InetSocketAddress(serverIp, serverPort));

    handler = new NettyClientInvocationHandler(servers, clientNums, connectTimeout, instanceName, methodTimeouts,
        codecType, RPCProtocol.TYPE);
    return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[] { clazz }, handler);
  }

  @Override
  public void shutdown(boolean immediately) {
    NettyClientFactory f = (NettyClientFactory) NettyClientFactory.getInstance();
    f.shutdown(immediately);
  }

  public NettyClientProxy(String serverIp, int serverPort, int clientNums, int connectTimeout) {
    this.clientNums = clientNums;
    this.connectTimeout = connectTimeout;
    this.serverIp = serverIp;
    this.serverPort = serverPort;
  }

  public NettyClientProxy(String serverIp, int serverPort) {
    this.serverIp = serverIp;
    this.serverPort = serverPort;
  }
}
