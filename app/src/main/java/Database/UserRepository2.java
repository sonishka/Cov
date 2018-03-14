package Database;

import java.util.List;

import Model.User2;
import io.reactivex.Flowable;

/**
 * Created by lenovo on 12/03/2018.
 */

public class UserRepository2 implements IUserDatasource2 {

    private IUserDatasource2 mLocalDataSource;
    private static UserRepository2 mInstance;

    public UserRepository2(IUserDatasource2 mLocalDataSource) {
        this.mLocalDataSource = mLocalDataSource;
    }

    public static UserRepository2 getmInstance(IUserDatasource2 mLocalDataSource){
        if(mInstance==null)
        {
            mInstance= new UserRepository2(mLocalDataSource);
        }
        return mInstance;
    }


    @Override
    public Flowable<User2> getUser2ById(int userID) {
        return mLocalDataSource.getUser2ById(userID);
    }

    @Override
    public Flowable<List<User2>> getAllUsers2() {
        return mLocalDataSource.getAllUsers2();
    }

    @Override
    public void insertUser2(User2... users) {
        mLocalDataSource.insertUser2(users);
    }

    @Override
    public void UpdateUser2(User2... users) {
        mLocalDataSource.UpdateUser2(users);
    }

    @Override
    public void DeleteUser2(User2 user) {
        mLocalDataSource.DeleteUser2(user);
    }

    @Override
    public void deleteAllUsers2() {
        mLocalDataSource.deleteAllUsers2();
    }
}
