package com.canyinghao.canopengl.demo;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AppActivity;
import com.canyinghao.canadapter.CanHolderHelper;
import com.canyinghao.canadapter.CanOnItemListener;
import com.canyinghao.canadapter.CanRVAdapter;
import com.canyinghao.canopengl.demo.gl.cube.BouncyCubeActivity;
import com.canyinghao.canopengl.demo.gl.sixstar.SixPointerStarActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.frame)
    FrameLayout frame;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    SpineBoy boy;

    View boyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        toolbar.setTitle(R.string.app_name);

        setSupportActionBar(toolbar);

        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.r = cfg.g = cfg.b = cfg.a = 8;


        boy = new SpineBoy();
        boyView = initializeForView(boy, cfg);

        if (boyView instanceof SurfaceView) {
            SurfaceView glView = (SurfaceView) boyView;
            glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
            glView.setZOrderOnTop(true);
        }


        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        final CanRVAdapter adapter = new CanRVAdapter<String>(rv, R.layout.item_main) {


            @Override
            protected void setView(CanHolderHelper helper, int position, String model) {


            }

            @Override
            protected void setItemListener(CanHolderHelper helper) {


            }
        };

        rv.setLayoutManager(mLayoutManager);
        rv.setAdapter(adapter);

        for (int i = 0; i < 50; i++) {
            adapter.addLastItem("");
        }


        adapter.setOnItemListener(new CanOnItemListener() {

            public void onRVItemClick(ViewGroup parent, View itemView, int position) {

                startActivity(new Intent(MainActivity.this, SpineBoyActivity.class));


            }

        });


        addBoy();

    }


    @OnClick(R.id.fab)
    public void click(View v) {


        boy.jump();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.item1:
                startActivity(new Intent(this, SixPointerStarActivity.class));
                return true;

            case R.id.item2:
                startActivity(new Intent(this, BouncyCubeActivity.class));
                return true;

        }


        return super.onOptionsItemSelected(item);
    }


    long startTime;


    int tag = 0;
    int oldOffsetX;
    int oldOffsetY;


    public void addBoy() {
        // 设置载入view WindowManager参数
        final WindowManager mWM = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        final WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();


        boyView.setOnTouchListener(new View.OnTouchListener() {
            // 触屏监听
            float lastX, lastY;

            public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();

                float x = event.getX();
                float y = event.getY();

                if (tag == 0) {
                    oldOffsetX = wmParams.x; // 偏移量
                    oldOffsetY = wmParams.y; // 偏移量
                }


                if (action == MotionEvent.ACTION_DOWN) {
                    lastX = x;
                    lastY = y;

                    startTime = System.currentTimeMillis();

                } else if (action == MotionEvent.ACTION_MOVE) {
                    wmParams.x += (int) (x - lastX); // 偏移量
                    wmParams.y += (int) (y - lastY); // 偏移量

                    tag = 1;
                    mWM.updateViewLayout(boyView, wmParams);
                } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    int newOffsetX = wmParams.x;
                    int newOffsetY = wmParams.y;

                    long endTime = System.currentTimeMillis();

//                    if (oldOffsetX == newOffsetX && oldOffsetY == newOffsetY) {
                    if ((Math.abs(oldOffsetX - newOffsetX) + Math.abs(oldOffsetY - newOffsetY)) < 10) {

                        if (endTime - startTime > 500) {

                            if (boyView.getTag() == null) {
                                boy.zoomBig();

                                boyView.setTag("");

                                wmParams.width = dp2Px(160);
                                wmParams.height = dp2Px(280);

                                mWM.updateViewLayout(boyView, wmParams);
                            } else {
                                boy.zoomSmall();
                                boyView.setTag(null);

                                wmParams.width = dp2Px(80);
                                wmParams.height = dp2Px(140);

                                mWM.updateViewLayout(boyView, wmParams);
                            }

                        } else {
                            boy.jump();
                        }
                    } else {
                        tag = 0;
                        boy.jump();
                    }
                }
                return true;
            }
        });


        int type = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            type = WindowManager.LayoutParams.TYPE_TOAST;
        } else {
            type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        wmParams.type = type;


        // type是关键，这里的2002表示系统级窗口，你也可以试试2003。
        wmParams.flags = 40;// 这句设置桌面可控

        wmParams.width = dp2Px(80);
        wmParams.height = dp2Px(140);
        wmParams.format = -3; // 透明

        mWM.addView(boyView, wmParams);// 这句是重点 给WindowManager中丢入刚才设置的值


    }


    public int dp2Px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    @Override
    protected void onDestroy() {
        getWindowManager().removeView(boyView);
        super.onDestroy();
    }
}
