/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package master;

import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Ihab BERRO
 */
public class Handler extends Thread {

    protected ConcurrentHashMap<String, LoginHandler> loginHandlerList;
    protected ConcurrentHashMap<String, StreamHandler> streamHandlerList;

    protected Socket socket;
    protected PrintWriter output;
    protected Scanner input;

    protected long id;
    protected String ip;
    protected int port;

    protected enum HandleType {
        Login, Stream
    };
    protected HandleType handleType;

    public Handler(
            ConcurrentHashMap<String, LoginHandler> loginHandlerList,
            ConcurrentHashMap<String, StreamHandler> streamHandlerList,
            Socket socket) throws Exception {
        System.out.println("Handler: const");
        this.loginHandlerList = loginHandlerList;
        this.streamHandlerList = streamHandlerList;
        this.socket = socket;

        output = new PrintWriter(socket.getOutputStream(), true);
        input = new Scanner(socket.getInputStream());

        id = Thread.currentThread().getId();
    }

    public void run() {
        try {
            sendMessage("+OK");
            if (input.hasNextLine() && !authentication(input.nextLine())) {
                endConnection();
                return;
            };
            if (handleType == HandleType.Login) {
                for (StreamHandler handler : streamHandlerList.values()) {
                    update("update " + handler.ip + ":" + handler.port + " " + handler.getMin() + " " + handler.getMax());
                }
            }
            while (input.hasNextLine()) {
                if (!handleCommand(input.nextLine())) {
                    return;
                };
            }
        } catch (SocketException ex) {
            removeHandler();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private boolean authentication(String msg) {
        System.out.println("Handler: " + msg);
        String cmd[] = msg.split(" ");
        if (cmd.length != 2 || !cmd[0].toLowerCase().equals("key")) {
            return false;
        }
        if (!cmd[1].equals("passwordkey")) {
            return false;
        }
        addHandler();
        sendMessage("+OK");
        return true;
    }

    private void disconnect() throws Exception {
        removeHandler();
        endConnection("+OK");
    }

    private boolean handleCommand(String msg) throws Exception {
        System.out.println("Handler: " + msg.toString());
        String cmd[] = msg.split(" ");
        if (cmd.length == 0) {
            endConnection();
            return false;
        }
        switch (cmd[0].toLowerCase()) {
            case "disconnect":
                disconnect();
                return false;
            case "setstatus":
                if (setStatus(cmd[1])) {
                    sendMessage("+OK");
                } else {
                    sendMessage("-NOK");
                }
                break;
            default:
                endConnection();
                return false;
        }
        return true;
    }

    private void endConnection() throws Exception {
        sendMessage("-NOK");
        socket.close();
    }

    private void endConnection(String msg) throws Exception {
        sendMessage(msg);
        socket.close();
    }

    public void sendMessage(String msg) {
        System.out.println("sendMessage: " + msg);
        output.println(msg);
    }

    private void addHandler() {
        if (handleType == HandleType.Login) {
            loginHandlerList.put(Long.toString(id), (LoginHandler) this);
        } else {
            streamHandlerList.put(Long.toString(id), (StreamHandler) this);
        }
    }

    private void removeHandler() {
        if (handleType == HandleType.Login) {
            loginHandlerList.remove(Long.toString(id));
        } else {
            streamHandlerList.remove(Long.toString(id));
        }
    }

    protected boolean setStatus(String cmd) {
        System.out.println("Parent");
        return true;
    }

    protected void update(String msg) {
        System.out.println("update " + msg);
        sendMessage("update " + msg);
    }
}
