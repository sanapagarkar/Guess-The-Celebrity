package com.sana.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebURLs = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();
    int chosenCeleb=0;
    ImageView downloadImg;
    int locationOfCorrectAnswer;
    String answers[]= new String[4];
    Button button0;
    Button button1;
    Button button2;
    Button button3;

    public class ImageDownload extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {

            downloadImg=(ImageView)findViewById(R.id.imageView);
            try {
                URL url=new URL(urls[0]);
                HttpURLConnection urlConnection=(HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream inputStream=urlConnection.getInputStream();
                Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {
        String result="";
        URL url;
        HttpURLConnection urlConnection = null;

        @Override
        protected String doInBackground(String... strings) {
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();

            }
                return "Sorry";
        }
    }

    public void celebChosen(View view) {
        if (view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer)))
        {
            Toast.makeText(this, "Correct", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "No it was "+celebNames.get(chosenCeleb), Toast.LENGTH_SHORT).show();
        }
        createNewQuestion();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button0=(Button) findViewById(R.id.button0);
        button1=(Button) findViewById(R.id.button1);
        button2=(Button) findViewById(R.id.button2);
        button3=(Button) findViewById(R.id.button3);
        //http://www.posh24.se/kandisar
        String result =null;
        DownloadTask task=new DownloadTask();
        try {
            result=task.execute("http://www.posh24.se/kandisar").get();
            String Splitresult[]=result.split("<div class=\"sidebarContainer\">");

            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m= p.matcher(Splitresult[0]);
            while (m.find()){
                celebURLs.add(m.group(1));
            }
            p=Pattern.compile("alt\"(.*?)\"");
            m=p.matcher(Splitresult[0]);
            while (m.find()){
                celebNames.add(m.group(1));
            }
            createNewQuestion();

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    public void createNewQuestion(){
        Random random= new Random();
        chosenCeleb = random.nextInt(celebURLs.size());

        ImageDownload Imagetask= new ImageDownload();
        Bitmap bitmap;
        try {
            bitmap=Imagetask.execute(celebURLs.get(chosenCeleb)).get();
            downloadImg.setImageBitmap(bitmap);
            locationOfCorrectAnswer=random.nextInt(4);
            int incorrect;
            for(int i=0;i<4;i++)
            {
                if(i==locationOfCorrectAnswer)
                {
                    answers[i]=celebNames.get(i);
                }
                else{
                    incorrect=random.nextInt(celebURLs.size());
                    while (incorrect == locationOfCorrectAnswer)
                        incorrect=random.nextInt(celebURLs.size());
                    answers[i]=celebNames.get(incorrect);
                }
            }
            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
