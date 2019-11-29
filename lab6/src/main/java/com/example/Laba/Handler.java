package com.example.Laba;

import akka.actor.ActorRef;
import org.apache.zookeeper.ZooKeeper;

public class Handler {
    ZooKeeper zoo;
    String path;
    ActorRef storage;

    Handler(ZooKeeper zoo, ActorRef storage, String path){
        this.zoo = zoo;
        this.storage = storage;
        this.path = path;
    }
}