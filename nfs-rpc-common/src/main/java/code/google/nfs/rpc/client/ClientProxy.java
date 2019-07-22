package code.google.nfs.rpc.client;

public interface ClientProxy {

  Client getClient() throws Exception;

  Object getServiceProxy(String instanceName, int codecType, Class<?> clazz);

  void shutdown(boolean immediately);
}
