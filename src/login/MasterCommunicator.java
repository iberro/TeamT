/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author Ihab BERRO
 */
public class MasterCommunicator extends Thread {

    Socket clientSoccket;
    int serverPort;
    private PrintWriter output;
    private Scanner input;

    public MasterCommunicator() throws Exception {
        clientSoccket = new Socket("127.0.0.1", 1234);
        output = new PrintWriter(clientSoccket.getOutputStream(), true);
        input = new Scanner(clientSoccket.getInputStream());
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

        if (input.hasNextLine() && !isOK(input.nextLine())) {
            return false;
        }
        output.println("ConnectAs Login");
        if (input.hasNextLine() && !isOK(input.nextLine())) {
            return false;
        }
        output.println("Key " + key);
        if (input.hasNextLine() && !isOK(input.nextLine())) {
            return false;
        }
        return true;
    }

    private boolean handleCommand(String msg) throws Exception {
        System.out.println("Master comm:" + msg);
        String cmd[] = msg.split(" ");
        if (cmd.length == 0) {
            return false;
        }
        switch (cmd[0].toLowerCase()) {
            case "getStatus":
                //
                //
                //
                //
                //
                break;
            case "update":
                //
                //
                //
                //
                //
                break;
            default:
                break;
        }
        return true;
    }

    public boolean setStatus(String status, int priority) throws Exception {
        output.println("SetStatus " + status + " " + Integer.toString(priority));
        if (input.hasNextLine() && !isOK(input.nextLine())) {
            return false;
        }
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
