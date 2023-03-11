package fr.samneo.android.controller;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import fr.samneo.android.R;
import fr.samneo.android.model.User;

public class MainActivity extends AppCompatActivity {
    private TextView m_greetingTextView;
    private EditText m_nameEditText;
    private Button m_playButton;
    private User m_user;
    private static final int GAME_ACTIVITY_REQUEST_CODE = 42;
    private static final String SHARED_PREF_USER_INFO = "SHARED_PREF_USER_INFO";
    private static final String SHARED_PREF_USER_INFO_NAME = "SHARED_PREF_USER_INFO_NAME";
    private static final String SHARED_PREF_USER_INFO_SCORE = "SHARED_PREF_USER_INFO_SCORE";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (GAME_ACTIVITY_REQUEST_CODE == requestCode && RESULT_OK == resultCode) {
            m_user.setScore(data.getIntExtra(GameActivity.BUNDLE_EXTRA_SCORE, 0));
            getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE)
                    .edit()
                    .putString(SHARED_PREF_USER_INFO_NAME, m_user.getFirstName())
                    .putInt(SHARED_PREF_USER_INFO_SCORE, m_user.getScore())
                    .apply();
            recreate();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_greetingTextView = findViewById(R.id.main_textview_greeting);
        m_nameEditText = findViewById(R.id.main_edittext_name);
        m_playButton = findViewById(R.id.main_button_play);
        m_user = new User();
        SharedPreferences preferences = getSharedPreferences(SHARED_PREF_USER_INFO, MODE_PRIVATE);
        String tempFirstName = preferences.getString(SHARED_PREF_USER_INFO_NAME, null);
        int tempScore = preferences.getInt(SHARED_PREF_USER_INFO_SCORE, 0);
        if (tempFirstName != null) {
            m_nameEditText.setText(tempFirstName);
            m_nameEditText.setSelection(tempFirstName.length());
            m_greetingTextView
                    .setText("Bon retour " + tempFirstName +
                            "\n\nVotre score précédent était de : " + tempScore);
        } else {
            m_playButton.setEnabled(false);
        }

        m_nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                m_playButton.setEnabled(!s.toString().isEmpty());//Si pas vide on active le bouton
            }
        });
        m_playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_user.setFirstName(m_nameEditText.getText().toString());
                //TODO: Formule non deprecated à étudier
                /*ActivityResultLauncher<Intent> startActivityForResult = registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                                Intent data = result.getData();
                                // ...
                            }
                        }
                );
                Intent gameActivityIntent = new Intent(MainActivity.this, GameActivity.class);
                startActivityForResult.launch(gameActivityIntent);
                 */
                Intent gameActivityIntent = new Intent(MainActivity.this, GameActivity.class);
                startActivityForResult(gameActivityIntent, GAME_ACTIVITY_REQUEST_CODE);
            }
        });
    }
}
