/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Ihab BERRO
 */
public class ClientHandler extends Thread {

    private ConcurrentHashMap<String, ClientHandler> clientHandlerList;

    private Socket socket;
    private PrintWriter output;
    private Scanner input;
    private LoginServer loginServer;

    private long id;

    public ClientHandler(Socket socket, ConcurrentHashMap<String, ClientHandler> clientHandlerList, LoginServer loginServer) throws Exception {
        this.clientHandlerList = clientHandlerList;
        this.socket = socket;
        this.loginServer = loginServer;

        output = new PrintWriter(socket.getOutputStream(), true);
        input = new Scanner(socket.getInputStream());

        id = Thread.currentThread().getId();
    }

    @Override
    public void run() {
        try {
            addHandler();
            sendMessage("+OK");
            if (input.hasNextLine() && !authentication(input.nextLine())) {
                endConnection();
                return;
            };
            disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean authentication(String msg) {
        String cmd[] = msg.split(" ");

        if (cmd.length != 4 || !cmd[0].toLowerCase().equals("signin")) {
            return false;
        }
        if (!cmd[1].equals("client")) {
            return false;
        }
        if (!cmd[2].equals("key")) {
            return false;
        }
        StreamAddr stream = loginServer.getStream(Integer.parseInt(cmd[3]));
        sendMessage(stream.getIp() + ":" + Integer.toString(stream.getPort()));
        return true;
    }

    private void disconnect() throws Exception {
        removeHandler();
        endConnection("+OK");
    }

    private void endConnection() throws Exception {
        sendMessage("-NOK");
        socket.close();
    }

    private void endConnection(String msg) throws Exception {
        sendMessage(msg);
        socket.close();
    }

    private void sendMessage(String msg) {
        output.println(msg);
    }

    private void addHandler() {
        clientHandlerList.put(Long.toString(id), this);
    }

    private void removeHandler() {
        clientHandlerList.remove(Long.toString(id));
    }
}
