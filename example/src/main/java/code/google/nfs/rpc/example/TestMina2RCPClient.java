package code.google.nfs.rpc.example;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import code.google.nfs.rpc.Codecs;
import code.google.nfs.rpc.mina2.client.MinaClientFactory;
import code.google.nfs.rpc.mina2.client.MinaClientInvocationHandler;
import code.google.nfs.rpc.protocol.RPCProtocol;

public class TestMina2RCPClient {
  public static void main(String[] args) {

    Map<String, Integer> methodTimeouts = new HashMap<String, Integer>();
    // so u can specialize some method timeout
    methodTimeouts.put("*", 500);

    List<InetSocketAddress> servers = new ArrayList<>();
    servers.add(new InetSocketAddress("127.0.0.1", 18888));

    // Protocol also support Protobuf & Java,if u use Protobuf,u need call
    // PBDecoder.addMessage first.
    int codectype = Codecs.HESSIAN_CODEC;
    MinaClientInvocationHandler handler = new MinaClientInvocationHandler(servers, 1, 30,
            "helloworld", methodTimeouts, codectype, RPCProtocol.TYPE);
    HelloWorldService service = (HelloWorldService) Proxy.newProxyInstance(TestMina2RCPClient.class.getClassLoader(),
        new Class<?>[] { HelloWorldService.class }, handler);

    // Use proxy to call
    String result = service.sayHello("hello");
    System.out.println(result);

    MinaClientFactory f = (MinaClientFactory) handler.getClientFactory();
    f.shutdown(false);
  }
}
