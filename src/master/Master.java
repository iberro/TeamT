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

    ServerSocket servSocket;
    int PORT = 1234;
    Socket clientSocket;

    //ConcurrentHashMap<LoginHandler, > l;
    
    public Master() {

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
            System.out.println(ex.toString());
        }
    }

    private void handleConnection(Socket clientSocket) {

        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    PrintWriter output = new PrintWriter(clientSocket.getOutputStream(),true);                
                    Scanner input = new Scanner(clientSocket.getInputStream());
                    output.println("ssss");
                    String d = input.nextLine();
                    String msgRecv[] = d.split(" ");
                    
                    if ( (msgRecv.length != 2) || (!msgRecv[0].toLowerCase().equals("ConnectAs".toLowerCase())) ){
                        System.out.println("Fail.");
                        clientSocket.close();
                        return;
                    }
                    switch (msgRecv[1].toLowerCase()) {
                        case "login" :
                            System.out.println("Login");
                            break;
                        case "stream" :
                            System.out.println("Stream");
                            break;    
                        default :
                            clientSocket.close();
                            return;
                    }
                    clientSocket.close();
                } catch (Exception ex) {
                    System.out.println(ex.toString());
                }
            }
        });
        thread.start();
    }

}