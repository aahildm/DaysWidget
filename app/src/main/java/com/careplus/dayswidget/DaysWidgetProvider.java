package com.careplus.dayswidget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class DaysWidgetProvider extends AppWidgetProvider {

    private static final int TARGET_YEAR  = 2026;
    private static final int TARGET_MONTH = Calendar.NOVEMBER;
    private static final int TARGET_DAY   = 6;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int widgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, widgetId);
        }
        scheduleMidnightUpdate(context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        scheduleMidnightUpdate(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        cancelMidnightUpdate(context);
    }

    static void updateWidget(Context context, AppWidgetManager manager, int widgetId) {
        long daysLeft = computeDaysRemaining();
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        views.setTextViewText(R.id.tv_days_count, String.valueOf(Math.max(daysLeft, 0)));
        manager.updateAppWidget(widgetId, views);
    }

    static void updateAllWidgets(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        ComponentName component = new ComponentName(context, DaysWidgetProvider.class);
        int[] ids = manager.getAppWidgetIds(component);
        for (int id : ids) {
            updateWidget(context, manager, id);
        }
    }

    private static long computeDaysRemaining() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        Calendar target = Calendar.getInstance();
        target.set(TARGET_YEAR, TARGET_MONTH, TARGET_DAY, 0, 0, 0);
        target.set(Calendar.MILLISECOND, 0);
        long diffMs = target.getTimeInMillis() - today.getTimeInMillis();
        return TimeUnit.MILLISECONDS.toDays(diffMs);
    }

    private static void scheduleMidnightUpdate(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;
        Intent intent = new Intent(context, WidgetUpdateReceiver.class);
        intent.setAction("com.careplus.dayswidget.UPDATE_WIDGET");
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Calendar midnight = Calendar.getInstance();
        midnight.add(Calendar.DAY_OF_YEAR, 1);
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MILLISECOND, 0);
        am.setInexactRepeating(AlarmManager.RTC, midnight.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pi);
    }

    private static void cancelMidnightUpdate(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;
        Intent intent = new Intent(context, WidgetUpdateReceiver.class);
        intent.setAction("com.careplus.dayswidget.UPDATE_WIDGET");
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        am.cancel(pi);
    }
}
