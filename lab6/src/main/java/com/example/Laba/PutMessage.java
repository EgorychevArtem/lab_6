package com.example.Laba;

import java.util.List;

public class PutMessage {
    List<String> servers;

    public PutMessage(List<String> servers){
        this.servers = servers;
    }

    public List<String> getServers() {
        return servers;
    }
}
