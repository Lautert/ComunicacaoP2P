package com.service;

import com.ServerCenterMessage;
import com.Constantes;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClienteServerCenterService {
    
    private Socket socket;
    private ObjectOutputStream output;
    
    // ESTABELECE A CONEXAO COM O SERVIDOR, MAS AINDA NÃO INFORMOU NADA A ELE
    public Socket connect() {
        try {
            this.socket = new Socket("localhost", Constantes.SERVER_PORT);
            this.output = new ObjectOutputStream(socket.getOutputStream());
        } catch (UnknownHostException ex) {
            Logger.getLogger(ClienteServerCenterService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ClienteServerCenterService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return socket;
    } 
   
    public void send(ServerCenterMessage message) {
        try {
            output.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(ClienteServerCenterService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
