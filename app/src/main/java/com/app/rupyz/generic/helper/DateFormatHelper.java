package com.app.rupyz.generic.helper;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class DateFormatHelper {
    public static String conUnSupportedDateToString(String dateStr) {
        try {
            if (dateStr.equalsIgnoreCase("") || dateStr.isEmpty() || dateStr == null) {
                return "";
            } else {
                String year = dateStr.substring(0, 4);
                String month = dateStr.substring(4, 6);
                String date = dateStr.substring(6, 8);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date convDate = dateFormat.parse(date + "-" + month + "-" + year);
                SimpleDateFormat convDateFormat = new SimpleDateFormat("dd MMM yyyy");
                return convDateFormat.format(convDate);
            }
        } catch (Exception ex) {
            return "";
        }
    }

    public static String getProfileDate(String value) {
        try {
            String[] dateArray = value.split("T");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date convDate = dateFormat.parse(dateArray[0]);
            SimpleDateFormat convDateFormat = new SimpleDateFormat("MMMM yyyy");
            return convDateFormat.format(convDate);
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }


    @SuppressLint("SimpleDateFormat")
    public static Date addOneMonthToDate(String date) {
        Calendar cal = Calendar.getInstance();
        try {
            Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
            cal.setTime(startDate);
            cal.add(Calendar.MONTH, 1);
            return cal.getTime();
        } catch (Exception exception) {
            return new Date();
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static Boolean isDate1EqualThenDate2(String date1, String date2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return Objects.requireNonNull(sdf.parse(date1)).equals(sdf.parse(date2));
        } catch (Exception exception) {
            return false;
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static Boolean isDate1BeforeThenDate2(String date1, String date2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return Objects.requireNonNull(sdf.parse(date1)).before(sdf.parse(date2));
        } catch (Exception exception) {
            return false;
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static Boolean isDate1GreaterThenDate2(String date1, String date2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return Objects.requireNonNull(sdf.parse(date1)).after(sdf.parse(date2));
        } catch (Exception exception) {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    public static Boolean isDateBetweenOneMonth(String date1Str, String date2Str) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            LocalDate date1 = LocalDate.parse(date1Str, formatter);
            LocalDate date2 = LocalDate.parse(date2Str, formatter);

            Period period = Period.between(date1, date2);
            int monthsDifference = Math.abs(period.getMonths());
            int yearsDifference = Math.abs(period.getYears());

            if (yearsDifference > 0) {
                return false;
            }
            return monthsDifference < 1;
        } catch (Exception exception) {
            return false;
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertIsoToDateAndTimeFormat(String strDate) {
        TimeZone utc = TimeZone.getTimeZone("UTC");

        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
        DateFormat dateAndTimeFormat = new SimpleDateFormat("dd MMM, yy -hh:mm a", Locale.ENGLISH);
        originalFormat.setTimeZone(utc);
        try {
            Date date = originalFormat.parse(strDate);
            return dateAndTimeFormat.format(date);
        } catch (Exception exception) {
            return new Date().toString();
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertIsoToMonthAndTimeFormat(String strDate) {
        TimeZone utc = TimeZone.getTimeZone("UTC");

        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
        DateFormat dateAndTimeFormat = new SimpleDateFormat("dd MMM, hh:mm a", Locale.ENGLISH);
        originalFormat.setTimeZone(utc);
        try {
            Date date = originalFormat.parse(strDate);
            return dateAndTimeFormat.format(date);
        } catch (Exception exception) {
            return new Date().toString();
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertStringToDateAndTimeFormat(Date date) {
        try {
            return new SimpleDateFormat("dd MMM yyyy, hh:mm a").format(date);
        } catch (Exception exception) {
            return new Date().toString();
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertDateToIsoFormat(String strDate) {
        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
        try {
            return originalFormat.format(Objects.requireNonNull(originalFormat.parse(strDate)));
        } catch (Exception exception) {
            return new Date().toString();
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertDateToTimeFormat(String strDate) {
        TimeZone utc = TimeZone.getTimeZone("UTC");
        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
        DateFormat dateAndTimeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        originalFormat.setTimeZone(utc);
        try {
            Date date = originalFormat.parse(strDate);
            return dateAndTimeFormat.format(date);
        } catch (Exception exception) {
            return new Date().toString();
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertDateToIsoFormat(Date date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(date);
        } catch (Exception exception) {
            return new Date().toString();
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertDateToIsoUTCFormat(Date date) {
        TimeZone utc = TimeZone.getTimeZone("UTC");
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            format.setTimeZone(utc);
            return format.format(date);
        } catch (Exception exception) {
            return new Date().toString();
        }
    }

    public static String getOrderDate(String value) {
        try {
            String[] dateArray = value.split("T");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date convDate = dateFormat.parse(dateArray[0]);
            SimpleDateFormat convDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            return convDateFormat.format(convDate);
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getPictureDate(String inputDateString) {
        try {

            ZonedDateTime utcDateTime = ZonedDateTime.parse(inputDateString);
            // Convert to India Standard Time (IST)
            ZonedDateTime istDateTime = utcDateTime.withZoneSameInstant(java.time.ZoneId.of("Asia/Kolkata"));
            // Define a formatter for the desired India date format (e.g., dd-MM-yyyy hh:mm:ss a)
            DateTimeFormatter formattedDate = new DateTimeFormatterBuilder().appendPattern("dd-MMM-yyyy hh:mm a").toFormatter();
            return istDateTime.format(formattedDate);
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }


    public static String getYoutubeVideoDate(String value) {
        try {
            String[] dateArray = value.split("T");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date convDate = dateFormat.parse(dateArray[0]);
            SimpleDateFormat convDateFormat = new SimpleDateFormat("dd-MMMM-yyyy");
            return convDateFormat.format(convDate);
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertDateToMonthStringFormat(Date date) {
        try {
            return new SimpleDateFormat("dd MMM yyyy").format(date);
        } catch (Exception exception) {
            return new Date().toString();
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertDateToMonthWithoutYearFormat(Date date) {
        try {
            return new SimpleDateFormat("dd MMM").format(date);
        } catch (Exception exception) {
            return new Date().toString();
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertStringToMonthFormat(String date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date convDate = dateFormat.parse(date);
            SimpleDateFormat convDateFormat = new SimpleDateFormat("dd MMM");
            return convDateFormat.format(convDate);
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }
    
    @SuppressLint("SimpleDateFormat")
    public static String convertStringToMonthAndYearFormatYYYMMDD(String date) {
        try {
            SimpleDateFormat dateFormat =  new SimpleDateFormat("dd MMM yyyy");
            Date convDate = dateFormat.parse(date);
            SimpleDateFormat convDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	        assert convDate != null;
	        return convDateFormat.format(convDate);
        } catch (Exception exception) {
            return null;
        }
    }


    @SuppressLint("SimpleDateFormat")
    public static String convertStringISOToCustomDateAndTimeFormat(String date, SimpleDateFormat convertDateFormat) {
        TimeZone utc = TimeZone.getTimeZone("UTC");
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            dateFormat.setTimeZone(utc);
            Date convDate = dateFormat.parse(date);
            assert convDate != null;
            return convertDateFormat.format(convDate);
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertStringToCustomDateFormat(String date, SimpleDateFormat convertDateFormat) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date convDate = dateFormat.parse(date);
            assert convDate != null;
            return convertDateFormat.format(convDate);
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertDateToCustomDateFormat(Date date, SimpleDateFormat convertDateFormat) {
        try {
            return convertDateFormat.format(date);
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertDateToMonthAndYearFormat(Date date) {
        try {
            return new SimpleDateFormat("MMM yyyy").format(date);
        } catch (Exception exception) {
            return new Date().toString();
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertDateTo_YYYY_MM_DD_Format(Date date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").format(date);
        } catch (Exception exception) {
            return new Date().toString();
        }
    }

    public static String getMonthDate(String value) {
        try {
            String[] dateArray = value.split("T");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date convDate = dateFormat.parse(dateArray[0]);
            SimpleDateFormat convDateFormat = new SimpleDateFormat("dd MMMM yyyy");
            return convDateFormat.format(convDate);
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public static String getGraphDate(String value) {
        try {
            String[] dateArray = value.split(" ");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date convDate = dateFormat.parse(dateArray[0]);
            SimpleDateFormat convDateFormat = new SimpleDateFormat("MMM-yy");
            Log.e("TAG", "getGraphDate: " + convDateFormat.format(convDate));
            return convDateFormat.format(convDate);
        } catch (Exception ex) {
            Log.e("TAG", "getGraphDate: " + ex.getMessage());
            return ex.getMessage();
        }
    }


    public static float getDifferenceBetweenDates(String open_date) {
        try {
            String year = open_date.substring(0, 4);
            String month = open_date.substring(4, 6);
            String date = open_date.substring(6, 8);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date convDate = dateFormat.parse(date + "-" + month + "-" + year);
            Date currentDate = new Date();
            long diff = currentDate.getTime() - convDate.getTime();
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(diff);
            int numOfDays = (int) (diff / (1000l * 60 * 60 * 24));
            int years = numOfDays / 365;
            int months = (numOfDays % 365) / 30;
            return Float.parseFloat(years + "." + months);
        } catch (Exception ex) {
            return 0;
        }
    }

    public static String convertSanctionDate(String dateStr) {
        try {
            if (dateStr.isEmpty()) {
                return "";
            } else {
                DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = sdf.parse(dateStr);
                SimpleDateFormat convDateFormat = new SimpleDateFormat("dd MMM yyyy");
                return convDateFormat.format(date);
            }
        } catch (Exception e) {
            return "";
        }
    }

    public static String convertSanctionDateOrder(String dateStr) {
        try {
            if (dateStr.isEmpty()) {
                return "";
            } else {
                DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date date = sdf.parse(dateStr);
                SimpleDateFormat convDateFormat = new SimpleDateFormat("dd MMM yy");
                return convDateFormat.format(date);
            }
        } catch (Exception e) {
            return "";
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static Date convertStringToDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date);
        } catch (Exception exception) {
            return new Date();
        }
    }

    public static String dateFormatEMI(String strDate) {
        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        DateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Date date = null;
        String formattedDate = "";
        try {
            date = originalFormat.parse(strDate);
            formattedDate = targetFormat.format(date);  // 20120821
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formattedDate;
    }

    public static String getDayOfWeek(Date date) {
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.getDefault());
        return dayFormat.format(date);
    }

    public static int getMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }

    public static int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    public static String getTime(Integer time) {
        String retVal = "00:00";
        int hours = time / 60;
        int minutes = time % 60;
        return hours + ":" + minutes;
    }

    public static String getTimeInMinFormat(Integer time) {
        StringBuilder timeString = new StringBuilder();
        int hours = time / 60;
        int minutes = time % 60;

        if (hours != 0){
            timeString.append(hours);
            timeString.append(" Hrs");
            timeString.append(" ");
        }

        if (minutes != 0){
            timeString.append(minutes);
            timeString.append(" Mins");
        }

        return timeString.toString();
    }


    public static long convertStringTimeToLong(String timeStamp) {
        // Define the date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ", Locale.ENGLISH);

        // Set the timezone to UTC
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        // Parse the timestamp string into a Date object
        Date date;
        try {
            date = dateFormat.parse(timeStamp);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        // Get the timestamp in milliseconds
        assert date != null;
        return date.getTime(); // Print the timestamp in milliseconds
    }
}
