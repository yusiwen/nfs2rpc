package code.google.nfs.rpc.example;

import code.google.nfs.rpc.Codecs;
import code.google.nfs.rpc.mina2.client.MinaClientProxy;

public class TestMina2RCPClient {
  public static void main(String[] args) {

    MinaClientProxy proxy = new MinaClientProxy("127.0.0.1", 18888);
    HelloWorldService service = (HelloWorldService) proxy.getServiceProxy("helloworld", Codecs.HESSIAN_CODEC, HelloWorldService.class);

    // Use proxy to call
    String result = service.sayHello("hello");
    System.out.println(result);

    proxy.shutdown(false);
  }
}
