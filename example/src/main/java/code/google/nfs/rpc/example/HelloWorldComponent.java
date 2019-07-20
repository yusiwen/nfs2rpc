package code.google.nfs.rpc.example;

public class HelloWorldComponent implements HelloWorldService {
  public String sayHello(String word) {
    return word + " return by server";
  }
}
