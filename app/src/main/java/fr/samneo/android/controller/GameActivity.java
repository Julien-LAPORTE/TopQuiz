package fr.samneo.android.controller;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.Arrays;


import fr.samneo.android.databinding.ActivityGameBinding;
import icepick.Icepick;
import icepick.State;

import fr.samneo.android.model.Question;
import fr.samneo.android.model.QuestionBank;
import fr.samneo.android.model.User;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String BUNDLE_EXTRA_SCORE = "BUNDLE_EXTRA_SCORE";
    private ActivityGameBinding m_binding;
    private int m_answerIndex;
    @State int m_remainingQuestionCount = 3;
    @State(User.class) User m_user;
    @State(QuestionBank.class) QuestionBank m_questionBank;
    @State boolean m_enableTouchEvents = true;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return m_enableTouchEvents && super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void onClick(View v) {
        showToastAnswer(v);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);

        if (m_user == null) m_user = new User();
        if (m_questionBank == null) m_questionBank = generateQuestionBank();
        if (m_remainingQuestionCount <= 0) endGame();

        m_binding = ActivityGameBinding.inflate(getLayoutInflater());
        View view = m_binding.getRoot();
        setContentView(view);

        m_binding.gameButton0.setOnClickListener(this);
        m_binding.gameButton1.setOnClickListener(this);
        m_binding.gameButton2.setOnClickListener(this);
        m_binding.gameButton3.setOnClickListener(this);

        displayQuestion(m_questionBank.getCurrentQuestion());
    }

    private QuestionBank generateQuestionBank() {
        Question question1 = new Question("Qui est le créateur d'Android?",
                Arrays.asList(
                        "Andy Rubin",
                        "Steve Wozniak",
                        "Jake Wharton",
                        "Paul Smith"),
                0
        );
        Question question2 = new Question("Quand le premier homme a marché sur la lune?",
                Arrays.asList(
                        "1958",
                        "1962",
                        "1967",
                        "1969"),
                3
        );
        Question question3 = new Question("Quel est le numéro de la maison des simpsons?",
                Arrays.asList(
                        "42",
                        "101",
                        "666",
                        "742"),
                3
        );
        return new QuestionBank(Arrays.asList(question1, question2, question3));
    }

    private void displayQuestion(final Question question) {
        m_binding.gameTextviewQuestion.setText(question.getQuestion());

        m_binding.gameButton0.setText(question.getChoiceList().get(0));
        m_binding.gameButton1.setText(question.getChoiceList().get(1));
        m_binding.gameButton2.setText(question.getChoiceList().get(2));
        m_binding.gameButton3.setText(question.getChoiceList().get(3));

        m_answerIndex = question.getAnswerIndex();
    }

    private void pause(int timeMilliSeconds) {
        try {
            Thread.sleep(timeMilliSeconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        getNextQuestion();
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getNextQuestion();
                //dispatchTouchEvent();
            }
        }, timeMilliSeconds);*/
    }

    private boolean isGoodAnswer(View v) {
        int index;
        if (v == m_binding.gameButton0) {
            index = 0;
        } else if (v == m_binding.gameButton1) {
            index = 1;
        } else if (v == m_binding.gameButton2) {
            index = 2;
        } else if (v == m_binding.gameButton3) {
            index = 3;
        } else {
            throw new IllegalStateException("Unknow clicked view : " + v);
        }
        m_remainingQuestionCount--;
        return index == m_answerIndex;
    }

    private void showToastAnswer(View v) {
        Context context = getApplicationContext();
        CharSequence text;
        int duration = Toast.LENGTH_SHORT;

        if (isGoodAnswer(v)) {
            text = "Bonne réponse";
            m_user.setScore(+1);
        } else {
            text = "Mauvaise réponse";
        }
        m_enableTouchEvents = false;
        Toast.makeText(context, text, duration).show();
        pause(2500);
    }

    private void getNextQuestion() {
        if (!m_questionBank.isEndQuestionList() && m_remainingQuestionCount > 0) {
            displayQuestion(m_questionBank.getNextQuestion());
            m_enableTouchEvents = true;
            recreate();
        } else {
            endGame();
        }
    }

    private void endGame() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        builder.setCancelable(false);
        builder.setTitle("C'est terminé!")
                .setMessage("Votre score est " + m_user.getScore())
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.putExtra(BUNDLE_EXTRA_SCORE, m_user.getScore());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                })
                .create()
                .show();
    }

}
