
package code.google.nfs.rpc.benchmark;

/**
 * Just for Reflection RPC Benchmark
 *
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public interface BenchmarkTestService {

  public Object execute(Object request);

  public Object executePB(PB.RequestObject request);

}
