package code.google.nfs.rpc.grizzly.benchmark;

import java.util.List;

import org.glassfish.grizzly.threadpool.GrizzlyExecutorService;
import org.glassfish.grizzly.threadpool.ThreadPoolConfig;

import code.google.nfs.rpc.benchmark.AbstractSimpleProcessorBenchmarkClient;
import code.google.nfs.rpc.benchmark.ClientRunnable;
import code.google.nfs.rpc.client.ClientFactory;
import code.google.nfs.rpc.grizzly.client.GrizzlyClientFactory;

/**
 * Grizzly Simple Benchmark Client
 *
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class GrizzlySimpleBenchmarkClient extends AbstractSimpleProcessorBenchmarkClient {

  public static void main(String[] args) throws Exception {
    new GrizzlySimpleBenchmarkClient().run(args);
  }

  public ClientFactory getClientFactory() {
    return GrizzlyClientFactory.getInstance();
  }

  @Override
  protected void startRunnables(List<ClientRunnable> runnables) {
    ThreadPoolConfig tpc = ThreadPoolConfig.defaultConfig().copy().setPoolName("benchmarkclient")
        .setMaxPoolSize(runnables.size()).setCorePoolSize(runnables.size());

    GrizzlyExecutorService executorService = GrizzlyExecutorService.createInstance(tpc);

    for (int i = 0; i < runnables.size(); i++) {
      ClientRunnable runnable = runnables.get(i);
      executorService.execute(runnable);
    }
  }
}
