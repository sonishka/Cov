package Database;

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

public interface IUserDatasource2 {

    Flowable<User2> getUser2ById(int userID);
    Flowable<List<User2>> getAllUsers2();
    void insertUser2(User2... users);
    void UpdateUser2(User2... users);
    void DeleteUser2(User2 user);
    void deleteAllUsers2();
}
