package com.example.firsttask;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.util.Log;


public class MainActivity extends AppCompatActivity {
    public static final String KEY_EXTRA_ANSWER = "com.example.firsttask.correctAnswer";

    private static final int REQUEST_CODE_PROMPT = 0;

    private Button trueButton;
    private Button falseButton;
    private Button nextButton;

    private boolean answerWasShown = false;

    private TextView questionTextView;
    private TextView scoreTextView;

    private static final String TAG = "MainActivity";

    private static final String KEY_CURRENT_INDEX = "currentIndex";

    private Question[] questions = new Question[] {
            new Question(R.string.question_text_1, true),
            new Question(R.string.question_text_2, false),
            new Question(R.string.question_text_3, true),
            new Question(R.string.question_text_4, false),
            new Question(R.string.question_text_5, true)
    };

    private int currentIndex = 0;
    private int correctAnswers = 0;
    private boolean hasAnswered = false;

    private void checkAnswerCorrectness(boolean userAnswer) {
        // Проверяем, была ли уже просмотрена подсказка
        if (answerWasShown) {
            Toast.makeText(this, R.string.answer_was_shown, Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверяем, был ли уже дан ответ на текущий вопрос
        if (hasAnswered) {
            Toast.makeText(this, R.string.already_answered, Toast.LENGTH_SHORT).show();
            return;
        }

        // Проверяем правильность ответа
        boolean correctAnswer = questions[currentIndex].isTrueAnswer();
        int resultMessageId;

        if (userAnswer == correctAnswer) {
            resultMessageId = R.string.button_true;
            correctAnswers++;
        } else {
            resultMessageId = R.string.button_false;
        }
        Toast.makeText(this, resultMessageId, Toast.LENGTH_SHORT).show();
        hasAnswered = true;
        updateScore();
    }


    private void updateScore() {
        String scoreText = "Correct answers: " + correctAnswers;
        scoreTextView.setText(scoreText);
    }

    private void setNextQuestion(){
        questionTextView.setText(questions[currentIndex].getQuestionId());
        hasAnswered = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        Log.d(TAG, "Wywolana zastala metoda: onSaveInstanceState");
        outState.putInt(KEY_CURRENT_INDEX, currentIndex);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Metoda onCreate została wywołana");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null){
            currentIndex = savedInstanceState.getInt(KEY_CURRENT_INDEX);
        }

        trueButton = findViewById(R.id.true_button);
        falseButton = findViewById(R.id.false_button);
        nextButton = findViewById(R.id.next_button);
        Button promptButton = findViewById(R.id.hint_button);
        questionTextView = findViewById(R.id.question_text_view);
        scoreTextView = findViewById(R.id.score_text_view);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        trueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswerCorrectness(true);
            }
        });

        falseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswerCorrectness(false);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentIndex = (currentIndex + 1) % questions.length;
                answerWasShown = false;
                setNextQuestion();
            }
        });

        promptButton.setOnClickListener((v) -> {
            Intent intent = new Intent(MainActivity.this, PromptActivity.class);
            boolean correctAnswer = questions[currentIndex].isTrueAnswer();
            intent.putExtra(KEY_EXTRA_ANSWER, correctAnswer);
            startActivityForResult(intent, REQUEST_CODE_PROMPT);
        });



        setNextQuestion();
        updateScore(); // Инициализация отображения счета
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart wywołana");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume wywołana");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause wywołana");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop wywołana");
        Toast.makeText(this, "Activity is stopping", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy wywołana");
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {return;}
        if (requestCode == REQUEST_CODE_PROMPT) {
            if (data == null) {return;}
            answerWasShown = data.getBooleanExtra(PromptActivity.KEY_EXTRA_ANSWER_SHOWN, false);
        }
    }
}

