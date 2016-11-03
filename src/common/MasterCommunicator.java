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

    protected Socket clientSoccket;
    protected int serverPort;
    protected PrintWriter output;
    protected Scanner input;

    protected Server mainServer;

    protected enum ServerType {
        Login, Stream
    };
    protected ServerType serverType;

    public MasterCommunicator(Server mainServer) throws Exception {
        clientSoccket = new Socket("127.0.0.1", 1234);
        output = new PrintWriter(clientSoccket.getOutputStream(), true);
        input = new Scanner(clientSoccket.getInputStream());

        this.mainServer = mainServer;
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
        output.println("ConnectAs " + serverType.toString());
        if (input.hasNextLine() && !isOK(input.nextLine())) {
            return false;
        }
        System.out.println("Key " + key);
        output.println("Key " + key);
        if (input.hasNextLine() && !isOK(input.nextLine())) {
            return false;
        }
        return true;
    }

    protected boolean handleCommand(String msg) throws Exception {
        return false;
    }

    public synchronized boolean setStatus(String status, int priority) throws Exception {
        output.println("SetStatus " + status + " " + Integer.toString(priority));
        System.out.println("SetStatus " + status + " " + Integer.toString(priority));
        //if (input.hasNextLine() && !isOK(input.nextLine())) {
        //    return false;
        //}
        return true;
    }

    private boolean isOK(String msg) {
        System.out.println("Master comm:" + msg);
        if (!msg.toLowerCase().equals("+ok")) {
            return false;
        }
        return true;
    }
}
