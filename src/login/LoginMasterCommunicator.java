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

    public LoginMasterCommunicator(LoginServer loginServer) throws Exception {
        super(loginServer);
        
        serverType = ServerType.Login;
    }

    private boolean handleCommand(String msg) throws Exception {
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
                    case "addStream":
                        if (cmd.length != 5) {
                            return true;
                        }
                        mainServer.addStream(
                                cmd[2].split(":")[0],
                                Integer.parseInt(cmd[2].split(":")[1]),
                                Long.parseLong(cmd[3]),
                                Long.parseLong(cmd[4]));
                        break;
                    case "removeStream":
                        if (cmd.length != 3) {
                            return true;
                        }
                        mainServer.removeStream(
                                cmd[2].split(":")[0],
                                Integer.parseInt(cmd[2].split(":")[1]));
                        break;
                    case "updateStreamMin":
                        if (cmd.length != 4) {
                            return true;
                        }
                        mainServer.updateStreamMin(
                                cmd[2].split(":")[0],
                                Integer.parseInt(cmd[2].split(":")[1]),
                                Long.parseLong(cmd[3]));
                        break;
                    case "updateStreamMax":
                        if (cmd.length != 4) {
                            return true;
                        }
                        mainServer.updateStreamMax(
                                cmd[2].split(":")[0],
                                Integer.parseInt(cmd[2].split(":")[1]),
                                Long.parseLong(cmd[3]));
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
