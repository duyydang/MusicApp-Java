package com.example.myapplication;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;

import android.util.Log;

import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
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
    ArrayList<String> lyricLineArraylist = new ArrayList<>();
    ArrayList<Float> timeLyricLine = new ArrayList<>();
    ArrayList<Lyric> fullLyric = new ArrayList<>();
    StringBuilder lyric;
    LyricLineAdapter adapter;
    private int mCurrentLinePosition = 0;
    long[][] wordTimes;
    int x=0,y=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AnhXa();
        //Set mp3 play
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

        convertTo2dArray();

        // Test mảng 2 chiều
//        wordTimes[0] = new long[7];
//        wordTimes[0][0] = 35144;
//        wordTimes[0][1] = 35587;
//        wordTimes[0][2] = 36006;
//        wordTimes[0][3] = 36475;
//        wordTimes[0][4] = 36972;
//        wordTimes[0][5] = 37495;
//        wordTimes[0][6] = 37939;
//        wordTimes[1] = new long[7];
//        wordTimes[1][0] = 42641;
//        wordTimes[1][1] = 43085;
//        wordTimes[1][2] = 43476;
//        wordTimes[1][3] = 43869;
//        wordTimes[1][4] = 44443;
//        wordTimes[1][5] = 45017;
//        wordTimes[1][6] = 45488;

        adapter.setWordTimes(wordTimes);

        // Start mp3 and call fuction
        mediaPlayer.start();
        SetTimeTotal();
        UpdateTimeSong();
        //Change time playing when change seekbar
        skSong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int positon, boolean b) {
                float currentPosition = positon / 1000;
                adapter.updateCurrentTime(positon);
                for (int i = 0; i < timeLyricLine.size() - 1; i++) {
                    float startTime = timeLyricLine.get(i);
                    float nextStartTime = timeLyricLine.get(i + 1);
                    if (currentPosition >= startTime && currentPosition < nextStartTime) {
                        // Cập nhật vị trí của dòng hiện tại đang được phát
                        mCurrentLinePosition = i;
                        // Cuộn ListView xuống dòng hiện tại đang được phát
                        recyclerViewSong.getLayoutManager().scrollToPosition(mCurrentLinePosition);
                        break;
                    }
                }
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
    private void convertTo2dArray(){
        int numSentences = 0;
        for (Lyric word : fullLyric){
            if(Character.isUpperCase(word.getText().charAt(0))){
                numSentences++;
            }
        }
        wordTimes = new long[numSentences+1][];

        int start = 0;
        int sentenceIndex = 0;
        for (int i = 0; i < fullLyric.size(); i++){
            String word = fullLyric.get(i).getText();
            if (Character.isUpperCase(word.charAt(0))){
                int sentenceLength = i - start;
                wordTimes[sentenceIndex] = new long[sentenceLength];
                for ( int j = 0; j<sentenceLength; j++){
                    wordTimes[sentenceIndex][j] = (long) (fullLyric.get(start+j).getStartTime()*1000);
                }
                sentenceIndex++;
                start=i;
            }
        }
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
                float startTime = Float.parseFloat(parser.getAttributeValue(null, "va"));
                String text = parser.nextText();
                //build Lyric for lyricLineArraylist
                lyric.append(text);
                //add lyric and time
                fullLyric.add(new Lyric(startTime,text));
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

    //Can use "for()" in fullLyricLineArrayList taken above and find first Letter. If first Letter is "Upcase" then this is First Line
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
                float startTime = Float.valueOf(parser.getAttributeValue(null, "va"));
                timeLyricLine.add((float) (startTime - 0.5));
            }
            eventType = parser.next();
        }
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
        adapter = new LyricLineAdapter(this, lyricLineArraylist);
        recyclerViewSong.setAdapter(adapter);
    }
}