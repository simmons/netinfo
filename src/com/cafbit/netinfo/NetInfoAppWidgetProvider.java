/*
 * Copyright 2011 David Simmons
 * http://cafbit.com/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cafbit.netinfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.cafbit.netlib.Address;
import com.cafbit.netlib.InterfaceInfo;
import com.cafbit.netlib.NetUtil;
import com.cafbit.netlib.NetUtil.NetInfoException;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.RemoteViews;

public class NetInfoAppWidgetProvider extends AppWidgetProvider {
    
    static private Map<Integer,AppWidgetManager> widgetIdMap =
        new HashMap<Integer,AppWidgetManager>();
    private String addressText = "";
    
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int id : appWidgetIds) {
            widgetIdMap.remove(id);
        }
    }

    @Override
    public void onDisabled(Context context) {
        widgetIdMap.clear();
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        
        addressText = scan(context);

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i=0; i<N; i++) {
            widgetIdMap.put(appWidgetIds[i], appWidgetManager);
            int appWidgetId = appWidgetIds[i];
            render(context, appWidgetManager, appWidgetIds[i]);
        }
    }
    
    public void updateKnownWidgets(Context context) {
        // update the address string
        addressText = scan(context);
        // update the widgets
        for (Map.Entry<Integer,AppWidgetManager> entry : widgetIdMap.entrySet()) {
            render(context, entry.getValue(), entry.getKey());
        }
    }
    
    public void render(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.netinfo_appwidget);
        views.setTextViewText(R.id.text, addressText);

        // launch the activity when the user touches the widget
        Intent intent = new Intent(context, NetInfoActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.text, pendingIntent);
        
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
    
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            updateKnownWidgets(context);
        }
        super.onReceive(context, intent);
    }
    
    private String scan(Context context) {
        
        NetUtil netUtil = new NetUtil(context);
        
        List<InterfaceInfo> interfaces;
        try {
            interfaces = netUtil.getNetworkInformation();
        } catch (NetInfoException e) {
            return e.getMessage();
        }
        SortedSet<Address> addresses = new TreeSet<Address>();
        for (InterfaceInfo ii : interfaces) {
            for (Address a : ii.getAddresses()) {
                if (! a.isLoopback()) {
                    addresses.add(a);
                }
            }
        }
        if (addresses.isEmpty()) {
            return "no address";
        } else {
            return addresses.iterator().next().getIPAddress();
        }
    }

}
