package code.google.nfs.rpc.osgi;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import code.google.nfs.rpc.NamedThreadFactory;
import code.google.nfs.rpc.server.Server;

@Component(name = "code.google.nfs.rpc.osgi.test.minaserverwrapper", immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
public class MinaServerTestWrapper {

  private static final Log LOGGER = LogFactory.getLog(MinaServerTestWrapper.class);

  @Reference(service = Server.class, target = "(type=mina)", bind = "bind", unbind = "unbind")
  protected Server server = null;

  public void bind(Server s) {
    LOGGER.info("Binding MinaServer: " + s);
    server = s;
  }

  public void unbind(Server s) throws Exception {
    if (this.server == s) {
      server.stop();
      server = null;
    }
  }

  @Activate
  public void activate(final Map<String, Object> properties) throws Exception {

    ThreadFactory tf = new NamedThreadFactory("BUSINESSTHREADPOOL");
    ExecutorService threadPool = new ThreadPoolExecutor(20, 100, 300, TimeUnit.SECONDS,
        new SynchronousQueue<Runnable>(), tf);
    int port = 18888;
    if (properties.containsKey("listen.port")) {
      port = Integer.parseInt((String)properties.get("listen.port"));
      LOGGER.info("Find listen.port = " + port);
    }
    server.start(port, threadPool);
  }

  @Deactivate
  public void deactivate() throws Exception {
    server.stop();
  }
}
