package com.example.Laba;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import akka.actor.AbstractActor;
public class Storage {
    Random random = new Random();
    List<String> storage;

    Storage(){
        this.storage = new ArrayList<>();
    }

    public AbstractActor.Receive createReceive(){
        return createReceive()
    }
}
