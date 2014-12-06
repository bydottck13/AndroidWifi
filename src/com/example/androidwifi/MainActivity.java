package com.example.androidwifi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	WifiManager wifi;
	ListView lv;
	TextView textStatus;
	Button buttonScan;
	int size = 0;
	List<ScanResult> results;
	ArrayList<HashMap<String, String>> arraylist = new ArrayList<HashMap<String,String>>();
	SimpleAdapter adapter;
	
	private static final Map<String, String> wifichannel = new HashMap<String, String>();
	static{
		wifichannel.put("2412", "2.4G Ch01");wifichannel.put("2417", "2.4G Ch02");
		wifichannel.put("2422", "2.4G Ch03");wifichannel.put("2427", "2.4G Ch04");
		wifichannel.put("2432", "2.4G Ch05");wifichannel.put("2437", "2.4G Ch06");
		wifichannel.put("2442", "2.4G Ch07");wifichannel.put("2447", "2.4G Ch08");
		wifichannel.put("2452", "2.4G Ch09");wifichannel.put("2457", "2.4G Ch10");
		wifichannel.put("2462", "2.4G Ch11");wifichannel.put("2467", "2.4G Ch12");
		wifichannel.put("2472", "2.4G Ch13");wifichannel.put("2484", "2.4G Ch14");
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        textStatus = (TextView) findViewById(R.id.textView2);
        buttonScan = (Button) findViewById(R.id.button1);
        lv = (ListView) findViewById(R.id.listView1);
        
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if(wifi.isWifiEnabled()==false)
        {
        	AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        	dialog.setTitle("Remind");
        	dialog.setMessage("Your Wi-Fi is not enabled, enable?");
        	dialog.setIcon(android.R.drawable.ic_dialog_info);
        	dialog.setCancelable(false);
        	dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					wifi.setWifiEnabled(true);
					Toast.makeText(getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();	
				}
			});
        	dialog.show();
        }

        this.adapter = new SimpleAdapter(MainActivity.this, arraylist, R.layout.list, new String[] {"ssid","power","freq"}, new int[] {R.id.ssid, R.id.power, R.id.freq});
        lv.setAdapter(adapter);
        
        registerReceiver(new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				results = wifi.getScanResults();
				size = results.size();
			}
		}, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        
        buttonScan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				arraylist.clear();
				wifi.startScan();
				
				Toast.makeText(MainActivity.this, "Scanning..."+size, Toast.LENGTH_LONG).show();
				try {
					size = size -1;
					while(size >= 0)
					{
						HashMap<String, String> item = new HashMap<String, String>();
						item.put("ssid", results.get(size).SSID);
						item.put("power", new String(results.get(size).level+" dBm"));
						String wifichn = wifichannel.containsKey(new String(""+results.get(size).frequency))? wifichannel.get(new String(""+results.get(size).frequency)):"5G";
						item.put("freq", wifichn);
						arraylist.add(item);
						size--;
						adapter.notifyDataSetChanged();
					}
			        Collections.sort(arraylist, new Comparator<HashMap<String, String>>() {

						@Override
						public int compare(HashMap<String, String> lhs,
								HashMap<String, String> rhs) {
							// TODO Auto-generated method stub
							return ((String) lhs.get("power")).compareTo((String) rhs.get("power"));
						}
					});
					textStatus.setText(arraylist.get(0).get("ssid"));
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
       
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
