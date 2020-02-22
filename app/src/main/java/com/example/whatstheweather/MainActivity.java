package com.example.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Button button;
    EditText editText;
    String cityName="";
    TextView textView;

    DownloadWeatherData downloadWeatherData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        editText = findViewById(R.id.editText);
        textView = findViewById(R.id.textView2);
    }

    public void getWeatherInfo(View view){

        cityName =  editText.getText().toString();

       downloadWeatherData = new DownloadWeatherData();
       downloadWeatherData.execute("http://openweathermap.org/data/2.5/weather?q="+cityName+"&appid=b6907d289e10d714a6e88b30761fae22");

        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public class DownloadWeatherData extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            URL url;
            HttpURLConnection httpURLConnection;
            String result ="";

            try{
                url = new URL(strings[0]);
                httpURLConnection =(HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);

                int data = reader.read();
                while(data != -1){
                    char c = (char) data;
                    result += c;

                    data = reader.read();
                }
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Couldn't find city :(", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return "Failed to download data";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i("Weather Data", s);

            String weather;
            String temperature;

            try {
                JSONObject jsonObject = new JSONObject(s);
                weather = jsonObject.getString("weather");
                temperature = String.valueOf(jsonObject.getJSONObject("main").getDouble("temp"));
                Log.i("Temperature", temperature);

                JSONArray jsonArray = new JSONArray(weather);
                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    Log.i("main", jsonObject1.getString("main"));
                    Log.i("description", jsonObject1.getString("description"));


                    textView.setText("Temp: " + temperature + " C " + "\n" +
                            jsonObject1.getString("main") + " : " + jsonObject1.getString("description"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Couldn't find city :(", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
