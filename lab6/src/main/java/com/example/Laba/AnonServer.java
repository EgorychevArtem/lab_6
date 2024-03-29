package com.example.Laba;

import akka.actor.ActorRef;
import akka.http.javadsl.server.Route;
import akka.pattern.Patterns;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Request;
import org.asynchttpclient.Response;

import java.net.ConnectException;
import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.regex.Pattern;

import static akka.http.javadsl.server.Directives.*;

public class AnonServer {
    private static String URL = "url";
    private static String COUNT = "count";
    static AsyncHttpClient http;
    static ActorRef storage;
    static ZooKeeper zoo;

    public AnonServer(ActorRef storage, AsyncHttpClient httpClient, ZooKeeper zoo) {
        this.http = httpClient;
        this.storage = storage;
        this.zoo = zoo;
    }

    public static Route createRoute(){
        return route(
                get(() -> parameter(URL, url ->
                        parameter(COUNT, count ->
                                handleGetWithUrlCount(url, Integer.parseInt(count)))
                                )
                        )
        );
    }

    public static Route handleGetWithUrlCount(String url, int count){
        CompletionStage<Response> result;
        if (count == 0){
            result = fetch(http.prepareGet(url).build());
        } else{
            result = Redirect(url, count--);
        }
        return completeOKWithFutureString(result.thenApply(Response::getResponseBody));
    }

    public static CompletionStage<Response> Redirect(String url, int count){
        return Patterns.ask(storage, new GetRandomMessage(), Duration.ofSeconds(3))
                .thenApply(o -> ((ReturnMessage)o).server)
                .thenCompose(z ->
                        fetch(createServerRequest(getServUrl(z), url, count))
                .handle( (result, ex) ->  BadDiretion(result, ex, z)));

    }

    private static Response BadDiretion(Response result, Throwable ex, String z) {
        if (ex instanceof ConnectException){
            storage.tell(new DeleteMessage(z), ActorRef.noSender());
        }
        return result;
    }

    private static String getServUrl(String znode) {
        try {
            return new String(zoo.getData(znode, false, null));
        } catch (KeeperException | InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    public static CompletionStage<Response> fetch(Request req){
        return http.executeRequest(req).toCompletableFuture();
    }

    public static Request createServerRequest(String servurl, String url, int count){
        return http.prepareGet(servurl).addQueryParam(URL, url)
                .addQueryParam(COUNT, Integer.toString(count)).build();
    }
}
