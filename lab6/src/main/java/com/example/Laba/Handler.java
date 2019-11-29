package com.example.Laba;

import akka.actor.ActorRef;
import org.apache.zookeeper.*;

import java.util.logging.Logger;

public class Handler {
    static final Logger log = Logger.getLogger(Handler.class.getName());
    ZooKeeper zoo;
    String path;
    ActorRef storage;

    Handler(ZooKeeper zoo, ActorRef storage, String path){
        this.zoo = zoo;
        this.storage = storage;
        this.path = path;

    }

    public void checkChildrenCallback(WatchedEvent e){
        try{
            this.storage.tell(new PutMessage(zoo.getChildren(path, this::checkChildrenCallback).stream()
            .map(s -> path + "/" + s)));
        }catch ()
    }


    public void createServer(String name, String host, int port) throws Exception{
        String Serverpath = zoo.create(
                path + "/" + name,
                (host + ":" + port).getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL);
        log.info("ServerPath:" + Serverpath);
    }
}
