package com.text.linechartstext;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private LineCharts lineCharts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lineCharts = findViewById(R.id.linecharts);
        String[] dataX = new String[]{"8:00", "10:00", "12:00", "14:00", "16:00"};
        Float[] dataY = new Float[]{10f, 20f, 30f, 40f};
        String[] lineData = new String[]{"18", "5", "28", "20", "13"};
        lineCharts.setData(dataX, dataY, lineData);
        lineCharts.setTitle("自定义折线图1");

    }
}
