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
public class StreamHandler extends Handler{

    public StreamHandler(ConcurrentHashMap<String, Handler> handlerList, Socket socket) throws Exception{
        super(handlerList, socket);
        handleType = HandleType.Stream;
    }
    private boolean setStatus(){
        System.out.println("Child 1");
        return true;
    }
}
