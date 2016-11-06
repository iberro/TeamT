/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stream;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Ihab BERRO
 */
public class ClientHandler extends Thread {

    private ConcurrentHashMap<String, ArrayList<ClientHandler>> clientHandlerList;
    private Socket socket;
    private PrintWriter output;
    private Scanner input;

    public ClientHandler(Socket clientSocket, ConcurrentHashMap<String, ArrayList<ClientHandler>> clientHandlerList) {
        this.socket = clientSocket;
        this.clientHandlerList = clientHandlerList;
    }

    public void run() {
    }
}
