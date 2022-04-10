import io.grpc.Server;
import io.grpc.ServerBuilder;
import server.SumServer;

import java.io.IOException;

public class MainServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(5555).addService(new SumServer()).build();

        System.out.println("Server starting ... ");

        server.start();
        server.awaitTermination();

    }
}
