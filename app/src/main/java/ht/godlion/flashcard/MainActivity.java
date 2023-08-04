package ht.godlion.flashcard;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView flashcardQuestion;
    private TextView flashcardAnswerHint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.flashcardQuestion = ((TextView) findViewById(R.id.flashcardQuestion));
        this.flashcardAnswerHint = ((TextView) findViewById(R.id.flashcardAnswerHint));
        this.flashcardQuestion.setOnClickListener(view -> showAnswer());
        this.flashcardAnswerHint.setOnClickListener(view -> showQuestion());

    }

    private void showAnswer () {
        this.flashcardQuestion.setVisibility(View.INVISIBLE);
        this.flashcardAnswerHint.setVisibility(View.VISIBLE);
    }

    private void showQuestion () {
        this.flashcardQuestion.setVisibility(View.VISIBLE);
        this.flashcardAnswerHint.setVisibility(View.INVISIBLE);
    }
}
