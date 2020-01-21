package com.bx.words;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class WordsAdapter extends RecyclerView.Adapter<WordsAdapter.MyViewHolder> {
    private List<Words> allwords = new ArrayList<>();
    private WordsViewModel wordsViewModel;
    private boolean userView;

    WordsAdapter(boolean userView, WordsViewModel wordsViewModel) {
        this.userView = userView;
        this.wordsViewModel = wordsViewModel;
    }

    //注册按键
    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewid, textViewEnglish, textViewChinese;
        Switch aSwitchChineseInvisiable;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewid = itemView.findViewById(R.id.textViewid);
            textViewEnglish = itemView.findViewById(R.id.textViewEnglish);
            textViewChinese = itemView.findViewById(R.id.textViewChinese);
            aSwitchChineseInvisiable = itemView.findViewById(R.id.switchstyle);
        }
    }

    //读取数据
    void setAllwords(List<Words> allwords) {
        this.allwords = allwords;
    }

    //创建View时判断Switch状态 再决定返回那个View
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView;
        if (userView) {
            itemView = layoutInflater.inflate(R.layout.cell_card, parent, false);
        } else {
            itemView = layoutInflater.inflate(R.layout.cell_normal, parent, false);
        }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final Words words = allwords.get(position);
        holder.textViewid.setText(String.valueOf(position + 1));
        holder.textViewEnglish.setText(words.getEnglishWords());
        holder.textViewChinese.setText(words.getChineseMeaning());

        //switch
        holder.aSwitchChineseInvisiable.setOnCheckedChangeListener(null);
        if (words.isChineseInvisiable()) {
            holder.textViewChinese.setVisibility(View.GONE);
            holder.aSwitchChineseInvisiable.setChecked(true);
        } else {
            holder.textViewChinese.setVisibility(View.VISIBLE);
            holder.aSwitchChineseInvisiable.setChecked(false);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("http://m.youdao.com/dict?le=eng&q=" + holder.textViewEnglish.getText());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                holder.itemView.getContext().startActivity(intent);
            }
        });
        //隐藏中文
        holder.aSwitchChineseInvisiable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    holder.textViewChinese.setVisibility(View.GONE);
                    words.setChineseInvisiable(true);
                    wordsViewModel.updateWord(words);
                } else {
                    holder.textViewChinese.setVisibility(View.VISIBLE);
                    words.setChineseInvisiable(false);
                    wordsViewModel.updateWord(words);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return allwords.size();
    }
}
