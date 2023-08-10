package ht.godlion.flashcard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import static ht.godlion.flashcard.MainActivity.MAIN_EXTRA_Key;

public class EditFlashCardActivity extends AppCompatActivity {
    public static final String FLASHCARD_EXTRA_Key = "flashCard_id";
    private EditText inputFlashCardQuestion;

    private EditText inputAnswer1;
    private EditText inputAnswer2;
    private EditText inputAnswer3;


    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_flash_card);
        this.inputFlashCardQuestion = findViewById(R.id.inputFlashcardQuestion);
        this.inputAnswer1 = findViewById(R.id.inputAnswer1);
        this.inputAnswer2 = findViewById(R.id.inputAnswer2);
        this.inputAnswer3 = findViewById(R.id.inputAnswer3);

        if (getIntent().getExtras() != null) {
            String editString = getIntent().getExtras().getString(FLASHCARD_EXTRA_Key);
            assert editString != null;
            String[] arrOfStr = editString.split("/;/");
            String flashcardQuestionText = arrOfStr[0];

            String answer1 = arrOfStr[1];
            String answer2 = arrOfStr[2];
            String answer3 = arrOfStr[3];

            this.inputFlashCardQuestion.setText(flashcardQuestionText);
            this.inputAnswer1.setText(answer1);
            this.inputAnswer2.setText(answer2);
            this.inputAnswer3.setText(answer3);
        } else this.inputFlashCardQuestion.setFocusable(true);
        FloatingActionButton fabSave = findViewById(R.id.fabSave);
        fabSave.setOnClickListener(view -> onSaveFlashCard());
        FloatingActionButton fabCancel = findViewById(R.id.fabCancel);
        fabCancel.setOnClickListener(view -> onCancelFlashCard());
    }

    private void onSaveFlashCard () {
        if (this.inputFlashCardQuestion.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.question_is_empty), Toast.LENGTH_SHORT).show();
        }
        else if (this.inputAnswer1.getText().toString().trim().isEmpty() || this.inputAnswer2.getText().toString().trim().isEmpty() || this.inputAnswer3.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.answer_is_empty), Toast.LENGTH_SHORT).show();
        }
        else {
            String editString = this.inputFlashCardQuestion.getText().toString().trim() +
                    "/;/" +
                    this.inputAnswer1.getText().toString().trim() +
                    "/;/" +
                    this.inputAnswer2.getText().toString().trim() +
                    "/;/" +
                    this.inputAnswer3.getText().toString().trim();
            Intent save = new Intent( this, MainActivity.class );
            save.putExtra(MAIN_EXTRA_Key, editString);
            setResult(RESULT_OK, save);
            startActivity(save);
        }

    }



    private void onCancelFlashCard () {
        finish();
    }
}
