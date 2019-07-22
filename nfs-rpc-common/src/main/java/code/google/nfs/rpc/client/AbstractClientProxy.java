package code.google.nfs.rpc.client;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractClientProxy implements ClientProxy {

  protected int clientNums = 1;
  protected int connectTimeout = 30;
  protected String serverIp = null;
  protected int serverPort;

  protected Map<String, Integer> methodTimeouts = new HashMap<String, Integer>();

  public void addMethodTimeout(String methodName, int timeout) {
    methodTimeouts.put(methodName, timeout);
  }

  public int getConnectTimeout() {
    return connectTimeout;
  }

  public void setConnectTimeout(int connectTimeout) {
    this.connectTimeout = connectTimeout;
  }

  public int getClientNums() {
    return clientNums;
  }

  public void setClientNums(int clientNums) {
    this.clientNums = clientNums;
  }

  public String getServerIp() {
    return serverIp;
  }

  public void setServerIp(String serverIp) {
    this.serverIp = serverIp;
  }

  public int getServerPort() {
    return serverPort;
  }

  public void setServerPort(int serverPort) {
    this.serverPort = serverPort;
  }
}
