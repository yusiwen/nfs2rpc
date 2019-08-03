package code.google.nfs.rpc.example;

import code.google.nfs.rpc.Codecs;
import code.google.nfs.rpc.netty.client.NettyClientProxy;

public class TestNettyRCPClient {
  public static void main(String[] args) {

    NettyClientProxy proxy = new NettyClientProxy("192.168.2.54", 18887);
    HelloWorldService service = (HelloWorldService) proxy.getServiceProxy("helloworld", Codecs.HESSIAN_CODEC, HelloWorldService.class);

    // Use proxy to call
    String result = service.sayHello("hello");
    System.out.println(result);

    proxy.shutdown(false);
  }
}
