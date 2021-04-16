package com.example.parsing;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Handler handler;
    private Button parseButton;
    private EditText textUrl;
    private TextView textName;
    private TextView textDescription;
    private TextView textPrice;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                textName.setText(msg.getData().getString("name"));
                textDescription.setText(msg.getData().getString("description"));
                textPrice.setText(msg.getData().getString("price"));
            }
        };

        textUrl = (EditText) findViewById(R.id.editTextTextPersonName);
        textName = (TextView) findViewById(R.id.textView);
        textDescription = (TextView) findViewById(R.id.textView2);
        textPrice = (TextView) findViewById(R.id.textView3);
        parseButton = (Button) findViewById(R.id.button);
        parseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = textUrl.getText().toString();
                Thread thread = new Thread(new AnotherRunnable(url));
                thread.start();
            }
        });

    }

    class AnotherRunnable implements Runnable {
        private String url;

        public AnotherRunnable(String url){
            this.url = url;
        }

        @Override
        public void run() {
            Document html = null;
            try {
                html = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.114 Safari/537.36").get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Elements name = null, description = null, price = null;
            if (url.contains("tsum.ru")) {
                name = html.select(".item__specifications h1 a");
                description = html.select(".item__description");
                price = html.select("item-price");
            }
            else if (url.contains("lamoda.ru")) {
                name = html.select(".product-title__brand-name");
                description = html.select(".product-title__model-name");
                price = html.select(".product-prices-root");
            }
            else if (url.contains("wildberries.ru")) {
                name = html.select(".brand");
                description = html.select(".name");
                price = html.select(".final-cost");
            }
            else if (url.contains("gloria-jeans.ru")) {
                name = html.select(".js-name-product");
                description = html.select(".js-name-product");
                price = html.select(".js-price-info");
            }
            else if (url.contains("dsquared2.com")) {
                name = html.select(".itemAction-title");
                description = html.select(".itemAction-title");
                price = html.select(".priceUpdater");
            }
            publishProgress(name.text(), description.text(), price.text());
        }

        private void publishProgress(String name, String description, String price) {
            Bundle bundle = new Bundle();
            bundle.putString("name", name);
            bundle.putString("description", description);
            bundle.putString("price", price);
            Message message = new Message();
            message.setData(bundle);
            handler.sendMessage(message);
        }
    }
}