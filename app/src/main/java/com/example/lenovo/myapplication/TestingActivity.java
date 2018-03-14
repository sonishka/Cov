package com.example.lenovo.myapplication;

import android.content.DialogInterface;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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

import Database.UserRepository2;
import Local.UserDatabase;
import Local.UserDatasource2;
import Model.User2;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class TestingActivity extends AppCompatActivity {

    private ListView lstUser2 ;
    private FloatingActionButton fab2;


    //Adapter
    List<User2> userList2 = new ArrayList<>();
    ArrayAdapter adapter;

    //Database
    private CompositeDisposable compositeDisposable;
    private UserRepository2 userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testing);

        //Init
        compositeDisposable= new CompositeDisposable();

        //Init View
        lstUser2=(ListView) findViewById(R.id.lstUsers2);
        fab2=(FloatingActionButton)findViewById(R.id.fab2);

        adapter= new ArrayAdapter(this,android.R.layout.simple_expandable_list_item_1,userList2);
        registerForContextMenu(lstUser2);
        lstUser2.setAdapter(adapter);

        //Database
        UserDatabase userDatabase= UserDatabase.getmInstance(this); //create database
        userRepository  = UserRepository2.getmInstance(UserDatasource2.getInstance(userDatabase.user2DAO()));


        //load all data from database
        loadData();

        //Event
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add new user
                //random email
                Disposable disposable= io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {

                    @Override
                    public void subscribe(ObservableEmitter<Object> e)throws Exception{
                        User2 user = new User2("EDMTDev",
                                UUID.randomUUID().toString()+"@gmail.com");

                        userRepository.insertUser2(user);
                        e.onComplete();

                    }
                })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Consumer() {
                            @Override
                            public void accept(Object o) throws Exception {
                                Toast.makeText(TestingActivity.this,"User added",Toast.LENGTH_LONG).show();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Toast.makeText(TestingActivity.this,""+throwable.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    private void loadData() {
        //User RXJava

        Disposable disposable = userRepository.getAllUsers2()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<User2>>(){
                               @Override
                               public void accept(List<User2> users)throws Exception{
                                   onGetAllUsersSuccess(users);
                               }
                           }, new Consumer<Throwable>(){
                               @Override
                               public void accept(Throwable throwable)throws Exception{

                                   Toast.makeText(TestingActivity.this,""+throwable.getMessage(),Toast.LENGTH_LONG).show();
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

    private void onGetAllUsersSuccess(List<User2> users) {
        userList2.clear();
        userList2.addAll(users);
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
                userRepository.deleteAllUsers2();
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
                        Toast.makeText(TestingActivity.this,""+throwable.getMessage(),Toast.LENGTH_LONG).show();
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
        final User2 user= userList2.get(info.position);

        switch (item.getItemId())
        {
            case 0: // UPDATE
            {
                final EditText edtName= new EditText(TestingActivity.this);
                edtName.setText(user.getNom());
                edtName.setHint("Enter your name");
                new AlertDialog.Builder(TestingActivity.this)
                        .setTitle("Edit")
                        .setMessage("Edit user name")
                        .setView(edtName)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Log.e(" onContextItemSelected", "MOOOOOOONNN MEEESSSAAAAGGGGEEE");

                                if (TextUtils.isEmpty(edtName.getText().toString()))
                                    return;
                                else{
                                    user.setNom(edtName.getText().toString());
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
                new AlertDialog.Builder(TestingActivity.this)
                        .setMessage("Do you want to delete "+user.toString())
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //    Log.e(" onContextItemSelected", "Quelque chose");
                                deleteUser(user);
                            }
                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //  Log.e(" onContextItemSelected", "Quelque chose");
                        dialogInterface.dismiss();
                    }
                }).create().show();
            }
            break;

        }
        return true;
    }

    private void deleteUser(final User2 user) {
        Disposable disposable= io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {

            @Override
            public void subscribe(ObservableEmitter<Object> e)throws Exception{
                userRepository.DeleteUser2(user);
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
                        Toast.makeText(TestingActivity.this,""+throwable.getMessage(),Toast.LENGTH_LONG).show();
                    }
                },new Action(){
                    @Override
                    public void run() throws Exception {
                        loadData();//refresh data
                    }
                });
        compositeDisposable.add(disposable);

    }

    private void updateUser(final User2 user) {
        Disposable disposable= io.reactivex.Observable.create(new ObservableOnSubscribe<Object>() {

            @Override
            public void subscribe(ObservableEmitter<Object> e)throws Exception{
                userRepository.UpdateUser2(user);
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
                        Toast.makeText(TestingActivity.this,""+throwable.getMessage(),Toast.LENGTH_LONG).show();
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
