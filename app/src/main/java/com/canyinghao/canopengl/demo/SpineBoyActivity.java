package com.canyinghao.canopengl.demo;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AppActivity;
import com.canyinghao.canadapter.CanHolderHelper;
import com.canyinghao.canadapter.CanOnItemListener;
import com.canyinghao.canadapter.CanRVAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yangjian on 16/2/5.
 */
public class SpineBoyActivity extends AppActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.frame)
    FrameLayout frame;


    SpineBoy boy;

    View boyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);



        toolbar.setTitle(getString(R.string.app_name));

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

                startActivity(new Intent(SpineBoyActivity.this, EmptyActivity.class));


            }

        });


        addBoy();


    }


    @OnClick({R.id.fab})
    public void click() {

        boy.jump();


    }


    float offSetX;
    float offSetY;

    long startTime;


    int tag = 0;
    int oldOffsetX;
    int oldOffsetY;


    public void addBoy() {
        // 设置载入view WindowManager参数


        final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(dp2Px(80), dp2Px(140));


        frame.addView(boyView, params);


        boyView.setOnTouchListener(new View.OnTouchListener() {
            // 触屏监听
            float lastX, lastY;

            public boolean onTouch(View v, MotionEvent e) {


                switch (e.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        startTime = System.currentTimeMillis();
                        lastY = e.getY();
                        lastX = e.getX();

                        if (tag == 0) {
                            oldOffsetX = boyView.getLeft(); // 偏移量
                            oldOffsetY = boyView.getTop(); // 偏移量
                        }

                        break;

                    case MotionEvent.ACTION_MOVE:
                        tag = 1;
                        float tempX = e.getX();
                        float tempY = e.getY();

                        float tempOffSetX = tempX - lastX;
                        float tempOffSetY = tempY - lastY;

                        offSetX += tempOffSetX;
                        offSetY += tempOffSetY;

                        lastX = tempX;
                        lastY = tempY;

                        boyView.layout((int) (boyView.getLeft() + offSetX), (int) (boyView.getTop() + offSetY), (int) (boyView.getRight() + offSetX), (int) (boyView.getBottom() + offSetY));

                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:

                        int newOffsetX = boyView.getLeft();
                        int newOffsetY = boyView.getTop();

                        long endTime = System.currentTimeMillis();

//                        if (oldOffsetX == newOffsetX && oldOffsetY == newOffsetY) {
                        if ((Math.abs(oldOffsetX - newOffsetX) + Math.abs(oldOffsetY - newOffsetY)) < 10) {

                            if (endTime - startTime > 500) {

                                if (boyView.getTag() == null) {
                                    boy.zoomBig();

                                    boyView.setTag("");

                                    params.width = dp2Px(160);
                                    params.height = dp2Px(280);

                                    boyView.setLayoutParams(params);

                                } else {
                                    boy.zoomSmall();
                                    boyView.setTag(null);

                                    params.width = dp2Px(80);
                                    params.height = dp2Px(140);

                                    boyView.setLayoutParams(params);
                                }

                            } else {
                                boy.jump();
                            }
                        } else {
                            tag = 0;
                            boy.jump();
                        }
                        break;


                }


                return true;
            }
        });


    }


    public int dp2Px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
