/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package master;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Ihab BERRO
 */
public class LoginHandler extends Handler {

    public LoginHandler(
            ConcurrentHashMap<String, LoginHandler> loginHandlerList,
            ConcurrentHashMap<String, StreamHandler> streamHandlerList,
            Socket socket) throws Exception{
        super(loginHandlerList, streamHandlerList, socket);
        handleType = HandleType.Login;
    }
    
    @Override
    protected boolean setStatus(String cmd){
        System.out.println("setStatus " + cmd);
        return true;
    }
}
