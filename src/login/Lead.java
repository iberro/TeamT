/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

/**
 *
 * @author Ihab BERRO
 */
public class Lead {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        int loginServerPORT = 1235;
        String masterAddress = "127.0.0.1";
        
        try {
            LoginServer loginServer = new LoginServer(masterAddress, loginServerPORT);
            loginServer.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
