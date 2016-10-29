/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package master;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ihab BERRO
 */
public class Master extends Thread {

    private ServerSocket servSocket;
    private int PORT = 1234;
    private Socket clientSocket;

    private ConcurrentHashMap<String, Handler> loginHandlerList;
    private ConcurrentHashMap<String, Handler> streamHandlerList;

    public Master() {
        loginHandlerList = new ConcurrentHashMap<String, Handler>();
        streamHandlerList = new ConcurrentHashMap<String, Handler>();
    }

    @Override
    public void run() {
        try {
            servSocket = new ServerSocket(PORT);
            while (true) {
                clientSocket = servSocket.accept();
                handleConnection(clientSocket);
            }

        } catch (IOException ex) {
            //Logger.getLogger(Master.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }

    private void handleConnection(Socket clientSocket) {

        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
                    Scanner input = new Scanner(clientSocket.getInputStream());
                    output.println("Connected to Master.");
                    String msgRecv[] = input.nextLine().split(" ");

                    if ((msgRecv.length != 2) || (!msgRecv[0].toLowerCase().equals("connectas"))) {
                        output.println("Fail.");
                        clientSocket.close();
                        return;
                    }
                    switch (msgRecv[1].toLowerCase()) {
                        case "login":
                            LoginHandler newLogin = new LoginHandler(loginHandlerList, clientSocket);
                            newLogin.start();
                            break;
                        case "stream":
                            StreamHandler newStream = new StreamHandler(streamHandlerList, clientSocket);
                            newStream.start();
                            break;
                        default:
                            clientSocket.close();
                            return;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        thread.start();
    }

}
