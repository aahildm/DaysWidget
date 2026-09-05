package com.careplus.dayswidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WidgetUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("com.careplus.dayswidget.UPDATE_WIDGET".equals(intent.getAction())) {
            DaysWidgetProvider.updateAllWidgets(context);
        }
    }
}
