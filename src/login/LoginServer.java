/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

import common.Server;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import master.LoginHandler;
import master.StreamHandler;

/**
 *
 * @author Ihab BERRO
 */
public class LoginServer extends Server {

    private ServerSocket servSocket;
    private int port;
    private Socket clientSocket;

    private LoginMasterCommunicator masterCommunicator;
    private ArrayList<StreamAddr> streamArray;
    private ConcurrentHashMap<String, ClientHandler> clientHandlerList;
    private ReentrantLock lockArray;

    public LoginServer(String masterAddress, int loginServerPORT) throws Exception {
        port = loginServerPORT;
        masterCommunicator = new LoginMasterCommunicator(masterAddress, this);
        
        clientHandlerList = new ConcurrentHashMap<>();
        streamArray = new ArrayList<>();
        
        lockArray = new ReentrantLock();
    }

    @Override
    public void run() {
        try {
            if (!masterCommunicator.connect("passwordkey")) {
                System.out.println("Login: not conn");
                return;
            }
            masterCommunicator.start();

            servSocket = new ServerSocket(port);
            while (true) {
                clientSocket = servSocket.accept();
                System.out.println("Login: new client");
                ClientHandler clientHandler = new ClientHandler(clientSocket, clientHandlerList, this);
                clientHandler.start();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void addStream(String ip, int port, int min, int max) {
        lockArray.lock();
        System.out.println("Login add stream: " + ip + Integer.toString(port) + Long.toString(min) + Long.toString(max));
        if ((streamArray.size()>0) &&(max > streamArray.get(0).getMin())) {
            streamArray.add(0, new StreamAddr(min, max, ip, port));
            lockArray.unlock();
            return;
        }
        for (int i = 1; i < streamArray.size() - 1; i++) {
            if ((min > streamArray.get(i).getMin()) && (max < streamArray.get(i).getMax())) {
                streamArray.add(i, new StreamAddr(min, max, ip, port));
                lockArray.unlock();
                return;
            }
        }
        streamArray.add(new StreamAddr(min, max, ip, port));
        lockArray.unlock();
    }

    @Override
    public void removeStream(String ip, int port) {
        lockArray.lock();
        System.out.println("Login remove stream: " + ip + Integer.toString(port));
        for (StreamAddr stream : streamArray) {
            if ((stream.getIp().equals(ip)) && (stream.getPort() == port)) {
                streamArray.remove(stream);
                lockArray.unlock();
                return;
            }
        }
        lockArray.unlock();
    }

    @Override
    public void updateStreamMin(String ip, int port, int newMin) {
        lockArray.lock();
        System.out.println("Login update min stream: " + ip + Integer.toString(port) + Long.toString(newMin));
        for (StreamAddr stream : streamArray) {
            if ((stream.getIp().equals(ip)) && (stream.getPort() == port)) {
                stream.setMin(newMin);
                lockArray.unlock();
                return;
            }
        }
        lockArray.unlock();
    }

    @Override
    public void updateStreamMax(String ip, int port, int newMax) {
        lockArray.lock();
        try {
            System.out.println("Login update max stream: " + ip + Integer.toString(port) + Long.toString(newMax));
            for (StreamAddr stream : streamArray) {
                if ((stream.getIp().equals(ip)) && (stream.getPort() == port)) {
                    stream.setMax(newMax);
                    lockArray.unlock();
                    return;
                }
            }
        } finally {
            lockArray.unlock();
        }
    }

    public StreamAddr getStream(long freq) {
        lockArray.lock();
        for (StreamAddr stream : streamArray) {
            if (freq >= stream.getMin() && freq <= stream.getMax()) {
                lockArray.unlock();
                return stream;
            }
        }
        lockArray.unlock();
        return null;
    }
    
    private void initClientList(){
        
    }
}
