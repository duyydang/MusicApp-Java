package com.example.myapplication;

import android.animation.ValueAnimator;
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
    ArrayList<Lyric> lyricArrayList;

    double mCurrentPostion;

    public void setmCurrentPostion(double mCurrentPostion) {
        this.mCurrentPostion = mCurrentPostion;
    }

    public LyricLineAdapter(Context context, List<String> lyricLineList, ArrayList<Lyric> lyricArrayList) {
        this.context = context;
        this.lyricLineList = lyricLineList;
        this.lyricArrayList = lyricArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.line_lyric,parent,false);
        return new ViewHolder(itemView);
    }
    @Override
    public int getItemCount() {
        return lyricLineList.size();
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("positionCurrent", position+"");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Loading highlight Word this
                handler.postDelayed(this,100);
            }
        },100);
        animateText(holder.txtLyric,lyricLineList.get(position),50);
    }

    private void animateText(TextView textView, String text, int delay) {
        textView.setText("");
        textView.setVisibility(View.VISIBLE);
        android.os.Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            int index = 0;
            @Override
            public void run() {
                if (index < text.length()) {
                    SpannableString spannableString = new SpannableString(text);
                    spannableString.setSpan(new ForegroundColorSpan(Color.RED), 0, index + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    textView.setText(spannableString);
                    index++;
                    handler.postDelayed(this, delay);
                }
            }
        };
        handler.postDelayed(runnable, 500);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtLyric;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtLyric = (TextView) itemView.findViewById(R.id.textViewLineLyric);
        }
    }
}
