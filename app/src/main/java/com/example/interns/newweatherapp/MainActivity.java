package com.example.interns.newweatherapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    TextView desciptionNow, description3h, description6h, tempNow, temp3h, temp6h, tempMin, tempMax, windSpeed;
    EditText town;
    ImageView imageNow, image3h, image6h;
    int dayNum, counterNine;
    String all;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dayNum = 0;
        counterNine = 0;

        town = (EditText) findViewById(R.id.town);

        imageNow = (ImageView) findViewById(R.id.nowImage);
        image3h = (ImageView) findViewById(R.id.image3h);
        image6h = (ImageView) findViewById(R.id.image6h);

        Button button = (Button) findViewById(R.id.button);
        Button nextDay = (Button) findViewById(R.id.next);
        Button previousDay = (Button) findViewById(R.id.previous);

        desciptionNow = (TextView) findViewById(R.id.descriptionNow);
        description3h = (TextView) findViewById(R.id.description3h);
        description6h = (TextView) findViewById(R.id.decription6h);
        tempNow = (TextView) findViewById(R.id.tempNow);
        temp3h = (TextView) findViewById(R.id.temp3h);
        temp6h = (TextView) findViewById(R.id.temp6h);
        tempMin = (TextView) findViewById(R.id.tempMin);
        tempMax = (TextView) findViewById(R.id.tempMax);
        windSpeed = (TextView) findViewById(R.id.windSpeed);
        // main button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dayNum = 1;
                new Weather().execute(town.getText().toString()); // async task only do in background, gets json
                update(all);//updates screen
            }
        });

        nextDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dayNum < 5 && dayNum > 0) {
                    dayNum++;
                    update(all);
                }
            }
        });

        previousDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dayNum <= 5 && dayNum > 1) {
                    dayNum--;
                    update(all);
                }
            }
        });

    }

    public void update(String s)
    {


        try {
            JSONArray array = new JSONArray() {};
            JSONObject response = new JSONObject(s);//makes string into json
            JSONArray list = response.getJSONArray("list");//gets main json array


                counterNine = 0;//counts days past
                String we1;
                String we;
                // checks which day
                for (int i = 0; i < list.length(); i++) {
                    we = list.getJSONObject(i).getString("dt_txt");
                    we1 = we.substring(12);
                    if (we1.equals("9:00:00"))
                        counterNine++;
                    if ((dayNum - counterNine == 1) || dayNum == 1) {
                        array.put(list.getJSONObject(i));
                        array.put(list.getJSONObject(i + 1));
                        array.put(list.getJSONObject(i + 2));
                        break;
                    }

                }
                // updating everything
                JSONObject oneDay = array.getJSONObject(0);
                JSONObject main = oneDay.getJSONObject("main");

                tempNow.setText("temperature: " + main.getString("temp"));
                tempMin.setText("temp min: " + main.getString("temp_min"));
                tempMax.setText("temp max: " + main.getString("temp_max"));

                JSONArray weather = oneDay.getJSONArray("weather");
                main = weather.getJSONObject(0);

                desciptionNow.setText(main.getString("description"));
                Picasso.with(MainActivity.this).load("http://openweathermap.org/img/w/" + main.getString("icon") + ".png").into(imageNow);

                JSONObject wind = oneDay.getJSONObject("wind");

                windSpeed.setText("wind speed: " + wind.getString("speed"));

                oneDay = array.getJSONObject(1);
                main = oneDay.getJSONObject("main");

                temp3h.setText("temperature: " + main.getString("temp"));

                weather = oneDay.getJSONArray("weather");
                main = weather.getJSONObject(0);

                description3h.setText(main.getString("description"));
                Picasso.with(MainActivity.this).load("http://openweathermap.org/img/w/" + main.getString("icon") + ".png").into(image3h);

                oneDay = array.getJSONObject(2);
                main = oneDay.getJSONObject("main");

                temp6h.setText("temperature: " + main.getString("temp"));

                weather = oneDay.getJSONArray("weather");
                main = weather.getJSONObject(0);

                description6h.setText(main.getString("description"));
                Picasso.with(MainActivity.this).load("http://openweathermap.org/img/w/" + main.getString("icon") + ".png").into(image6h);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class Weather extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {

            String returnData = "";
            String townURL = "http://api.openweathermap.org/data/2.5/forecast?q=" + params[0] + "&units=metric&appid=387de3884604e9f3a5cafb8d506c6ad5";

            try {
                URL obj = new URL(townURL);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                con.setRequestMethod("GET");

                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                while ((line = br.readLine()) != null)
                    returnData += line;
                con.disconnect();

                int responseCode = con.getResponseCode();

                System.out.println("Response Code : " + responseCode);
                System.out.println("Response Data: " + returnData);

            } catch (Exception e) {
                e.printStackTrace();
            }
            all = returnData;
            return "";
        }

        @Override
        protected void onPostExecute(String s) {

        }

    }
}