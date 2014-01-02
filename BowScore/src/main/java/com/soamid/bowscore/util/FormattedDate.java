package com.soamid.bowscore.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Soamid on 04.11.13.
 */
public class FormattedDate extends Date {

    private SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public FormattedDate() {
        super();
    }

    public FormattedDate(long time) {
        super(time);
    }

    @Override
    public String toString() {
        return dt.format(this);
    }
}
