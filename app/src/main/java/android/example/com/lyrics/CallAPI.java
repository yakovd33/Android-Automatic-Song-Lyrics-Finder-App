package android.example.com.lyrics;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
//import javax.net.ssl.HttpsURLConnection;
import java.net.HttpURLConnection;

/**
 * Created by yakov on 1/20/2018.
 */

public class CallAPI extends AsyncTask<String, String, String> {
    private static String result = "";
    private apiActivity activity;
    private int code = 0;

    public CallAPI (apiActivity activity, int code) {
        this.activity = activity;
        this.code = code;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        this.activity.apiCallback(this.code, this.getResult());
        super.onPostExecute(s);
    }

    @Override
    protected String doInBackground(String... params) {
        String urlString = params[0];

        String inputLine;
        try {
            //Create a URL object holding our url
            URL myUrl = new URL(urlString);
            //Create a connection
            HttpURLConnection connection =(HttpURLConnection) myUrl.openConnection();

            //Connect to our url
            connection.connect();
            //Create a new InputStreamReader
            InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
            //Create a new buffered reader and String Builder
            BufferedReader reader = new BufferedReader(streamReader);
            StringBuilder stringBuilder = new StringBuilder();
            //Check if the line we are reading is not null
            while((inputLine = reader.readLine()) != null){
                stringBuilder.append(inputLine);
            }
            //Close our InputStream and Buffered reader
            reader.close();
            streamReader.close();
            //Set our result equal to our stringBuilder
            this.result = stringBuilder.toString();
        } catch (Exception e) {
            Log.i("Exception msg: ", e.getMessage());
        }

        return this.result;
    }

    public String getResult () {
        return this.result;
    }
}