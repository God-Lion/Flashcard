package ht.godlion.flashcard;

import static ht.godlion.flashcard.EditFlashCardActivity.ADD_FLASHCARD_REQUEST_CODE;
import static ht.godlion.flashcard.EditFlashCardActivity.EDIT_FLASHCARD_REQUEST_CODE;
import static ht.godlion.flashcard.EditFlashCardActivity.FLASHCARD_EXTRA_EDIT_ID;
import static ht.godlion.flashcard.EditFlashCardActivity.FLASHCARD_EXTRA_Key;
import static nl.dionsegijn.konfetti.core.Position.Relative;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import ht.godlion.flashcard.db.FlashCardsDB;
import ht.godlion.flashcard.db.FlashCardsDao;
import ht.godlion.flashcard.model.FlashCard;
import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.core.emitter.EmitterConfig;
import nl.dionsegijn.konfetti.core.models.Shape;
import nl.dionsegijn.konfetti.core.models.Size;
import nl.dionsegijn.konfetti.xml.KonfettiView;

public class MainActivity extends AppCompatActivity {
    private TextView tvTimer;
    private CardView cardView;
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
    private CountDownTimer countdownTimer;
    private KonfettiView konfettiView;
    private Shape.DrawableShape drawableShape;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.tvTimer = findViewById(R.id.timer);
        this.cardView = findViewById(R.id.cardView);
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

        final Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_heart);
        assert drawable != null;
        this.drawableShape = new Shape.DrawableShape(drawable, true, true);

        this.konfettiView = findViewById(R.id.konfettiView);
        EmitterConfig emitterConfig = new Emitter(5L, TimeUnit.SECONDS).perSecond(50);
        Party party = new PartyFactory(emitterConfig)
                .angle(270)
                .spread(90)
                .setSpeedBetween(1f, 5f)
                .timeToLive(2000L)
                .shapes(new Shape.Rectangle(0.2f), this.drawableShape)
                .sizes(new Size(12, 5f, 0.2f))
                .position(0.0, 0.0, 1.0, 0.0)
                .build();

        countdownTimer = new CountDownTimer(16000, 1000) {
            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                tvTimer.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
            }
        };

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
                startTimer();
            }
        }
        this.tvFlashcardQuestion.setOnClickListener(this::showQuestion);
        this.tvFlashcardAnswerHint.setOnClickListener(this::showAnswer);
        this.btnPrev.setOnClickListener(this::onPrevFlashCard);
        this.btnNext.setOnClickListener(this::onNextFlashCard);
        this.btnDelete.setOnClickListener(view -> onDeleteFlashCard());
        this.btnAnswer1.setOnClickListener(this::onAnswer1);
        this.btnAnswer2.setOnClickListener(this::onAnswer2);
        this.btnAnswer3.setOnClickListener(this::onAnswer3);

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

    private void showQuestion (@NonNull View view) {
        int cx = this.cardView.getWidth() / 2;
        int cy = this.cardView.getHeight() / 2;
        float finalRadius = (float) Math.hypot(cx, cy);
        Animator anim = ViewAnimationUtils.createCircularReveal(this.tvFlashcardAnswerHint, cx, cy, 0f, finalRadius);
        this.tvFlashcardQuestion.setVisibility(View.INVISIBLE);
        this.tvFlashcardAnswerHint.animate().rotationY(0).setDuration(0).start();
        this.tvFlashcardAnswerHint.setVisibility(View.VISIBLE);
        anim.setDuration(1000);
        anim.start();
    }


    private void showAnswer (@NonNull View view) {
        this.cardView.setCameraDistance(25000);
        this.cardView.setCameraDistance(25000);
        this.cardView.animate()
                .rotationY(90)
                .setDuration(200)
                .withEndAction(
                        () -> {
                            this.tvFlashcardAnswerHint.setVisibility(View.INVISIBLE);
                            this.tvFlashcardQuestion.setVisibility(View.VISIBLE);
                            this.cardView.setRotationY(-90);
                            this.cardView.animate()
                                    .rotationY(0)
                                    .setDuration(200)
                                    .start();
                        }
                ).start();
    }


    private void onPrevFlashCard (@NonNull View view) {
        final Animation leftInAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.left_in);
        final Animation rightOutAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.right_out);

        this.tvFlashcardQuestion.setVisibility(View.VISIBLE);
        this.tvFlashcardAnswerHint.setVisibility(View.INVISIBLE);
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

        this.cardView.startAnimation(rightOutAnim);

        rightOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                FlashCard flashcard = allFlashcards.get(currentCardDisplayedIndex);
                tvFlashcardQuestion.setText(flashcard.getQuestion());
                tvFlashcardAnswerHint.setText(flashcard.getAnswer());
                btnAnswer1.setText(flashcard.getAnswer());
                btnAnswer2.setText(flashcard.getWrong_answer1());
                btnAnswer3.setText(flashcard.getWrong_answer2());

                cardView.startAnimation(leftInAnim);
                startTimer();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        this.btnAnswer1.setBackgroundColor(getResources().getColor(R.color.green, null));
        this.btnAnswer2.setBackgroundColor(getResources().getColor(R.color.green, null));
        this.btnAnswer3.setBackgroundColor(getResources().getColor(R.color.green, null));
    }

    private void onNextFlashCard (@NonNull View view) {
        final Animation leftOutAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.left_out);
        final Animation rightInAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.right_in);

        this.tvFlashcardQuestion.setVisibility(View.VISIBLE);
        this.tvFlashcardAnswerHint.setVisibility(View.INVISIBLE);
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

        this.cardView.startAnimation(leftOutAnim);

        leftOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                FlashCard flashcard = allFlashcards.get(currentCardDisplayedIndex);
                tvFlashcardQuestion.setText(flashcard.getQuestion());
                tvFlashcardAnswerHint.setText(flashcard.getAnswer());
                btnAnswer1.setText(flashcard.getAnswer());
                btnAnswer2.setText(flashcard.getWrong_answer1());
                btnAnswer3.setText(flashcard.getWrong_answer2());

                cardView.startAnimation(rightInAnim);
                startTimer();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        this.btnAnswer1.setBackgroundColor(getResources().getColor(R.color.green, null));
        this.btnAnswer2.setBackgroundColor(getResources().getColor(R.color.green, null));
        this.btnAnswer3.setBackgroundColor(getResources().getColor(R.color.green, null));
    }

    private void onAnswer1(@NonNull View view) {
        view.setBackgroundColor(getResources().getColor(R.color.orange, null));
        EmitterConfig emitterConfig = new Emitter(100L, TimeUnit.MILLISECONDS).max(100);
        konfettiView.start(
                new PartyFactory(emitterConfig)
                        .spread(360)
                        .shapes(Arrays.asList(Shape.Square.INSTANCE, Shape.Circle.INSTANCE, drawableShape))
                        .colors(Arrays.asList(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                        .setSpeedBetween(0f, 30f)
                        .position(new Relative(0.5, 0.3))
                        .build()
        );
    }

    private void onAnswer2(@NonNull View view) {
        view.setBackgroundColor(getResources().getColor(R.color.red, null));

    }

    private void onAnswer3(@NonNull View view) {
        view.setBackgroundColor(getResources().getColor(R.color.red, null));

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

    private void onAddNewFlashCard () {
        startActivity(new Intent(this, EditFlashCardActivity.class));
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

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

    private void startTimer() {
        this.countdownTimer.cancel();
        this.countdownTimer.start();
    }
}
