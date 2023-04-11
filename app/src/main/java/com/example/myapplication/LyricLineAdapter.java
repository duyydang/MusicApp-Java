package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LyricLineAdapter extends RecyclerView.Adapter<LyricLineAdapter.ViewHolder> {
    Context context;
    List<String> lyricLineList;
    private long mCurrentTime = 0; // thời gian hiện tại của bài hát
    long[][] wordTimes = new long[26][];

    public void setWordTimes(long[][] wordTimes) {
        this.wordTimes = wordTimes;
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
        holder.txtLyric.setText(lyricLineList.get(position));
        highlightTextWithTiming(holder.txtLyric,lyricLineList.get(position),wordTimes[position],mCurrentTime);
    }

    private void highlightTextWithTiming(TextView textView, String sentence, long[] timings, long currentTime) {
        String[] words = sentence.split("\\s+"); // tách câu thành các từ riêng lẻ
        SpannableString spannableString = new SpannableString(sentence); // tạo một SpannableString mới cho câu đó
        for (int i = 0; i < words.length; i++) {
            long wordStartTime = timings[i];
            long wordEndTime = i == timings.length - 1 ? Long.MAX_VALUE : timings[i + 1]; // thời gian kết thúc của từ sau
            if (currentTime >= wordStartTime && currentTime < wordEndTime) {
                // nếu thời gian hiện tại nằm trong khoảng thời gian tô của từ đó, tô màu từ đó
                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.RED);
                spannableString.setSpan(foregroundColorSpan, 0, sentence.indexOf(words[i]) + words[i].length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        textView.setText(spannableString); // set lại text view với các từ đã được tô màu
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtLyric;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtLyric = (TextView) itemView.findViewById(R.id.textViewLineLyric);
        }
    }
    // hàm cập nhật mCurrentTime và notifyDataSetChanged()
    public void updateCurrentTime(long currentTime) {
        mCurrentTime = currentTime;
        notifyDataSetChanged();
    }
}
