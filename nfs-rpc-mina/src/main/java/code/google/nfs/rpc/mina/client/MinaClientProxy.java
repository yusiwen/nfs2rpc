package code.google.nfs.rpc.mina.client;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import code.google.nfs.rpc.client.AbstractClientProxy;
import code.google.nfs.rpc.client.Client;
import code.google.nfs.rpc.protocol.RPCProtocol;

public class MinaClientProxy extends AbstractClientProxy {

  private MinaClientInvocationHandler handler = null;

  @Override
  public Client getClient() throws Exception {
    return MinaClientFactory.getInstance().get(serverIp, serverPort, connectTimeout, clientNums);
  }

  @Override
  public Object getServiceProxy(String instanceName, int codecType, Class<?> clazz) {

    if (methodTimeouts.size() == 0) {
      methodTimeouts.put("*", DEFAULT_TIMEOUT);
    }

    List<InetSocketAddress> servers = new ArrayList<>();
    servers.add(new InetSocketAddress(serverIp, serverPort));

    handler = new MinaClientInvocationHandler(servers, clientNums, connectTimeout, instanceName, methodTimeouts,
        codecType, RPCProtocol.TYPE);
    return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[] { clazz }, handler);
  }

  @Override
  public void shutdown(boolean immediately) {
    MinaClientFactory f = (MinaClientFactory) MinaClientFactory.getInstance();
    f.shutdown(immediately);
  }

  public MinaClientProxy(String serverIp, int serverPort, int clientNums, int connectTimeout) {
    this.clientNums = clientNums;
    this.connectTimeout = connectTimeout;
    this.serverIp = serverIp;
    this.serverPort = serverPort;
  }

  public MinaClientProxy(String serverIp, int serverPort) {
    this.serverIp = serverIp;
    this.serverPort = serverPort;
  }
}
