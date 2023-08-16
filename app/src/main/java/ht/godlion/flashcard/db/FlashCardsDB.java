package ht.godlion.flashcard.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import ht.godlion.flashcard.model.FlashCard;

@Database(entities = FlashCard.class, version = 1)
public abstract class FlashCardsDB extends RoomDatabase {
    public abstract FlashCardsDao flashcardsDBDao();

    public static final String DATABSE_NAME = "flashcardDb";
    private static FlashCardsDB instance;

    public static FlashCardsDB getInstance(Context context) {
        if (instance == null)
            instance = Room.databaseBuilder(context, FlashCardsDB.class, DATABSE_NAME)
                    .allowMainThreadQueries()
                    .build();
        return instance;
    }
}
