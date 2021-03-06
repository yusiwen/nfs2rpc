package code.google.nfs.rpc.netty.benchmark;

import code.google.nfs.rpc.benchmark.AbstractBenchmarkServer;
import code.google.nfs.rpc.netty.server.NettyServer;
import code.google.nfs.rpc.server.Server;

/**
 * Netty RPC Benchmark Server
 *
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class NettyBenchmarkServer extends AbstractBenchmarkServer {

  public static void main(String[] args) throws Exception {
    new NettyBenchmarkServer().run(args);
  }

  @Override
  public Server getServer() {
    return new NettyServer();
  }
}
