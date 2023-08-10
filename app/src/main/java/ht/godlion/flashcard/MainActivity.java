package ht.godlion.flashcard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import static ht.godlion.flashcard.EditFlashCardActivity.FLASHCARD_EXTRA_Key;

public class MainActivity extends AppCompatActivity {
    public static final String MAIN_EXTRA_Key = "main_id";

    private TextView tvFlashcardQuestion;
    private TextView tvFlashcardAnswerHint;
    private String flashcardQuestionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.tvFlashcardQuestion = findViewById(R.id.flashcardQuestion);
        this.tvFlashcardAnswerHint = findViewById(R.id.flashcardAnswerHint);
        Button btnAnswer1 = findViewById(R.id.btnAnswer1);
        Button btnAnswer2 = findViewById(R.id.btnAnswer2);
        Button btnAnswer3 = findViewById(R.id.btnAnswer3);

        if (getIntent().getExtras() != null) {
            String editString = getIntent().getExtras().getString(MAIN_EXTRA_Key);
            System.out.println("editString "+ editString);
            assert editString != null;
            String[] arrOfStr = editString.split("/;/");
            String flashcardQuestionText = arrOfStr[0];
            String answer1 = arrOfStr[1];
            String answer2 = arrOfStr[2];
            String answer3 = arrOfStr[3];

            this.tvFlashcardQuestion.setText(flashcardQuestionText);
            this.tvFlashcardAnswerHint.setText(answer1);


            this.flashcardQuestionText = flashcardQuestionText;
            btnAnswer1.setText(answer1);
            btnAnswer2.setText(answer2);
            btnAnswer3.setText(answer3);
            RelativeLayout relativeLayout = findViewById(R.id.parent);
            Snackbar
                    .make(relativeLayout, getString(R.string.saveSuccessCard), Snackbar.LENGTH_LONG).show();

        } else {
            this.flashcardQuestionText = getString(R.string.flashcardQuestion);
        }

        this.tvFlashcardQuestion.setOnClickListener(view -> showAnswer());
        this.tvFlashcardAnswerHint.setOnClickListener(view -> showQuestion());

        FloatingActionButton fabEdit = findViewById(R.id.fabEdit);
        fabEdit.setOnClickListener(view -> onEditFlashCard());

        FloatingActionButton fabNew = findViewById(R.id.fabNew);
        fabNew.setOnClickListener(view -> onAddNewFlashCard());
    }

    private void showAnswer () {
        this.tvFlashcardQuestion.setVisibility(View.INVISIBLE);
        this.tvFlashcardAnswerHint.setVisibility(View.VISIBLE);
    }

    private void showQuestion () {
        this.tvFlashcardQuestion.setVisibility(View.VISIBLE);
        this.tvFlashcardAnswerHint.setVisibility(View.INVISIBLE);
    }

    private void onEditFlashCard () {
        String answer1 = getString(R.string.answer1);
        String answer2 = getString(R.string.answer2);
        String answer3 = getString(R.string.answer3);

        String editString = this.flashcardQuestionText +
                "/;/" +
                answer1 +
                "/;/" +
                answer2 +
                "/;/" +
                answer3;

        Intent edit = new Intent( this, EditFlashCardActivity.class );
        edit.putExtra(FLASHCARD_EXTRA_Key, editString);
        startActivity(edit);
    }

    private void onAddNewFlashCard () { startActivity(new Intent(this, EditFlashCardActivity.class)); }

}
