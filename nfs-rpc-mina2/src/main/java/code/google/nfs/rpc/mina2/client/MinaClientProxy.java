package code.google.nfs.rpc.mina2.client;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import code.google.nfs.rpc.protocol.RPCProtocol;

public class MinaClientProxy {

  private MinaClientInvocationHandler handler = null;

  public Object getServiceProxy(String serverIp, int serverPort, String instanceName, int codecType, Class<?> clazz) {

    Map<String, Integer> methodTimeouts = new HashMap<String, Integer>();
    methodTimeouts.put("*", 500);

    List<InetSocketAddress> servers = new ArrayList<>();
    servers.add(new InetSocketAddress(serverIp, serverPort));

    handler = new MinaClientInvocationHandler(servers, 1, 30, instanceName, methodTimeouts,
        codecType, RPCProtocol.TYPE);
    return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class<?>[] { clazz }, handler);
  }

  public void shutdown(boolean immediately) {
    if (handler != null) {
      MinaClientFactory f = (MinaClientFactory) handler.getClientFactory();
      f.shutdown(immediately);
    }
  }
}
