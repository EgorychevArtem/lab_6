package com.example.Laba;

import akka.actor.ActorRef;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.regex.Pattern;

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
            result = Get(http.prepareGet(url).build());
        } else{
            result = Redirect(url, count--);
        }
        return completeOKWithFutureString(result.thenApply(Response::getResponseBody));
    }

    public CompletionStage<Response> Redirect(String url, int count){
        return Patterns.ask(storage, new GetRandomMessage(), Duration.ofSeconds(3))
                .thenApply(o -> ((ReturnMessage)o).server)
                .thenCompose(z ->
                {
                    try {
                        return Get(createServerRequest(new String(zoo.getData(z,false,null)), url, count));
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                })
                .handle((re))

    }

    public CompletionStage<Response> Get(Request req){
        return http.executeRequest(req).toCompletableFuture();
    }

    public Request createServerRequest(String servurl, String url, int count){
        return http.prepareGet(servurl).addQueryParam("url", url)
                .addQueryParam("count", Integer.toString(count)).build();
    }
}
