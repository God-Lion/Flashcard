package ht.godlion.flashcard;

import static ht.godlion.flashcard.EditFlashCardActivity.ADD_FLASHCARD_REQUEST_CODE;
import static ht.godlion.flashcard.EditFlashCardActivity.EDIT_FLASHCARD_REQUEST_CODE;
import static ht.godlion.flashcard.EditFlashCardActivity.FLASHCARD_EXTRA_EDIT_ID;
import static ht.godlion.flashcard.EditFlashCardActivity.FLASHCARD_EXTRA_Key;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ht.godlion.flashcard.db.FlashCardsDB;
import ht.godlion.flashcard.db.FlashCardsDao;
import ht.godlion.flashcard.model.FlashCard;

public class MainActivity extends AppCompatActivity {
    private TextView tvFlashcardQuestion;
    private TextView tvFlashcardAnswerHint;
    private ImageView btnPrev;
    private ImageView btnNext;
    private ImageView btnDelete;
    private Button btnAnswer1;
    private Button btnAnswer2;
    private Button btnAnswer3;
    private FlashCardsDao dao;
    private ArrayList<FlashCard> allFlashcards;
    private int currentCardDisplayedIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.tvFlashcardQuestion = findViewById(R.id.flashcardQuestion);
        this.tvFlashcardAnswerHint = findViewById(R.id.flashcardAnswerHint);
        this.btnPrev = findViewById(R.id.prevCard);
        this.btnNext = findViewById(R.id.nextCard);
        this.btnDelete = findViewById(R.id.deleteCard);
        this.btnAnswer1 = findViewById(R.id.btnAnswer1);
        this.btnAnswer2 = findViewById(R.id.btnAnswer2);
        this.btnAnswer3 = findViewById(R.id.btnAnswer3);

        this.dao = FlashCardsDB.getInstance(this).flashcardsDBDao();
        this.allFlashcards = new ArrayList<>();
        List<FlashCard> list = this.dao.getFlashCards();
        this.allFlashcards.addAll( list );

        if (this.allFlashcards.size() == 0 || this.allFlashcards.size() == 1) {
            this.btnPrev.setVisibility(View.INVISIBLE);
            this.btnNext.setVisibility(View.INVISIBLE);
        }
        if (this.allFlashcards.size() > 1) {
            this.btnPrev.setVisibility(View.VISIBLE);
            this.btnNext.setVisibility(View.VISIBLE);
        }
        if (this.currentCardDisplayedIndex == 0) this.btnPrev.setVisibility(View.INVISIBLE);

        if (this.allFlashcards.size() == 0 ) this.btnDelete.setVisibility(View.INVISIBLE);
        else this.btnDelete.setVisibility(View.VISIBLE);

        if (getIntent().getExtras() != null) {
            int requestCode = getIntent().getExtras().getInt(FLASHCARD_EXTRA_Key, 0);
            RelativeLayout relativeLayout = findViewById(R.id.parent);

            if(requestCode == ADD_FLASHCARD_REQUEST_CODE) {
                this.allFlashcards = new ArrayList<>();
                list = this.dao.getFlashCards();
                this.allFlashcards.addAll( list );
                int lastIndex = this.allFlashcards.size() - 1;
                FlashCard flashcard = this.allFlashcards.get(lastIndex);
                this.tvFlashcardQuestion.setText(flashcard.getQuestion());
                this.tvFlashcardAnswerHint.setText(flashcard.getAnswer());
                this.btnAnswer1.setText(flashcard.getAnswer());
                this.btnAnswer2.setText(flashcard.getWrong_answer1());
                this.btnAnswer3.setText(flashcard.getWrong_answer2());
                Snackbar
                        .make(relativeLayout, getString(R.string.saveSuccessCard), Snackbar.LENGTH_LONG).show();
            }
            if(requestCode == EDIT_FLASHCARD_REQUEST_CODE) {
                int id = getIntent().getExtras().getInt(FLASHCARD_EXTRA_EDIT_ID, 0);
                FlashCard flashcard = this.dao.getFlashcardById(id);
                this.tvFlashcardQuestion.setText(flashcard.getQuestion());
                this.tvFlashcardAnswerHint.setText(flashcard.getAnswer());
                this.btnAnswer1.setText(flashcard.getAnswer());
                this.btnAnswer2.setText(flashcard.getWrong_answer1());
                this.btnAnswer3.setText(flashcard.getWrong_answer2());
                Snackbar
                        .make(relativeLayout, getString(R.string.modifySuccessCard), Snackbar.LENGTH_LONG).show();
            }
        } else {
            if (this.allFlashcards != null && !this.allFlashcards.isEmpty()) {
                FlashCard flashcard = allFlashcards.get(0);
                this.tvFlashcardQuestion.setText(flashcard.getQuestion());
                this.tvFlashcardAnswerHint.setText(flashcard.getAnswer());
                this.btnAnswer1.setText(flashcard.getAnswer());
                this.btnAnswer2.setText(flashcard.getWrong_answer1());
                this.btnAnswer3.setText(flashcard.getWrong_answer2());
            }
        }
        this.tvFlashcardQuestion.setOnClickListener(view -> showAnswer());
        this.tvFlashcardAnswerHint.setOnClickListener(view -> showQuestion());
        this.btnPrev.setOnClickListener(view -> onPrevFlashCard());
        this.btnNext.setOnClickListener(view -> onNextFlashCard());
        this.btnDelete.setOnClickListener(view -> onDeleteFlashCard());
        this.btnAnswer1.setOnClickListener(view -> onAnswer1());
        this.btnAnswer2.setOnClickListener(view -> onAnswer2());
        this.btnAnswer3.setOnClickListener(view -> onAnswer3());

        FloatingActionButton fabEdit = findViewById(R.id.fabEdit);
        fabEdit.setOnClickListener(view -> onEditFlashCard());

        FloatingActionButton fabNew = findViewById(R.id.fabNew);
        fabNew.setOnClickListener(view -> onAddNewFlashCard());
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadFlashCards();
    }

    private void showAnswer () {
        this.tvFlashcardQuestion.setVisibility(View.INVISIBLE);
        this.tvFlashcardAnswerHint.setVisibility(View.VISIBLE);
    }

    private void showQuestion () {
        this.tvFlashcardQuestion.setVisibility(View.VISIBLE);
        this.tvFlashcardAnswerHint.setVisibility(View.INVISIBLE);
    }

    private void onPrevFlashCard () {
        if (this.allFlashcards.size() == 0 || this.allFlashcards.size() == 1) return;

        if (this.currentCardDisplayedIndex <= (this.allFlashcards.size() - 1)) this.currentCardDisplayedIndex--;
        if (this.currentCardDisplayedIndex == 0) this.btnPrev.setVisibility(View.INVISIBLE);
        if ((this.currentCardDisplayedIndex > 0) || (this.currentCardDisplayedIndex + 1) <= this.allFlashcards.size())
            this.btnNext.setVisibility(View.VISIBLE);

        int cardToDisplayIndex = getRandom(0, this.allFlashcards.size() - 1);

        while (cardToDisplayIndex == this.currentCardDisplayedIndex) {
            cardToDisplayIndex = getRandom(0, this.allFlashcards.size() - 1);
        }

        this.currentCardDisplayedIndex = cardToDisplayIndex;

        FlashCard flashcard = this.allFlashcards.get(this.currentCardDisplayedIndex);
        this.tvFlashcardQuestion.setText(flashcard.getQuestion());
        this.tvFlashcardAnswerHint.setText(flashcard.getAnswer());
        this.btnAnswer1.setText(flashcard.getAnswer());
        this.btnAnswer2.setText(flashcard.getWrong_answer1());
        this.btnAnswer3.setText(flashcard.getWrong_answer2());
    }

    private void onNextFlashCard () {
        if (this.allFlashcards.size() == 0 || this.allFlashcards.size() == 1) return;

        if (this.currentCardDisplayedIndex < (this.allFlashcards.size() - 1)) this.currentCardDisplayedIndex++;
        if ((this.currentCardDisplayedIndex + 1) == this.allFlashcards.size()) this.btnNext.setVisibility(View.INVISIBLE);
        if (this.currentCardDisplayedIndex > 0 || (this.currentCardDisplayedIndex + 1) == this.allFlashcards.size())
            this.btnPrev.setVisibility(View.VISIBLE);

        int cardToDisplayIndex = getRandom(0, this.allFlashcards.size() - 1);

        while (cardToDisplayIndex == this.currentCardDisplayedIndex) {
            cardToDisplayIndex = getRandom(0, this.allFlashcards.size() - 1);
        }

        this.currentCardDisplayedIndex = cardToDisplayIndex;

        FlashCard flashcard = this.allFlashcards.get(this.currentCardDisplayedIndex);
        this.tvFlashcardQuestion.setText(flashcard.getQuestion());
        this.tvFlashcardAnswerHint.setText(flashcard.getAnswer());
        this.btnAnswer1.setText(flashcard.getAnswer());
        this.btnAnswer2.setText(flashcard.getWrong_answer1());
        this.btnAnswer3.setText(flashcard.getWrong_answer2());
    }

    private void onAnswer1() {
    }

    private void onAnswer2() {
    }

    private void onAnswer3() {
    }

    private void onEditFlashCard () {
        if (this.allFlashcards.isEmpty())
            Toast.makeText(this, getString(R.string.emptyCard), Toast.LENGTH_SHORT).show();
        else {
            this.allFlashcards.size();
            FlashCard flashcard = this.allFlashcards.get(this.currentCardDisplayedIndex);
            Intent edit = new Intent(this, EditFlashCardActivity.class);
            edit.putExtra(FLASHCARD_EXTRA_Key, flashcard.getId());
            startActivity(edit);
        }
    }

    private void onAddNewFlashCard () { startActivity(new Intent(this, EditFlashCardActivity.class)); }

    private void onDeleteFlashCard () {
        FlashCard flashcard = this.allFlashcards.get(this.currentCardDisplayedIndex);
        new AlertDialog.Builder( MainActivity.this )
                .setMessage(getString(R.string.delete) + flashcard.getQuestion() + getString(R.string.card))
                .setPositiveButton(getString(R.string.delete), ( dialogInterface, i ) -> {
                    this.dao.deleteFlashCard(flashcard);
                    this.allFlashcards.remove(this.currentCardDisplayedIndex);
                    this.allFlashcards = new ArrayList<>();
                    List<FlashCard> list = this.dao.getFlashCards();
                    this.allFlashcards.addAll( list );
                    if(this.allFlashcards.isEmpty()){
                        this.tvFlashcardQuestion.setText(getString(R.string.question_is_empty));
                        this.tvFlashcardAnswerHint.setText(getString(R.string.flashcardAnswerHint));
                        this.btnAnswer1.setText(getString(R.string.answerEmpty));
                        this.btnAnswer2.setText(getString(R.string.answerEmpty));
                        this.btnAnswer3.setText(getString(R.string.answerEmpty));
                        this.btnDelete.setVisibility(View.INVISIBLE);
                    } else {
                        int lastIndex = this.allFlashcards.size() - 1;
                        FlashCard fc = this.allFlashcards.get(lastIndex);
                        this.tvFlashcardQuestion.setText(fc.getQuestion());
                        this.tvFlashcardAnswerHint.setText(fc.getAnswer());
                        this.btnAnswer1.setText(fc.getAnswer());
                        this.btnAnswer2.setText(fc.getWrong_answer1());
                        this.btnAnswer3.setText(fc.getWrong_answer2());
                    }
                })
                .setNegativeButton(getString(R.string.cancel), ( dialogInterface, i ) -> dialogInterface.cancel())
                .setCancelable(false)
                .create().show();
    }

    private void loadFlashCards () {
        this.allFlashcards = new ArrayList<>();
        List<FlashCard> list = dao.getFlashCards();
        this.allFlashcards.addAll( list );
    }

    public int getRandom(int minNumber, int maxNumber) {
        Random rand = new Random();
        return rand.nextInt((maxNumber - minNumber) + 1) + minNumber;
    }

}
