package com.example.android.shoppingcalculator;

import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.util.*;

public class MainActivity extends AppCompatActivity
{
    ListView priceList;
    LinkedList<String> entries;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set button color
        Button button = (Button)findViewById(R.id.calc_btn);
        button.getBackground().setColorFilter(0xFFF48FB1, PorterDuff.Mode.MULTIPLY);

        // get ListView object from xml
        priceList = (ListView) findViewById(R.id.list);

        entries = new LinkedList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, entries);

        priceList.setAdapter(adapter);
        setListViewHeightBasedOnChildren(priceList);
    }

    /**
     * Gets values from fields and outputs the price per unit
     * @param view
     */
    public void calculate(View view)
    {
        // vibrate on press
        vibrate();

        // don't calculate if no base price or number of units
        if (getBasePrice().equals("") || getNumUnits().equals("")) {
            return;
        }

        // get specified percent off value, set to 0 if none
        double percentOff;
        if (getPercentOff().equals("")) {
            percentOff = 0;
        }
        else {
            percentOff = Double.parseDouble(getPercentOff());
        }

        // get values from user inputs
        double basePrice = Double.parseDouble(getBasePrice());
        double numUnits = Double.parseDouble(getNumUnits());

        double result = calculatePerUnit(basePrice, numUnits, percentOff);
        addEntry(getName(), numUnits, getUnits(), result);
        adapter.notifyDataSetChanged();
        setListViewHeightBasedOnChildren(priceList);
    }

    private String getName()
    {
        EditText name = (EditText)findViewById(R.id.name_inp);
        return name.getText().toString().trim();
    }

    private String getUnits()
    {
        EditText units = (EditText)findViewById(R.id.units_inp);
        return units.getText().toString().trim();
    }

    private String getBasePrice()
    {
        EditText basePrice = (EditText)findViewById(R.id.base_price_inp);
        return basePrice.getText().toString();
    }

    private String getNumUnits()
    {
        EditText numUnits = (EditText)findViewById(R.id.num_unit_inp);
        return numUnits.getText().toString();
    }

    private String getPercentOff()
    {
        EditText percentOff = (EditText)findViewById(R.id.percent_off_inp);
        return percentOff.getText().toString();
    }

    /**
     * Calculates the price per unit after discounts
     * @param basePrice the price on tag before percent off discounts
     * @param numUnits the number of units
     * @param percentOff the value of the percent off discount in percents, 0 if none
     * @return the price per unit
     */
    private double calculatePerUnit(double basePrice, double numUnits, double percentOff)
    {
        return (basePrice - (basePrice * (percentOff * .01))) / numUnits;
    }

    private void addEntry(String name, double numUnits, String units, double result)
    {
        if (numUnits == Math.floor(numUnits)) {
            adapter.add(name + ", " + (int)numUnits + " " + units + ": " + result);
        }
        else {
            adapter.add(name + ", " + numUnits + " " + units + ": " + result);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        if (v.getId() == R.id.list)
        {

        }
    }

    private void vibrate()
    {
        View view = findViewById(R.id.calc_btn);
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
    }

    /**** Method for Setting the Height of the ListView dynamically.
     **** Hack to fix the issue of not showing all the items of the ListView
     **** when placed inside a ScrollView  ****/
    public static void setListViewHeightBasedOnChildren(ListView listView)
    {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
