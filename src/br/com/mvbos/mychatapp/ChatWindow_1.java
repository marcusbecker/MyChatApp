/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.mychatapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mbecker
 */
public class ChatWindow_1 extends javax.swing.JFrame {

    //server
    private ServerSocket serverSocket = null;
    private List<Socket> clients = new ArrayList(10);
    private StringBuilder sbRecive = new StringBuilder();
    private boolean serverMode = false;
    //client
    private Socket client = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private boolean clientMode = false;
    
    //Default
    private final String END_CHAT = "-39";

    private void startClient() {
        try {
            if (!clientMode) {
                client = new Socket(tfConUrl.getText(), Integer.parseInt(tfConPort.getText()));
                out = new PrintWriter(client.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                listenServer();

            } else {
                out.close();
                in.close();
                client.close();
            }

            clientMode = !clientMode;

        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: taranis.");
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: taranis.");
        }
    }

    private void startServer() {
        try {
            if (!serverMode) {
                serverSocket = new ServerSocket(Integer.parseInt(tfConPort.getText()));
            } else {
                for (Socket clt : clients) {
                    sendClientMessage(clt, END_CHAT);
                }
                serverSocket.close();
            }
            serverMode = !serverMode;

        } catch (IOException e) {
            Logger.getLogger(ChatWindow_1.class.getName()).log(Level.SEVERE, "Could not listen on port.");
        }

        new Thread() {

            @Override
            public void run() {
                try {
                    while (serverMode) {
                        Socket clientSocket = serverSocket.accept();
                        sendClientMessage(clientSocket, "Hello...");

                        clients.add(clientSocket);
                        sbRecive.append(clientSocket.toString()).append(" in chat.\n");
                        tfRecive.setText(sbRecive.toString());

                        new listenMultiClients(clientSocket).start();
                    }

                    for (Socket clt : clients) {
                        clt.close();
                    }

                    serverSocket.close();

                } catch (IOException ex) {
                    Logger.getLogger(ChatWindow_1.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }.start();

    }

    private void listenServer() {

        new Thread() {

            @Override
            public void run() {
                try {
                    while (clientMode) {
                        String fromServer = in.readLine();
                        if (fromServer != null) {
                            if (fromServer.equals(END_CHAT)) {
                                sbRecive.append("Server say good bye!");
                                tfRecive.setText(sbRecive.toString());

                                out.close();
                                in.close();
                                client.close();
                                
                                clientMode = false;
                                btnConnect.setSelected(false);
                                
                                break;
                            }

                            sbRecive.append("Server say: ");
                            sbRecive.append(fromServer).append("\n");
                            tfRecive.setText(sbRecive.toString());
                        }
                    }

                } catch (IOException ex) {
                    Logger.getLogger(ChatWindow_1.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }.start();
    }

    class listenMultiClients extends Thread {

        private Socket client;

        public listenMultiClients(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            while (serverMode) {
                try {
                    BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                    String inputLine = in.readLine();
                    if (inputLine != null) {
                        if (inputLine.equals(END_CHAT)) {
                            sbRecive.append(client.toString()).append(" say good bye! ");
                            tfRecive.setText(sbRecive.toString());

                            client.close();
                            clients.remove(client);
                            break;
                        }

                        sbRecive.append(client.toString()).append(" say: ");
                        sbRecive.append(inputLine).append("\n");
                        tfRecive.setText(sbRecive.toString());
                    }

                } catch (IOException ex) {
                    Logger.getLogger(ChatWindow_1.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void listenOneClient() {
        new Thread() {

            @Override
            public void run() {
                while (serverMode) {
                    try {
                        for (Socket clt : clients) {
                            System.out.println("ini");
                            BufferedReader in = new BufferedReader(new InputStreamReader(clt.getInputStream()));
                            String inputLine = in.readLine();
                            if (inputLine != null) {
                                sbRecive.append(clt.toString()).append(" say: ");
                                sbRecive.append(inputLine).append("\n");
                                tfRecive.setText(sbRecive.toString());
                            }

                            System.out.println("end");
                        }

                    } catch (IOException ex) {
                        Logger.getLogger(ChatWindow_1.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }.start();
    }

    private void sendConversation() {
        String msg = tfSend.getText();

        sbRecive.append("You say: ");
        sbRecive.append(msg).append("\n");
        tfRecive.setText(sbRecive.toString());

        if (serverMode) {
            for (Socket clt : clients) {
                sendClientMessage(clt, msg);
            }

        } else if (clientMode) {
            sendClientMessage(client, msg);
        }

        tfSend.setText("");
    }

    private void sendClientMessage(Socket clientSocket, String message) {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println(message);

        } catch (IOException e) {
            Logger.getLogger(ChatWindow_1.class.getName()).log(Level.SEVERE, "Accept failed.");
        }
    }

    public ChatWindow_1() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        btnStartServer = new javax.swing.JToggleButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tfSend = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        tfRecive = new javax.swing.JTextArea();
        btnSend = new javax.swing.JButton();
        tfConUrl = new javax.swing.JTextField();
        tfConPort = new javax.swing.JTextField();
        tfConName = new javax.swing.JTextField();
        btnConnect = new javax.swing.JToggleButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnStartServer.setText("Start server");

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, btnConnect, org.jdesktop.beansbinding.ELProperty.create("${!selected}"), btnStartServer, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        btnStartServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartServerActionPerformed(evt);
            }
        });

        tfSend.setColumns(20);
        tfSend.setRows(5);
        jScrollPane1.setViewportView(tfSend);

        tfRecive.setColumns(20);
        tfRecive.setRows(5);
        jScrollPane2.setViewportView(tfRecive);

        btnSend.setText("Send");
        btnSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendActionPerformed(evt);
            }
        });

        tfConUrl.setText("localhost");

        tfConPort.setText("4321");

        tfConName.setText("becker");

        btnConnect.setText("Connect on server");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, btnStartServer, org.jdesktop.beansbinding.ELProperty.create("${!selected}"), btnConnect, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        btnConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConnectActionPerformed(evt);
            }
        });

        jLabel1.setText("IP / Port / Name");

        jLabel2.setText("OR");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane2)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnSend))
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(btnStartServer, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(tfConUrl, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tfConPort, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(tfConName, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnConnect, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfConUrl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfConPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfConName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnConnect)
                    .addComponent(btnStartServer)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(btnSend)
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnStartServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartServerActionPerformed
        startServer();
    }//GEN-LAST:event_btnStartServerActionPerformed

    private void btnSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendActionPerformed
        sendConversation();
    }//GEN-LAST:event_btnSendActionPerformed

    private void btnConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConnectActionPerformed
        startClient();
    }//GEN-LAST:event_btnConnectActionPerformed
    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnConnect;
    private javax.swing.JButton btnSend;
    private javax.swing.JToggleButton btnStartServer;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField tfConName;
    private javax.swing.JTextField tfConPort;
    private javax.swing.JTextField tfConUrl;
    private javax.swing.JTextArea tfRecive;
    private javax.swing.JTextArea tfSend;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
