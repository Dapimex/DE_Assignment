package com.equation.differential.differentialgraphplotter;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

public class MainActivity extends AppCompatActivity {


    TextView step;

    TextView x0;

    TextView y0;

    TextView x_fin;

    Button start;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        step = findViewById(R.id.steps);
        x0 = findViewById(R.id.x0);
        y0 = findViewById(R.id.y0);
        x_fin = findViewById(R.id.x_fin);
        start = findViewById(R.id.start);
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GraphActivity.class);
                intent
                        .putExtra("step", step.getText().toString())
                        .putExtra("x0", x0.getText().toString())
                        .putExtra("y0", y0.getText().toString())
                        .putExtra("x_fin", x_fin.getText().toString());

                startActivity(intent);
            }
        };
        start.setOnClickListener(listener);
    }

}
