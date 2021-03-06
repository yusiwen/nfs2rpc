package code.google.nfs.rpc.grizzly.client;

import java.io.IOException;
import java.util.List;

import org.glassfish.grizzly.filterchain.BaseFilter;
import org.glassfish.grizzly.filterchain.FilterChainContext;
import org.glassfish.grizzly.filterchain.NextAction;

import code.google.nfs.rpc.ResponseWrapper;
import code.google.nfs.rpc.client.Client;

/**
 * Grizzly Client Handler
 *
 * @author <a href="mailto:bluedavy@gmail.com">bluedavy</a>
 */
public class GrizzlyClientHandler extends BaseFilter {

  private Client client;

  public void setClient(Client client) {
    this.client = client;
  }

  @Override
  public NextAction handleRead(FilterChainContext ctx) throws IOException {
    final Object message = ctx.getMessage();

    IllegalStateException error = null;

    try {
      if (message instanceof List) {
        @SuppressWarnings("unchecked")
        List<ResponseWrapper> responses = (List<ResponseWrapper>) message;
        client.putResponses(responses);
      } else if (message instanceof ResponseWrapper) {
        ResponseWrapper response = (ResponseWrapper) message;
        client.putResponse(response);
      } else {
        error = new IllegalStateException("receive message error,only support List || ResponseWrapper");
      }
    } catch (Exception e) {
      error = new IllegalStateException(e);
    }

    if (error != null) {
      throw error;
    }

    return ctx.getStopAction();
  }
}
