package com;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ServerCenterMessage implements Serializable {

    // PESSOA ORIGEM
    private String name;
    // PORT QUE O CLIENTE ESTA DISPONIBILIZANDO
    private Integer port;
    // USUARIOS ONLINE
    private Map<String, Integer> setOnlines = new HashMap<String, Integer>();
    // CONTROLADOR
    private ActionCenter action;

    public enum ActionCenter {
        CONNECT,
        DISCONNECT,
        FILE,
        USERS_ONLINE
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

    public Map<String, Integer> getSetOnlines() {
        return setOnlines;
    }

    public void setSetOnlines(Map<String, Integer> setOnlines) {
        this.setOnlines = setOnlines;
    }

    public ActionCenter getAction() {
        return action;
    }

    public void setAction(ActionCenter action) {
        this.action = action;
    }
}
