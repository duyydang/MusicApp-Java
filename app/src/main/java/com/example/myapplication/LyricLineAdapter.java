package com.example.myapplication;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

public class LyricLineAdapter extends RecyclerView.Adapter<LyricLineAdapter.ViewHolder> {
    Context context;
    List<String> lyricLineList;
    ArrayList<Lyric> lyricArrayList;



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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtLyric.setText(lyricLineList.get(position));
    }

    @Override
    public int getItemCount() {
        return lyricLineList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView txtLyric;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtLyric = (TextView) itemView.findViewById(R.id.textViewLineLyric);
        }
    }
}
