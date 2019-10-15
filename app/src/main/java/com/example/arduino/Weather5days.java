package com.example.arduino;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Weather5days extends AppCompatActivity {

    double lat, lon;//위도 경도 값
    private GpsInfo gps;
    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 1000;
    private final int PERMISSIONS_ACCESS_COARSE_LOCATION = 1001;
    private boolean isAccessFineLocation = false;
    private boolean isAccessCoarseLocation = false;
    private boolean isPermission = false;

    TextView todaytxt,tomorrowtxt,tomorrow2txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        gps = new GpsInfo(Weather5days.this);
        Weather5days.MyAsyncTask myAsyncTask=new Weather5days.MyAsyncTask();
        myAsyncTask.execute();

        todaytxt = (TextView)findViewById(R.id.Txt1);
        tomorrowtxt= (TextView)findViewById(R.id.Txt2);
        tomorrow2txt = (TextView)findViewById(R.id.Txt3);
    }



    private void fine_weather(String url) {
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{
                    JSONArray lists=response.getJSONArray("list");
                    JSONObject today=lists.getJSONObject(0);
                    JSONObject tomorrow=lists.getJSONObject(8);
                    JSONObject tomorrow2=lists.getJSONObject(16);

                    String todayweather=today.getJSONArray("weather").getJSONObject(0).getString("description");
                    String tomorrowweather=tomorrow.getJSONArray("weather").getJSONObject(0).getString("description");
                    String tomorrow2weather=tomorrow2.getJSONArray("weather").getJSONObject(0).getString("description");

                    todaytxt.setText(new WeatherHangeul(todayweather).getWeather());
                    tomorrowtxt.setText(new WeatherHangeul(tomorrowweather).getWeather());
                    tomorrow2txt.setText(new WeatherHangeul(tomorrow2weather).getWeather());

                    JSONArray array = response.getJSONArray("weather");

                    JSONObject main_object=response.getJSONObject("main");
                    JSONObject object=array.getJSONObject(0);

                    JSONObject wind_object=response.getJSONObject("wind");
                    String wind_speed=String.valueOf(wind_object.getDouble("speed"));
                    String mtemp = String.valueOf(main_object.getDouble("temp"));
                    String mhumi = String.valueOf(main_object.getDouble("humidity"));
                    String mdes = object.getString("description");
                    String mcity = response.getString("name");

                    /*temp.setText(mtemp);
                    city.setText(mcity);
                    WeatherHangeul weatherHangeul = new WeatherHangeul(mdes);
                    mdes=weatherHangeul.getWeather();
                    weather.setText(mdes);
                    humidity.setText(mhumi);
                    wind.setText(wind_speed);*/

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf=new SimpleDateFormat("EEEE-MM-DD");
                    String formatted_date=sdf.format(calendar.getTime());

                    /*date.setText(formatted_date);

                    double temp_int = Double.parseDouble(mtemp);
                    double centi = (temp_int-32)/1.8000;
                    centi=Math.round(centi);
                    int i=(int)centi;
                    temp.setText(i+"°C");*/

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue queue= Volley.newRequestQueue(this);
        queue.add(jor);
    }



    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_ACCESS_FINE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            isAccessFineLocation = true;

        } else if (requestCode == PERMISSIONS_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            isAccessCoarseLocation = true;
        }

        if (isAccessFineLocation && isAccessCoarseLocation) {
            isPermission = true;
        }
    }

    // 전화번호 권한 요청
    private void callPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_ACCESS_FINE_LOCATION);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {
            isPermission = true;
        }
    }

    public class MyAsyncTask extends AsyncTask<Integer, Integer, String> {

        @Override
        protected String doInBackground(Integer... integers) {

            lat = gps.getLatitude();
            lon = gps.getLongitude();
            callPermission();  // 권한 요청을 해야 함
            String url = "https://api.openweathermap.org/data/2.5/forecast?"+
                    "lat=" + lat +
                    "&lon="+ lon +
                    "&appid=25101ddb40fe8f611b992f17f1d60b23" +
                    "&cnt=" + 18;
            Log.e("url=",url);
            return url;
        }

        @Override
        protected void onPostExecute(String url) {
            super.onPostExecute(url);
            fine_weather(url);
            Log.e("onresume","의 마지막부분");
        }
    }

}