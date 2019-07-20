
package code.google.nfs.rpc.protocol;

import com.google.protobuf.Message;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ProtocolBuf Decoder
 *
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class PBDecoder implements Decoder {

  private static ConcurrentHashMap<String, Message> messages = new ConcurrentHashMap<String, Message>();

  public static void addMessage(String className, Message message) {
    messages.putIfAbsent(className, message);
  }

  public Object decode(String className, byte[] bytes) throws Exception {
    Message message = messages.get(className);
    return message.newBuilderForType().mergeFrom(bytes).build();
  }

}
