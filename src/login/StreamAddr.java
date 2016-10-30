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
public class StreamAddr {

    private long min;
    private long max;
    private String ip;
    private int port;

    public long getMin() {
        return min;
    }

    public void setMin(long min) {
        this.min = min;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public StreamAddr() {
    }

    public StreamAddr(long min, long max, String ip, int port) {
        this.min = min;
        this.max = max;
        this.ip = ip;
        this.port = port;
    }
}
