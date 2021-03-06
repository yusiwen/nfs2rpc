package code.google.nfs.rpc.benchmark;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import code.google.nfs.rpc.client.ClientFactory;

/**
 * Test for RPC based on direct call Benchmark
 *
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public abstract class AbstractSimpleProcessorBenchmarkClient extends AbstractBenchmarkClient {

  public ClientRunnable getClientRunnable(String targetIP, int targetPort, int clientNums, int rpcTimeout, int dataType,
      int requestSize, CyclicBarrier barrier, CountDownLatch latch, long endTime, long startTime) {
    return new SimpleProcessorBenchmarkClientRunnable(getClientFactory(), targetIP, targetPort, clientNums, rpcTimeout,
        dataType, requestSize, barrier, latch, startTime, endTime);
  }

  public abstract ClientFactory getClientFactory();

}
