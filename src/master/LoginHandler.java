/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package master;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Ihab BERRO
 */
public class LoginHandler extends Thread {

    private ConcurrentHashMap<String, LoginHandler> loginHandlerList;
    private Socket socket;
    PrintWriter output;
    Scanner input;
    private long id;

    public LoginHandler(ConcurrentHashMap<String, LoginHandler> loginHandlerList, Socket socket) throws Exception {
        this.loginHandlerList = loginHandlerList;
        this.socket = socket;

        output = new PrintWriter(socket.getOutputStream(), true);
        input = new Scanner(socket.getInputStream());

        id = Thread.currentThread().getId();
    }

    public void setOutStream(PrintWriter output) {
        this.output = output;
    }

    public void setInputStream(Scanner input) {
        this.input = input;

    }

    public void run() {
        try {
            System.out.println("22222");
            if (input.hasNextLine() && !authentication(input.nextLine())) {
                endConnection();
                return;
            };
            System.out.println("3333");
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

        if (cmd.length != 2 || !cmd[0].equals("key")) {
            return false;
        }
        if (!cmd[1].equals("passwordkey")) {
            return false;
        }
        addLogin();
        return true;
    }

    private void endConnection() throws Exception {
        System.out.println("Fail.");
        socket.close();
    }

    private void endConnection(String msg) throws Exception {
        System.out.println(msg);
        socket.close();
    }

    private boolean handleCommand(String msg) throws Exception {
        String cmd[] = msg.split(" ");
        if (cmd.length == 0) {
            endConnection();
            return false;
        }
        switch (cmd[0].toLowerCase()) {
            case "disconnect":
                endConnection("Bye");
                return false;
            case "setstatus":
                //*****************************
                //*****************************
                System.out.println("Status: " + cmd[1]);
                //*****************************
                break;
            default:
                endConnection();
                return false;
        }
        return true;
    }

    private void addLogin() {
        loginHandlerList.put(Long.toString(id), this);
    }
}
