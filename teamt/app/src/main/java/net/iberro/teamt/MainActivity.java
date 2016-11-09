package net.iberro.teamt;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    int freq;
    String loginIP;
    int loginPort;
    String streamIP;
    int streamPort;


    StreamThread streamThread;

    //Audio data
    public byte[] buffer;
    public static DatagramSocket udpSocket;
    public static DatagramPacket udpPacket;

    AudioRecord recorder;

    private int sampleRate = 16000 ;
    private int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
    private boolean status = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = (Button) findViewById(R.id.button3);

        try {
            udpSocket = new DatagramSocket();
        }catch (Exception ex){
            Log.d("Error", ex.toString());
            ex.printStackTrace();
        }

        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    status = true;
                    broadcasting();
                    Log.d("Brodcast", "start");
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    status = false;
                    recorder.release();
                    Log.d("Brodcast", "stop");
                }
                return true;
            }
        });

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

    public void broadcasting() {
        Thread broadcastThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    byte[] buffer = new byte[minBufSize];

                    final InetAddress destination = InetAddress.getByName(streamIP);

                    recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleRate,channelConfig,audioFormat,minBufSize*10);
                    recorder.startRecording();

                    while(status == true) {
                        minBufSize = recorder.read(buffer, 0, buffer.length);

                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
                        outputStream.write(freq);
                        outputStream.write(buffer);
                        udpPacket = new DatagramPacket (outputStream.toByteArray(),buffer.length,destination,streamPort+1);
                        udpSocket.send(udpPacket);
                    }
                } catch (Exception ex) {
                    Log.d("Error", ex.toString());
                    ex.printStackTrace();
                }
            }

        });
        broadcastThread.start();
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
