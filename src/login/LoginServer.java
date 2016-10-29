/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import master.LoginHandler;
import master.StreamHandler;

/**
 *
 * @author Ihab BERRO
 */
public class LoginServer extends Thread {

    private ServerSocket servSocket;
    private int PORT = 1235;
    private Socket clientSocket;

    private MasterCommunicator masterCommunicator;
    private ConcurrentHashMap<String, ClientHandler> clientHandlerList;

    public LoginServer() throws Exception {
        masterCommunicator = new MasterCommunicator();
        clientHandlerList = new ConcurrentHashMap<String, ClientHandler>();
    }

    @Override
    public void run() {
        try {
            if (!masterCommunicator.connect("passwordkey")) {
                        System.out.println("Login: not conn");
                return;
            }
            masterCommunicator.start();

            servSocket = new ServerSocket(PORT);
            while (true) {
                clientSocket = servSocket.accept();
                System.out.println("Login: new client");
                ClientHandler clientHandler = new ClientHandler(clientSocket, clientHandlerList);
                clientHandler.start();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
