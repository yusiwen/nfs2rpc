package code.google.nfs.rpc.grizzly.benchmark;

import code.google.nfs.rpc.benchmark.AbstractBenchmarkServer;
import code.google.nfs.rpc.grizzly.server.GrizzlyServer;
import code.google.nfs.rpc.server.Server;

/**
 * Grizzly RPC Benchmark Server
 *
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class GrizzlyBenchmarkServer extends AbstractBenchmarkServer {

  public static void main(String[] args) throws Exception {
    // String[] myArgs = new String[] {"9090", "20", "100"};
    new GrizzlyBenchmarkServer().run(args);
    while (true) {
      Thread.sleep(Integer.MAX_VALUE);
    }
  }

  public Server getServer() {
    return new GrizzlyServer();
  }
}
