package code.google.nfs.rpc.grizzly.serialize;

import org.glassfish.grizzly.Buffer;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.memory.Buffers;

import code.google.nfs.rpc.protocol.ByteBufferWrapper;

/**
 * Grizzly ByteBuffer Wrapper
 *
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class GrizzlyByteBufferWrapper implements ByteBufferWrapper {

  private Buffer buffer;
  private FilterChainContext ctx;

  public GrizzlyByteBufferWrapper(FilterChainContext ctx) {
    this.ctx = ctx;
  }

  public GrizzlyByteBufferWrapper(Buffer buffer) {
    this.buffer = buffer;
  }

  public ByteBufferWrapper get(int capacity) {
    buffer = Buffers.wrap(ctx.getMemoryManager(), new byte[capacity]);
    return this;
  }

  public Buffer getBuffer() {
    return buffer;
  }

  @Override
  public byte readByte() {
    return buffer.get();
  }

  @Override
  public void readBytes(byte[] data) {
    buffer.get(data);
  }

  @Override
  public int readInt() {
    return buffer.getInt();
  }

  @Override
  public int readableBytes() {
    return buffer.remaining();
  }

  @Override
  public int readerIndex() {
    return buffer.position();
  }

  @Override
  public void setReaderIndex(int readerIndex) {
    buffer.position(readerIndex);
  }

  @Override
  public void writeByte(byte data) {
    buffer.put(data);
  }

  @Override
  public void writeByte(int index, byte data) {
    buffer.put(index, data);
  }

  @Override
  public void writeBytes(byte[] data) {
    buffer.put(data);
  }

  @Override
  public void writeInt(int data) {
    buffer.putInt(data);
  }
}
