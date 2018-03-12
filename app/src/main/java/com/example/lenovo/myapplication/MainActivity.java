package com.example.lenovo.myapplication;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import Database.UserRepository;
import Local.UserDatabase;
import Local.UserDatasource;
import Model.User;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    private ListView lstUser ;
    private FloatingActionButton fab;


    //Adapter
    List<User> userList = new ArrayList<>();
    ArrayAdapter adapter;

    //Database
    private CompositeDisposable compositeDisposable;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init
        compositeDisposable= new CompositeDisposable();

        //Init View
        lstUser=(ListView) findViewById(R.id.lstUsers);
        fab=(FloatingActionButton)findViewById(R.id.fab);

        adapter= new ArrayAdapter(this,android.R.layout.simple_expandable_list_item_1,userList);
        registerForContextMenu(lstUser);
        lstUser.setAdapter(adapter);

        //Database
        UserDatabase userDatabase= UserDatabase.getmInstance(this); //create database
        userRepository  = UserRepository.getmInstance(UserDatasource.getInstance(userDatabase.userDAO()));


        //load all data from database
        loadData();

        //Event
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add new user
                //random email
                Disposable disposable= io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {

                    @Override
                    public void subscribe(ObservableEmitter<Object> e)throws Exception{
                        User user = new User("EDMTDev",
                                UUID.randomUUID().toString()+"@gmail.com");

                        userRepository.insertUser(user);
                        e.onComplete();

                    }
                })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Consumer() {
                            @Override
                            public void accept(Object o) throws Exception {
                                Toast.makeText(MainActivity.this,"User added",Toast.LENGTH_LONG).show();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(MainActivity.this,""+throwable.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    private void loadData() {
        //User RXJava

        Disposable disposable = userRepository.getAllUsers()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<User>>(){
                               @Override
                               public void accept(List<User> users)throws Exception{
                                   onGetAllUsersSuccess(users);
                               }
                           }, new Consumer<Throwable>(){
                               @Override
                               public void accept(Throwable throwable)throws Exception{

                                   Toast.makeText(MainActivity.this,""+throwable.getMessage(),Toast.LENGTH_LONG).show();
                               }
                           },new Action(){
                               @Override
                               public void run() throws Exception {
                                   loadData();//refresh data
                               }
                           }

                );
        compositeDisposable.add(disposable);
    }

    private void onGetAllUsersSuccess(List<User> users) {
        userList.clear();
        userList.addAll(users);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_clear:
                deleteAllUsers();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllUsers() {
        Disposable disposable= io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {

            @Override
            public void subscribe(ObservableEmitter<Object> e)throws Exception{
                userRepository.deleteAllUsers();
                e.onComplete();

            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this,""+throwable.getMessage(),Toast.LENGTH_LONG).show();
                    }
                },new Action(){
                    @Override
                    public void run() throws Exception {
                        loadData();//refresh data
                    }
                });
        compositeDisposable.add(disposable);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        menu.setHeaderTitle("Select Actions:");

        menu.add(Menu.NONE,0,Menu.NONE,"UPDATE");
        menu.add(Menu.NONE,1,Menu.NONE,"DELETE");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        final User user= userList.get(info.position);
        switch (item.getItemId())
        {
            case 0: // UPDATE
            {
                final EditText edtName= new EditText(MainActivity.this);
                edtName.setText(user.getName());
                edtName.setHint("Enter your name");
                new AlertDialog.Builder(MainActivity.this)
                .setTitle("Edit")
                .setMessage("Edit user name")
                .setView(edtName)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (TextUtils.isEmpty(edtName.getText().toString()))
                            return;
                        else{
                            user.setName(edtName.getText().toString());
                            updateUser(user);
                        }
                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
            }break;
            case 1://DELETE
            {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Do you want to delete "+user.toString())
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                               deleteUser(user);
                            }
                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
            }
            break;

        }
        return true;
    }

    private void deleteUser(final User user) {
        Disposable disposable= io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {

            @Override
            public void subscribe(ObservableEmitter<Object> e)throws Exception{
                userRepository.DeleteUser(user);
                e.onComplete();

            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this,""+throwable.getMessage(),Toast.LENGTH_LONG).show();
                    }
                },new Action(){
                    @Override
                    public void run() throws Exception {
                        loadData();//refresh data
                    }
                });
        compositeDisposable.add(disposable);

    }

    private void updateUser(final User user) {
        Disposable disposable= io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {

            @Override
            public void subscribe(ObservableEmitter<Object> e)throws Exception{
                userRepository.UpdateUser(user);
                e.onComplete();

            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this,""+throwable.getMessage(),Toast.LENGTH_LONG).show();
                    }
                },new Action(){
                    @Override
                    public void run() throws Exception {
                        loadData();//refresh data
                    }
                });
        compositeDisposable.add(disposable);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
