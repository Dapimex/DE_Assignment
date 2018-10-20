package com.equation.differential.differentialgraphplotter;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class GraphActivity extends AppCompatActivity {

    GraphView plotter;

    ArrayList<DataPoint> dataPoints;

    LineGraphSeries<DataPoint> euler;

    LineGraphSeries<DataPoint> impEuler;

    LineGraphSeries<DataPoint> runge;

    private double step;
    private double x0;
    private double y0;
    private double x_fin;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.graph_layout);
        try {
            step = Double.parseDouble(getIntent().getSerializableExtra("step").toString());
            x0 = Double.parseDouble(getIntent().getSerializableExtra("x0").toString());
            y0 = Double.parseDouble(getIntent().getSerializableExtra("y0").toString());
            x_fin = Double.parseDouble(getIntent().getSerializableExtra("x_fin").toString());
            dataPoints = new ArrayList<>();
            createGraph();
        }catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }


    private void createGraph() {
        DataPoint[] dp = euler(x0, y0, x_fin, step);
        DataPoint[] dp2 = impEuler(x0, y0, x_fin, step);
        DataPoint[] dp3 = runge(x0, y0, x_fin, step);

        euler = new LineGraphSeries<>(dp);
        impEuler = new LineGraphSeries<>(dp2);
        runge = new LineGraphSeries<>(dp3);

        euler.setColor(Color.RED);
        impEuler.setColor(Color.GREEN);
        runge.setColor(Color.BLUE);
        euler.setTitle("Euler's Method");
        impEuler.setTitle("Improved Euler's Method");
        runge.setTitle("Runge-Kutta's Method");

        plotter = findViewById(R.id.plotter);
        plotter.addSeries(euler);
        plotter.addSeries(impEuler);
        plotter.addSeries(runge);

        plotter.getViewport().setScalable(true);
        plotter.getViewport().setScalableY(true);
        plotter.getViewport().setScrollable(true);
        plotter.getViewport().setScrollableY(true);


        plotter.getLegendRenderer().setVisible(true);
        plotter.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);
    }

    private double funct(double x, double y) {
        return y/x + x/y;
    }

    private DataPoint[] euler(Double x0, Double y0, double x_fin, double step) {
        dataPoints.clear();
        while (x0 <= x_fin) {
            dataPoints.add(new DataPoint(x0, y0));
            double d = step*funct(x0, y0);
            x0 += step;
            y0 += d;
            if (x0.isNaN() || y0.isNaN()) break;
        }
        DataPoint[] res = new DataPoint[dataPoints.size()];
        for (int i = 0; i < dataPoints.size(); i++) res[i] = dataPoints.get(i);

        return res;
    }
    private DataPoint[] impEuler(Double x0, Double y0, double x_fin, double step) {
        dataPoints.clear();
        while (x0 <= x_fin) {
            dataPoints.add(new DataPoint(x0, y0));
            double d = step*funct(x0 + step/2, y0 + step/2*funct(x0, y0));
            x0 += step;
            y0 += d;
            if (x0.isNaN() || y0.isNaN()) break;
        }
        DataPoint[] res = new DataPoint[dataPoints.size()];
        for (int i = 0; i < dataPoints.size(); i++) res[i] = dataPoints.get(i);

        return res;
    }
    private DataPoint[] runge(Double x0, Double y0, double x_fin, double step) {
        dataPoints.clear();
        while (x0 <= x_fin) {
            dataPoints.add(new DataPoint(x0, y0));
            double k1 = funct(x0, y0);
            double k2 = funct(x0 + step/2, y0 + step/2*k1);
            double k3 = funct(x0 + step/2, y0 + step/2*k2);
            double k4 = funct(x0 + step, y0 + step*k3);
            double d = step/6*(k1+2*k2+2*k3+k4);
            x0 += step;
            y0 += d;
            if (x0.isNaN() || y0.isNaN()) break;
        }
        DataPoint[] res = new DataPoint[dataPoints.size()];
        for (int i = 0; i < dataPoints.size(); i++) res[i] = dataPoints.get(i);

        return res;
    }

}
