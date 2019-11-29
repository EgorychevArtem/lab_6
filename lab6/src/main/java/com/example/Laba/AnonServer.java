package com.example.Laba;

import akka.actor.ActorRef;
import org.apache.zookeeper.ZooKeeper;
import org.asynchttpclient.AsyncHttpClient;

public class AnonServer {
    AsyncHttpClient http;
    ActorRef storage;
    ZooKeeper zoo;
    public AnonServer(ActorRef storage, AsyncHttpClient httpClient, ZooKeeper zoo) {
        this.http = httpClient;
        this.storage = storage;
        this.zoo = zoo;
    }
}
