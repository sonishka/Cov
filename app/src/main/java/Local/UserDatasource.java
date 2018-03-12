package Local;

import java.util.List;

import Database.IUserDatasource;
import Model.User;
import io.reactivex.Flowable;

/**
 * Created by lenovo on 12/03/2018.
 */

public class UserDatasource implements IUserDatasource {

    private UserDAO userDAO;
    private static UserDatasource mInstance;

    public UserDatasource(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public static UserDatasource getInstance(UserDAO userDAO){

        if (mInstance== null){
            mInstance= new UserDatasource(userDAO);
        }
        return mInstance;
    }

    @Override
    public Flowable<User> getUserById(int userID) {
        return userDAO.getUserById(userID);
    }

    @Override
    public Flowable<List<User>> getAllUsers() {
        return userDAO.getAllUsers();
    }

    @Override
    public void insertUser(User... users) {

        userDAO.insertUser(users);
    }

    @Override
    public void UpdateUser(User... users) {

        userDAO.UpdateUser(users);
    }

    @Override
    public void DeleteUser(User user) {

        userDAO.DeleteUser(user);
    }

    @Override
    public void deleteAllUsers() {
        userDAO.deleteAllUsers();

    }
}
