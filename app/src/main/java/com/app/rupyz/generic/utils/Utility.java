package com.app.rupyz.generic.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.Toast;

import com.app.rupyz.BuildConfig;
import com.app.rupyz.generic.model.user.UserViewModel;
import com.app.rupyz.model_kt.DateFilterModel;
import com.app.rupyz.model_kt.OrganizationInfoModel;
import com.app.rupyz.sales.login.LoginActivity;
import com.app.rupyz.ui.organization.onboarding.activity.MobileNumberActivity;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utility {
    private Context mContext;

    public Utility(Context mContext) {
        this.mContext = mContext;
    }

    public void logout() {
        SharedPref.getInstance().clearSharePref();
        PermissionModel.Companion.getINSTANCE().clearPermissionModel();
        if (mContext != null) {
            Intent intent = new Intent(mContext, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            mContext.startActivity(intent);
        }
    }

    public static void shareApp(Context mContext, BitmapDrawable bitmapDrawable) {
        try {
            Bitmap bitmap1;
            bitmap1 = bitmapDrawable.getBitmap();
            String imgBitmapPath = MediaStore.Images.Media.insertImage(
                    mContext.getContentResolver(), bitmap1, "Rupyz", null);
            Uri imgBitmapUri = Uri.parse(imgBitmapPath);
            String shareText = "Digitize your business and grow 5X. Collaborate with your sales team, office staff, and buyers with automated sales, ordering, and stock management.\n"
                    + "Take control of your business with real-time analytics. \n"
                    + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("*/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, imgBitmapUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            mContext.startActivity(Intent.createChooser(shareIntent, "Share App"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void shareAppUsingBitmap(Context mContext, Bitmap bitmapDrawable) {
        try {

            String imgBitmapPath = MediaStore.Images.Media.insertImage(
                    mContext.getContentResolver(), bitmapDrawable, "", null);
            Uri imgBitmapUri = Uri.parse(imgBitmapPath);
            String shareText = "Digitize your business and grow 5X. Collaborate with your sales team, office staff, and buyers with automated sales, ordering, and stock management.\n"
                    + "Take control of your business with real-time analytics. \n"
                    + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("*/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, imgBitmapUri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            mContext.startActivity(Intent.createChooser(shareIntent, "Share App"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void rateApp(Context mContext) {
        try {
            Uri uri = Uri.parse("market://details?id=" + mContext.getPackageName());
            Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
            try {
                mContext.startActivity(myAppLinkToMarket);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void shareWhatsApp(Context mContext, String legalName, String link) {
        String shareText = "Hello!!, \uD83D\uDE0A\n" +
                "\n" +
                "See our latest website - " + BuildConfig.PROFILE_BASE_URL + link + "\n" +
                "\n" +
                "\uD83C\uDF81 See our Latest Products\n" +
                "✔️ Place Order online\n" +
                "\uD83D\uDCB0 Multiple Payment options available\n" +
                "\n" +
                "Thank You\n" +
                "Team " + legalName + "\uD83D\uDE04";
        try {
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.setType("text/plain");
            whatsappIntent.setPackage("com.whatsapp");
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            try {
                mContext.startActivity(whatsappIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(mContext, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void shareMyProfileWithAll(Context mContext, String legalName, String link) {
        String shareText = "Hello!!\n" +
                "\n" +
                "```" + legalName + "``` is a growing business enterprise. We are now online.\n" +
                "Check our Product & Services, latest product catalogue & product range at:" + "\n" +
                "" + BuildConfig.PROFILE_BASE_URL + link + "\n" +
                "\n" +
                "Supercharge your business with Rupyz credible business\n" +
                "community app: " + " https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            mContext.startActivity(Intent.createChooser(shareIntent, "Share via"));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void shareOthersProfileWithAll(Context mContext, String legalName, String link) {
        String shareText = "Hello!!\n" +
                "```" + legalName + "``` is a growing business enterprise. They are now online.\n" +
                "Check their Product & services, latest product catalogue & product range at:\n" +
                "" + BuildConfig.PROFILE_BASE_URL + link + "\n" +
                "\n" +
                "Supercharge your business with Rupyz credible business community app: " +
                " https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            mContext.startActivity(Intent.createChooser(shareIntent, "Share via"));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void shareMyProductWithAll(Context mContext, String productName, String link) {
        String shareText = "Hello,\n" +
                "Thank you for showing interest in our product & Service.\n" +
                "```" + productName + "```\n\n" +
                "" + link + "\n" +
                "\nTap above to order or check more product & service.\n" +
                "Super charge your business with Rupyz credible business" +
                "community app: " + " https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            mContext.startActivity(Intent.createChooser(shareIntent, "Share via"));

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    public static void shareOthersProductWithAll(Context mContext, String productName, String link) {
        String shareText = "Hello,\n" +
                "I found an interesting product & Service.\n" +
                "```" + productName + "```\n\n" +
                "" + link + "\n" +
                "Tap above to order or check more product & service.\n" +
                "Super charge your business with Rupyz credible business community app: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID;

        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            mContext.startActivity(Intent.createChooser(shareIntent, "Share via"));

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static long getDateInMilliSeconds(String givenDateString, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        long timeInMilliseconds = 1;
        try {
            Date mDate = sdf.parse(givenDateString);
            if (mDate != null) {
                timeInMilliseconds = mDate.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMilliseconds;
    }

    public String getDeviceName() {
        try {
            return Build.MODEL;
        } catch (Exception ex) {
            return "";
        }
    }

    public String getDeviceManufacturer() {
        try {
            return Build.MANUFACTURER;
        } catch (Exception ex) {
            return "";
        }
    }

    public String getOS() {
        try {
            String sdkVersion = Build.VERSION.RELEASE; // e.g. sdkVersion := 8;
            return "Android SDK " + sdkVersion;
        } catch (Exception ex) {
            return "";
        }
    }

    public static DateFilterModel getLastTwelveMonthDateFilterModel() {
        DateFormat monthFormat = new SimpleDateFormat("MM-yyyy", Locale.ENGLISH);

        Calendar monthCalendar = Calendar.getInstance();
        String currentMonth = monthFormat.format(Calendar.getInstance().getTime());

        monthCalendar.add(Calendar.MONTH, -11);

        String lastTwelveMonth = monthFormat.format(monthCalendar.getTime());

        DateFilterModel monthModel = new DateFilterModel();
        monthModel.setTitle(AppConstant.LAST_TWELVE_MONTH);
        monthModel.setStartDate(lastTwelveMonth);
        monthModel.setEnd_date(currentMonth);
        monthModel.setFilter_type(AppConstant.MONTHLY);
        monthModel.setSelected(true);
        return monthModel;
    }



}
