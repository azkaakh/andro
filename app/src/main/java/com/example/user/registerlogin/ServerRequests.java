package com.example.user.registerlogin;

/**
 * Created by user on 07/03/2016.
 */
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class ServerRequests {
    ProgressDialog progressDialog; //to show a load in bar when a server request is being executed
    public static final int CONNECTION_TIMEOUT = 1000* 15;
    public static final String SERVER_ADDRESS="http://mabox.web44.net/";//fill the name of the domain here to access the php server


    public ServerRequests(Context context){
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");
    }

    public void storeUserDataInBackground(User user, GetUserCallback userCallback){
        progressDialog.show();
        new StoreUserDataAsyncTask(user, userCallback).execute();
    }

    public void fetchUserDataInBackground(User user, GetUserCallback callback){
        progressDialog.show();
        new fetchUserDataAsyncTask(user,callback).execute();

    }

    public class StoreUserDataAsyncTask extends AsyncTask<Void,Void, Void> {
        User user;
        GetUserCallback userCallback;
        public StoreUserDataAsyncTask(User user, GetUserCallback userCallback){
            this.user = user;
            this.userCallback = userCallback;


        }
        @Override
        protected Void doInBackground(Void... params) {

            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("name", user.name));
            dataToSend.add(new BasicNameValuePair("age", user.age + ""));
            dataToSend.add(new BasicNameValuePair("username", user.username));
            dataToSend.add(new BasicNameValuePair("password", user.password));

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS+ "Register.php");
            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                try {
                    client.execute(post);
                } catch (IOException e) {  //E
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return null;


        }

        @Override
        protected void onPostExecute(Void aVoid) {

            progressDialog.dismiss();
            userCallback.done(null);
            super.onPostExecute(aVoid);
        }


    }

    public class fetchUserDataAsyncTask extends AsyncTask<Void,Void, User> { //used to get the processed data (between app and server) from the background
        User user;
        GetUserCallback userCallback;

        public fetchUserDataAsyncTask(User user, GetUserCallback userCallback) {
            this.user = user;
            this.userCallback = userCallback;


        }

        User returnedUser=null;

        @Override
        protected User doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();

            dataToSend.add(new BasicNameValuePair("username", user.username));
            dataToSend.add(new BasicNameValuePair("password", user.password));

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS+ "FetchUserData.php");


            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                try {
                    HttpResponse httpResponse = client.execute(post);

                    HttpEntity entity = httpResponse.getEntity();
                    String result = EntityUtils.toString(entity);
                    JSONObject jObject= new JSONObject(result);

                    if(jObject.length() == 0){
                        returnedUser = null;
                    }else{
                        String name = jObject.getString("name");
                        int age = jObject.getInt("age"+"");

                        returnedUser = new User(name,age,user.username,user.password);
                    }

                } catch (IOException e) {  //E
                    e.printStackTrace();
                } catch (JSONException e) { //E
                    e.printStackTrace();
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return returnedUser;

        }

        @Override
        protected void onPostExecute(User returnedUser) {

            progressDialog.dismiss();
            userCallback.done(returnedUser);
            super.onPostExecute(returnedUser);
        }
    }
}
