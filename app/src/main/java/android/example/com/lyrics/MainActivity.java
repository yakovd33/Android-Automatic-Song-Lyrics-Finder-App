package android.example.com.lyrics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements apiActivity {
    private static final int API_CALL_CODE = 555;

    IntentFilter iF;
    TextView status = null;
    TextView lyricsTv;
    ImageView songDetsCover;
    Context mainContext;
    TextView loading_informer;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String cmd = intent.getStringExtra("command");
            Log.i("recieveddd", action);

            Log.d("Music", cmd + " : " + action);

            long album_id = intent.getLongExtra("id", 0);
            String artist = intent.getStringExtra("artist");
            String album = intent.getStringExtra("album");
            String track = intent.getStringExtra("track");
            boolean playing = intent.getBooleanExtra("playing", false);

            Log.d("Music", artist + " : " + album + " : " + track + " - " + Long.toString(album_id));

            if (!playing) {
                status.setText("No music is playing");
            }

            status.setText(artist + " - " + album + " - " + track);

            CallAPI api = new CallAPI(MainActivity.this, API_CALL_CODE);
            api.execute("http://api.vagalume.com.br/search.php?art=" + artist + "&mus=" + track);
            loading_informer.setVisibility(View.VISIBLE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainContext = MainActivity.this;

        iF = new IntentFilter();

        iF.addAction("com.android.music.metachanged");
        iF.addAction("com.android.music.playbackcomplete");
        iF.addAction("com.android.music.queuechanged");

        iF.addAction("com.android.yakovmusic.metachanged");
        iF.addAction("com.android.yakovmusic.playbackcomplete");
        iF.addAction("com.android.yakovmusic.queuechanged");

        //HTC Music
        iF.addAction("com.htc.music.playbackcomplete");
        iF.addAction("com.htc.music.metachanged");
        //MIUI Player
        iF.addAction("com.miui.player.playbackcomplete");
        iF.addAction("com.miui.player.metachanged");
        //Real
        iF.addAction("com.real.IMP.playbackcomplete");
        iF.addAction("com.real.IMP.metachanged");
        //SEMC Music Player
        iF.addAction("com.sonyericsson.music.playbackcontrol.ACTION_TRACK_STARTED");
        iF.addAction("com.sonyericsson.music.playbackcontrol.ACTION_PAUSED");
        iF.addAction("com.sonyericsson.music.TRACK_COMPLETED");
        iF.addAction("com.sonyericsson.music.metachanged");
        iF.addAction("com.sonyericsson.music.playbackcomplete");
        //rdio
        iF.addAction("com.rdio.android.metachanged");
        //Samsung Music Player
        iF.addAction("com.samsung.sec.android.MusicPlayer.playbackcomplete");
        iF.addAction("com.samsung.sec.android.MusicPlayer.metachanged");
        iF.addAction("com.sec.android.app.music.playbackcomplete");
        iF.addAction("com.sec.android.app.music.metachanged");

        this.status = (TextView) this.findViewById(R.id.status);
        lyricsTv = (TextView) findViewById(R.id.lyricsTv);
        songDetsCover = (ImageView) findViewById(R.id.songDetsCover);
        loading_informer = (TextView) findViewById(R.id.lyrics_loading_informer);

        AudioManager manager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if (!manager.isMusicActive()) {
            status.setText("No music active");
        }

        registerReceiver(mReceiver, iF);
    }

    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    protected void onResume() {
        registerReceiver(mReceiver, iF);
        super.onResume();
    }

    @Override
    public void apiCallback(int code, String response) {
        if (code == API_CALL_CODE) {
            loading_informer.setVisibility(View.GONE);
            try {
                Log.i("response length is", Integer.toString(response.length()));
                if (response.length() > 0) {
                    Log.i("response is", response);
                    JSONObject head = new JSONObject(response);
                    if (head.has("mus")) {
                        String musStr = head.getString("mus");
                        JSONArray mus = new JSONArray(musStr);
                        JSONObject musJO = new JSONObject(mus.getString(0));
                        String lyrics = musJO.getString("text").toString();
                        if (lyrics.length() > 0) {
                            lyricsTv.setText(lyrics);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}