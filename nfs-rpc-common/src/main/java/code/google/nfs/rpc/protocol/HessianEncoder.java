package code.google.nfs.rpc.protocol;

import com.caucho.hessian.io.Hessian2Output;

import java.io.ByteArrayOutputStream;

/**
 * Hessian Encoder,use Hessian2
 *
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class HessianEncoder implements Encoder {

  public byte[] encode(Object object) throws Exception {
    ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
    Hessian2Output output = new Hessian2Output(byteArray);
    output.writeObject(object);
    output.close();
    byte[] bytes = byteArray.toByteArray();
    return bytes;
  }

}
