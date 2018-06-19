package openhack.table3;

import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import com.github.t9t.minecraftrconclient.RconClient;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.util.Config;

import java.io.IOException;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/mcrcon". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/mcrcon
     * 2. curl {your host}/api/mcrcon?command=HTTP%20Query
     */
    @FunctionName("mcrcon")
    public HttpResponseMessage<String> mcrcon(
            @HttpTrigger(name = "req", methods = {"get", "post"}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        String query = request.getQueryParameters().get("command");
        String command = request.getBody().orElse(query);

        if (command == null) {
            command = "list";
        }

        String server = "";
        int port = 25575;
        String password = "";

        try {
            RconClient client = RconClient.open(server, port, password);
            String response = client.sendCommand(command);
            client.close();
            return request.createResponse(200, response);
        }
        catch (Exception e) {
            return request.createResponse(404, "Error " + e);
        }
    }

    @FunctionName("servers")
    public HttpResponseMessage<String> servers(
            @HttpTrigger(name = "req", methods = {"get", "post"}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        String query = request.getQueryParameters().get("server");
        String server = request.getBody().orElse(query);

        if (server == null) {
            ApiClient client = Config.defaultClient();
            Configuration.setDefaultApiClient(client);

            CoreV1Api api = new CoreV1Api();
            V1PodList list = api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
            for (V1Pod item : list.getItems()) {
                System.out.println(item.getMetadata().getName());
            }
        }
    }
}
