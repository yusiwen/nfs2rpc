
package code.google.nfs.rpc.protocol;

/**
 * Encoder Interface
 *
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public interface Encoder {

  /**
   * Encode Object to byte[]
   */
  public byte[] encode(Object object) throws Exception;

}
