
package code.google.nfs.rpc.benchmark;

import code.google.nfs.rpc.benchmark.PB.RequestObject;

/**
 * Just for Reflection RPC Benchmark
 *
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class BenchmarkTestServiceImpl implements BenchmarkTestService {

  private int responseSize;

  public BenchmarkTestServiceImpl(int responseSize) {
    this.responseSize = responseSize;
  }

  // support java/hessian/pb codec
  public Object execute(Object request) {
    return new ResponseObject(responseSize);
  }

  public Object executePB(RequestObject request) {
    throw new UnsupportedOperationException("unsupported");
  }

}
