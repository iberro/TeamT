/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stream;

/**
 *
 * @author Ihab BERRO
 */
public class Lead {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            StreamServer loginServer = new StreamServer("127.0.0.1", 1238);
            loginServer.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
