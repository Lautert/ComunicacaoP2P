package com.service;


import com.ServerCenterMessage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author lautert-vm
 */
public class ClienteP2PServer {
    
    private Socket socket = null;
    private ObjectOutputStream output = null;
    
    public Socket connect(Integer port) {
        try {
            this.socket = new Socket("localhost", port);
            this.output = new ObjectOutputStream(socket.getOutputStream());
        } catch (UnknownHostException ex) {
            Logger.getLogger(ClienteP2PServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClienteP2PServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return socket;
    }
    
    public void send(ServerCenterMessage message) {
        try {
            output.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(ClienteP2PServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
