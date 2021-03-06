
package code.google.nfs.rpc.server;

/**
 * Direct RPC Call Server Processor Interface
 *
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public interface ServerProcessor {

  /**
   * Handle request,then return Object
   *
   * @param request
   * @return Object
   * @throws Exception
   */
  public Object handle(Object request) throws Exception;

}
