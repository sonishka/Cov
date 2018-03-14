package Local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import Model.User2;
import io.reactivex.Flowable;

/**
 * Created by lenovo on 12/03/2018.
 */
@Dao
public interface User2DAO {

    @Query("SELECT * FROM  users2 WHERE id =:userID")
    Flowable<User2> getUser2ById(int userID);

    @Query("SELECT * FROM users2")
    Flowable<List<User2>> getAllUsers2();

    @Insert
    void insertUser2(User2... users);

    @Update
    void UpdateUser2(User2... users);

    @Delete
    void DeleteUser2(User2 user);

    @Query("DELETE FROM users2")
    void deleteAllUsers2();

}
