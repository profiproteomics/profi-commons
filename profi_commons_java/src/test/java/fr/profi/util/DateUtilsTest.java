package fr.proline.util;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

public class DateUtilsTest {

    @Test
    public void testParse() {
	final String strDate = "20121221";

	final Calendar cal = Calendar.getInstance();
	cal.set(Calendar.DAY_OF_MONTH, 21);
	cal.set(Calendar.MONTH, Calendar.DECEMBER);
	cal.set(Calendar.YEAR, 2012);

	final Date expectedDate = DateUtils.clearTime(cal.getTime());

	final Date parsedDate = DateUtils.parseReleaseDate(strDate);

	assertEquals("Parsed Date", expectedDate, parsedDate);

	final String formattedDate = DateUtils.formatReleaseDate(parsedDate);

	assertEquals("Formatted Date", strDate, formattedDate);
    }

}
