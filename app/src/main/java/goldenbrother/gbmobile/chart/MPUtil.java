package goldenbrother.gbmobile.chart;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import goldenbrother.gbmobile.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by linchpinub4 on 22/1/16.
 */
public class MPUtil {

    private static int textSize = 10;

    public static ArrayList<BarDataSet> getDataSet(Context context, List<Float> dataList) {

        ArrayList<BarDataSet> dataSets = new ArrayList<>();

        ArrayList<BarEntry> valueSet = new ArrayList<>();


        for (int i = 0; i < dataList.size(); i++) {
            valueSet.add(new BarEntry(dataList.get(i), i));
        }
        int[] colors = {ContextCompat.getColor(context,R.color.colorPrimary)};

        BarDataSet barDataSet = new MyBarDataSet(valueSet, "");
        barDataSet.setColors(colors);
        barDataSet.setValueTextSize(textSize);
        barDataSet.setBarSpacePercent(50);

        dataSets.add(barDataSet);

        return dataSets;
    }

    public static ArrayList<String> getXAxisValues(int dataSize) {

        ArrayList<String> xAxis = new ArrayList<>();

        for (int i = 0; i < dataSize; i++) {
            xAxis.add(String.valueOf(i + 1));
        }
        return xAxis;
    }

    public static void drawChart(final Context context, BarChart chart, BarData data) {
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        xAxis.setDrawLabels(true);
        xAxis.setDrawAxisLine(true);
        xAxis.setAxisLineWidth(2);  // x ashix line size
        xAxis.setAxisLineColor(ContextCompat.getColor(context,R.color.colorPrimary));
        xAxis.setDrawGridLines(false);
        xAxis.setTextSize(12);
        xAxis.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary));

        chart.getAxisRight().setDrawAxisLine(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getAxisRight().setDrawLabels(false);

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setDrawGridLines(false);
        yAxis.setTextSize(12);
        yAxis.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary));
        yAxis.setAxisLineColor(ContextCompat.getColor(context,R.color.colorPrimary));
        yAxis.setAxisLineWidth(2);  // y ashix line size

        data.setValueTextColor(ContextCompat.getColor(context,R.color.colorPrimary)); // bar data level
        data.setValueTextSize(12); // bar data size

        // bar data size

        chart.setData(data);
        chart.setDescription("");
        chart.invalidate();
        chart.setHorizontalScrollBarEnabled(true);
        chart.setVisibleXRange(4);
        chart.getLegend().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDoubleTapToZoomEnabled(false);
    }
}
