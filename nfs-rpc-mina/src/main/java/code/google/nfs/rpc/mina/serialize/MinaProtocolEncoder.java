package code.google.nfs.rpc.mina.serialize;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import code.google.nfs.rpc.protocol.ProtocolUtils;

/**
 * Encode message
 *
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class MinaProtocolEncoder extends ProtocolEncoderAdapter {

  @Override
  public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception {
    MinaByteBufferWrapper wrapper = new MinaByteBufferWrapper();
    ProtocolUtils.encode(message, wrapper);
    wrapper.getByteBuffer().flip();
    out.write(wrapper.getByteBuffer());
  }

}
