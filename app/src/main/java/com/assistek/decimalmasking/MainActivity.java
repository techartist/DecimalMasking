package com.assistek.decimalmasking;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.regex.Pattern;


public class MainActivity extends ActionBarActivity {
    EditText etNumberToFormat;
    EditText etDecimalMask;
    EditText etMaxLenghth;
    Button button;
    String sDecimalMask = "";
    String sNumberToFormat = "";
    String sNumberToFormatOriginal = "";
    String sDecimalMaskOriginal = "";
    int iMaxLength = 10;
    int numberOfDecimalPlaces = 0;

    private InputFilter filterDecimalMask = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            for (int i = 0; i < source.length(); i++) {
                if (!"#0,.".contains(Character.toString(source.charAt(i)))) {
                    return "";
                }
            }
            return null;
        }
    };

    private InputFilter filterNumbers = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            for (int i = 0; i < source.length(); i++) {
                /*if (source.toString().equalsIgnoreCase(",")) {
                    return ",";
                }*/
                if (!"1234567890.,".contains(Character.toString(source.charAt(i)))) {
                    return "";
                }
            }
            return null;


        }
    };

    /** The InputFilter used to limit EditText fields with decimal places.
     *  This filter does most of the heavy lifting */
    protected InputFilter decimal = new InputFilter() {


        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            if (sDecimalMask.equalsIgnoreCase("")) {
                return null;

            } else {
                if (source.length() > 0) {
                    if (dest.toString().contains(getDelimiter())) {
                        String[] splitDest = dest.toString().split(Pattern.quote(getDelimiter()));
                        if (splitDest.length > 1) {
                            if (splitDest[1].length() >= numberOfDecimalPlaces) {
                                return "";
                            }
                        }
                    } else {
                        if ((dest.length() + 1 + 1) == iMaxLength) {
                            return getDelimiter() + source;
                        }
                    }
                }
                return null;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button)findViewById(R.id.button);
        etNumberToFormat = (EditText)findViewById(R.id.numberToFormat);
        etDecimalMask = (EditText)findViewById(R.id.decimalMask);
        etMaxLenghth = (EditText)findViewById(R.id.maxLength);
        iMaxLength = Integer.valueOf(etMaxLenghth.getText().toString());

        //set on focus change listeners
        View.OnFocusChangeListener ofcNumberToFormat = new MyFocusNumberFormatChangeListener();
        etNumberToFormat.setOnFocusChangeListener(ofcNumberToFormat);
        View.OnFocusChangeListener ofcMaxLengthListener = new MyFocusMaxLengthChangeListener();

        etMaxLenghth.setOnFocusChangeListener(ofcMaxLengthListener);
        View.OnFocusChangeListener ofcDelimiterChange = new MyFocusDecimalChangeListener();
        etDecimalMask.setOnFocusChangeListener(ofcDelimiterChange);

        //set filters and Text Watchers
        //etNumberToFormat.setFilters(new InputFilter[] {filterNumbers, new InputFilter.LengthFilter(iMaxLength),decimal});
        etNumberToFormat.setFilters(new InputFilter[] { filterNumbers,new InputFilter.LengthFilter(iMaxLength),decimal});
        etDecimalMask.setFilters(new InputFilter[] { filterDecimalMask, new InputFilter.LengthFilter(iMaxLength) });
        //etMaxLenghth.setFilters((new InputFilter[] { filterMaxLength }));
        etMaxLenghth.setInputType(InputType.TYPE_CLASS_NUMBER );
        //etNumberToFormat.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        etNumberToFormat.setInputType(InputType.TYPE_CLASS_TEXT);
        etDecimalMask.addTextChangedListener(watchMask);
        etMaxLenghth.addTextChangedListener(watchMaxLength);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetValues();

            }
        });

        //get Values
        sDecimalMask = etDecimalMask.getText().toString();
        sNumberToFormat = etNumberToFormat.getText().toString();
        sNumberToFormatOriginal = etNumberToFormat.getText().toString();
        sDecimalMaskOriginal = etDecimalMask.getText().toString();
        iMaxLength = Integer.valueOf(etMaxLenghth.getText().toString())

        //formatNumber();

        ;

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //take the values in the decimal mask and do the calculations
    public void calculateDecimalPlaces () {
        sDecimalMask = etDecimalMask.getText().toString();
        String[] decimalMask = getsDecimalMask();
        if (decimalMask.length > 1) {
            numberOfDecimalPlaces = decimalMask[1].length();
        }

    }

    public void resetValues() {
        etNumberToFormat.setText("");
        etDecimalMask.setText("");
        if (etMaxLenghth.getText().toString().length() > 0) {
            iMaxLength = Integer.valueOf(etMaxLenghth.getText().toString());
        } else {
            iMaxLength = 10;
        }

        calculateDecimalPlaces();
        //etNumberToFormat.setFilters(new InputFilter[] {filterNumbers, new InputFilter.LengthFilter(iMaxLength),decimal});
        etNumberToFormat.setFilters(new InputFilter[] { filterNumbers,new InputFilter.LengthFilter(iMaxLength),decimal});

    }

    private String[] getsDecimalMask() {
        sDecimalMask = etDecimalMask.getText().toString();
        String delimiter = getDelimiter();

        if (!delimiter.equalsIgnoreCase("")) {
            String[] decimalMask = sDecimalMask.split(Pattern.quote(delimiter));
            return decimalMask;
        } else {
            String[] decimalMask = {""};
            return decimalMask;
        }


    }

    private String getDelimiter() {
        String delimiter = "";
        sDecimalMask = etDecimalMask.getText().toString();
        if (sDecimalMask.contains(".")) {
            delimiter = ".";
        }
        if (sDecimalMask.contains(",")) {
            delimiter = ",";
        }
        return delimiter;
    }



    private class MyFocusMaxLengthChangeListener implements View.OnFocusChangeListener {
        public void onFocusChange(View v, boolean hasFocus) {
            if (v.getId() == R.id.maxLength && !hasFocus) {
                String [] maxLength = null;
                String sMaxLength = "";
                if (etMaxLenghth.getText().toString().contains(".")) {
                    maxLength = etMaxLenghth.getText().toString().split(Pattern.quote("."));

                    sMaxLength = maxLength[0].replace(".","").replace(",","");
                }
                if (etMaxLenghth.getText().toString().contains(",")) {
                    maxLength = etMaxLenghth.getText().toString().split(Pattern.quote(","));
                    sMaxLength = maxLength[0].replace(",","").replace(".","");
                }
                if (sMaxLength.equalsIgnoreCase("")) {
                    sMaxLength = etMaxLenghth.getText().toString();
                } else {
                    etMaxLenghth.setText(sMaxLength);
                }
                iMaxLength = Integer.valueOf(sMaxLength);
                calculateDecimalPlaces();
                etDecimalMask.setFilters(new InputFilter[] { filterDecimalMask, new InputFilter.LengthFilter(iMaxLength) });
                etNumberToFormat.setFilters(new InputFilter[] { filterNumbers,new InputFilter.LengthFilter(iMaxLength),decimal});
                //etNumberToFormat.setFilters(new InputFilter[] { filterNumbers, new InputFilter.LengthFilter(iMaxLength), decimal });
                collapseKeyboard(v);

            }
        }
    }

    private class MyFocusDecimalChangeListener implements View.OnFocusChangeListener {
        public void onFocusChange(View v, boolean hasFocus) {
            if (v.getId() == R.id.decimalMask && !hasFocus) {

                collapseKeyboard(v);
            }

        }
    }




    private class MyFocusNumberFormatChangeListener implements View.OnFocusChangeListener {

        public void onFocusChange(View v, boolean hasFocus) {

            if (v.getId() == R.id.numberToFormat && !hasFocus) {
                String delimiter = getDelimiter();
                String[] number = sNumberToFormat.split(Pattern.quote(delimiter));
                if (number.length == 1) {
                    formatNumber();
                    //etNumberToFormat.setText(sNumberToFormat);
                }
                collapseKeyboard(v);
            }
        }
    }

    private void formatNumber() {
        sNumberToFormat = etNumberToFormat.getText().toString();
        sDecimalMask = etDecimalMask.getText().toString();
        boolean periodMask = false;
        String delimiter = getDelimiter();
        String[] decimalMask = getsDecimalMask();


        if (decimalMask.length == 1) {
            return;
        } else {

            if (delimiter.equalsIgnoreCase(",")) {
                //decimal format only currently works with dot delimiters.
                sDecimalMask = sDecimalMask.replace(",", ".");
            }

            DecimalFormat df = new DecimalFormat(sDecimalMask);
            df.setRoundingMode(RoundingMode.UP);

            sNumberToFormat = df.format(Float.valueOf(sNumberToFormat.replace(",", ".")));
            //if (maxNumber > Float.valueOf(sNumberToFormat)) {
            if (delimiter.equalsIgnoreCase(",")) {
                sNumberToFormat = sNumberToFormat.replace(".", ",");
            }
            etNumberToFormat.setText(sNumberToFormat);

        }

    }



    public static void collapseKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    private TextWatcher watchMask = new TextWatcher(){

        String originalText = "";

        @Override
        public void afterTextChanged(Editable arg0) {

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {

        }

        @Override
        public void onTextChanged(CharSequence s, int a, int b, int c) {
            sNumberToFormat = etNumberToFormat.getText().toString();
            sDecimalMask = etDecimalMask.getText().toString();


            String[] decimalMask = getsDecimalMask();
            //can't have more than one decimal point
            if (decimalMask.length > 2) {
                etDecimalMask.setText(decimalMask[0] + getDelimiter() + decimalMask[1]);
            }
            if (decimalMask[0].contains(getDelimiter())) {
                return;
            }

            calculateDecimalPlaces();
           //etNumberToFormat.setFilters(new InputFilter[] {filterNumbers, new InputFilter.LengthFilter(iMaxLength),decimal});
            etNumberToFormat.setFilters(new InputFilter[] { filterNumbers,new InputFilter.LengthFilter(iMaxLength),decimal});

            String delimiter = getDelimiter();
            if (!sNumberToFormat.equalsIgnoreCase("") && !sDecimalMask.equalsIgnoreCase("")) {
                if (decimalMask[0].contains(delimiter)) {
                    return;
                }
                //if (!sNumberToFormat.equalsIgnoreCase("") && doWeNeedToDecimalMask()) {
                    //etNumberToFormat.setText(sNumberToFormatOriginal);
                    formatNumber();

               /* } else {
                    sNumberToFormatOriginal = etNumberToFormat.getText().toString();
                } */
            }


        }};

    private TextWatcher watchMaxLength = new TextWatcher(){

        String originalText = "";

        @Override
        public void afterTextChanged(Editable arg0) {

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,int arg3) {

        }

        @Override
        public void onTextChanged(CharSequence s, int a, int b, int c) {
            String [] maxLength = null;
            String sMaxLength = "";
            sMaxLength = etMaxLenghth.getText().toString();
            if (etMaxLenghth.getText().toString().contains(".")) {
                maxLength = etMaxLenghth.getText().toString().split(Pattern.quote("."));
                sMaxLength = maxLength[0].replace(".","").replace(",", "");
            }
            if (etMaxLenghth.getText().toString().contains(",")) {
                maxLength = etMaxLenghth.getText().toString().split(Pattern.quote("."));
                sMaxLength = maxLength[0].replace(",","").replace(".","");
            }
            if (sMaxLength.equalsIgnoreCase("") ) {
                etNumberToFormat.setText("");
                etDecimalMask.setText("");
            } else {
                //String sMaxLength = etMaxLenghth.getText().toString().replace(".","").replace(",","");

                iMaxLength = Integer.valueOf(sMaxLength);
                String textOfNumber = etNumberToFormat.getText().toString();
                int lengthOfText = textOfNumber.length();
                String tempValue = "";
                if (iMaxLength < lengthOfText) {
                    for (int i = 0; i < iMaxLength; i++) {
                        tempValue += String.valueOf(textOfNumber.charAt(i));
                    }
                    etNumberToFormat.setText(tempValue);
                }

                etNumberToFormat.setFilters(new InputFilter[]{filterNumbers, new InputFilter.LengthFilter(iMaxLength), decimal});
                etDecimalMask.setFilters(new InputFilter[]{filterDecimalMask, new InputFilter.LengthFilter(iMaxLength)});
            }


        }};







}
