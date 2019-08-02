package code.google.nfs.rpc.mina.serialize;

import org.apache.mina.core.buffer.IoBuffer;

import code.google.nfs.rpc.protocol.ByteBufferWrapper;

/**
 * Implements ByteBufferWrapper based on ByteBuffer
 *
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class MinaByteBufferWrapper implements ByteBufferWrapper {

  private IoBuffer byteBuffer;

  public MinaByteBufferWrapper() {
    ;
  }

  public MinaByteBufferWrapper(IoBuffer in) {
    this.byteBuffer = in;
  }

  @Override
  public ByteBufferWrapper get(int capacity) {
    byteBuffer = IoBuffer.allocate(capacity, false);
    return this;
  }

  @Override
  public byte readByte() {
    return byteBuffer.get();
  }

  @Override
  public void readBytes(byte[] dst) {
    byteBuffer.get(dst);
  }

  @Override
  public int readInt() {
    return byteBuffer.getInt();
  }

  @Override
  public int readableBytes() {
    return byteBuffer.remaining();
  }

  @Override
  public int readerIndex() {
    return byteBuffer.position();
  }

  @Override
  public void setReaderIndex(int index) {
    byteBuffer.position(index);
  }

  @Override
  public void writeByte(byte data) {
    byteBuffer.put(data);
  }

  @Override
  public void writeBytes(byte[] data) {
    byteBuffer.put(data);
  }

  @Override
  public void writeInt(int data) {
    byteBuffer.putInt(data);
  }

  public IoBuffer getByteBuffer() {
    return byteBuffer;
  }

  @Override
  public void writeByte(int index, byte data) {
    byteBuffer.put(index, data);
  }

}
