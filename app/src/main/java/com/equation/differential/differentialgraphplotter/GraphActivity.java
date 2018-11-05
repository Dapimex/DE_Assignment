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
    GraphView globalError;

    ArrayList<DataPoint> dataPoints;

    LineGraphSeries<DataPoint> euler;
    LineGraphSeries<DataPoint> impEuler;
    LineGraphSeries<DataPoint> runge;
    LineGraphSeries<DataPoint> exact;

    LineGraphSeries<DataPoint> errorEuler;
    LineGraphSeries<DataPoint> errorImpEuler;
    LineGraphSeries<DataPoint> errorRunge;

    private double step;
    private int steps;
    private double x0;
    private double y0;
    private double x_fin;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.graph_layout);
        try {
            x0 = Double.parseDouble(getIntent().getSerializableExtra("x0").toString());
            y0 = Double.parseDouble(getIntent().getSerializableExtra("y0").toString());
            x_fin = Double.parseDouble(getIntent().getSerializableExtra("x_fin").toString());
            steps = Integer.parseInt(getIntent().getSerializableExtra("step").toString());
            step = (x_fin - x0)/steps;
            dataPoints = new ArrayList<>();
            createGraph();
            createGlobalGraph();
        }catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    /**
     * method for creating main graph and local error graph
     */
    private void createGraph() {
        DataPoint[] dp = euler(x0, y0, x_fin, step);
        DataPoint[] dp2 = impEuler(x0, y0, x_fin, step);
        DataPoint[] dp3 = runge(x0, y0, x_fin, step);
        DataPoint[] exactDP = exactGraph(x0, y0, x_fin, step);
        DataPoint[] er = error(dp, exactDP);
        DataPoint[] er2 = error(dp2, exactDP);
        DataPoint[] er3 = error(dp3, exactDP);

        euler = new LineGraphSeries<>(dp);
        impEuler = new LineGraphSeries<>(dp2);
        runge = new LineGraphSeries<>(dp3);
        exact = new LineGraphSeries<>(exactDP);

        errorEuler = new LineGraphSeries<>(er);
        errorImpEuler = new LineGraphSeries<>(er2);
        errorRunge = new LineGraphSeries<>(er3);

        euler.setColor(Color.RED);
        impEuler.setColor(Color.GREEN);
        runge.setColor(Color.BLUE);
        exact.setColor(Color.YELLOW);
        euler.setTitle("Euler's Method");
        impEuler.setTitle("Improved Euler's Method");
        runge.setTitle("Runge-Kutta's Method");
        exact.setTitle("Exact Solution");

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
        plotter.addSeries(exact);

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

    /**
     * method for creating global error graph
     */
    private void createGlobalGraph() {
        DataPoint[] globalEuler = new DataPoint[steps];
        DataPoint[] globalImpEuler = new DataPoint[steps];
        DataPoint[] globalRunge = new DataPoint[steps];

        for (int i = 0; i < steps; i++) {
            double init_step = (x_fin - x0)/(i+1);

            DataPoint[] euler = euler(x0, y0, x_fin, init_step);
            DataPoint[] impEuler = impEuler(x0, y0, x_fin, init_step);
            DataPoint[] runge = runge(x0, y0, x_fin, init_step);
            DataPoint[] exact = exactGraph(x0, y0, x_fin, init_step);

            DataPoint[] errorEuler = error(euler, exact);
            DataPoint[] errorImpEuler = error(impEuler, exact);
            DataPoint[] errorRunge = error(runge, exact);

            globalEuler[i] = new DataPoint((i + 1), globalError(errorEuler));
            globalImpEuler[i] = new DataPoint((i + 1), globalError(errorImpEuler));
            globalRunge[i] = new DataPoint((i + 1), globalError(errorRunge));
        }

        LineGraphSeries<DataPoint> glEuler = new LineGraphSeries<>(globalEuler);
        LineGraphSeries<DataPoint> glImpEuler = new LineGraphSeries<>(globalImpEuler);
        LineGraphSeries<DataPoint> glRunge = new LineGraphSeries<>(globalRunge);

        glEuler.setTitle(getResources().getString(R.string.glEuler));
        glImpEuler.setTitle(getResources().getString(R.string.glImpEuler));
        glRunge.setTitle(getResources().getString(R.string.glRunge));

        glEuler.setColor(Color.RED);
        glImpEuler.setColor(Color.GREEN);
        glRunge.setColor(Color.BLUE);

        globalError = findViewById(R.id.gl_error);
        globalError.setTitle(getResources().getString(R.string.global_error_name));

        globalError.addSeries(glEuler);
        globalError.addSeries(glImpEuler);
        globalError.addSeries(glRunge);

        globalError.getViewport().setScalable(true);
        globalError.getViewport().setScalableY(true);
        globalError.getViewport().setScrollable(true);
        globalError.getViewport().setScrollableY(true);


        globalError.getLegendRenderer().setVisible(true);
        globalError.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);
    }

    /**
     * finds average value of error in local error function
     * @param error graph of local error for some step
     * @return average value of error (y-axis in point of global error graph)
     */
    private double globalError(DataPoint[] error) {
        double res = 0;
        for (DataPoint e:error) {
            res += e.getY();
        }
        return res/error.length;
    }

    /**
     * Solving initial value problem (according to exact solution)
     * @param x initial value of x
     * @param y initial value of y
     * @return value of C (constant)
     */
    private double ivp(double x, double y) {
        return Math.pow(y, 2)/Math.pow(x, 2) - 2*Math.log(x);
    }

    /**
     * Solving function y'=f(x,y) (this case, y/x + x/y)
     * @param x value of x
     * @param y value of y
     * @return y' for that case
     */
    private double funct(double x, double y) {
        return x/y + y/x;
    }

    /**
     * Solving exact solution in each point
     * @param x value of x (one point)
     * @param c constant value, solved in IVP for this input
     * @return y in that point
     */
    private double exact_solution(double x,  double c) {
        if (y0 > 0) return x*Math.sqrt(c + 2*Math.log(x));
        else return -x*Math.sqrt(c + 2*Math.log(x));
    }

    /**
     * All next methods are for finding points according to numerical method (or exact). Also for error
     * @param x0 initial value of x
     * @param y0 initial value of y
     * @param x_fin final value of x
     * @param step actually step
     * @return array of DataPoints, that will be shown in graph
     */
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
    private DataPoint[] exactGraph(Double x0, Double y0, double x_fin, double step) {
        dataPoints.clear();
        double c = ivp(x0, y0);
        while (x0 <= x_fin) {
            dataPoints.add(new DataPoint(x0, y0));
            x0 += step;
            y0 = exact_solution(x0, c);
        }
        DataPoint[] res = new DataPoint[dataPoints.size()];
        for (int i = 0; i < dataPoints.size(); i++) {
            res[i] = dataPoints.get(i);
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
