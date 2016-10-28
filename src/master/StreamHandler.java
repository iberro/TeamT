/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package master;

import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Ihab BERRO
 */
public class StreamHandler extends Thread{

    private ConcurrentHashMap<String, StreamHandler> streamHandlerList;
    private Socket socket;
    
    public StreamHandler(ConcurrentHashMap<String, StreamHandler> streamHandlerList, Socket socket) {
        this.streamHandlerList = streamHandlerList;
        this.socket = socket;
    }
    
    public void run(){
    
    }
}
