package com.example.Laba;


import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import org.apache.zookeeper.ZooKeeper;
import org.asynchttpclient.AsyncHttpClient;

import java.io.IOException;
import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;

import static org.asynchttpclient.Dsl.asyncHttpClient;

public class App {
    static Logger log = Logger.getLogger(App.class.getName());

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: Anonymizer <host> <port>");
            System.exit(-1);
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        final ZooKeeper zoo = new ZooKeeper("127.0.0.1:2181", 3000, e -> log.info(e.toString()));
        ActorSystem system = ActorSystem.create("routes");
        final Http http = Http.get(system);
        final ActorMaterializer materializer = ActorMaterializer.create(system);
        AsyncHttpClient httpClient = asyncHttpClient();

        ActorRef storage = system.actorOf(Props.create(Storage.class));

        Handler handler = new Handler(zoo, storage, "/servers");
        handler.createServer("localhost" + port, host, port);

        AnonServer server = new AnonServer(storage, httpClient, zoo);
        final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = AnonServer.createRoute().flow(system, materializer);
        final CompletionStage<ServerBinding> binding = http.bindAndHandle(
                routeFlow,
                ConnectHttp.toHost(host, port),
                materializer
        );

        System.out.println("Server online at " + host + ":" + port + "/");
        System.out.println("Press RETURN to stop...");
        System.in.read();

        httpClient.close();
        Handler.removeAllWatches();
        zoo.close();
        binding
                .thenCompose(ServerBinding::unbind)
                .thenAccept(unbound -> system.terminate());
    }
}
