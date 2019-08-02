package code.google.nfs.rpc.example;

import code.google.nfs.rpc.Codecs;
import code.google.nfs.rpc.mina.client.MinaClientProxy;

public class TestMinaRCPClient {
  public static void main(String[] args) {

    MinaClientProxy proxy = new MinaClientProxy("192.168.2.54", 18888);
    HelloWorldService service = (HelloWorldService) proxy.getServiceProxy("helloworld", Codecs.HESSIAN_CODEC, HelloWorldService.class);

    // Use proxy to call
    String result = service.sayHello("hello");
    System.out.println(result);

    proxy.shutdown(false);
  }
}
