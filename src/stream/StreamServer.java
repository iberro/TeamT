/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stream;

import common.Server;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Ihab BERRO
 */
public class StreamServer extends Server {

    private ServerSocket servSocket;
    private int port;
    private Socket clientSocket;
    private Broadcaster broadcaster;

    private StreamMasterCommunicator masterCommunicator;
    private ConcurrentHashMap<String, ArrayList<ClientHandler>> clientHandlerList;

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

    public StreamServer(String masterIP, int port) throws Exception {

        masterCommunicator = new StreamMasterCommunicator(masterIP, this);
        clientHandlerList = new ConcurrentHashMap<>();

        //get local host ip
        //
        //
        this.ip = "192.168.1.105";
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
            masterCommunicator.setStatus("setip:" + ip, 1);
            masterCommunicator.setStatus("setport:" + port, 1);
            Thread.sleep(1000);
            System.out.println("stream: min " + min + " max " + max);
            initClientList();

            broadcaster = new Broadcaster(clientHandlerList, port + 1);
            broadcaster.start();

            servSocket = new ServerSocket(port);
            while (true) {
                clientSocket = servSocket.accept();
                System.out.println("stream: new client");
                ClientHandler clientHandler = new ClientHandler(clientSocket, clientHandlerList);
                clientHandler.start();
                //clientSocket.close();
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

    private void initClientList() {
        for (int i = min; i <= max; i++) {
            clientHandlerList.put(Integer.toString(i), new ArrayList<ClientHandler>());
        }
    }

    public class Broadcaster extends Thread {

        ConcurrentHashMap<String, ArrayList<ClientHandler>> clientHandlerList;
        int port;
        DatagramSocket udpSocket;
        DatagramPacket udpPacket;
        byte[] buf = new byte[256];

        public Broadcaster(ConcurrentHashMap<String, ArrayList<ClientHandler>> clientHandlerList, int port) throws Exception {
            System.out.println("init broadcaster");
            udpSocket = new DatagramSocket(port);
        }

        private void broadcasting(String freq, byte[] data) {
            for (ClientHandler client : clientHandlerList.get(freq)) {
                //client.s
            }
        }

        @Override
        public void run() {
            System.out.println("start broadcaster");
            try {
                while (true) {
                    byte[] buf = new byte[256];
                    udpPacket = new DatagramPacket(buf, 256);
                    udpSocket.receive(udpPacket);
                    udpPacket.getData();

                    System.out.println("udp receve " + buf[0]);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
