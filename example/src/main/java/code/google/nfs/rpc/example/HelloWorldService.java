package code.google.nfs.rpc.example;

import code.google.nfs.rpc.Service;

public interface HelloWorldService extends Service {
  public String sayHello(String word);
}
