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

import java.util.List;

import com.cafbit.netlib.InterfaceInfo;
import com.cafbit.netlib.NetUtil;
import com.cafbit.netlib.NetUtil.NetInfoException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class NetInfoActivity extends Activity {

	public static final String TAG = "NetInfo";
	
	private ListView listView;
	private NetUtil netUtil;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        listView = new ListView(this);
        setContentView(listView);
        
        netUtil = new NetUtil(this);
    }
    
	@Override
	protected void onResume() {
		super.onResume();
		scan();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.options_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.about:
	    	about();
	    	return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}

	private void scan() {
		// fetch interface information
		List<InterfaceInfo> interfaces;
		try {
			interfaces = netUtil.getNetworkInformation();
		} catch (NetInfoException e) {
			Log.e(TAG, "error: "+e.getMessage(), e);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder
				.setTitle("Error")
				.setMessage(e.getMessage())
			    .setCancelable(false)
			    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						NetInfoActivity.this.finish();
					}
			    });
			AlertDialog alert = builder.create();
			alert.show();
			return;
		}
		
		InterfaceInfoAdapter adapter = new InterfaceInfoAdapter(this, interfaces);
		listView.setAdapter(adapter);
	}
	
	private void about() {
        LayoutInflater factory = LayoutInflater.from(this);
        View view = factory.inflate(R.layout.about,null);
        TextView urlView = (TextView) view.findViewById(R.id.about_url);
        Linkify.addLinks(urlView, Linkify.WEB_URLS);
        TextView descriptionView = (TextView) view.findViewById(R.id.about_description);
        descriptionView.setMovementMethod(new ScrollingMovementMethod());

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder
			.setTitle("About")
			.setIcon(android.R.drawable.ic_dialog_info)
			.setView(view)
		    .setCancelable(false)
		    .setNeutralButton("OK", null);
		AlertDialog alert = builder.create();
		alert.show();
	}
}