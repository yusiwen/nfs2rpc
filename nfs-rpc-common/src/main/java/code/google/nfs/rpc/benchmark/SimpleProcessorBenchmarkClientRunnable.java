package code.google.nfs.rpc.benchmark;

import com.google.protobuf.ByteString;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import code.google.nfs.rpc.Codecs;
import code.google.nfs.rpc.client.ClientFactory;
import code.google.nfs.rpc.protocol.SimpleProcessorProtocol;

/**
 * Simple Processor RPC Benchmark Client Thread
 *
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class SimpleProcessorBenchmarkClientRunnable implements ClientRunnable {

  private static final Log LOGGER = LogFactory.getLog(SimpleProcessorBenchmarkClientRunnable.class);

  private int requestSize;

  private CyclicBarrier barrier;

  private CountDownLatch latch;

  private long endTime;

  private boolean running = true;

  private ClientFactory factory;

  private String targetIP;

  private int targetPort;

  private int clientNums;

  private int rpcTimeout;

  private int codecType;

  // response time spread
  private long[] responseSpreads = new long[9];

  // error request per second
  private long[] errorTPS = null;

  // error response times per second
  private long[] errorResponseTimes = null;

  // tps per second
  private long[] tps = null;

  // response times per second
  private long[] responseTimes = null;

  // benchmark startTime
  private long startTime;

  // benchmark maxRange
  private int maxRange;

  public SimpleProcessorBenchmarkClientRunnable(ClientFactory factory, String targetIP, int targetPort, int clientNums,
      int rpcTimeout, int codecType, int requestSize, CyclicBarrier barrier, CountDownLatch latch, long startTime,
      long endTime) {
    this.factory = factory;
    this.targetIP = targetIP;
    this.targetPort = targetPort;
    this.clientNums = clientNums;
    this.rpcTimeout = rpcTimeout;
    this.codecType = codecType;
    this.requestSize = requestSize;
    this.barrier = barrier;
    this.latch = latch;
    this.startTime = startTime;
    this.endTime = endTime;
    maxRange = (Integer.parseInt(String.valueOf((endTime - startTime))) / 1000) + 1;
    errorTPS = new long[maxRange];
    errorResponseTimes = new long[maxRange];
    tps = new long[maxRange];
    responseTimes = new long[maxRange];
    // init
    for (int i = 0; i < maxRange; i++) {
      errorTPS[i] = 0;
      errorResponseTimes[i] = 0;
      tps[i] = 0;
      responseTimes[i] = 0;
    }
  }

  public void run() {
    try {
      barrier.await();
    } catch (Exception e) {
      // IGNORE
    }
    if (codecType == Codecs.PB_CODEC) {
      runPB();
    } else {
      runJavaAndHessian();
    }
    latch.countDown();
  }

  private void runJavaAndHessian() {
    while (running) {
      Object requestObject = new RequestObject(requestSize);
      long beginTime = System.currentTimeMillis();
      if (beginTime >= endTime) {
        running = false;
        break;
      }
      try {
        Object response = null;
        response = factory.get(targetIP, targetPort, 1000, clientNums).invokeSync(requestObject, rpcTimeout, codecType,
            SimpleProcessorProtocol.TYPE);
        long currentTime = System.currentTimeMillis();
        if (beginTime <= startTime) {
          continue;
        }
        long consumeTime = currentTime - beginTime;
        sumResponseTimeSpread(consumeTime);
        int range = Integer.parseInt(String.valueOf(beginTime - startTime)) / 1000;
        if (range >= maxRange) {
          System.err.println("benchmark range exceeds maxRange,range is: " + range + ",maxRange is: " + maxRange);
          continue;
        }
        if (((ResponseObject) response).getBytes() != null) {
          tps[range] = tps[range] + 1;
          responseTimes[range] = responseTimes[range] + consumeTime;
        } else {
          LOGGER.error("server return response is null");
          errorTPS[range] = errorTPS[range] + 1;
          errorResponseTimes[range] = errorResponseTimes[range] + consumeTime;
        }
      } catch (Exception e) {
        LOGGER.error("client.invokeSync error", e);
        long currentTime = System.currentTimeMillis();
        if (beginTime <= startTime) {
          continue;
        }
        long consumeTime = currentTime - beginTime;
        sumResponseTimeSpread(consumeTime);
        int range = Integer.parseInt(String.valueOf(beginTime - startTime)) / 1000;
        if (range >= maxRange) {
          System.err.println("benchmark range exceeds maxRange,range is: " + range + ",maxRange is: " + maxRange);
          continue;
        }
        errorTPS[range] = errorTPS[range] + 1;
        errorResponseTimes[range] = errorResponseTimes[range] + consumeTime;
      }
    }
  }

  private void runPB() {
    while (running) {
      code.google.nfs.rpc.benchmark.PB.RequestObject.Builder objectBuilder = PB.RequestObject.newBuilder();
      objectBuilder.setBytesObject(ByteString.copyFrom(new byte[requestSize]));
      Object requestObject = objectBuilder.build();
      long beginTime = System.currentTimeMillis();
      if (beginTime >= endTime) {
        running = false;
        break;
      }
      try {
        Object response = null;
        response = factory.get(targetIP, targetPort, 1000, clientNums).invokeSync(requestObject, rpcTimeout, codecType,
            SimpleProcessorProtocol.TYPE);
        long currentTime = System.currentTimeMillis();
        if (beginTime <= startTime) {
          continue;
        }
        long consumeTime = currentTime - beginTime;
        sumResponseTimeSpread(consumeTime);
        int range = Integer.parseInt(String.valueOf(beginTime - startTime)) / 1000;
        if (range >= maxRange) {
          System.err.println("benchmark range exceeds maxRange,range is: " + range + ",maxRange is: " + maxRange);
          continue;
        }
        if (((PB.ResponseObject) response).getBytesObject().toByteArray().length > 0) {
          tps[range] = tps[range] + 1;
          responseTimes[range] = responseTimes[range] + consumeTime;
        } else {
          LOGGER.error("server return response is null");
          errorTPS[range] = errorTPS[range] + 1;
          errorResponseTimes[range] = errorResponseTimes[range] + consumeTime;
        }
      } catch (Exception e) {
        LOGGER.error("client.invokeSync error", e);
        long currentTime = System.currentTimeMillis();
        if (beginTime <= startTime) {
          continue;
        }
        long consumeTime = currentTime - beginTime;
        sumResponseTimeSpread(consumeTime);
        int range = Integer.parseInt(String.valueOf(beginTime - startTime)) / 1000;
        if (range >= maxRange) {
          System.err.println("benchmark range exceeds maxRange,range is: " + range + ",maxRange is: " + maxRange);
          continue;
        }
        errorTPS[range] = errorTPS[range] + 1;
        errorResponseTimes[range] = errorResponseTimes[range] + consumeTime;
      }
    }
  }

  public List<long[]> getResults() {
    List<long[]> results = new ArrayList<long[]>();
    results.add(responseSpreads);
    results.add(tps);
    results.add(responseTimes);
    results.add(errorTPS);
    results.add(errorResponseTimes);
    return results;
  }

  private void sumResponseTimeSpread(long responseTime) {
    if (responseTime <= 0) {
      responseSpreads[0] = responseSpreads[0] + 1;
    } else if (responseTime > 0 && responseTime <= 1) {
      responseSpreads[1] = responseSpreads[1] + 1;
    } else if (responseTime > 1 && responseTime <= 5) {
      responseSpreads[2] = responseSpreads[2] + 1;
    } else if (responseTime > 5 && responseTime <= 10) {
      responseSpreads[3] = responseSpreads[3] + 1;
    } else if (responseTime > 10 && responseTime <= 50) {
      responseSpreads[4] = responseSpreads[4] + 1;
    } else if (responseTime > 50 && responseTime <= 100) {
      responseSpreads[5] = responseSpreads[5] + 1;
    } else if (responseTime > 100 && responseTime <= 500) {
      responseSpreads[6] = responseSpreads[6] + 1;
    } else if (responseTime > 500 && responseTime <= 1000) {
      responseSpreads[7] = responseSpreads[7] + 1;
    } else if (responseTime > 1000) {
      responseSpreads[8] = responseSpreads[8] + 1;
    }
  }

}
