package code.google.nfs.rpc.protocol;

import com.esotericsoftware.kryo.io.Output;

import code.google.nfs.rpc.benchmark.KryoUtils;

/**
 * Kryo Encoder
 *
 * @author <a href="mailto:jlusdy@gmail.com">jlusdy</a>
 */
public class KryoEncoder implements Encoder {
  /**
   * @param object
   * @return
   * @throws Exception
   */
  @Override
  public byte[] encode(Object object) throws Exception {
    Output output = new Output(256);
    KryoUtils.getKryo().writeClassAndObject(output, object);
    return output.toBytes();
  }

}
