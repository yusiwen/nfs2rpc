package code.google.nfs.rpc.osgi;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import code.google.nfs.rpc.NamedThreadFactory;
import code.google.nfs.rpc.server.Server;

@Component(name = "MinaServerTestWrapper", immediate = true)
public class MinaServerTestWrapper implements BundleActivator {

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
  public void activate() throws Exception {

    ThreadFactory tf = new NamedThreadFactory("BUSINESSTHREADPOOL");
    ExecutorService threadPool = new ThreadPoolExecutor(20, 100, 300, TimeUnit.SECONDS,
        new SynchronousQueue<Runnable>(), tf);
    server.start(18888, threadPool);
  }

  @Override
  public void start(BundleContext context) throws Exception {
    LOGGER.info("MinaServerTestWrapper started");
  }

  @Override
  public void stop(BundleContext context) throws Exception {
    if (server != null)
      server.stop();
  }
}
