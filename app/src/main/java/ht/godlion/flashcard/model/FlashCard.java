package ht.godlion.flashcard.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "flashcard")
public class FlashCard {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "question")
    private String question;
    @ColumnInfo(name = "answer")
    private String answer;
    @ColumnInfo(name = "wrong_answer1")
    private String wrong_answer1;
    @ColumnInfo(name = "wrong_answer2")
    private String wrong_answer2;
    @ColumnInfo(name = "date")
    private long flashcardDate;

    public FlashCard() {}

    public FlashCard(String question, String answer, String wrong_answer1, String wrong_answer2, long flashcardDate ) {
        this.question = question;
        this.answer = answer;
        this.wrong_answer1 = wrong_answer1;
        this.wrong_answer2 = wrong_answer2;
        this.flashcardDate = flashcardDate;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public long getFlashcardDate () {
        return flashcardDate;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getWrong_answer1() {
        return wrong_answer1;
    }

    public void setWrong_answer1(String wrong_answer1) {
        this.wrong_answer1 = wrong_answer1;
    }

    public String getWrong_answer2() {
        return wrong_answer2;
    }

    public void setWrong_answer2(String wrong_answer2) {
        this.wrong_answer2 = wrong_answer2;
    }

    public void setFlashcardDate (long flashcardDate ) {
        this.flashcardDate = flashcardDate;
    }

    public int getId () {
        return id;
    }

    public void setId ( int id ) {
        this.id = id;
    }

    @NonNull
    @Override
    public String toString() {
        return "FlashCard{" +
                "id=" + id +
                ", question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", wrong_answer1='" + wrong_answer1 + '\'' +
                ", wrong_answer2='" + wrong_answer2 + '\'' +
                ", flashcardDate=" + flashcardDate +
                '}';
    }
}
