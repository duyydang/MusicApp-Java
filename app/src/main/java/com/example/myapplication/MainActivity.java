package com.example.myapplication;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {
    TextView txtTitle, lyricsText, txtTimeTotal, txtTimeSong;
    LinearLayout lyricsContainer;
    SeekBar skSong;
    MediaPlayer mediaPlayer = new MediaPlayer();
    ImageButton btnPlay;
    ArrayList<Lyric> lyricArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AnhXa();
        mediaPlayer = MediaPlayer.create(this, R.raw.beat);
        try {
            readLyricsFromXml(R.raw.lyric);
        } catch (XmlPullParserException e) {
            Log.e("Lyrics", "Error parsing XML: " + e.getMessage());
        } catch (IOException e) {
            Log.e("Lyrics", "Error reading XML file: " + e.getMessage());
        }
        mediaPlayer.start();
        SetTimeTotal();
        UpdateTimeSong();
        skSong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(skSong.getProgress());
            }
        });
    }


    public void readLyricsFromXml(int resourceId) throws XmlPullParserException, IOException {
        // Open the input stream for the XML file
        InputStream inputStream = getResources().openRawResource(resourceId);

        // Create a new XML pull parser factory and configure it
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);

        // Create a new XML pull parser instance and set its input source
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(inputStream, null);

        // Parse the XML document and log each line to the console
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && parser.getName().equals("param")) {
                lyricArrayList.add(new Lyric(0, "b"));
            }
            if (eventType == XmlPullParser.START_TAG && parser.getName().equals("i")) {
                // Extract the start time and text data for the line
                double startTime = Double.parseDouble(parser.getAttributeValue(null, "va"));
                String text = parser.nextText();

                // Do something with the data here (e.g. log it to the console)
                Log.d("Lyrics", "Start time: " + startTime + ", Text: " + text);
                lyricArrayList.add(new Lyric(startTime, text));
            }

            // Move on to the next XML event
            eventType = parser.next();
        }
        // Close the input stream for the XML file
        inputStream.close();
    }

    private void UpdateTimeSong() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //format Time to minues:secound
                SimpleDateFormat formatTime = new SimpleDateFormat("mm:ss");
                txtTimeSong.setText(formatTime.format(mediaPlayer.getCurrentPosition()));
                skSong.setProgress(mediaPlayer.getCurrentPosition());
                handler.postDelayed(this, 100);
            }
        }, 100);
    }

    private void SetTimeTotal() {
        //format Time to minues:secound
        SimpleDateFormat formatTime = new SimpleDateFormat("mm:ss");
        txtTimeTotal.setText(formatTime.format(mediaPlayer.getDuration()));
        skSong.setMax(mediaPlayer.getDuration());
    }

    private void AnhXa() {
        txtTitle = (TextView) findViewById(R.id.textViewTitle);
        txtTimeSong = (TextView) findViewById(R.id.textViewTimeSong);
        txtTimeTotal = (TextView) findViewById(R.id.textViewTimeTotal);
        skSong = (SeekBar) findViewById(R.id.seekBar);
        btnPlay = (ImageButton) findViewById(R.id.imgPlay);

        lyricsText = findViewById(R.id.lyrics_text);
        lyricsContainer = findViewById(R.id.lyrics_container);
    }
}