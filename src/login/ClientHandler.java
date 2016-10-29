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

    private long id;

    public ClientHandler(Socket socket, ConcurrentHashMap<String, ClientHandler> clientHandlerList) throws Exception {
        this.clientHandlerList = clientHandlerList;
        this.socket = socket;

        output = new PrintWriter(socket.getOutputStream(), true);
        input = new Scanner(socket.getInputStream());

        id = Thread.currentThread().getId();
    }

    @Override
    public void run() {
        try {
            sendMessage("Connected.");
            if (input.hasNextLine() && !authentication(input.nextLine())) {
                endConnection();
                return;
            };
            while (input.hasNextLine()) {
                if (!handleCommand(input.nextLine())) {
                    return;
                };
            }
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
        //
        //
        //
        //
        //
        addHandler();
        //send address
        //
        sendMessage("+OK");
        return true;
    }

    private void disconnect() throws Exception {
        clientHandlerList.remove(Long.toString(id));
        endConnection("+Bye");
    }

    private boolean handleCommand(String msg) throws Exception {
       /* String cmd[] = msg.split(" ");
        if (cmd.length == 0) {
            endConnection();
            return false;
        }
        switch (cmd[0].toLowerCase()) {
            case "disconnect":
                disconnect();
                return false;
            case "setstatus":
                if (setStatus()) {
                    sendMessage("+OK");
                } else {
                    sendMessage("-NOK");
                }
                break;
            default:
                endConnection();
                return false;
        }*/
        return true;
    }

    private void endConnection() throws Exception {
        sendMessage("Fail.");
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
