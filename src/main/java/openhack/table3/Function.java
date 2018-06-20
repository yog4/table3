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
        String server = request.getQueryParameters().get("server");
        String portStr = request.getQueryParameters().get("port");
        String password = request.getQueryParameters().get("password");
        String command = request.getBody().orElse(query);
        context.getLogger().info("Input for command = " + command);
        context.getLogger().info("Input for server = " + server);
        context.getLogger().info("Input for port = " + portStr);
        context.getLogger().info("Input for password = " + password);

        int port = 25575;

        if (command == null) {
            command = "list";
        }

        if (server == null) {
            return request.createResponse(404, "Error: server not specified");
        }

        if (portStr == null) {
            port = 25575;
        }
        else {
            try {
                port = Integer.parseInt(portStr);
            }
            catch (Exception e) {
                return request.createResponse(404, "Error: port must be a number");
            }
        }
        context.getLogger().info("Using port = " + port);

        if (password == null) {
            return request.createResponse(404, "Error: password not specified");
        }

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

}
