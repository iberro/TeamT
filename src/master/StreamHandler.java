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
public class StreamHandler extends Handler {

    private int min;
    private int max;

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public StreamHandler(
            ConcurrentHashMap<String, LoginHandler> loginHandlerList,
            ConcurrentHashMap<String, StreamHandler> streamHandlerList,
            Socket socket) throws Exception {
        super(loginHandlerList, streamHandlerList, socket);
        handleType = HandleType.Stream;

        //To Change
        //
        //
        min = streamHandlerList.size() * 1000;
        max = min + 999;

    }

    @Override
    protected boolean setStatus(String cmd) {
        System.out.println("setStatus " + cmd);
        switch (cmd.toLowerCase()) {
            case "getmin":
                update("updateStreamMin " + Integer.toString(min));
                break;
            case "getmax":
                update("updateStreamMax " + Integer.toString(max));
                break;
            default:
                switch (cmd.toLowerCase().split(":")[0]) {
                    case "setip":
                        ip = cmd.split(":")[1];
                        break;
                    case "setport":
                        port = Integer.parseInt(cmd.split(":")[1]);
                        //Update Login info
                        for (LoginHandler loginHandler : loginHandlerList.values()) {
                            loginHandler.update("addStream " + ip + ":" + Integer.toString(port) + " " + Integer.toString(min) + " " + Integer.toString(max));
                        }
                        break;
                }
                break;
        }
        return true;
    }

}
