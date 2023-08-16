package ht.godlion.flashcard.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ht.godlion.flashcard.model.FlashCard;

@SuppressWarnings("ALL")
@Dao
public interface FlashCardsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFlashCard ( FlashCard flashcard );

    @Delete
    void deleteFlashCard( FlashCard ... flashcard );

    @Update
    void updateFlashCard ( FlashCard flashcard );

    @Query( "SELECT * FROM flashcard" )
    List<FlashCard> getFlashCards ();

    @Query( "SELECT * FROM flashcard WHERE id = :flashcardId" )
    FlashCard getFlashcardById ( int flashcardId );

    @Query( "DELETE FROM flashcard WHERE id = :flashcardId" )
    void deleteFlashcardById ( int flashcardId );
}
