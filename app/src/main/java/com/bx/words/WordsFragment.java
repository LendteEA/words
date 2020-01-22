package com.bx.words;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class WordsFragment extends Fragment {
    private WordsViewModel wordsViewModel;
    private WordsAdapter wordsAdapter1, wordsAdapter2;
    private RecyclerView recyclerView;
    private LiveData<List<Words>> seachWords;
    private static final String VIEW_TYPE_SHP = "view_type_shp";
    private static final String IS_USING_CARD_VIEW = "is_using_card_view";
    private List<Words> allWords;
    private boolean undoAction;
    private DividerItemDecoration dividerItemDecoration;


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
                SharedPreferences shp = requireActivity().getSharedPreferences(VIEW_TYPE_SHP, Context.MODE_PRIVATE);
                boolean viewType = shp.getBoolean(IS_USING_CARD_VIEW, false);
                SharedPreferences.Editor editor = shp.edit();

                if (viewType) {
                    recyclerView.setAdapter(wordsAdapter1);
                    recyclerView.addItemDecoration(dividerItemDecoration);
                    editor.putBoolean(IS_USING_CARD_VIEW, false);
                } else {
                    recyclerView.setAdapter(wordsAdapter2);
                    recyclerView.removeItemDecoration(dividerItemDecoration);
                    editor.putBoolean(IS_USING_CARD_VIEW, true);
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
                seachWords.removeObservers(getViewLifecycleOwner());      //将初始化的观察者移除，再进行下面新的观察 防止出现碰撞
                seachWords = wordsViewModel.findWordsWithPatten(patten);  //模糊查询
                seachWords.observe(getViewLifecycleOwner(), new Observer<List<Words>>() {
                    @Override
                    public void onChanged(List<Words> words) {
                        int temp = wordsAdapter1.getItemCount();
                        allWords = words;
                        if (temp != words.size()) {
//                             //从第0号位插入
//                            wordsAdapter1.notifyItemInserted(0);
//                            wordsAdapter2.notifyItemInserted(0);

                            wordsAdapter1.submitList(words);
                            wordsAdapter2.submitList(words);
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
        //防止序号不自动改变 添加在动画结束后刷新--屏幕显示--的序列号
        recyclerView.setItemAnimator(new DefaultItemAnimator() {
            @Override
            public void onAnimationFinished(@NonNull RecyclerView.ViewHolder viewHolder) {
                super.onAnimationFinished(viewHolder);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (linearLayoutManager != null) {
                    int firstPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int lastPosition = linearLayoutManager.findLastVisibleItemPosition();
                    for (int i = firstPosition; i <= lastPosition; i++) {
                        WordsAdapter.MyViewHolder holder = (WordsAdapter.MyViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
                        if (holder != null) {
                            holder.textViewid.setText(String.valueOf(i + 1));
                        }

                    }
                }

            }
        });
        SharedPreferences shp = requireActivity().getSharedPreferences(VIEW_TYPE_SHP, Context.MODE_PRIVATE);
        boolean viewType = shp.getBoolean(IS_USING_CARD_VIEW, false);
        //滑动删除边界初始化
        dividerItemDecoration=new DividerItemDecoration(requireActivity(),DividerItemDecoration.VERTICAL);
        if (viewType) {
            recyclerView.setAdapter(wordsAdapter2);
        } else {
            recyclerView.setAdapter(wordsAdapter1);
            recyclerView.addItemDecoration(dividerItemDecoration);
        }

        //搜索器初始化 不过滤搜索所有
        seachWords = wordsViewModel.getAllWords();
        seachWords.observe(getViewLifecycleOwner(), new Observer<List<Words>>() {
            @Override
            public void onChanged(List<Words> words) {
                int temp = wordsAdapter1.getItemCount();
                allWords = words;
                if (temp != words.size()) {
//                    //从第0号位插入
//                   wordsAdapter1.notifyItemInserted(0);
//                   wordsAdapter2.notifyItemInserted(0);
                    if (temp < words.size() && !undoAction) {
                        recyclerView.smoothScrollBy(0, -200);
                    }
                    wordsAdapter1.submitList(words);
                    wordsAdapter2.submitList(words);
                    undoAction = false;
                }
            }
        });

        //滑动删除                                                                      拖动设置                                               滑动设置
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP|ItemTouchHelper.DOWN, ItemTouchHelper.START | ItemTouchHelper.END) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                Words wordsFrom=allWords.get(viewHolder.getAdapterPosition());
//                Words wordsTo=allWords.get(target.getAdapterPosition());
//                //交换id
//                int idTemp=wordsFrom.getId();
//                wordsFrom.setId(wordsTo.getId());
//                wordsTo.setId(idTemp);
//                wordsViewModel.updateWord(wordsFrom,wordsTo);
//                //视图位置交换
//                wordsAdapter1.notifyItemMoved(viewHolder.getAdapterPosition(),target.getAdapterPosition());
//                wordsAdapter2.notifyItemMoved(viewHolder.getAdapterPosition(),target.getAdapterPosition());
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final Words wordsToDelete = allWords.get(viewHolder.getAdapterPosition());
                wordsViewModel.deleteWord(wordsToDelete);
                //撤销键
                Snackbar.make(requireActivity().findViewById(R.id.wordsFragment), "删除词汇", Snackbar.LENGTH_LONG)
                        .setAction("撤销", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                undoAction = true;
                                wordsViewModel.insertWord(wordsToDelete);
                            }
                        })
                        .show();
            }
            Drawable icon= ContextCompat.getDrawable(requireActivity(),R.drawable.ic_delete_forever_black_24dp);
            Drawable background=new ColorDrawable(Color.rgb(255,0,0));

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView=viewHolder.itemView;
                int iconMargin=(itemView.getHeight()-icon.getIntrinsicHeight())/2;

                int iconLeft,iconRight,iconTop,iconBottom;
                int backTop,backBottom,backLeft,backRight;
                backTop=itemView.getTop();
                backBottom=itemView.getBottom();
                iconTop=itemView.getTop()+(itemView.getHeight()-icon.getIntrinsicHeight())/2;
                iconBottom=iconTop+icon.getIntrinsicHeight();
                if(dX>0){
                    backLeft=itemView.getLeft();
                    backRight=itemView.getLeft()+(int)dX;
                    background.setBounds(backLeft,backTop,backRight,backBottom);
                    iconLeft=itemView.getLeft()+iconMargin;
                    iconRight=iconLeft+icon.getIntrinsicWidth();
                    icon.setBounds(iconLeft,iconTop,iconRight,iconBottom);
                }else if(dX<0){
                    backLeft=itemView.getRight();
                    backRight=itemView.getRight()+(int)dX;
                    background.setBounds(backLeft,backTop,backRight,backBottom);
                    iconRight=itemView.getRight()-iconMargin;
                    iconLeft=iconRight-icon.getIntrinsicWidth();
                    icon.setBounds(iconLeft,iconTop,iconRight,iconBottom);

                }else {
                    background.setBounds(0,0,0,0);
                    icon.setBounds(0,0,0,0);
                }
                background.draw(c);
                icon.draw(c);
            }
        }).attachToRecyclerView(recyclerView);

        //初始化悬浮按钮
        FloatingActionButton floatingActionButton = requireActivity().findViewById(R.id.floatingActionButton);
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
        assert imm != null;
        imm.hideSoftInputFromWindow(Objects.requireNonNull(getView()).getWindowToken(), 0);
        super.onResume();
    }
}
