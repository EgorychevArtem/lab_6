package com.example.Laba;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import akka.actor.AbstractActor;
public class Storage extends AbstractActor {
    static final Logger log = Logger.getLogger(Storage.class.getName());
    Random random = new Random();
    List<String> storage;

    Storage(){
        this.storage = new ArrayList<>();
    }

    @Override
    public Receive createReceive(){
        return receiveBuilder()
                .match(PutMessage.class, m ->{
                    log.info("Received servers: " + m.servers.toString());
                    this.storage.clear();
                    this.storage.addAll(m.servers);
                })
                .match(GetRandomMessage.class, m ->{
                    getSender().tell(
                            new ReturnMessage(storage.get(random.nextInt(storage.size()))),
                    );
                })
                .match(DeleteMessage.class, m->{
                    log.info("Delete servers: " + m.server);
                    this.storage.remove(m.server);
                })
                .build();

    }
}
