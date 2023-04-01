package com.example.myapplication;


import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TextView txtTitle, txtTimeTotal, txtTimeSong;
    RecyclerView recyclerViewSong;
    SeekBar skSong;
    MediaPlayer mediaPlayer = new MediaPlayer();
    ImageButton btnPlay;
    ArrayList<Lyric> lyricArrayList = new ArrayList<>();
    ArrayList<String> lyricLineArraylist = new ArrayList<>();
    ArrayList<Double> animationTimeArrrayList = new ArrayList<>();
    StringBuilder lyric;
    LyricLineAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AnhXa();
        lyricLineArraylist.add("...!");
        mediaPlayer = MediaPlayer.create(this, R.raw.beat);
        // call readLyricFromXml and try/catch
        try {
            readLyricsFromXml(R.raw.lyric);
            readTimeLineFromXML(R.raw.lyric);
        } catch (XmlPullParserException e) {
            Log.e("Lyrics", "Error parsing XML: " + e.getMessage());
        } catch (IOException e) {
            Log.e("Lyrics", "Error reading XML file: " + e.getMessage());
        }
        adapter = new LyricLineAdapter(this, lyricLineArraylist, lyricArrayList);
        recyclerViewSong.setAdapter(adapter);
        // start mp3 and call fuction
        mediaPlayer.start();
        SetTimeTotal();
        UpdateTimeSong();
        scrollListView();


        //Change time playing when change seekbar
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


    private void scrollListView() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            int scrollPosition = 1;

            @Override
            public void run() {
                // get current time mp3 play
                double currentPosition = mediaPlayer.getCurrentPosition() / 1000;
                if (currentPosition >= animationTimeArrrayList.get(scrollPosition)) {

                    recyclerViewSong.post(() -> {
                        recyclerViewSong.getLayoutManager().scrollToPosition(scrollPosition);
                        View view = recyclerViewSong.getLayoutManager().findViewByPosition(scrollPosition);
                        if (view != null) {
                            // Item đã được hiển thị trên màn hình
                            // Thực hiện các thao tác khác tại đây
                        } else {
                            // Item vẫn chưa được hiển thị trên màn hình
                            // Có thể thực hiện các thao tác khác tại đây hoặc đợi một khoảng thời gian
                        }
                    });

                    scrollPosition++;
                }
                handler.postDelayed(this, 50);
            }
        }, 100);
    }


    private void readLyricsFromXml(int resourceId) throws XmlPullParserException, IOException {
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
            if (eventType == XmlPullParser.START_TAG && parser.getName().equals("i")) {
                // Extract the start time and text data for the line
                double startTime = Double.parseDouble(parser.getAttributeValue(null, "va"));
                String text = parser.nextText();

                // Add text and time
                Log.d("Lyrics", "Start time: " + startTime + ", Text: " + text);
                lyric.append(text).append(" ");
                lyricArrayList.add(new Lyric(startTime, text));
            } else
                // If meet new Line then ...
                if (eventType == XmlPullParser.START_TAG && parser.getName().equals("param")) {
                    //Get any Lyric Line = String Builder
                    lyricLineArraylist.add(String.valueOf(lyric));
                    lyric = new StringBuilder();
                }
            // Move on to the next XML event
            eventType = parser.next();
        }
        // Delete index 0 is null and add last lyric line
        lyricLineArraylist.remove(0);
        lyricLineArraylist.add(String.valueOf(lyric));
        // Close the input stream for the XML file
        inputStream.close();
    }

    //Can use "for()" in lyricArrayList taken above and find first Letter. If first Letter is "Upcase" then this is First Line
    //But add this funtion can use if Lyric not good ( Ex: All lyrics are capitalized )
    //This funtion can be drop if lyric "GOOD"
    public void readTimeLineFromXML(int resourceId) throws XmlPullParserException, IOException {
        InputStream inputStream = getResources().openRawResource(resourceId);

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();

        parser.setInput(inputStream, null);

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && parser.getName().equals("param")) {
                // Move to the first "line" tag
                parser.nextTag();
                while (parser.getName().equals("i") == false) {
                    parser.nextTag();
                }
                // Get the start time of the first "line" tag
                Double startTime = Double.valueOf(parser.getAttributeValue(null, "va"));
                animationTimeArrrayList.add(startTime-0.5);
                Log.d("startTime", startTime + "");
                // Exit the loop
            }
            eventType = parser.next();
        }
        inputStream.close();
    }
    // Use the start time

    private void UpdateTimeSong() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //format Time to minues:secound
                SimpleDateFormat formatTime = new SimpleDateFormat("mm:ss");
                txtTimeSong.setText(formatTime.format(mediaPlayer.getCurrentPosition()));
                //set progress seekbar = time play
                skSong.setProgress(mediaPlayer.getCurrentPosition());
                handler.postDelayed(this, 100);
            }
        }, 100);
    }

    private void SetTimeTotal() {
        //format Time to minues:secound
        SimpleDateFormat formatTime = new SimpleDateFormat("mm:ss");
        txtTimeTotal.setText(formatTime.format(mediaPlayer.getDuration()));

        //sex Max Seekbar = max Time file Mp3
        skSong.setMax(mediaPlayer.getDuration());
    }

    private void AnhXa() {
        txtTitle = (TextView) findViewById(R.id.textViewTitle);
        txtTimeSong = (TextView) findViewById(R.id.textViewTimeSong);
        txtTimeTotal = (TextView) findViewById(R.id.textViewTimeTotal);
        skSong = (SeekBar) findViewById(R.id.seekBar);
        btnPlay = (ImageButton) findViewById(R.id.imgPlay);
        //init recyclerView
        recyclerViewSong = (RecyclerView) findViewById(R.id.recyclerviewSong);
        recyclerViewSong.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewSong.setLayoutManager(layoutManager);

    }
}