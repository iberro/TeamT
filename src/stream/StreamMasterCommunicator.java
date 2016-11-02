/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stream;

import common.MasterCommunicator;
import common.Server;

/**
 *
 * @author Ihab BERRO
 */
public class StreamMasterCommunicator extends MasterCommunicator {

    public StreamMasterCommunicator(Server streamServer) throws Exception {
        super(streamServer);
        this.mainServer = streamServer;
        serverType = ServerType.Stream;
    }

    @Override
    protected boolean handleCommand(String msg) throws Exception {
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
                    case "updateStreamMin":
                        if (cmd.length != 4) {
                            return true;
                        }
                        mainServer.updateStreamMin(
                                cmd[2].split(":")[0],
                                Integer.parseInt(cmd[2].split(":")[1]),
                                Integer.parseInt(cmd[3]));
                        break;
                    case "updateStreamMax":
                        if (cmd.length != 4) {
                            return true;
                        }
                        mainServer.updateStreamMax(
                                cmd[2].split(":")[0],
                                Integer.parseInt(cmd[2].split(":")[1]),
                                Integer.parseInt(cmd[3]));
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        return true;
    }
}
