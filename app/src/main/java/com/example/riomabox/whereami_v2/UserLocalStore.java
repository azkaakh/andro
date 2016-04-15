package com.example.riomabox.whereami_v2;

/**
 * Created by user on 12/04/2016.
 */
import android.content.Context;
import android.content.SharedPreferences;


public class UserLocalStore {

    public static final String SP_NAME="userDetails";
    SharedPreferences userLocalDatabase; //allows us to store data on the phone

    public UserLocalStore(Context context){
        userLocalDatabase = context.getSharedPreferences(SP_NAME,0); //this is done because SharePreferences is class and in order to
        // use its functions, we pass a context that will carry out this job
        //note: 0 stands for default

    }

    public void storeUserData(User user){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit(); //this allows us to edit what is contained in the SharedPreference
        //  which is 'userLocalDatabase'
        spEditor.putString("name",user.name); //putString(key,data)
        spEditor.putInt("age",user.age);
        spEditor.putString("username",user.username);
        spEditor.putString("password",user.password);

        spEditor.commit();//important in order to update the data


    }

    public User getLoggedInUser(){ //getter for the user data
        String name = userLocalDatabase.getString("name",""); //getString will get the string with key "name", if it didn't find it, it return ""
        String password = userLocalDatabase.getString("password","");
        String username = userLocalDatabase.getString("username","");
        int age = userLocalDatabase.getInt("age",-1);

        User storedUser = new User(name,age,username,password);
        return storedUser;
    }

    public void setUserLoggedIn(boolean loggedIn){//if the user is logged in, loggedIn will be true
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putBoolean("loggedIn",loggedIn);
        spEditor.commit();
    }

    public void clearUserData(){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.clear();
        spEditor.commit();
    }

    public boolean getUserLoggedIn(){
        if(userLocalDatabase.getBoolean("loggedIn",false) == true){
            return true;
        }else{
            return false;
        }
    }
}