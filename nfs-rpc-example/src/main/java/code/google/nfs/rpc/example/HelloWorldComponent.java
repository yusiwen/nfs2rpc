package code.google.nfs.rpc.example;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.annotations.Component;

import code.google.nfs.rpc.Service;
import code.google.nfs.rpc.protocol.RPCProtocol;

@Component(name = "code.google.nfs.rpc.example.helloworldservice", service = Service.class)
public class HelloWorldComponent implements HelloWorldService {

  private static final Log logger = LogFactory.getLog(HelloWorldComponent.class);

  public String sayHello(String word) {
    logger.debug("IN sayHello()");
    return word + " return by server";
  }

  @Override
  public int getServiceProtocolType() {
    return RPCProtocol.TYPE;
  }

  @Override
  public String getServiceName() {
    return "helloworld";
  }
}
