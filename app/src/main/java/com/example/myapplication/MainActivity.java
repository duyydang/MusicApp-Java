package com.example.myapplication;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.Lyric;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private List<Lyric> lyrics;
    private TextView textViewLyric;
    private SeekBar seekBar;
    private Button buttonPlay;

    TextView lyricsTextView;
    Array

    // ... code tiếp theo
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mediaPlayer = MediaPlayer.create(this, R.raw.song);

        // Khởi tạo TextView để hiển thị lời bài hát
        lyricsTextView = (TextView)findViewById(R.id.lyrics_text_view);

        // Đọc lời bài hát từ file XML và lưu trữ chúng trong danh sách Lyric
        lyricsList = readLyricsFromXml();

        // Sử dụng Handler để cập nhật vị trí hiện tại của bài hát
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                updateLyrics();
                handler.postDelayed(this, 1000);
            }
        });

    }

    private ArrayList<Lyric> readLyricsFromXml(int resourceId) {
        ArrayList<Lyric> lyricLines = new ArrayList<>();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();

            InputStream in = getResources().openRawResource(resourceId);
            parser.setInput(in, null);

            int eventType = parser.getEventType();
            int startTime = 0;
            String content = "";

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagname = parser.getName();

                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagname.equalsIgnoreCase("line")) {
                            startTime = Integer.parseInt(parser.getAttributeValue(null, "startTime"));
                            content = parser.nextText();
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (tagname.equalsIgnoreCase("line")) {
                            lyricLines.add(new Lyric(startTime, text));
                        }
                        break;

                    default:
                        break;
                }

                eventType = parser.next();
            }

            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lyricLines;
    }
}