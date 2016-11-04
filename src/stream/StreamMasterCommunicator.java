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

    public StreamMasterCommunicator(String masterIP, Server streamServer) throws Exception {
        super(masterIP, streamServer);
        this.mainServer = streamServer;
        serverType = ServerType.Stream;
    }

    private void rcvUpdate(String cmd[]) {
        if (cmd.length < 2) {
            return;
        }
        switch (cmd[1].toLowerCase()) {
            case "updatestreammin":
                if (cmd.length != 4) {
                    return;
                }
                mainServer.updateStreamMin(
                        cmd[2].split(":")[0],
                        Integer.parseInt(cmd[2].split(":")[1]),
                        Integer.parseInt(cmd[3]));
                break;
            case "updatestreammax":
                if (cmd.length != 4) {
                    return;
                }
                mainServer.updateStreamMax(
                        cmd[2].split(":")[0],
                        Integer.parseInt(cmd[2].split(":")[1]),
                        Integer.parseInt(cmd[3]));
                break;
            default:
                break;
        }
    }

    @Override
    protected boolean handleCommand(String msg) throws Exception {
        System.out.println("Master comm:" + msg);
        String cmd[] = msg.split(" ");
        if ((cmd == null) || (cmd.length == 0)) {
            return true;
        }
        switch (cmd[0].toLowerCase()) {
            case "getStatus":
                //
                //
                //
                setStatus("Normal", 10);
                break;
            case "update":
                rcvUpdate(cmd);
                break;
            default:
                break;
        }
        return true;
    }
}
