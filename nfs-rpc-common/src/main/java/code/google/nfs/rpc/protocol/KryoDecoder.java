package code.google.nfs.rpc.protocol;

import com.esotericsoftware.kryo.io.Input;

import code.google.nfs.rpc.benchmark.KryoUtils;

/**
 * Kryo Decoder
 *
 * @author <a href="mailto:jlusdy@gmail.com">jlusdy</a>
 */
public class KryoDecoder implements Decoder {
  /**
   * @param className
   * @param bytes
   * @return
   * @throws Exception
   */
  @Override
  public Object decode(String className, byte[] bytes) throws Exception {
    Input input = new Input(bytes);
    return KryoUtils.getKryo().readClassAndObject(input);
  }
}
