package com;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class P2PMessage implements Serializable {

    // PESSOA ORIGEM
    private String name;
    // PORT QUE O CLIENTE ESTA DISPONIBILIZANDO
    private Integer port;
    // CONTROLADOR
    private ActionP2P action;

    public enum ActionP2P {
        CONNECT,
        FILE
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPort() {
        return this.port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public ActionP2P getAction() {
        return action;
    }

    public void setAction(ActionP2P action) {
        this.action = action;
    }
}
