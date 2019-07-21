package code.google.nfs.rpc.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;

import code.google.nfs.rpc.NamedThreadFactory;
import code.google.nfs.rpc.mina2.server.MinaServer;
import code.google.nfs.rpc.protocol.RPCProtocol;
import code.google.nfs.rpc.server.Server;

public class TestMina2RPCServer {
  public static void main(String[] args) throws Exception {
    Server server = new MinaServer();
    server.registerProcessor(RPCProtocol.TYPE, "helloworld", new HelloWorldComponent());
    ThreadFactory tf = new NamedThreadFactory("BUSINESSTHREADPOOL");
    ExecutorService threadPool = new ThreadPoolExecutor(20, 100, 300, TimeUnit.SECONDS,
        new SynchronousQueue<Runnable>(), tf);
    server.start(18888, threadPool);
  }
}
