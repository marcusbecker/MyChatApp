/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mychatapp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mbecker
 */
public class Server {

    public enum Comands {

        END_CHAT("-39");
        private String cmd;

        Comands(String cmd) {
            this.cmd = cmd;
        }

        String getComand() {
            return cmd;
        }
    };
    private ServerSocket serverSocket = null;
    private List<Client> clients = new ArrayList(10);
    private StringBuilder sbHistory = new StringBuilder();
    private boolean serverMode = false;
    private int port;
    private String serverName;

    public Server(int port, String serverName) {
        this.port = port;
        this.serverName = serverName;
    }

    public List<Client> getClients() {
        return clients;
    }

    public void stopService() {
        serverMode = false;
        for (Client clt : clients) {
            clt.sendCommandToClient(clt, Comands.END_CHAT.getComand());
            clt.close();
        }

        try {
            serverSocket.close();

            Logger.getLogger(Server.class.getName()).log(Level.INFO, "Server close connection.");

        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void runService() {

        try {
            serverSocket = new ServerSocket(port);
            serverMode = true;
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }


        new Thread() {

            @Override
            public void run() {
                while (serverMode) {
                    Socket clientSocket;

                    try {
                        clientSocket = serverSocket.accept();
                        Client clt = new Client(clientSocket, serverName);
                        clients.add(clt);
                        distribute(clt, "in chat.");
                        Logger.getLogger(Server.class.getName()).log(Level.INFO, "add clt {0}", clt.getName());
                        new Server.listenMultiClients(clt).start();

                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }


                }
            }
        }.start();

    }

    class listenMultiClients extends Thread {

        private Client client;

        public listenMultiClients(Client client) {
            this.client = client;
        }

        @Override
        public void run() {
            while (serverMode) {

                String inputLine = client.listenServer();
                if (inputLine != null && inputLine.trim().length() > 0) {
                    if (inputLine.trim().equals(Comands.END_CHAT.getComand())) {
                        distribute(client, " say good bye!-");
                        client.close();
                        clients.remove(client);

                        Logger.getLogger(Server.class.getName()).log(Level.INFO, "Client: {0}, exit.", new String[]{client.getName()});
                        break;
                    }

                    Logger.getLogger(Server.class.getName()).log(Level.INFO, "Client: {0}, send msg: {1}", new String[]{client.getName(), inputLine});
                    distribute(client, inputLine);
                }
            }
        }
    }

    public String getContent() {
        return sbHistory.toString();
    }

    private void distribute(Client clt, String message) {
        sbHistory.append(clt.getName()).append(":").append(message);
        for (Client c : clients) {
            c.sendMessageToClient(clt, message);//recive from server
        }
        //sbRecive.append(message);
    }

    void remove(Client client) {
        clients.remove(client);
    }
}
