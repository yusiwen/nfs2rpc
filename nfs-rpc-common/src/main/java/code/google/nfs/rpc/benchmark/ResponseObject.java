package code.google.nfs.rpc.benchmark;

import java.io.Serializable;

/**
 * Just for RPC Benchmark Test,response object
 *
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class ResponseObject implements Serializable {

  private static final long serialVersionUID = 1L;

  private byte[] bytes = null;

  public ResponseObject(int size) {
    bytes = new byte[size];
  }

  public byte[] getBytes() {
    return bytes;
  }

}
