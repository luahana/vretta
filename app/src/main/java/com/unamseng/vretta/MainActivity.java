package com.unamseng.vretta;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    TextView tvGameOver;
    ArrayList<QAClass> QAList;
    Random rand;
    QAClass QAobject;
    int count = 0;
    int bestCount = 0;
    TextView tvCounter;
    RelativeLayout rl;
    String colorArray[] = {"#FF33B5E5", "#FFAA66CC", "#FF99CC00", "#FFFFBB33", "#FFFF4444", "#FF0099CC"};
    TextSwitcher mSwitcher;
    Animation shake;
    Animation slide_in_down;
    LinearLayout llgameover;
    Button buttonYES;
    Button buttonNO;
    Button buttonReplay;
    ProgressBar progressBar;

    MyCountDownTimer myCountDownTimer;

    void changeBackgroundColor() {
        Random i = new Random();
        int c = i.nextInt(6-1) + 1;
        rl = (RelativeLayout) findViewById(R.id.background);
        rl.setBackgroundColor(Color.parseColor(colorArray[c]));
    }

    void loadData() {
        QAList = new ArrayList<QAClass>();
        QAList.add(new QAClass(3,1,5,false));
        QAList.add(new QAClass(3,2,5,true));
        QAList.add(new QAClass(3,1,8,false));
        QAList.add(new QAClass(1,1,2,true));
        QAList.add(new QAClass(2,2,5,false));
        QAList.add(new QAClass(3,3,7,false));
        QAList.add(new QAClass(1,3,4,true));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadData();
        changeBackgroundColor();

        tvGameOver = (TextView)findViewById(R.id.tvGameOver);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setScaleY(3f);
        llgameover = (LinearLayout)findViewById(R.id.llgameover);
        tvCounter = (TextView) findViewById(R.id.textviewCounter);
        tvCounter.setText("0");

        //Animation
        shake = AnimationUtils.loadAnimation(this, R.anim.shakeanim);
        slide_in_down = AnimationUtils.loadAnimation(this, R.anim.slide_in_down);

        rand = new Random();
        int index = rand.nextInt(QAList.size());
        QAobject = QAList.get(index);

        mSwitcher = (TextSwitcher)findViewById(R.id.textSwitcher);
        mSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView myText = new TextView(MainActivity.this);
                myText.setGravity(Gravity.CENTER);
                myText.setTextSize(60);
                myText.setTextColor(Color.WHITE);
                return myText;
            }
        });

        Animation in = AnimationUtils.loadAnimation(this, R.anim.slide_in_right);
        Animation out = AnimationUtils.loadAnimation(this, R.anim.slide_out_left);

        mSwitcher.setInAnimation(in);
        mSwitcher.setOutAnimation(out);
        mSwitcher.setCurrentText(QAobject.first + "+" + QAobject.second+"\n"+"=" + QAobject.third);


        progressBar.setProgress(30);
        progressBar.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        myCountDownTimer = new MyCountDownTimer(3000, 1);


        buttonReplay = (Button) findViewById(R.id.buttonReplay);
        buttonReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonNO.setEnabled(true);
                buttonYES.setEnabled(true);
                llgameover.setVisibility(View.GONE);
                changeBackgroundColor();
                tvCounter.setText("0");
                count = 0;

                //rand = new Random();
                int index = rand.nextInt(QAList.size());
                QAobject = QAList.get(index);
                mSwitcher.setCurrentText(QAobject.first + "+" + QAobject.second+"\n"+"=" + QAobject.third);
                progressBar.setProgress(30);
            }
        });

        buttonYES = (Button) findViewById(R.id.buttonYES);
        buttonYES.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (QAobject.check) {
                    // show next question
                    int index = rand.nextInt(QAList.size());
                    QAobject = QAList.get(index);
                    mSwitcher.setText(QAobject.first + "+" + QAobject.second+"\n"+"=" + QAobject.third);
                    count++;
                    tvCounter.setText(String.valueOf(count));

                    myCountDownTimer.cancel();
                    myCountDownTimer.start();

                } else {
                    //end the quiz
                    if (count > bestCount) bestCount = count;
                    myCountDownTimer.cancel();
                    gameOver("Game Out");
                }
            }
        });

        buttonNO = (Button) findViewById(R.id.buttonNO);
        buttonNO.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!QAobject.check) {
                    // show next question
                    int index = rand.nextInt(QAList.size());
                    QAobject = QAList.get(index);
                    mSwitcher.setText(QAobject.first + "+" + QAobject.second+"\n"+"=" + QAobject.third);
                    count++;
                    tvCounter.setText(String.valueOf(count));

                    myCountDownTimer.cancel();
                    myCountDownTimer.start();
                } else {
                    //end the quiz
                    if (count > bestCount) bestCount = count;
                    myCountDownTimer.cancel();
                    gameOver("Game Out");
                }
            }
        });
    }

    void gameOver(String over) {
        tvGameOver.setText(over + "\n New " + count + "\n Best " + bestCount);
        findViewById(R.id.background).startAnimation(shake);
        llgameover.setVisibility(View.VISIBLE);
        llgameover.startAnimation(slide_in_down);
        llgameover.bringToFront();
        buttonYES.setEnabled(false);
        buttonNO.setEnabled(false);
    }

    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            //textCounter.setText(String.valueOf(millisUntilFinished));
            int progress = (int) (millisUntilFinished/100);
            progressBar.setProgress(progress);
        }

        @Override
        public void onFinish() {
            //textCounter.setText("Task completed");
            gameOver("Time Out");
            progressBar.setProgress(0);
        }
    }

}
