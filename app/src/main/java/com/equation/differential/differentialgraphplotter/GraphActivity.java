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
    GraphView error;

    ArrayList<DataPoint> dataPoints;

    LineGraphSeries<DataPoint> euler;

    LineGraphSeries<DataPoint> impEuler;

    LineGraphSeries<DataPoint> runge;

    LineGraphSeries<DataPoint> errorEuler;
    LineGraphSeries<DataPoint> errorImpEuler;
    LineGraphSeries<DataPoint> errorRunge;

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
        DataPoint[] exactEuler = exactGraph(dp);
        DataPoint[] exactImpEuler = exactGraph(dp2);
        DataPoint[] exactRunge = exactGraph(dp3);
        DataPoint[] er = error(dp, exactEuler);
        DataPoint[] er2 = error(dp2, exactImpEuler);
        DataPoint[] er3 = error(dp3, exactRunge);

        euler = new LineGraphSeries<>(dp);
        impEuler = new LineGraphSeries<>(dp2);
        runge = new LineGraphSeries<>(dp3);

        errorEuler = new LineGraphSeries<>(er);
        errorImpEuler = new LineGraphSeries<>(er2);
        errorRunge = new LineGraphSeries<>(er3);

        euler.setColor(Color.RED);
        impEuler.setColor(Color.GREEN);
        runge.setColor(Color.BLUE);
        euler.setTitle("Euler's Method");
        impEuler.setTitle("Improved Euler's Method");
        runge.setTitle("Runge-Kutta's Method");

        errorEuler.setColor(Color.RED);
        errorImpEuler.setColor(Color.GREEN);
        errorRunge.setColor(Color.BLUE);
        errorEuler.setTitle("Euler's Error");
        errorImpEuler.setTitle("Improved Euler's Error");
        errorRunge.setTitle("Runge-Kutta's Error");

        plotter = findViewById(R.id.plotter);
        plotter.setTitle(getResources().getString(R.string.plot_name));

        plotter.addSeries(euler);
        plotter.addSeries(impEuler);
        plotter.addSeries(runge);

        plotter.getViewport().setScalable(true);
        plotter.getViewport().setScalableY(true);
        plotter.getViewport().setScrollable(true);
        plotter.getViewport().setScrollableY(true);


        plotter.getLegendRenderer().setVisible(true);
        plotter.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);

        error = findViewById(R.id.error);
        error.setTitle(getResources().getString(R.string.error_name));

        error.addSeries(errorEuler);
        error.addSeries(errorImpEuler);
        error.addSeries(errorRunge);

        error.getViewport().setScalable(true);
        error.getViewport().setScalableY(true);
        error.getViewport().setScrollable(true);
        error.getViewport().setScrollableY(true);


        error.getLegendRenderer().setVisible(true);
        error.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);
    }

    private double ivp(double x, double y) {
        return Math.pow(y, 2)/Math.pow(x, 2) - 2*Math.log(x);
    }
    private double funct(double x, double y) {
        return y/x + x/y;
    }
    private double exact_solution(double x,  double c) {
        return x*Math.sqrt(c + Math.log(x));
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
    private DataPoint[] exactGraph(DataPoint[] method) {
        DataPoint[] res = new DataPoint[method.length];
        double c = ivp(method[0].getX(), method[0].getY());
        for (int i = 0; i < method.length; i++) {
            res[i] = new DataPoint(method[i].getX(), exact_solution(method[i].getX(), c));
        }
        return res;
    }
    private DataPoint[] error(DataPoint[] graph, DataPoint[] exact) {
        if (!(graph.length == exact.length)) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.not_match_length), Toast.LENGTH_SHORT).show();
            return null;
        }
        DataPoint[] res = new DataPoint[graph.length];
        for (int i = 0; i < graph.length; i++) {
            if (graph[i].getX() != exact[i].getX()) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.not_match_x), Toast.LENGTH_SHORT).show();
                return null;
            }
            res[i] = new DataPoint(graph[i].getX(), Math.abs(graph[i].getY() - exact[i].getY()));
        }
        return res;
    }
}
