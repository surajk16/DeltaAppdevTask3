package com.example.suraj.deltaappdevtask3;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.text.TextUtils.concat;

public class MainActivity extends AppCompatActivity {

    String JSON_STRING;
    TextView pname,id2,weight2,height2,type2,exp2;
    ImageView pimage;
    EditText editText;
    JSONObject jsonObject;
    JSONArray jsonArray;
    RelativeLayout r;
    Boolean present = false;

    public static ArrayList<String> strings = new ArrayList<>();
    public static ArrayList<Bitmap> bitmaps = new ArrayList<>();
    public static int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        r = (RelativeLayout) findViewById(R.id.pokemonlayout);
        pname = (TextView) findViewById(R.id.pname);
        editText = (EditText) findViewById(R.id.editText);
        weight2 = (TextView) findViewById(R.id.weight2);
        height2 = (TextView) findViewById(R.id.height2);
        type2 = (TextView) findViewById(R.id.type2);
        id2 = (TextView) findViewById(R.id.id2);
        exp2 = (TextView) findViewById(R.id.exp2);
        pimage = (ImageView) findViewById(R.id.pimage);


    }

    public void search (View v) {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        int index = 0;
        present = false;
        String s = editText.getText().toString();
        s = s.trim().toUpperCase();

        for (int i=0; i<count; i++) {
            String t = strings.get(i);
            if (s.equalsIgnoreCase(t)) {
                present = true;
                index = i;
                break;
            }
        }

        if (present) {
            String sTemp = strings.get(index);
            strings.remove(index); strings.add(0,sTemp);
            Bitmap bTemp = bitmaps.get(index);
            bitmaps.remove(index); bitmaps.add(0,bTemp);
        }

        new BackgroundTask().execute();
    }

    public void history (View v) {
        Intent i =new Intent(this,History.class);
        startActivity(i);
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    class BackgroundTask extends AsyncTask<Void,Void,String>
    {
        String json_url;

        @Override
        protected String doInBackground(Void... params) {
            try {
                URL url = new URL(json_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();

                while ((JSON_STRING=bufferedReader.readLine())!=null)
                {
                    stringBuilder.append(JSON_STRING+"\n");
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                String s = stringBuilder.toString().trim();

                jsonObject = new JSONObject(s.substring(s.indexOf("{"), s.lastIndexOf("}") + 1));
                jsonObject = (JSONObject) jsonObject.get("sprites");
                String picurl = jsonObject.get("front_default").toString();
                bitmaps.add(0,getBitmapFromURL(picurl));

                return s;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.i("me","here1");
            } catch (IOException e) {
                e.printStackTrace();
                Log.i("me","here2");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            json_url = "https://pokeapi.co/api/v2/pokemon/"+editText.getText().toString().toLowerCase().trim()+"/";

        }

        @Override
        protected void onPostExecute(String s) {

            editText.setText("");
            editText.clearFocus();
            View view = getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            if (s!=null) {
                r.setVisibility(View.VISIBLE);


                try {
                    jsonObject = new JSONObject(s.substring(s.indexOf("{"), s.lastIndexOf("}") + 1));
                    weight2.setText(jsonObject.get("weight").toString());
                    height2.setText(jsonObject.get("height").toString());
                    id2.setText(jsonObject.get("id").toString());
                    exp2.setText(jsonObject.get("base_experience").toString());
                    pname.setText(jsonObject.get("name").toString().toUpperCase());
                    if (!present) strings.add(0,jsonObject.get("name").toString().toUpperCase());

                    /*JSONObject picture = jsonObject;
                    picture = (JSONObject) picture.get("sprites");
                    String picurl = picture.get("front_default").toString();
                    Picasso.with(getApplicationContext()).load(picurl).into(pimage);
                    */
                    pimage.setImageBitmap(bitmaps.get(0));
                    if (present) bitmaps.remove(0);
                    count++;

                    jsonArray = jsonObject.getJSONArray("types");

                    String s1 = "";
                    int counts = 0;
                    while (counts < jsonArray.length()) {
                        JSONObject J1 = jsonArray.getJSONObject(counts);
                        J1 = (JSONObject) J1.get("type");
                        if (counts == 0) s1 = s1 + J1.get("name").toString();
                        else s1 = s1 + " | " + J1.get("name").toString();
                        counts++;
                    }

                    type2.setText(s1);

                    Toast.makeText(getApplicationContext(),"Item loaded succesfully",Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            else Toast.makeText(getApplicationContext(),"Entered pokemon not available.",Toast.LENGTH_LONG).show();
        }
    }
}
