package rts.pptviewer;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

//package com.rts.pptviewer.AppCompactPPTViewer;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.olivephone.office.TempFileManager;
import com.olivephone.office.powerpoint.DocumentSession;
import com.olivephone.office.powerpoint.DocumentSessionBuilder;
import com.olivephone.office.powerpoint.DocumentSessionStatusListener;
import com.olivephone.office.powerpoint.IMessageProvider;
import com.olivephone.office.powerpoint.ISystemColorProvider;
import com.olivephone.office.powerpoint.android.AndroidMessageProvider;
import com.olivephone.office.powerpoint.android.AndroidSystemColorProvider;
import com.olivephone.office.powerpoint.android.AndroidTempFileStorageProvider;
import com.olivephone.office.powerpoint.view.PersentationView;
import com.olivephone.office.powerpoint.view.SlideShowNavigator;
import com.olivephone.office.powerpoint.view.SlideView;

import java.io.File;


public class AppCompactPPTViewer extends RelativeLayout implements DocumentSessionStatusListener, OnClickListener {
    ImageView next;
    ImageView prev;
    ImageView settings;

    ImageView zoomin;
    ImageView zoomout;
    ProgressBar pb;
    TextView tv;
    LayoutParams params;
    private DocumentSession session;
    PersentationView slide;
    String path;
    Context ctx;
    AppCompatActivity act;
    private SlideShowNavigator navitator;
    private int currentSlideNumber;
    private float zoomlevel = 20.0F;
    final int NEXT = 1231;
    final int PREV = 1232;
    final int SLIDE = 1233;
    final int SETTINGS = 1234;
    final int ZOOMIN = 1235;
    final int ZOOMOUT = 1236;
    int next_img = 0;
    int prev_img = 0;
    int settings_img = 0;
    int zoomin_img = 0;
    int zoomout_img = 0;
    SimpleOnGestureListener simpleOnGestureListener = new SimpleOnGestureListener() {
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (AppCompactPPTViewer.this.zoomlevel > 25.0F) {
                return false;
            } else {
                float sensitvity = (float)AppCompactPPTViewer.this.dpToPx(100);
                if (e1.getX() - e2.getX() > sensitvity) {
                    AppCompactPPTViewer.this.next();
                } else if (e2.getX() - e1.getX() > sensitvity) {
                    AppCompactPPTViewer.this.prev();
                }

                return true;
            }
        }
    };
    GestureDetector gestureDetector;

    public AppCompactPPTViewer setNext_img(int next_img) {
        this.next_img = next_img;
        return this;
    }

    public AppCompactPPTViewer setPrev_img(int prev_img) {
        this.prev_img = prev_img;
        return this;
    }

    public AppCompactPPTViewer setSettings_img(int settings_img) {
        this.settings_img = settings_img;
        return this;
    }

    public AppCompactPPTViewer setZoomin_img(int zoomin_img) {
        this.zoomin_img = zoomin_img;
        return this;
    }

    public AppCompactPPTViewer setZoomout_img(int zoomout_img) {
        this.zoomout_img = zoomout_img;
        return this;
    }

    public int getTotalSlides() {
        return this.session != null && this.session.getPPTContext() != null ? this.navitator.getSlideCount() : -1;
    }

    void log(Object log) {
        Log.d("rex", log.toString());
    }

    @SuppressLint("WrongConstant")
    void toast(Object msg) {
        Toast.makeText(this.act, msg.toString(), 0).show();
    }

    @SuppressLint("WrongConstant")
    public void toggleActions() {
        if (this.next.getVisibility() == 4) {
            this.next.setVisibility(0);
            this.prev.setVisibility(0);
            this.zoomout.setVisibility(0);
            this.zoomin.setVisibility(0);
            this.next.setAlpha(0.0F);
            this.prev.setAlpha(0.0F);
            this.zoomout.setAlpha(0.0F);
            this.zoomin.setAlpha(0.0F);
            this.next.animate().setDuration(500L).alpha(1.0F).setListener((AnimatorListener)null);
            this.prev.animate().setDuration(500L).alpha(1.0F).setListener((AnimatorListener)null);
            this.zoomout.animate().setDuration(500L).alpha(1.0F).setListener((AnimatorListener)null);
            this.zoomin.animate().setDuration(500L).alpha(1.0F).setListener((AnimatorListener)null);
        } else {
            this.next.setAlpha(1.0F);
            this.prev.setAlpha(1.0F);
            this.zoomout.setAlpha(1.0F);
            this.zoomin.setAlpha(1.0F);
            this.next.animate().setDuration(500L).alpha(0.0F).setListener(new AnimatorListenerAdapter() {
                @SuppressLint("WrongConstant")
                public void onAnimationEnd(Animator animation) {

                    AppCompactPPTViewer.this.next.setVisibility(4);
                }
            });
            this.prev.animate().setDuration(500L).alpha(0.0F).setListener(new AnimatorListenerAdapter() {
                @SuppressLint("WrongConstant")
                public void onAnimationEnd(Animator animation) {
                    AppCompactPPTViewer.this.prev.setVisibility(4);
                }
            });
            this.zoomout.animate().setDuration(500L).alpha(0.0F).setListener(new AnimatorListenerAdapter() {
                @SuppressLint("WrongConstant")
                public void onAnimationEnd(Animator animation) {
                    AppCompactPPTViewer.this.zoomout.setVisibility(4);
                }
            });
            this.zoomin.animate().setDuration(500L).alpha(0.0F).setListener(new AnimatorListenerAdapter() {
                @SuppressLint("WrongConstant")
                public void onAnimationEnd(Animator animation) {
                    AppCompactPPTViewer.this.zoomin.setVisibility(4);
                }
            });
        }

    }

    public void setPath(String path) {
        this.path = path;
    }

    public void loadPPT(AppCompatActivity act, String path) {
        this.setPath(path);
        this.loadPPT(act);
    }

    public void loadPPT(AppCompatActivity act) {
        this.act = act;
        this.next.setImageResource(this.next_img);
        this.prev.setImageResource(this.prev_img);
        this.settings.setImageResource(this.settings_img);
        this.zoomin.setImageResource(this.zoomin_img);
        this.zoomout.setImageResource(this.zoomout_img);

        try {
            IMessageProvider msgProvider = new AndroidMessageProvider(this.ctx);
            TempFileManager tmpFileManager = new TempFileManager(new AndroidTempFileStorageProvider(this.ctx));
            ISystemColorProvider sysColorProvider = new AndroidSystemColorProvider();
            this.session = (new DocumentSessionBuilder(new File(this.path))).setMessageProvider(msgProvider).setTempFileManager(tmpFileManager).setSystemColorProvider(sysColorProvider).setSessionStatusListener(this).build();
            this.session.startSession();
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }

    int dpToPx(int dp) {
        return (int)((float)dp * Resources.getSystem().getDisplayMetrics().density);
    }

    int pxToDp(int px) {
        return (int)((float)px / Resources.getSystem().getDisplayMetrics().density);
    }

    @SuppressLint({"WrongConstant", "ResourceType"})
    public AppCompactPPTViewer(Context ctx, AttributeSet attr) {
        super(ctx, attr);
        this.gestureDetector = new GestureDetector(this.ctx, this.simpleOnGestureListener);

        this.ctx = ctx;
        this.next = new ImageView(ctx);
        this.prev = new ImageView(ctx);
        this.settings = new ImageView(ctx);
        this.zoomin = new ImageView(ctx);
        this.zoomout = new ImageView(ctx);
        this.pb = new ProgressBar(ctx);
        this.tv = new TextView(ctx);
        this.slide = new PersentationView(ctx, attr);
        this.next.setVisibility(4);
        this.prev.setVisibility(4);
        this.slide.setVisibility(4);
        this.zoomout.setVisibility(4);
        this.zoomin.setVisibility(4);
        this.next.setId(1231);
        this.prev.setId(1232);
        this.settings.setId(1234);
        this.slide.setId(1233);
        this.zoomin.setId(1235);
        this.zoomout.setId(1236);
        this.next.setOnClickListener(this);
        this.prev.setOnClickListener(this);
        this.settings.setOnClickListener(this);
        this.slide.setOnClickListener(this);
        this.zoomin.setOnClickListener(this);
        this.zoomout.setOnClickListener(this);
        this.slide.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                AppCompactPPTViewer.this.gestureDetector.onTouchEvent(event);
                return false;
            }
        });
        this.params = new LayoutParams(-2, -2);
        this.params.addRule(13);
        this.addView(this.slide, this.params);
        this.params = new LayoutParams(this.dpToPx(50), this.dpToPx(50));
        this.params.addRule(10);
        this.params.addRule(11);
        this.settings.setImageResource(this.settings_img);
        this.addView(this.settings, this.params);
        this.params = new LayoutParams(this.dpToPx(80), this.dpToPx(80));
        this.params.addRule(15);
        this.params.addRule(11);
        this.next.setImageResource(this.next_img);
        this.addView(this.next, this.params);
        this.params = new LayoutParams(this.dpToPx(80), this.dpToPx(80));
        this.params.addRule(15);
        this.params.addRule(9);
        this.prev.setImageResource(this.prev_img);
        this.addView(this.prev, this.params);
        this.params = new LayoutParams(-2, -2);
        this.params.addRule(13);
        this.pb.setBackgroundColor(Color.parseColor("#80000000"));
        this.addView(this.pb, this.params);
        this.params = new LayoutParams(this.dpToPx(60), this.dpToPx(60));
        int m = this.dpToPx(10);
        this.params.setMargins(m, m, m, m);
        LinearLayout ll = new LinearLayout(ctx);
        ll.setOrientation(0);
        this.zoomout.setImageResource(this.zoomin_img);
        this.zoomin.setImageResource(this.zoomin_img);
        ll.addView(this.zoomout, this.params);
        ll.addView(this.zoomin, this.params);
        this.params = new LayoutParams(-2, -2);
        this.params.addRule(14);
        this.params.addRule(12);
        this.params.bottomMargin = this.dpToPx(30);
        this.addView(ll, this.params);
        this.params = new LayoutParams(-2, -2);
        this.params.addRule(0, 1234);
        this.params.topMargin = this.dpToPx(20);
        this.tv.setTextAppearance(ctx, 16974079);
        this.addView(this.tv, this.params);
    }

    public void onDocumentException(Exception arg0) {
    }

    public void onDocumentReady() {
        this.act.runOnUiThread(new Runnable() {
            @SuppressLint("WrongConstant")
            public void run() {
                AppCompactPPTViewer.this.navitator = new SlideShowNavigator(AppCompactPPTViewer.this.session.getPPTContext());
                AppCompactPPTViewer.this.currentSlideNumber = AppCompactPPTViewer.this.navitator.getFirstSlideNumber() - 1;
                AppCompactPPTViewer.this.next();
                AppCompactPPTViewer.this.pb.setVisibility(4);
                AppCompactPPTViewer.this.slide.setVisibility(0);
            }
        });
    }

    public void onSessionEnded() {
    }

    public void onSessionStarted() {
    }

    private void navigateTo(int slideNumber) {
        this.log(slideNumber);
        this.tv.setText(slideNumber + " / " + this.getTotalSlides());
        SlideView slideShow = this.navitator.navigateToSlide(this.slide.getGraphicsContext(), slideNumber);
        this.slide.setContentView(slideShow);
    }

    private void next() {
        if (this.navitator != null) {
            if (this.navitator.getFirstSlideNumber() + this.navitator.getSlideCount() - 1 > this.currentSlideNumber) {
              //  this.toast("Slide Change");
//Here all work of timer will happens;
                new Remainder();
                this.navigateTo(++this.currentSlideNumber);
            } else {
                this.toast("IT'S THE LAST SLIDE");
            }
        }

    }

    private void prev() {
        if (this.navitator != null) {
            if (this.navitator.getFirstSlideNumber() < this.currentSlideNumber) {
                this.navigateTo(--this.currentSlideNumber);
            } else {
                this.toast("IT'S THE FIRST SLIDE");
            }
        }

    }

    public void onClick(View v) {
        switch(v.getId()) {
            case 1231:
                this.next();
                break;
            case 1232:
                this.prev();
            case 1233:
            default:
                break;
            case 1234:
                this.toggleActions();
                break;
            case 1235:
                this.zoomlevel += 5.0F;
                this.slide.notifyScale((double)this.zoomlevel / 100.0D);
                break;
            case 1236:
                this.zoomlevel -= 5.0F;
                this.slide.notifyScale((double)this.zoomlevel / 100.0D);
        }

    }
}
