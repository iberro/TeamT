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

    private ConcurrentHashMap<String, LoginHandler> loginHandlerList;
    private ConcurrentHashMap<String, StreamHandler> streamHandlerList;

    public Master() {
        System.out.println("Master: Construct");
        loginHandlerList = new ConcurrentHashMap<String, LoginHandler>();
        streamHandlerList = new ConcurrentHashMap<String, StreamHandler>();
    }

    @Override
    public void run() {
        try {
            System.out.println("Master: run");
            servSocket = new ServerSocket(PORT);
            while (true) {
                clientSocket = servSocket.accept();
                System.out.println("Master: new con");
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
                    output.println("+OK");
                    String inStr = input.nextLine();
                    System.out.println("Master: " + inStr);
                    String msgRecv[] = inStr.split(" ");
                    if ((msgRecv.length != 2) || (!msgRecv[0].toLowerCase().equals("connectas"))) {
                        output.println("-NOK");
                        clientSocket.close();
                        return;
                    }
                    switch (msgRecv[1].toLowerCase()) {
                        case "login":
                            LoginHandler newLogin = new LoginHandler(loginHandlerList, streamHandlerList, clientSocket);
                            newLogin.start();
                            break;
                        case "stream":
                            StreamHandler newStream = new StreamHandler(loginHandlerList, streamHandlerList, clientSocket);
                            newStream.start();
                            break;
                        default:
                            output.println("-NOK");
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
