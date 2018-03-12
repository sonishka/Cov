package Local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import Model.User;
import io.reactivex.Flowable;

/**
 * Created by lenovo on 12/03/2018.
 */
@Dao
public interface UserDAO {

    @Query("SELECT * FROM  users WHERE id =:userID")
    Flowable<User> getUserById(int userID);

    @Query("SELECT * FROM users")
    Flowable<List<User>> getAllUsers();

    @Insert
    void insertUser(User... users);

    @Update
    void UpdateUser(User... users);

    @Delete
    void DeleteUser(User user);

    @Query("DELETE FROM users")
    void deleteAllUsers();

}
