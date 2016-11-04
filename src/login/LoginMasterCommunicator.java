/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

import common.Server;
import common.MasterCommunicator;

/**
 *
 * @author Ihab BERRO
 */
public class LoginMasterCommunicator extends MasterCommunicator {

    public LoginMasterCommunicator(String masterIP, LoginServer loginServer) throws Exception {
        super(masterIP, loginServer);
        serverType = ServerType.Login;
    }

    private void rcvUpdate(String cmd[]) {

        if ((cmd == null) || (cmd.length < 2)) {
            return;
        }
        switch (cmd[1].toLowerCase()) {
            case "addstream":
                if (cmd.length != 5) {
                    return ;
                }
                mainServer.addStream(
                        cmd[2].split(":")[0],
                        Integer.parseInt(cmd[2].split(":")[1]),
                        Integer.parseInt(cmd[3]), Integer.parseInt(cmd[4]));
                break;
            case "removestream":
                if (cmd.length != 3) {
                    return;
                }
                mainServer.removeStream(
                        cmd[2].split(":")[0],
                        Integer.parseInt(cmd[2].split(":")[1]));
                break;
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
        if (cmd.length == 0) {
            return true;
        }
        switch (cmd[0].toLowerCase()) {
            case "getstatus":
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
