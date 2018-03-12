package Database;

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

public interface IUserDatasource {

    Flowable<User> getUserById(int userID);
Flowable<List<User>> getAllUsers();
void insertUser(User... users);
void UpdateUser(User... users);
void DeleteUser(User user);
void deleteAllUsers();
}
