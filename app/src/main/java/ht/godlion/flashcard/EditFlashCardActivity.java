package ht.godlion.flashcard;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;

import ht.godlion.flashcard.db.FlashCardsDB;
import ht.godlion.flashcard.db.FlashCardsDao;
import ht.godlion.flashcard.model.FlashCard;

public class EditFlashCardActivity extends AppCompatActivity {
    public static final String FLASHCARD_EXTRA_Key = "flashCard_id";
    public static final String FLASHCARD_EXTRA_EDIT_ID = "id";
    public static final int ADD_FLASHCARD_REQUEST_CODE = 100;
    public static final int EDIT_FLASHCARD_REQUEST_CODE = 200;
    private EditText inputFlashCardQuestion;

    private EditText inputAnswer1;
    private EditText inputAnswer2;
    private EditText inputAnswer3;
    private FlashCardsDao dao;
    private FlashCard temp;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_flash_card);
        this.inputFlashCardQuestion = findViewById(R.id.inputFlashcardQuestion);
        this.inputAnswer1 = findViewById(R.id.inputAnswer1);
        this.inputAnswer2 = findViewById(R.id.inputAnswer2);
        this.inputAnswer3 = findViewById(R.id.inputAnswer3);
        this.dao = FlashCardsDB.getInstance(this).flashcardsDBDao();
        if (getIntent().getExtras() != null) {
            int id = getIntent().getExtras().getInt(FLASHCARD_EXTRA_Key, 0);
            this.temp = this.dao.getFlashcardById(id);
            this.inputFlashCardQuestion.setText(this.temp.getQuestion());
            this.inputAnswer1.setText(this.temp.getAnswer());
            this.inputAnswer2.setText(this.temp.getWrong_answer1());
            this.inputAnswer3.setText(this.temp.getWrong_answer2());
        } else this.inputFlashCardQuestion.setFocusable(true);
        FloatingActionButton fabSave = findViewById(R.id.fabSave);
        fabSave.setOnClickListener(view -> onSaveFlashCard());
        FloatingActionButton fabCancel = findViewById(R.id.fabCancel);
        fabCancel.setOnClickListener(view -> onCancelFlashCard());
    }

    private void onSaveFlashCard () {
        String question = this.inputFlashCardQuestion.getText().toString();
        String answer = this.inputAnswer1.getText().toString();
        String wrong_answer1 = this.inputAnswer2.getText().toString();
        String wrong_answer2 = this.inputAnswer3.getText().toString();
        if (question.trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.question_is_empty), Toast.LENGTH_SHORT).show();
        }
        else if (answer.trim().isEmpty() || wrong_answer1.trim().isEmpty() || wrong_answer2.trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.answer_is_empty), Toast.LENGTH_SHORT).show();
        }
        else {
            Intent save = new Intent( this, MainActivity.class );
            long date = new Date().getTime();
            if ( this.temp == null ) {
                this.temp = new FlashCard(question, answer, wrong_answer1, wrong_answer2, date);
                this.dao.insertFlashCard(this.temp);
                save.putExtra(FLASHCARD_EXTRA_Key, ADD_FLASHCARD_REQUEST_CODE);
            } else {
                this.temp.setQuestion(question);
                this.temp.setAnswer(answer);
                this.temp.setWrong_answer1(wrong_answer1);
                this.temp.setWrong_answer2(wrong_answer2);
                this.temp.setFlashcardDate(date);
                this.dao.updateFlashCard(this.temp);
                save.putExtra(FLASHCARD_EXTRA_Key, EDIT_FLASHCARD_REQUEST_CODE);
                save.putExtra(FLASHCARD_EXTRA_EDIT_ID, this.temp.getId());
            }
            setResult(RESULT_OK, save);
            startActivity(save);
        }

    }


    private void onCancelFlashCard () { finish(); }
}
