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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

import fr.samneo.android.R;

import icepick.Icepick;
import icepick.State;

import fr.samneo.android.model.Question;
import fr.samneo.android.model.QuestionBank;
import fr.samneo.android.model.User;

public class GameActivity extends AppCompatActivity {
    public static final String BUNDLE_EXTRA_SCORE = "BUNDLE_EXTRA_SCORE";

    private TextView m_questionTextView;
    private Button[] m_arrayButtons = new Button[4];
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);

        if (m_user == null) m_user = new User();
        if (m_questionBank == null) m_questionBank = generateQuestionBank();
        if (m_remainingQuestionCount <= 0) endGame();

        setContentView(R.layout.activity_game);
        m_questionTextView = findViewById(R.id.game_textview_question);
        m_arrayButtons = new Button[4];
        //Un boucle qui initialise les 4 boutons du layout dans le tableau
        int temp;
        for (int i = 0; i < 4; i++) {
            temp = getResources().getIdentifier("game_button_" + i, "id", getPackageName());
            m_arrayButtons[i] = (Button) findViewById(temp);

            m_arrayButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showToastAnswer(v);

                }
            });
        }
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
        m_questionTextView.setText(question.getQuestion());

        for (int i = 0; i < 4; i++) {
            m_arrayButtons[i].setText(question.getChoiceList().get(i));
        }
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
        int index = 4;
        if (v == m_arrayButtons[0]) {
            index = 0;
        } else if (v == m_arrayButtons[1]) {
            index = 1;
        } else if (v == m_arrayButtons[2]) {
            index = 2;
        } else if (v == m_arrayButtons[3]) {
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
            m_user.setScore(m_user.getScore() + 1);
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
