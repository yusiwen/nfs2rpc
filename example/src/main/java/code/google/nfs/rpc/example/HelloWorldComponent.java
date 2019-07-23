package code.google.nfs.rpc.example;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;

public class HelloWorldComponent implements HelloWorldService {

  private static final Log logger = LogFactoryImpl.getLog(HelloWorldComponent.class);

  public String sayHello(String word) {
    logger.debug("IN sayHello()");
    return word + " return by server";
  }
}
