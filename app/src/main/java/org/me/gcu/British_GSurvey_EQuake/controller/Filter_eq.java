package org.me.gcu.British_GSurvey_EQuake.controller;

/* Mobile Platform Development Assignment Session 2020/2021 MHI322959/MHI325105
    Student Name : Kirubel Afework
*   Student No: S1732434
*/

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import org.me.gcu.British_GSurvey_EQuake.R;

import java.util.Calendar;
import java.util.TimeZone;


public class Filter_eq extends  AppCompatActivity {

    TextView textTitle;
    private Button dateRange;
    private TextView dateDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_eq);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        Intent i = getIntent();
//        String title = i.getStringExtra("location");
//        String link = i.getStringExtra("link");
//        String pubdate= i.getExtras().getString("pubdate");
//
//        ImageView imageView=findViewById(R.id.imageView2);
//        String a =link.replace(".html", ".jpg.0");
//        Picasso.get().load(a)
//                .placeholder(R.drawable.bg_grey)
//                .into(imageView);
//
//        textTitle = findViewById(R.id.detailTitle);
//        textTitle.setText(title);
//        textTitle = findViewById(R.id.pubdate);
//        textTitle.setText(pubdate);

        dateRange = findViewById(R.id.date_Rangepicker);


        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.clear();

        long today = MaterialDatePicker.todayInUtcMilliseconds();


        calendar.setTimeInMillis(today);

        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        long january = calendar.getTimeInMillis();

        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        long december = calendar.getTimeInMillis();

        CalendarConstraints.Builder constraintBuilder = new CalendarConstraints.Builder();
        constraintBuilder.setStart(january);
        constraintBuilder.setEnd(today);
        constraintBuilder.setValidator(DateValidatorPointBackward.now());

        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();
        builder.setTitleText("select a date");
        // builder.setSelection(today);
        builder.setCalendarConstraints(constraintBuilder.build());

        final MaterialDatePicker materialDatePicker = builder.build();

        dateRange.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                materialDatePicker.show(getSupportFragmentManager(), "date_picker");
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                dateDisplay.setText("Selected date" + materialDatePicker.getHeaderText());
            }
        });
    }
}
