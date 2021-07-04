package app.aacr.studypoint.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

import app.aacr.studypoint.R;

public class webview extends AppCompatActivity {
    private PDFView pdfView;
//    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        String pdfUrl = getIntent().getExtras().getString("uri");
        String id = getIntent().getExtras().getString("id");
        pdfView = findViewById(R.id.pdfView);
        new RetrivedPdffromFirebase().execute(pdfUrl);
        findViewById(R.id.add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.info.containsKey("downloaded_docs") && ((HashMap)MainActivity.info.get("downloaded_docs")).containsKey(id)){
                    Snackbar.make(pdfView, "File Already Exist...", Snackbar.LENGTH_LONG).show();
                }else {
                    HashMap add_pdf = new HashMap();
                    add_pdf.put(id,""+System.currentTimeMillis());
                    FirebaseDatabase.getInstance().getReference().child("Users").child(MainActivity.mobile).child("downloaded_docs").updateChildren(add_pdf);
                    Snackbar.make(pdfView, "File Added Successfully...", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    class RetrivedPdffromFirebase extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream pdfStream = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                if (httpURLConnection.getResponseCode() == 200) {
                    pdfStream = new BufferedInputStream(httpURLConnection.getInputStream());
                }

            } catch (IOException e) {
                return null;
            }
            return pdfStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            pdfView.fromStream(inputStream).load();
        }
    }
}