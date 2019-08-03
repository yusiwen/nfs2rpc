package code.google.nfs.rpc.example;

import code.google.nfs.rpc.Codecs;
import code.google.nfs.rpc.grizzly.client.GrizzlyClientProxy;

public class TestGrizzlyRCPClient {
  public static void main(String[] args) {

    GrizzlyClientProxy proxy = new GrizzlyClientProxy("192.168.2.54", 18886);
    HelloWorldService service = (HelloWorldService) proxy.getServiceProxy("helloworld", Codecs.HESSIAN_CODEC, HelloWorldService.class);

    // Use proxy to call
    String result = service.sayHello("hello");
    System.out.println(result);

    proxy.shutdown(false);
  }
}
