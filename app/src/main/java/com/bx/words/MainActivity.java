package com.bx.words;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button mInsertButton,mDeleteButton;
    private Switch aSwitch;

    WordsViewModel wordsViewModel;
    RecyclerView recyclerView;
    WordsAdapter wordsAdapter1,wordsAdapter2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInsertButton=findViewById(R.id.button_insert);
        mDeleteButton=findViewById(R.id.button_delete);
        aSwitch=findViewById(R.id.switchstyle);
        recyclerView=findViewById(R.id.recyler_view);

        wordsViewModel= ViewModelProviders.of(this).get(WordsViewModel.class);
        wordsAdapter1=new WordsAdapter(false,wordsViewModel);
        wordsAdapter2=new WordsAdapter(true,wordsViewModel);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(wordsAdapter1);

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    recyclerView.setAdapter(wordsAdapter2);
                    aSwitch.setText("Card模式");
                }else {

                    recyclerView.setAdapter(wordsAdapter1);
                    aSwitch.setText("normal模式");
                }
            }
        });

        wordsViewModel.getAllWords().observe(this, new Observer<List<Words>>() {
            @Override
            public void onChanged(List<Words> words) {
                int temp=wordsAdapter1.getItemCount();
                wordsAdapter1.setAllwords(words);
                wordsAdapter2.setAllwords(words);

                if(temp!=words.size()){
                    wordsAdapter1.notifyDataSetChanged();
                    wordsAdapter2.notifyDataSetChanged();
                }
            }
        });

        mInsertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] English = {
                        "one", "tow", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen", "fifteen"
                };
                String[] Chiness = {
                        "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "十一", "十二", "十三", "十四", "十五"
                };
                for(int i=0;i<English.length;i++){
                    wordsViewModel.insertWord(new Words(English[i],Chiness[i]));
                }
            }
        });
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wordsViewModel.deleteAllWord();
            }
        });
    }
}
