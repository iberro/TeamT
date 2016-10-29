/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

import java.net.Socket;

/**
 *
 * @author Ihab BERRO
 */
public class MasterCommunicator extends Thread {

    Socket clientSoccket;
    int serverPort;

    public MasterCommunicator() {

    }

    @Override
    public void run() {
        try {
            clientSoccket = new Socket("127.0.0.1", 1234);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
