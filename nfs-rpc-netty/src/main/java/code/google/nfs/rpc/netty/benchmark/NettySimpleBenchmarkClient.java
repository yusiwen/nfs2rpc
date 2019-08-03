package code.google.nfs.rpc.netty.benchmark;

import code.google.nfs.rpc.benchmark.AbstractSimpleProcessorBenchmarkClient;
import code.google.nfs.rpc.client.ClientFactory;
import code.google.nfs.rpc.netty.client.NettyClientFactory;

/**
 * Netty Direct Call RPC Benchmark Client
 *
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class NettySimpleBenchmarkClient extends AbstractSimpleProcessorBenchmarkClient {

  public static void main(String[] args) throws Exception {
    new NettySimpleBenchmarkClient().run(args);
  }

  @Override
  public ClientFactory getClientFactory() {
    return NettyClientFactory.getInstance();
  }
}
