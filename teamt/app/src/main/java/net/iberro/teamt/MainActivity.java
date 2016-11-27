package net.iberro.teamt;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaDataSource;
import android.media.MediaRecorder;
import android.media.MediaPlayer;
import android.net.rtp.AudioStream;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
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
    Socket streamSocket;
    boolean loginReady;
    boolean connected;

    LoginThread loginThread;
    StreamThread streamThread;

    //Audio data
    public byte[] buffer;
    public static DatagramSocket udpSocket;
    public static DatagramPacket udpPacket;

    AudioRecord recorder;
    MediaPlayer player;
    AudioStream audioStream;

    private int sampleRate = 8000;
    private int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    int minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
    private boolean status = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button = (Button) findViewById(R.id.send);

        try {
            udpSocket = new DatagramSocket();
        } catch (Exception ex) {
            Log.d("Error", ex.toString());
            ex.printStackTrace();
        }

        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    status = true;
                    broadcasting();
                    Log.d("Brodcast", "start");
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    status = false;
                    recorder.release();
                    Log.d("Brodcast", "stop");
                }
                return true;
            }
        });

        loginReady = false;
        connected = false;

        loginThread = new LoginThread();

        loginIP = "192.168.1.105";
        loginPort = 1235;

        Log.d("stream ", Integer.toString(minBufSize));

    }

    public void connect(View view) throws InterruptedException {
        if(!connected) {
            EditText editText = (EditText)findViewById(R.id.freq);
            freq = Integer.parseInt(editText.getText().toString());
            Log.d("freq", Integer.toString(freq));
            login();
            return;
        }

        makeConnect(false);
    }

    public void makeConnect(boolean status){

        Button buttonConnect = (Button)findViewById(R.id.connect);
        Button buttonTalk = (Button)findViewById(R.id.send);
        Button buttonInc = (Button)findViewById(R.id.inc);
        Button buttonDec = (Button)findViewById(R.id.dec);

        if (status) {
            connected = true;

            buttonConnect.setText("Disconnect");
            buttonTalk.setEnabled(true);
            buttonInc.setEnabled(false);
            buttonDec.setEnabled(false);
        }else {
            connected = false;

            //streamThread.stop();

            buttonConnect.setText("Connect");
            buttonTalk.setEnabled(false);
            buttonInc.setEnabled(true);
            buttonDec.setEnabled(true);
        }
    }

    public void login() {
        loginThread.start();
    }

    public void streamLogin() throws InterruptedException {
        streamThread = new StreamThread();
        streamThread.start();
    }

    public void dec(View view){
        EditText editText = (EditText)findViewById(R.id.freq);
        int temp = Integer.parseInt(editText.getText().toString());
        if(temp <= 0 ) return;
        temp--;
        editText.setText( Integer.toString(temp));
    }

    public void inc(View view){
        EditText editText = (EditText)findViewById(R.id.freq);
        int temp = Integer.parseInt(editText.getText().toString());
        temp++;
        editText.setText(Integer.toString(temp));
    }

    public void broadcasting() {
        Thread broadcastThread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {

                    byte[] buffer = new byte[minBufSize];

                    recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, sampleRate, channelConfig, audioFormat, minBufSize * 10);
                    recorder.startRecording();
                    DataOutputStream output = new DataOutputStream(streamSocket.getOutputStream());

                    while (status == true) {
                        /*minBufSize  = */
                        recorder.read(buffer, 0, buffer.length);
                        output.write(buffer);
                    }
                } catch (Exception ex) {
                    Log.d("Error", ex.toString());
                    ex.printStackTrace();
                }
            }

        });
        broadcastThread.start();
    }


    public class LoginThread extends Thread {
        public LoginThread() {
        }

        public void run() {
            try {
                Log.d("login ", "new socket");
                Socket socket = new Socket(loginIP, loginPort);
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                Scanner input = new Scanner(socket.getInputStream());
                String rcvMsg[];

                if (input.hasNextLine() && !input.nextLine().equals("+OK")) {
                    return;
                }
                output.println("signin client key " + Integer.toString(freq));
                if (input.hasNextLine()) {
                    rcvMsg = input.nextLine().split(":");
                    streamIP = rcvMsg[0];
                    streamPort = Integer.parseInt(rcvMsg[1]);
                    Log.d("stream ", rcvMsg[0] + ":" + rcvMsg[1]);

                }

                loginReady = true;
                streamLogin();
            } catch (Exception ex) {
                Log.d("Error ", ex.toString());
            }
        }
    }

    public class StreamThread extends Thread {
        public StreamThread() {
        }

        public void run() {
            try {
                Log.d("stream ", "new socket");
                streamSocket = new Socket(streamIP, streamPort);
                PrintWriter output = new PrintWriter(streamSocket.getOutputStream(), true);
                Scanner input = new Scanner(streamSocket.getInputStream());
                String rcvMsg[];

                if (input.hasNextLine() && !input.nextLine().equals("+OK")) {
                    return;
                }
                output.println("signin client key " + Integer.toString(freq));
                if (input.hasNextLine() && !input.nextLine().equals("+OK")) {
                    return;
                }
                Log.d("stream ", "registred");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        makeConnect(true);
                    }
                });

                DataInputStream inputData = new DataInputStream(streamSocket.getInputStream());
                byte[] buf ;
                AudioTrack audioTrack = new AudioTrack(
                        AudioManager.STREAM_MUSIC,
                        sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, 640,
                        AudioTrack.MODE_STATIC);

                while (true) {
                   buf = new byte[640];
                    inputData.read(buf, 0, 640);
                    Log.d("stream ", "Data");

                    try{ audioTrack.stop();}catch (Exception ex){}
                        audioTrack.flush();
                        audioTrack.write(buf, 0, buf.length);
                        audioTrack.play();
                }

            } catch (Exception ex) {
                Log.d("Error ", ex.toString());
                ex.printStackTrace();
            }
        }
    }


}
