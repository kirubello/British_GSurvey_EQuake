package org.me.gcu.British_GSurvey_EQuake.model;
/* Mobile Platform Development Assignment Session 2020/2021 MHI322959/MHI325105
    Student Name : Kirubel Afework
*   Student No: S1732434
*/
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.me.gcu.British_GSurvey_EQuake.R;

public class Details extends AppCompatActivity {
    TextView textTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        String title = i.getStringExtra("location");
        String link = i.getStringExtra("link");
        String pubdate= i.getStringExtra("pubdate");


        ImageView imageView=findViewById(R.id.imageView2);
        String a =link.replace(".html", ".jpg.0");
        Picasso.get().load(a)
              .placeholder(R.drawable.bg_grey)
                .into(imageView);


        textTitle = findViewById(R.id.detailTitle);
        textTitle.setText(title);
        textTitle = findViewById(R.id.pubdate);
        textTitle.setText(pubdate);
    }
}
