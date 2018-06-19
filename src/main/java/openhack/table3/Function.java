package openhack.table3;

import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import com.github.t9t.minecraftrconclient.RconClient;

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
}
