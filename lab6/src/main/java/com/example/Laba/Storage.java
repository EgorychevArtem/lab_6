package com.example.Laba;

import akka.actor.AbstractActor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Storage {
    Random random = new Random();
    List<String> storage;

    Storage(){
        this.storage = new ArrayList<>();
    }

    @Override
    public Receive createReceive(){

    }
}
