package com.example.myapplication;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LyricLineAdapter extends RecyclerView.Adapter<LyricLineAdapter.ViewHolder> {
    Context context;
    List<String> lyricLineList;
    int currentIndex = 0;
    long mCurrentPostion;
    String fullLyric;

    public void setFullLyric(String fullLyric) {
        this.fullLyric = fullLyric;
    }

    ArrayList<Float> lyricText = new ArrayList<>();

    public void setmCurrentPostion(long mCurrentPostion) {
        this.mCurrentPostion = mCurrentPostion;
    }

    public LyricLineAdapter(Context context, List<String> lyricLineList) {
        this.context = context;
        this.lyricLineList = lyricLineList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.line_lyric, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return lyricLineList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // remove space first
//        lyricLineList.get(position).substring(1);
//        // get startTimeWord similar postionLine from Arr[][]
        List<Long> startTimeWord = new ArrayList<>();
//        for (int i=0;i<lyricArrayList[position].length;i++){
//            startTimeWord.add(lyricArrayList[position][i]);
//        }
        holder.txtLyric.setText(lyricLineList.get(position));
//        Log.d("linePostion", position+"");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Loading highlight Word this
                hightlight(startTimeWord, lyricLineList.get(position), mCurrentPostion, holder.txtLyric);
                handler.postDelayed(this, 50);
            }
        }, 1000);
    }

    private void hightlight(List<Long> startTimes, String sentence, long currentTime, TextView textView) {
        String[] words = sentence.split(" ");
        float[] wordStartTimes = new float[words.length];
        for (int i = 0; i < words.length; i++) {
            if (i < startTimes.size()) {
                wordStartTimes[i] = startTimes.get(i);
            } else {
                wordStartTimes[i] = startTimes.get(startTimes.size() - 1);
            }
        }
        if (currentIndex < words.length && currentTime >= wordStartTimes[currentIndex]) {
            String currentWord = words[currentIndex];
            SpannableString spannableString = new SpannableString(sentence);
            spannableString.setSpan(new ForegroundColorSpan(Color.RED), 0, sentence.indexOf(currentWord) + currentWord.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            textView.setText(spannableString);
            currentIndex++;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtLyric;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtLyric = (TextView) itemView.findViewById(R.id.textViewLineLyric);
        }
    }
}
