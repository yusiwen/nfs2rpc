
package code.google.nfs.rpc.benchmark;

import java.util.List;

/**
 * client runnable,so we can collect results
 *
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public interface ClientRunnable extends Runnable {

  public List<long[]> getResults();

}
