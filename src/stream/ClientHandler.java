/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stream;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
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
    DataInputStream inputData;
    DataOutputStream outputData;

    private int freq;

    public ClientHandler(Socket clientSocket, ConcurrentHashMap<String, ArrayList<ClientHandler>> clientHandlerList) throws Exception {
        this.socket = clientSocket;
        this.clientHandlerList = clientHandlerList;

        output = new PrintWriter(socket.getOutputStream(), true);
        input = new Scanner(socket.getInputStream());
    }

    public void run() {
        try {
            sendMessage("+OK");
            if (input.hasNextLine() && !authentication(input.nextLine())) {
                endConnection();
                return;
            };
            inputData = new DataInputStream(socket.getInputStream());
            outputData = new DataOutputStream(socket.getOutputStream());

            while (true) {
                byte[] buf = new byte[640];
                inputData.read(buf, 0, 640);
                System.out.println("Data");
                for (ClientHandler client : clientHandlerList.get(Integer.toString(freq))){
                    if(client != this){
                        client.sendData(buf);
                    }
                }
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
        if (!cmd[1].toLowerCase().equals("client")) {
            return false;
        }
        if (!cmd[2].toLowerCase().equals("key")) {
            return false;
        }
        addHandler(Integer.parseInt(cmd[3]));
        return true;
    }

    private void addHandler(int freq) {
        this.freq = freq;
        System.out.println(freq);
        clientHandlerList.get(Integer.toString(freq)).add(this);
        System.out.println("Client added: " + freq);
    }

    private void disconnect() throws Exception {
        //removeHandler();
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

    private void sendData(byte[] data) {
        try {
            outputData.write(data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
