package goldenbrother.gbmobile.chart;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.List;

/**
 * Created by linchpinub4 on 22/1/16.
 */
public class MyBarDataSet extends BarDataSet {


    public MyBarDataSet(List<BarEntry> yVals, String label) {
        super(yVals, label);
    }

    @Override
    public int getColor(int index)
    {
        float indexVal = getEntryForXIndex(index).getVal();

        if (indexVal <= 50)
            return mColors.get(0);
        else if (indexVal > 50 && indexVal <= 150)
            return mColors.get(1);
        else if (indexVal > 150 && indexVal <= 300)
            return mColors.get(2);
        else
            return mColors.get(3);
    }

}

