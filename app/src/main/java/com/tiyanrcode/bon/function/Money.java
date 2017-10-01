package com.tiyanrcode.bon.function;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Created by sulistiyanto on 22-Apr-15.
 */
public class Money {

    public String money(int money){
        //convert Rp
        String rp;
        DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

        formatRp.setCurrencySymbol("");
        formatRp.setMonetaryDecimalSeparator(',');
        formatRp.setGroupingSeparator('.');

        kursIndonesia.setDecimalFormatSymbols(formatRp);
        rp = kursIndonesia.format(money);
        return rp;
    }
}
