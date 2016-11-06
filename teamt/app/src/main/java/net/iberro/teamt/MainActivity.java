package net.iberro.teamt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    int freq;
    String loginIP;
    int loginPort;
    String streamIP;
    int streamPort;

    StreamThread streamThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        freq = 10;
        loginIP = "192.168.1.131";
        loginPort = 1235;
    }

    public void login(View view) {
        LoginThread loginThread = new LoginThread();
        loginThread.start();
    }

    public void streamLogin(View view) {
        streamThread = new StreamThread();
        streamThread.start();
    }
    public void startSend(View view){

    }
    public void stoptSend(View view){

    }

    public class LoginThread extends Thread{
        public LoginThread(){}
        public void run(){
            try {
                Log.d("login ","new socket");
                Socket socket = new Socket(loginIP, loginPort);
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                Scanner input = new Scanner(socket.getInputStream());
                String rcvMsg[];

                if(input.hasNextLine() && !input.nextLine().equals("+OK")){

                    return;
                }
                output.println("signin client key " + Integer.toString(freq));
                if(input.hasNextLine()){
                    rcvMsg = input.nextLine().split(":");
                    streamIP = rcvMsg[0];
                    streamPort = Integer.parseInt(rcvMsg[1]);
                    Log.d("stream ", rcvMsg[0] + ":" + rcvMsg[1]);
                }

            } catch (Exception ex) {
                Log.d("Error ", ex.toString());
            }
        }
    }

    public class StreamThread extends Thread{
        public StreamThread(){}
        public void run(){
            try {
                Log.d("stream ","new socket");
                Socket socket = new Socket(streamIP, streamPort);
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                Scanner input = new Scanner(socket.getInputStream());
                String rcvMsg[];

                if(input.hasNextLine() && !input.nextLine().equals("+OK")){
                    return;
                }
                output.println("signin client key " + Integer.toString(freq));
                if(input.hasNextLine() && !input.nextLine().equals("+OK")) {
                    return;
                }
                Log.d("stream ","registred");
            } catch (Exception ex) {
                Log.d("Error ", ex.toString());
            }
        }
    }
}
