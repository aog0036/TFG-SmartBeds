package com.example.smartbeds;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class BedChartsActivity extends AppCompatActivity {

    private Context context = this;

    private TextView stateView;

    private int state = -1;
    private double prob=0.0;
    private float probFloat=0.0f;
    private int p1;
    private int p2;
    private int p3;
    private int p4;
    private int p5;
    private int p6;
    private int hr;
    private int rr;
    private int sv;
    private int hrv;
    private int b2b;

    private String date;

    private BedStreaming bedStreaming;

    private LineDataSet dataSetProb;
    private LineData lineDataProb;
    private List<Entry> listProb = Collections.synchronizedList(new ArrayList<Entry>());
    private LineChart chartProb;

    private LineDataSet dataSetPressures;
    private LineData lineDataPressures;
    private List<Entry> listP1 = Collections.synchronizedList(new ArrayList<Entry>());
    private List<Entry> listP2 = Collections.synchronizedList(new ArrayList<Entry>());
    private List<Entry> listP3 = Collections.synchronizedList(new ArrayList<Entry>());
    private List<Entry> listP4 = Collections.synchronizedList(new ArrayList<Entry>());
    private List<Entry> listP5 = Collections.synchronizedList(new ArrayList<Entry>());
    private List<Entry> listP6 = Collections.synchronizedList(new ArrayList<Entry>());
    private LineChart chartPressures;

    private LineDataSet dataSetVital;
    private LineData lineDataVital;
    private List<Entry> listHR = Collections.synchronizedList(new ArrayList<Entry>());
    private List<Entry> listRR = Collections.synchronizedList(new ArrayList<Entry>());
    private List<Entry> listSV = Collections.synchronizedList(new ArrayList<Entry>());
    private List<Entry> listHRV = Collections.synchronizedList(new ArrayList<Entry>());
    private List<Entry> listB2B = Collections.synchronizedList(new ArrayList<Entry>());
    private LineChart chartHR;
    private LineChart chartRR;
    private LineChart chartSV;
    private LineChart chartHRV;
    private LineChart chartB2B;

    private int counter=0;


    @Override
    protected void onDestroy(){
        super.onDestroy();
        bedStreaming.stop();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bed_charts);

        Session session = Session.getInstance();
        if(session.getToken()==null) {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        }

        Bundle b = getIntent().getExtras();
        String bedName = b.getString("bedName");

        TextView title = (TextView) findViewById(R.id.bed_charts_title);
        title.setText(bedName);

        stateView = (TextView) findViewById(R.id.bed_charts_state);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.bed_charts_tabs);

        String namespace=null;
        try {
            String urlParameters = "token=" + session.getToken() + "&bedname=" + bedName;
            JSONObject resultado = APIUtil.petitionAPI("/api/bed", urlParameters);
            namespace = (String) resultado.get("namespace");
        }catch (JSONException e){
            e.printStackTrace();
        }

        bedStreaming = new BedStreaming(0, bedName, namespace, this);

        //crear gráfica de probabilidad
        chartProb = (LineChart) findViewById(R.id.bed_charts_prob);
        generateChart(chartProb, 5, 0, 1);

        //crear gráfica p1
        chartPressures = (LineChart) findViewById(R.id.bed_charts_p1);
        generateChart(chartPressures, 5, 0, 10);

        //crear gráfica HR
        chartHR = (LineChart) findViewById(R.id.bed_charts_hr);
        generateChart(chartHR, -1, -1, -1);

        //crear gráfica RR
        chartRR = (LineChart) findViewById(R.id.bed_charts_rr);
        generateChart(chartRR, -1, -1, -1);

        //crear gráfica SV
        chartSV = (LineChart) findViewById(R.id.bed_charts_sv);
        generateChart(chartSV, -1, -1, -1);

        //crear gráfica HRV
        chartHRV = (LineChart) findViewById(R.id.bed_charts_hrv);
        generateChart(chartHRV, -1, -1, -1);

        //crear gráfica B2B
        chartB2B = (LineChart) findViewById(R.id.bed_charts_b2b);
        generateChart(chartB2B, -1, -1, -1);

    }

    public void refresh(JSONObject resultado){
        state=0;
        try {
            JSONArray array = (JSONArray) resultado.get("result");
            state = (int) array.get(0);
            prob = (double) array.get(1);
            probFloat = (float) prob;
            date = (String) resultado.get("instance");
            JSONArray pressures = (JSONArray) resultado.get("pressure");
            p1 = (int) pressures.get(0);
            p2 = (int) pressures.get(1);
            p3 = (int) pressures.get(2);
            p4 = (int) pressures.get(3);
            p5 = (int) pressures.get(4);
            p6 = (int) pressures.get(5);
            JSONArray vitals = (JSONArray) resultado.get("vital");
            hr = (int) vitals.get(0);
            rr = (int) vitals.get(1);
            sv = (int) vitals.get(2);
            hrv = (int) vitals.get(3);
            b2b = (int) vitals.get(4);

        }catch (JSONException e){
            e.printStackTrace();
        }catch(ClassCastException e){
            probFloat = 0.0f;
        }

        if(counter>270){
            listProb.remove(0);
            listP1.remove(0);
            listP2.remove(0);
            listP3.remove(0);
            listP4.remove(0);
            listP5.remove(0);
            listP6.remove(0);
            listHR.remove(0);
            listRR.remove(0);
            listSV.remove(0);
            listHRV.remove(0);
            listB2B.remove(0);
        }
        listProb.add(new Entry(counter,probFloat));
        listP1.add(new Entry(counter, p1));
        listP2.add(new Entry(counter, p2));
        listP3.add(new Entry(counter, p3));
        listP4.add(new Entry(counter, p4));
        listP5.add(new Entry(counter, p5));
        listP6.add(new Entry(counter, p6));
        listHR.add(new Entry(counter, hr));
        listRR.add(new Entry(counter, rr));
        listSV.add(new Entry(counter, sv));
        listHRV.add(new Entry(counter, hrv));
        listB2B.add(new Entry(counter, b2b));
        counter++;
        Log.d("COUNTER", ""+counter);
        Log.d("ARRAY", listProb.toString());

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //modificar etiqueta de estado
                switch (state){
                    case 0:
                        stateView.setText("Estado: dormido");
                        break;
                    case 1:
                        stateView.setText("Estado: crisis epiléptica");
                        break;
                    case 2:
                        stateView.setText("Estado: cama vacía");
                        break;
                    case 3:
                        stateView.setText("Estado: datos insuficientes");
                }

                //actualizar gráficas
                updateChart(chartProb, listProb, dataSetProb, lineDataProb, ContextCompat.getColor(context, R.color.chart_1), "Probabilidad de ataque");
                updateChart(chartHR, listHR, dataSetVital, lineDataVital, ContextCompat.getColor(context, R.color.chart_1), "HR");
                updateChart(chartRR, listRR, dataSetVital, lineDataVital, ContextCompat.getColor(context, R.color.chart_2), "RR");
                updateChart(chartSV, listSV, dataSetVital, lineDataVital, ContextCompat.getColor(context, R.color.chart_3), "SV");
                updateChart(chartHRV, listHRV, dataSetVital, lineDataVital, ContextCompat.getColor(context, R.color.chart_4), "HRV");
                updateChart(chartB2B, listB2B, dataSetVital, lineDataVital, ContextCompat.getColor(context, R.color.chart_5), "B2B");

                //actualizar grafica Pressures
                lineDataPressures = new LineData();

                //P1
                dataSetPressures = new LineDataSet(listP1, "P1");
                dataSetPressures.setColor(ContextCompat.getColor(context, R.color.chart_1));
                dataSetPressures.setLineWidth(3.0f);
                dataSetPressures.setDrawValues(false);
                dataSetPressures.setDrawCircles(false);
                lineDataPressures.addDataSet(dataSetPressures);
                chartPressures.setData(lineDataPressures);

                //P2
                dataSetPressures = new LineDataSet(listP2, "P2");
                dataSetPressures.setColor(ContextCompat.getColor(context, R.color.chart_2));
                dataSetPressures.setLineWidth(3.0f);
                dataSetPressures.setDrawValues(false);
                dataSetPressures.setDrawCircles(false);
                lineDataPressures.addDataSet(dataSetPressures);
                chartPressures.setData(lineDataPressures);

                //P3
                dataSetPressures = new LineDataSet(listP3, "P3");
                dataSetPressures.setColor(ContextCompat.getColor(context, R.color.chart_3));
                dataSetPressures.setLineWidth(3.0f);
                dataSetPressures.setDrawValues(false);
                dataSetPressures.setDrawCircles(false);
                lineDataPressures.addDataSet(dataSetPressures);
                chartPressures.setData(lineDataPressures);

                //P4
                dataSetPressures = new LineDataSet(listP4, "P4");
                dataSetPressures.setColor(ContextCompat.getColor(context, R.color.chart_4));
                dataSetPressures.setLineWidth(3.0f);
                dataSetPressures.setDrawValues(false);
                dataSetPressures.setDrawCircles(false);
                lineDataPressures.addDataSet(dataSetPressures);
                chartPressures.setData(lineDataPressures);

                //P5
                dataSetPressures = new LineDataSet(listP5, "P5");
                dataSetPressures.setColor(ContextCompat.getColor(context, R.color.chart_5));
                dataSetPressures.setLineWidth(3.0f);
                dataSetPressures.setDrawValues(false);
                dataSetPressures.setDrawCircles(false);
                lineDataPressures.addDataSet(dataSetPressures);
                chartPressures.setData(lineDataPressures);

                //P6
                dataSetPressures = new LineDataSet(listP6, "P6");
                dataSetPressures.setColor(ContextCompat.getColor(context, R.color.chart_6));
                dataSetPressures.setLineWidth(3.0f);
                dataSetPressures.setDrawValues(false);
                dataSetPressures.setDrawCircles(false);
                lineDataPressures.addDataSet(dataSetPressures);
                chartPressures.setData(lineDataPressures);

                chartPressures.invalidate();


            }

            private void updateChart(LineChart chart, List<Entry> list, LineDataSet dataSet, LineData lineData, int color, String label){
                dataSet = new LineDataSet(list, label);
                dataSet.setColor(color);
                dataSet.setLineWidth(3.0f);
                dataSet.setDrawValues(false);
                dataSet.setDrawCircles(false);
                lineData = new LineData(dataSet);
                chart.setData(lineData);
                chart.invalidate();
            }

        });


    }

    private void generateChart(LineChart chart, int count, int min, int max){
        chart.getDescription().setEnabled(false);
        chart.getLegend().setTextSize(20);
        chart.getLegend().setFormSize(20);
        YAxis right = chart.getAxisRight();
        right.setEnabled(false);
        if(count!=-1) {
            YAxis left = chart.getAxisLeft();
            left.setLabelCount(count);
            left.setAxisMinimum(min);
            left.setAxisMaximum(max);
        }
        chart.setNoDataText("Esperando datos");
        chart.setNoDataTextColor(ContextCompat.getColor(context, R.color.colorAccent));
    }
}
