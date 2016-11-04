/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author Ihab BERRO
 */
public class MasterCommunicator extends Thread {

    protected Socket masterSoccket;
    protected int serverPort;
    protected PrintWriter output;
    protected Scanner input;

    protected Server mainServer;
    private final int masterPORT = 1234;

    protected enum ServerType {
        Login, Stream
    };
    protected ServerType serverType;

    public MasterCommunicator(String masterIP, Server mainServer) throws Exception {

        masterSoccket = new Socket(masterIP, masterPORT);
        output = new PrintWriter(masterSoccket.getOutputStream(), true);
        input = new Scanner(masterSoccket.getInputStream());

        this.mainServer = mainServer;
    }

    protected void sendMessage(String msg) {
        output.println(msg);
    }

    private boolean isOK(String msg) {
        System.out.println("Master comm:" + msg);
        if (!msg.toLowerCase().equals("+ok")) {
            return false;
        }
        return true;
    }

    public void setStatus(String status, int priority) throws Exception {
        sendMessage("SetStatus " + status + " " + Integer.toString(priority));
        System.out.println("SetStatus " + status + " " + Integer.toString(priority));
    }

    @Override
    public void run() {
        try {
            while (input.hasNextLine()) {
                if (!handleCommand(input.nextLine())) {
                    return;
                };
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean connect(String key) throws Exception {
        System.out.println("Connect");
        if (input.hasNextLine() && !isOK(input.nextLine())) {
            return false;
        }
        System.out.println("ConnectAs " + serverType.toString());
        sendMessage("ConnectAs " + serverType.toString());
        if (input.hasNextLine() && !isOK(input.nextLine())) {
            return false;
        }
        System.out.println("Key " + key);
        sendMessage("Key " + key);
        if (input.hasNextLine() && !isOK(input.nextLine())) {
            return false;
        }
        return true;
    }

    protected boolean handleCommand(String msg) throws Exception {
        return false;
    }
}
