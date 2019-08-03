package code.google.nfs.rpc.osgi;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

@Component(name = "code.google.nfs.rpc.osgi.test.grizzlyserverwrapper", immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
public class GrizzlyServerTestWrapper {

  private static final Log LOGGER = LogFactory.getLog(GrizzlyServerTestWrapper.class);

  @Reference(service = Server.class, target = "(type=grizzly)", bind = "bind", unbind = "unbind")
  protected Server server = null;
  protected Worker worker = new Worker();
  private ExecutorService executor = Executors.newCachedThreadPool();

  public void bind(Server s) {
    LOGGER.info("Binding GrizzlyServer: " + s);
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

    int port = 18886;
    if (properties.containsKey("listen.port")) {
      port = Integer.parseInt((String) properties.get("listen.port"));
      LOGGER.info("Find listen.port = " + port);
    }
    worker.setPort(port);
    executor.execute(worker);
  }

  @Deactivate
  public void deactivate() throws Exception {
    server.stop();
    executor.shutdown();
  }

  /**
   * Thread worker that continuously prints a message.
   */
  private class Worker implements Runnable {

    private int port;

    public void run() {
      ThreadFactory tf = new NamedThreadFactory("BUSINESSTHREADPOOL");
      ExecutorService threadPool = new ThreadPoolExecutor(20, 100, 300, TimeUnit.SECONDS,
          new SynchronousQueue<Runnable>(), tf);
      try {
        server.start(port, threadPool);
      } catch (Exception e) {
        LOGGER.error("GrizzlyServer start error: ", e);
      }
    }

    public int getPort() {
      return port;
    }

    public void setPort(int port) {
      this.port = port;
    }

  }
}
