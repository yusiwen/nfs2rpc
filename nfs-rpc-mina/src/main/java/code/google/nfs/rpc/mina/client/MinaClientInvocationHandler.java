package code.google.nfs.rpc.mina.client;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import code.google.nfs.rpc.client.AbstractClientInvocationHandler;
import code.google.nfs.rpc.client.ClientFactory;

/**
 * Mina Client Invocation Handler for proxy
 *
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class MinaClientInvocationHandler extends AbstractClientInvocationHandler {

  public MinaClientInvocationHandler(List<InetSocketAddress> servers, int clientNums, int connectTimeout,
      String targetInstanceName, Map<String, Integer> methodTimeouts, int codecType, int protocolType) {
    super(servers, clientNums, connectTimeout, targetInstanceName, methodTimeouts, codecType, protocolType);
  }

  @Override
  public ClientFactory getClientFactory() {
    return MinaClientFactory.getInstance();
  }

}
