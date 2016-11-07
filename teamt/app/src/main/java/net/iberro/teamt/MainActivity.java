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
    Brodcaster brodcaster;

    StreamThread streamThread;

    DatagramSocket udpSocket;
    DatagramPacket udpPacket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = (Button) findViewById(R.id.button3);
        brodcaster = new Brodcaster();
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    //brodcaster.brodcasting();
                    Log.d("Brodcast", "start");
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
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

    public class Brodcaster{

        public byte[] buffer;
        public DatagramSocket socket;
        private int port=50005;

        AudioRecord recorder;

        private int sampleRate = 16000 ;
        private int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
        private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
        private boolean status = true;

        public Brodcaster(){

        }
        public void connect(){
            try {
                udpSocket = new DatagramSocket(12340);
            }catch (Exception ex){
                Log.d("Error ", ex.toString());
            }

        }
        public void start() throws Exception{
            DatagramSocket socket = new DatagramSocket();
            byte[] buffer = new byte[minBufSize];

            DatagramPacket packet;

            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,sampleRate,channelConfig,audioFormat,minBufSize*10);
            recorder.startRecording();


            minBufSize = recorder.read(buffer, 0, buffer.length);
            packet = new DatagramPacket (buffer, buffer.length, InetAddress.getByName(streamIP), streamPort + 1);

            socket.send(packet);
            recorder.stop();
        }
        public void stop(){

        }

    }
}
