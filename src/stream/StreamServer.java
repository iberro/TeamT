/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stream;

import common.Server;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Ihab BERRO
 */
public class StreamServer extends Server {

    private ServerSocket servSocket;
    private int port = 1235;
    private Socket clientSocket;

    private StreamMasterCommunicator masterCommunicator;
    private ConcurrentHashMap<String, ClientHandler> clientHandlerList;

    private int min;
    private int max;
    private String ip;

    public long getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public long getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public StreamServer(String ip, int port) throws Exception {

        masterCommunicator = new StreamMasterCommunicator(this);
        clientHandlerList = new ConcurrentHashMap<String, ClientHandler>();

        this.ip = ip;
        this.port = port;
        this.min = 0;
        this.max = 0;
    }

    @Override
    public void run() {
        try {
            if (!masterCommunicator.connect("passwordkey")) {
                System.out.println("stream: not conn");
                return;
            }
            masterCommunicator.start();
            masterCommunicator.setStatus("getMin", 1);
            masterCommunicator.setStatus("getMax", 1);

            while ((min == 0) && (max == 0)) {
                Thread.sleep(1000);
            }
            System.out.println("stream: min max ok");

            servSocket = new ServerSocket(port);
            while (true) {
                clientSocket = servSocket.accept();
                System.out.println("stream: new client");
                //ClientHandler clientHandler = new ClientHandler();
                //clientHandler.start();
                clientSocket.close();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateStreamMin(int min) {
        this.min = min;
    }

    public void updateStreamMax(int max) {
        this.max = max;
    }
}
