package fr.samneo.android.controller;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.samneo.android.R;
import fr.samneo.android.model.Question;
import fr.samneo.android.model.QuestionBank;
import fr.samneo.android.model.User;
import icepick.Icepick;
import icepick.State;

public class GameActivity extends AppCompatActivity {
    public static final String BUNDLE_EXTRA_SCORE = "BUNDLE_EXTRA_SCORE";
    public static final String BUNDLE_STATE_REMAINING_QUESTION_COUNT = "BUNDLE_STATE_REMAINING_QUESTION_COUNT";

    private TextView m_questionTextView;
    private Button[] m_arrayButtons = new Button[4];
    private int m_answerIndex;
    @State int m_remainingQuestionCount = 3;
    @State(User.class) User m_user;
    @State(QuestionBank.class) QuestionBank m_questionBank;
    private boolean m_enableTouchEvents = true;

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
        if (m_user == null) m_user = new User();
        if (m_questionBank == null) m_questionBank = generateQuestionBank();

        Icepick.restoreInstanceState(this, savedInstanceState);
        super.onCreate(savedInstanceState);
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

    //TODO : Passer la génération des question à la main activity afin de faire passer les questions en EXTRA à la game activity (pour ne pas les perdres sur destruction de la gameActivity)
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

    private QuestionBank restoreQuestionBank(String[] listQuestions) {

        List<Question> instanceOfQuestions = new ArrayList<Question>();
        String question = null;
        List<String> choiceList = new ArrayList<>();
        int answerIndex = 0;
        for (int i = 0; i < listQuestions.length; i++) {
            if (i % 6 == 0) {
                question = listQuestions[i];
            } else if (i == 5 || i % 6 == 5) {
                answerIndex = Integer.valueOf(listQuestions[i]);
            } else {
                choiceList.add(listQuestions[i]);
            }
            Question instanceQuestion = new Question(question, choiceList, answerIndex);
            instanceOfQuestions.add(instanceQuestion);
        }
        return new QuestionBank(instanceOfQuestions);
    }

    private void displayQuestion(final Question question) {
        m_questionTextView.setText(question.getQuestion());

        for (int i = 0; i < 4; i++) {
            m_arrayButtons[i].setText(question.getChoiceList().get(i));
        }
        m_answerIndex = question.getAnswerIndex();
    }

    //TODO : Fuite mémoire à corriger sur cette pause
    private void pause(int timeMilliSeconds) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getNextQuestion();
                //dispatchTouchEvent();
            }
        }, timeMilliSeconds);
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
        } else {
            endGame();
        }
        m_enableTouchEvents = true;
    }

    private void endGame() {
        //TODO : Bug si on clique en dehors de la fenêtre AlertDialog sans faire OK
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
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
