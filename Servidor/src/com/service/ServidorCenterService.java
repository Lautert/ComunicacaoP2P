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
            // cria um servidor socket
            serverSocket = new ServerSocket(Constantes.SERVER_PORT);
            System.out.println("Servidor Center on!");

            // loop que envia cada requisição para uma thread separa, gerida pela escuta
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
                // Logger.getLogger(ServidorCenterService.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("com.service.ServidorCenterService.ListenerSocket.<init>()");
            }
        }

        @Override
        public void run() {
            ServerCenterMessage message = null;
            try {
                // RECEBE AS MENSAGENS DO CLIENTE
                
                while ((message = (ServerCenterMessage) input.readObject()) != null) {
                    ActionCenter action = message.getAction();
                    System.out.println("\\/----------------------------------------\\/");

                    System.out.println(output);
                    System.out.println(message);

                    if (action.equals(ActionCenter.CONNECT)) {
                        connect(message, output);
                        // informa todos os presentes da lista de clientes mudou
                        sendOnlines();
                    } else if (action.equals(ActionCenter.DISCONNECT)) {
                        disconnect(message, output);
                        // informa todos os presentes da lista de clientes mudou
                        sendOnlines();
                    }
                    System.out.println("/\\----------------------------------------/\\");
                }
            } catch (IOException ex) {
                sendOnlines();
//                System.out.println(message.getName()+"["+message.getPort()+"]"+" deixou o chat!");
                disconnect(message, output);
            } catch (ClassNotFoundException ex) {
                sendOnlines();
//                Logger.getLogger(ServidorCenterService.class.getName()).log(Level.SEVERE, null, ex);
                disconnect(message, output);
            }
        }
    }
    
    private void connect(ServerCenterMessage message, ObjectOutputStream output){
        String nome = message.getName()+"["+message.getPort()+"]";
        
        mapOnlines.put(nome, output);
        serversOpens.put(nome, message.getPort());
        System.out.println("Novo Usuario Conectado : "+nome);
        // envia mensagem de volta com a mesma action de CONNECTION para informar que foi conectado
        send(message, output);
    }
    
    private void disconnect(ServerCenterMessage message, ObjectOutputStream output) {
        String nome = message.getName()+"["+message.getPort()+"]";
        
        mapOnlines.remove(nome);
        serversOpens.remove(nome);
        System.out.println("Usuario Desconectado : "+nome);
    }

    private void send(ServerCenterMessage message, ObjectOutputStream output) {
        try {
            output.writeObject(message);
        } catch (IOException ex) {
//            Logger.getLogger(ServidorCenterService.class.getName()).log(Level.SEVERE, null, ex);
            disconnect(message, output);
        }
    }

    private void sendOnlines() {
        // ENVIA A LISTA DE NOMES PARA TODOS USUARIOS
        for (Map.Entry<String, ObjectOutputStream> kv : mapOnlines.entrySet()) {

            ServerCenterMessage message = new ServerCenterMessage();
            message.setAction(ActionCenter.USERS_ONLINE);
            message.setSetOnlines(serversOpens);
            System.out.println(serversOpens);
            System.out.println(kv.getValue());
            this.send(message, kv.getValue());
        }
    }
}
