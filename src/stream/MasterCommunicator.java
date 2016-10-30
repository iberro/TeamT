/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stream;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 *
 * @author Ihab BERRO
 */
public class MasterCommunicator extends Thread{
    
    Socket clientSoccket;
    int serverPort;
    private PrintWriter output;
    private Scanner input;

    private StreamServer streamServer;

    public MasterCommunicator(StreamServer streamServer) throws Exception {
        clientSoccket = new Socket("127.0.0.1", 1234);
        output = new PrintWriter(clientSoccket.getOutputStream(), true);
        input = new Scanner(clientSoccket.getInputStream());

        this.streamServer = streamServer;
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
        output.println("ConnectAs Stream");
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
                setStatus("Normal", 10);
                break;
            case "update":
                if (cmd.length < 2) {
                    return true;
                }
                switch (cmd[1]) {
                   /*case "addStream":
                        if (cmd.length != 5) {
                            return true;
                        }
                        streamServer.addStream(
                                cmd[2].split(":")[0],
                                Integer.parseInt(cmd[2].split(":")[1]),
                                Long.parseLong(cmd[3]),
                                Long.parseLong(cmd[4]));
                        break;
                    case "removeStream":
                        if (cmd.length != 3) {
                            return true;
                        }
                        loginServer.removeStream(
                                cmd[2].split(":")[0],
                                Integer.parseInt(cmd[2].split(":")[1]));
                        break;
                    case "updateStreamMin":
                        if (cmd.length != 4) {
                            return true;
                        }
                        loginServer.updateStreamMin(
                                cmd[2].split(":")[0],
                                Integer.parseInt(cmd[2].split(":")[1]),
                                Long.parseLong(cmd[3]));
                        break;
                    case "updateStreamMax":
                        if (cmd.length != 4) {
                            return true;
                        }
                        loginServer.updateStreamMax(
                                cmd[2].split(":")[0],
                                Integer.parseInt(cmd[2].split(":")[1]),
                                Long.parseLong(cmd[3]));
                        break;*/
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        return true;
    }

    public boolean setStatus(String status, int priority) throws Exception {
        output.println("SetStatus " + status + " " + Integer.toString(priority));
        System.out.println("Stream SetStatus " + status + " " + Integer.toString(priority));
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
