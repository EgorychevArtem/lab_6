package com.example.Laba;

import akka.actor.ActorRef;
import akka.http.javadsl.server.Route;
import org.apache.zookeeper.ZooKeeper;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;

import java.util.concurrent.CompletionStage;

import static akka.http.javadsl.server.Directives.*;

public class AnonServer {
    AsyncHttpClient http;
    ActorRef storage;
    ZooKeeper zoo;

    public AnonServer(ActorRef storage, AsyncHttpClient httpClient, ZooKeeper zoo) {
        this.http = httpClient;
        this.storage = storage;
        this.zoo = zoo;
    }

    public Route createRoute(){
        return route(
                get(() -> parameter("url", url ->
                        parameter("count", count ->
                                handleGetWithUrlCount(url, Integer.parseInt(count)))
                                )
                        )
        );
    }

    public Route handleGetWithUrlCount(String url, int count){
        CompletionStage<Response> result;
        if (count == 0){
            result =
        }
    }


    public CompletionStage<Response> Get(Request req){
        return http.executeRequest(req).toCompletableFuture();
    }

}
