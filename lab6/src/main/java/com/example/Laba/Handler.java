package com.example.Laba;

import akka.actor.ActorRef;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
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

    public void createServer(String name, String host, int port) throws KeeperException, InterruptedException {
        String Serverpath = zoo.create(
                path + "/" + name,
                (host + ":" + port).getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL);
    }
}
