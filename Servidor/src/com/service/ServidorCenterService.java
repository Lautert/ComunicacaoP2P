/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ServerCenterMessage;
import com.ServerCenterMessage.ActionCenter;
import com.Constantes;

public class ServidorCenterService {

    private ServerSocket serverSocket;
    private Socket socket;
    private Map<String, ObjectOutputStream> mapOnlines = new HashMap<String, ObjectOutputStream>();
    private Map<String, Integer> serversOpens = new HashMap<>();

    public ServidorCenterService() {
        try {
            serverSocket = new ServerSocket(Constantes.SERVER_PORT);
            System.out.println("Servidor Center on!");

            while (true) {
                socket = serverSocket.accept();
                new Thread(new ListenerSocket(socket)).start();
            }

        } catch (IOException ex) {
            System.out.println("A porta desejada ja esta é uso!");
        }
    }

    private class ListenerSocket implements Runnable {

        private ObjectOutputStream output;
        private ObjectInputStream input;

        public ListenerSocket(Socket socket) {
            try {
                this.output = new ObjectOutputStream(socket.getOutputStream());
                this.input = new ObjectInputStream (socket.getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(ServidorCenterService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {
            ServerCenterMessage message = null;
            try {
                while ((message = (ServerCenterMessage) input.readObject()) != null) {
                    ActionCenter action = message.getAction();

                    if (action.equals(ActionCenter.CONNECT)) {
                        connect(message, output);
                    } else if (action.equals(ActionCenter.DISCONNECT)) {
                        disconnect(message, output);
                    } 
                }
            } catch (IOException ex) {
                sendOnlines();
//                System.out.println(message.getName()+"["+message.getPort()+"]"+" deixou o chat!");
                disconnect(message, output);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ServidorCenterService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void connect(ServerCenterMessage message, ObjectOutputStream output){
        mapOnlines.put(message.getName()+"["+message.getPort()+"]" , output);
        serversOpens.put(message.getName()+"["+message.getPort()+"]", message.getPort());
        System.out.println("Novo Usuario Conectado : "+message.getName()+"["+message.getPort()+"]");
        send(message, output);
        sendOnlines();
    }
    
    private void disconnect(ServerCenterMessage message, ObjectOutputStream output) {
        mapOnlines.remove(message.getName()+"["+message.getPort()+"]");
        serversOpens.remove(message.getName()+"["+message.getPort()+"]");
        System.out.println("Usuario Desconectado : "+message.getName()+"["+message.getPort()+"]");
        sendOnlines();
    }

    private void send(ServerCenterMessage message, ObjectOutputStream output) {
        try {
            output.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(ServidorCenterService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendOnlines() {
        // CONVERTE O MAP DE USUARIO ONLINE EM UM SET DE NOMES
        Set<String> setNames = new HashSet<String>();
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            setNames.add(kv.getKey());
        }

        ServerCenterMessage message = new ServerCenterMessage();
        message.setAction(ActionCenter.USERS_ONLINE);
        message.setSetOnlines(serversOpens);

        // ENVIA A LISTA DE NOMES PARA TODOS USUARIOS
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {
            this.send(message, kv.getValue());
        }
    }
}
