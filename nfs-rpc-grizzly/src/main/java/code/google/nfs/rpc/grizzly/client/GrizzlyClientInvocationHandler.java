package code.google.nfs.rpc.grizzly.client;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import code.google.nfs.rpc.client.AbstractClientInvocationHandler;
import code.google.nfs.rpc.client.ClientFactory;

/**
 * Grizzly Client Invocation Handler for RPC
 *
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class GrizzlyClientInvocationHandler extends AbstractClientInvocationHandler {

  public GrizzlyClientInvocationHandler(List<InetSocketAddress> servers, int clientNums, int connectTimeout,
      String targetInstanceName, Map<String, Integer> methodTimeouts, int codecType, int protocolType) {
    super(servers, clientNums, connectTimeout, targetInstanceName, methodTimeouts, codecType, protocolType);
  }

  @Override
  public ClientFactory getClientFactory() {
    return GrizzlyClientFactory.getInstance();
  }

}
