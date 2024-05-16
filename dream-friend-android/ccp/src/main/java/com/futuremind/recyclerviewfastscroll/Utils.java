package com.futuremind.recyclerviewfastscroll;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.View;

import java.util.Calendar;

/**
 * Created by mklimczak on 31/07/15.
 */
public class Utils {

    public static float getViewRawY(View view) {
        int[] location = new int[2];
        location[0] = 0;
        location[1] = (int) view.getY();
        ((View)view.getParent()).getLocationInWindow(location);
        return location[1];
    }

    public static float getViewRawX(View view) {
        int[] location = new int[2];
        location[0] = (int) view.getX();
        location[1] = 0;
        ((View)view.getParent()).getLocationInWindow(location);
        return location[0];
    }

    public static float getValueInRange(float min, float max, float value) {
        float minimum = Math.max(min, value);
        return Math.min(minimum, max);
    }

    public static void setBackground(View view, Drawable drawable){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        } else {
            view.setBackgroundDrawable(drawable);
        }
    }

    public static String getMyPrettyDate(long neededTimeMilis) {
        Calendar nowTime = Calendar.getInstance();
        Calendar neededTime = Calendar.getInstance();
        neededTime.setTimeInMillis(neededTimeMilis);

        if ((neededTime.get(Calendar.YEAR) == nowTime.get(Calendar.YEAR))) {

            if ((neededTime.get(Calendar.MONTH) == nowTime.get(Calendar.MONTH))) {

                if (neededTime.get(Calendar.DATE) - nowTime.get(Calendar.DATE) == 1) {
                    //here return like "Tomorrow at 12:00"
                    return "Tomorrow at " + DateFormat.format("HH:mm a", neededTime);

                } else if (nowTime.get(Calendar.DATE) == neededTime.get(Calendar.DATE)) {
                    //here return like "Today at 12:00 am"
                    return DateFormat.format("HH:mm a", neededTime).toString();

                } else if (nowTime.get(Calendar.DATE) - neededTime.get(Calendar.DATE) == 1) {
                    //here return like "Yesterday at 12:00"
                    return "Yesterday";// at " + DateFormat.format("HH:mm a", neededTime)

                } else {
                    //here return like "May 31, 12:00"
                    return DateFormat.format("MMMM d", neededTime).toString();
                }

            } else {
                //here return like "May 31, 12:00"
                return DateFormat.format("MMMM d", neededTime).toString();
            }

        } else {
            //here return like "May 31 2010, 12:00" - it's a different year we need to show it
            return DateFormat.format("MMMM dd yyyy, HH:mm", neededTime).toString();
        }
    }


}
