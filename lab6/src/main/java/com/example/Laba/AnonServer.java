package com.example.Laba;

import akka.actor.ActorRef;
import akka.http.javadsl.server.Route;
import org.apache.zookeeper.ZooKeeper;
import org.asynchttpclient.AsyncHttpClient;

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
                get(() -> parametr("url", url ->
                        parameter()
                                )
                        )
        )
    }
}
