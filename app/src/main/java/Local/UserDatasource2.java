package Local;

import java.util.List;

import Database.IUserDatasource2;
import Model.User2;

import io.reactivex.Flowable;

/**
 * Created by lenovo on 12/03/2018.
 */

public class UserDatasource2 implements IUserDatasource2 {

    private User2DAO userDAO;
    private static UserDatasource2 mInstance;

    public UserDatasource2(User2DAO userDAO) {
        this.userDAO = userDAO;
    }

    public static UserDatasource2 getInstance(User2DAO userDAO){

        if (mInstance== null){
            mInstance= new UserDatasource2(userDAO);
        }
        return mInstance;
    }


    @Override
    public Flowable<User2> getUser2ById(int userID) {
        return userDAO.getUser2ById(userID);
    }

    @Override
    public Flowable<List<User2>> getAllUsers2() {
        return userDAO.getAllUsers2();
    }

    @Override
    public void insertUser2(User2... users) {
        userDAO.insertUser2(users);
    }

    @Override
    public void UpdateUser2(User2... users) {
        userDAO.UpdateUser2(users);
    }

    @Override
    public void DeleteUser2(User2 user) {
        userDAO.DeleteUser2(user);
    }

    @Override
    public void deleteAllUsers2() {
        userDAO.deleteAllUsers2();
    }
}
