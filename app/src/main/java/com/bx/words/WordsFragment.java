package com.bx.words;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class WordsFragment extends Fragment {
    private WordsViewModel wordsViewModel;
    private WordsAdapter wordsAdapter1, wordsAdapter2;
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private LiveData<List<Words>> seachWords;
    private static final String VIEW_TYPE_SHP="view_type_shp";
    private static final String IS_USING_CARD_VIEW ="is_using_card_view";


    public WordsFragment() {
        // Required empty public constructor
        //显示菜单选项
        setHasOptionsMenu(true);
    }

    //实现菜单功能
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cleardata:
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle("清空数据");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        wordsViewModel.deleteAllWord();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create();
                builder.show();
                break;
            case R.id.switchViewType:
                //用户选择View模式 并将使用习惯记录下来
                SharedPreferences shp=requireActivity().getSharedPreferences(VIEW_TYPE_SHP,Context.MODE_PRIVATE);
                boolean viewType = shp.getBoolean(IS_USING_CARD_VIEW,false);
                SharedPreferences.Editor editor=shp.edit();

                if (viewType) {
                    recyclerView.setAdapter(wordsAdapter1);
                    editor.putBoolean(IS_USING_CARD_VIEW,false);
                } else {
                    recyclerView.setAdapter(wordsAdapter2);
                    editor.putBoolean(IS_USING_CARD_VIEW,true);
                }
                editor.apply();         //储存模式
        }
        return super.onOptionsItemSelected(item);
    }

    //创建菜单选项 并实现搜索功能
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        //设置搜索条长度
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setMaxWidth(700);
        //搜索条监听
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //模糊查询
                String patten = newText.trim();   //去除空格
                seachWords.removeObservers(requireActivity());      //将初始化的观察者移除，再进行下面新的观察 防止出现碰撞
                seachWords = wordsViewModel.findWordsWithPatten(patten);  //模糊查询
                seachWords.observe(requireActivity(), new Observer<List<Words>>() {
                    @Override
                    public void onChanged(List<Words> words) {
                        int temp = wordsAdapter1.getItemCount();
                        wordsAdapter1.setAllwords(words);
                        wordsAdapter2.setAllwords(words);
                        if (temp != words.size()) {
                            wordsAdapter1.notifyDataSetChanged();
                            wordsAdapter2.notifyDataSetChanged();
                        }
                    }
                });
                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_words, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        wordsViewModel = ViewModelProviders.of(requireActivity()).get(WordsViewModel.class);
        recyclerView = requireActivity().findViewById(R.id.recycleView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        wordsAdapter1 = new WordsAdapter(false, wordsViewModel);
        wordsAdapter2 = new WordsAdapter(true, wordsViewModel);
        SharedPreferences shp=requireActivity().getSharedPreferences(VIEW_TYPE_SHP,Context.MODE_PRIVATE);
       boolean viewType=shp.getBoolean(IS_USING_CARD_VIEW,false);
       if(viewType){
           recyclerView.setAdapter(wordsAdapter2);
       }else {
           recyclerView.setAdapter(wordsAdapter1);
       }

        //搜索器初始化 不过滤搜索所有
        seachWords = wordsViewModel.getAllWords();
        seachWords.observe(requireActivity(), new Observer<List<Words>>() {
            @Override
            public void onChanged(List<Words> words) {
                int temp = wordsAdapter1.getItemCount();
                wordsAdapter1.setAllwords(words);
                wordsAdapter2.setAllwords(words);
                if (temp != words.size()) {
                    wordsAdapter1.notifyDataSetChanged();
                    wordsAdapter2.notifyDataSetChanged();
                }
            }
        });
        //初始化悬浮按钮
        floatingActionButton = requireActivity().findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_wordsFragment_to_wordsAddFragment);
            }
        });

    }

    @Override
    public void onResume() {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        super.onResume();
    }
}
