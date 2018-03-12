package Database;

import java.util.List;

import Model.User;
import io.reactivex.Flowable;

/**
 * Created by lenovo on 12/03/2018.
 */

public class UserRepository implements IUserDatasource {

    private IUserDatasource mLocalDataSource;
    private static UserRepository mInstance;

    public UserRepository(IUserDatasource mLocalDataSource) {
        this.mLocalDataSource = mLocalDataSource;
    }

    public static UserRepository getmInstance(IUserDatasource mLocalDataSource){
        if(mInstance==null)
        {
            mInstance= new UserRepository(mLocalDataSource);
        }
        return mInstance;
    }

    @Override
    public Flowable<User> getUserById(int userID) {
        return mLocalDataSource.getUserById(userID);
    }

    @Override
    public Flowable<List<User>> getAllUsers() {
        return mLocalDataSource.getAllUsers();
    }

    @Override
    public void insertUser(User... users) {

        mLocalDataSource.insertUser(users);
    }

    @Override
    public void UpdateUser(User... users) {

        mLocalDataSource.UpdateUser(users);
    }

    @Override
    public void DeleteUser(User user) {

        mLocalDataSource.DeleteUser(user);
    }

    @Override
    public void deleteAllUsers() {
        mLocalDataSource.deleteAllUsers();

    }
}
