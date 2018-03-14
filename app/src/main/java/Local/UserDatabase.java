package Local;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import Model.User;
import Model.User2;

import static Local.UserDatabase.DATABASE_VERSION;

/**
 * Created by lenovo on 12/03/2018.
 */

@Database(entities = {User.class,User2.class}, version = DATABASE_VERSION)
public abstract class UserDatabase extends RoomDatabase{
    public static final int DATABASE_VERSION=2;
    public static final String DATABASE_NAME ="EDMT-Database-Room";

    public abstract UserDAO userDAO();
    public abstract User2DAO user2DAO();

    private static UserDatabase mInstance;

    public static UserDatabase getmInstance(Context context){
        if(mInstance==null){
            mInstance= Room.databaseBuilder(context,UserDatabase.class,DATABASE_NAME).addMigrations(MIGRATION_1_2)
            .build();
        }
        return mInstance;
    }



    static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE 'users2' ('id' INTEGER NOT NULL, "
            +"'nom' TEXT, "
            +"'email' TEXT, "
            +" PRIMARY KEY ('id'));");
        }
    };



}
