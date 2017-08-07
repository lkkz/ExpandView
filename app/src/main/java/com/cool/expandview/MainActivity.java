package com.cool.expandview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.cool.expandviewlibrary.ExpandView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Toolbar.OnMenuItemClickListener {

    private Toolbar mToolbar;
    private ImageView mIvSearchBack;
    private RecyclerView mRecycleview;
    private List<String> mDatas = new ArrayList<>();
    private ExpandView expandView;
    private ExpandView mImgExpandView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        initToolBar();
        initListener();
    }

    private void initToolBar() {
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mIvSearchBack = (ImageView) findViewById(R.id.iv_search_back);
        mRecycleview = (RecyclerView) findViewById(R.id.recycleview);
        expandView = (ExpandView) findViewById(R.id.expandview);
        mImgExpandView = (ExpandView) findViewById(R.id.ev_img);

        MyAdapter myAdapter = new MyAdapter(this, mDatas);
        mRecycleview.setLayoutManager(new LinearLayoutManager(this));
        mRecycleview.setAdapter(myAdapter);
    }

    private void initData() {
        mDatas.add("优酷");
        mDatas.add("土豆");
        mDatas.add("爱奇艺");
        mDatas.add("哔哩哔哩");
        mDatas.add("youtube");
        mDatas.add("斗鱼");
    }

    private void initListener() {
        mIvSearchBack.setOnClickListener(this);
        mToolbar.setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                expandView.doExpandAnim();
                break;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_search_back:
                    expandView.doPackupAnim();
                break;
        }
    }

    public void viewExpand(View view){
        mImgExpandView.doExpandAnim();
    }

    public void viewPickup(View view){
        mImgExpandView.doPackupAnim();
    }

}
