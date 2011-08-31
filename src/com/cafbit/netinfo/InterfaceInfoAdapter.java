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

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.cafbit.netlib.Address;
import com.cafbit.netlib.InterfaceInfo;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class InterfaceInfoAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<InterfaceInfo> interfaceInfoList;
    
    public InterfaceInfoAdapter(Activity activity) {
        init(activity, null);
    }

    public InterfaceInfoAdapter(Activity activity, List<InterfaceInfo> devices) {
        init(activity, devices);
    }

    private void init(Activity activity, List<InterfaceInfo> interfaceInfoList) {
        this.inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.interfaceInfoList = new ArrayList<InterfaceInfo>(interfaceInfoList);
    }
    
    @Override
    public int getCount() {
        return interfaceInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.interfaceInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String line1, line2;
        View view;
        TextView text;

        if (convertView == null) {
            view = inflater.inflate(android.R.layout.simple_list_item_2, parent, false);
        } else {
            view = convertView;
        }
        
        int numDevices = this.interfaceInfoList.size();
        if ((position >= 0) && (position < numDevices)) {
            InterfaceInfo ii = this.interfaceInfoList.get(position);
            
            line1 = ii.getNetworkInterface().getDisplayName();
            
            StringBuilder sb = new StringBuilder();
            String interfaceFlags = ii.getFlagStrings();
            if (interfaceFlags.length()>0) {
                sb.append(interfaceFlags+"\n");
            }
            boolean first = true;
            for (Address address : ii.getAddresses()) {
                if (! first) {
                    sb.append("\n");
                }
                sb.append(address.toString());
                first = false;
            }
            line2 = sb.toString();
        } else {
            // this never happens
            line1 = "";
            line2 = "";
        }
        
        text = (TextView) view.findViewById(android.R.id.text1);
        text.setText(line1);
        
        TextView text2 = (TextView) view.findViewById(android.R.id.text2);
        text2.setText(line2);

        return view;
    }
    
    public void addDevice(InterfaceInfo interfaceInfo) {
        int idx;
        if ((idx = interfaceInfoList.indexOf(interfaceInfo)) != -1) {
            // overwrite an existing element, if the specified
            // device already matches one in our list.
            interfaceInfoList.set(idx, interfaceInfo);
        } else {
            interfaceInfoList.add(interfaceInfo);
        }
        notifyDataSetChanged();
    }
    
    public void clear() {
        interfaceInfoList.clear();
        notifyDataSetChanged();
    }
    
}
