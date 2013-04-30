/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mychatapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mbecker
 */
public class Client {

    private Socket sClient;
    private String name;
    private String serverName;
    private PrintWriter out;
    private BufferedReader in;
    private StringBuilder serverRecive;
    private boolean active;

    //used in client application
    public Client(String name) {
        this.name = name;
        serverRecive = new StringBuilder();
    }

    //used in server application
    public Client(Socket socket, String serverName) {
        try {
            sClient = socket;
            out = new PrintWriter(sClient.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(sClient.getInputStream()));

            //First say names
            this.serverName = serverName;
            out.println(serverName);
            name = in.readLine();

            active = true;

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //used in client application
    public boolean connect(String server, int port) {
        try {
            sClient = new Socket(server, port);
            out = new PrintWriter(sClient.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(sClient.getInputStream()));

            //First say names
            serverName = in.readLine();
            out.println(name);

            active = true;
            return true;

        } catch (UnknownHostException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        return false;
    }

    public void close() {
        if (active) {
            try {
                //out.println(Server.Comands.END_CHAT.getComand());
                //out.close();
                //in.close();
                sClient.close();
            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }

            active = false;
        }
    }

    public void sendMessageToServer(String message) {
        Logger.getLogger(Client.class.getName()).log(Level.INFO, "sendMessageToServer");
        if (active) {
            out.println(message);
        } else {
            Logger.getLogger(Client.class.getName()).log(Level.INFO, "Not connected.");
        }
    }

    public void sendCommandToClient(Client otherClient, String message) {
        Logger.getLogger(Client.class.getName()).log(Level.INFO, "sendCommandToClient");
        if (active) {
            out.println(message);
        } else {
            Logger.getLogger(Client.class.getName()).log(Level.INFO, "Not connected.");
        }
    }

    public void sendMessageToClient(Client otherClient, String message) {
        Logger.getLogger(Client.class.getName()).log(Level.INFO, "sendMessageToClient");
        if (active) {
            String cltName = this.equals(otherClient) ? "You" : otherClient.getName();
            out.println(cltName + " say: " + message);
        } else {
            Logger.getLogger(Client.class.getName()).log(Level.INFO, "Not connected.");
        }
    }

    public String listenServer() {
        if (active) {
            try {
                return in.readLine() + "\n";
            } catch (IOException ex) {
                Logger.getLogger(ChatWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            Logger.getLogger(Client.class.getName()).log(Level.INFO, "Not connected.");
        }

        return null;
    }

    private void listenServer_() {

        new Thread() {

            @Override
            public void run() {
                try {
                    while (active) {
                        String fromServer = in.readLine();
                        if (fromServer != null) {
                            serverRecive.append(fromServer).append("\n");
                        }
                    }

                } catch (IOException ex) {
                    Logger.getLogger(ChatWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.start();
    }

    public String getServerName() {
        return serverName;
    }

    public String getName() {
        return name;
    }
}
