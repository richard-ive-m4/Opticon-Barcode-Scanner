package com.metafour.barcode.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.metafour.barcode.BarcodeScan;
import com.metafour.barcode.ScanCallback;
import com.metafour.barcode.ScanningIntentHandler;
import com.metafour.barcode.datawedge.DatawedgeIntentHandler;
import com.metafour.barcode.opticon.OpticonIntentHandler;

import android.util.Log;

public class OpticonBarcodeReaderPlugin extends CordovaPlugin {

	private ScanningIntentHandler intentHandler;
	protected static String TAG = "OpticonBarcodeReaderPlugin";

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		
	}

	@Override
	public boolean execute(String action, JSONArray args,
			final CallbackContext callbackContext) throws JSONException {
		
		Log.e(TAG, "Action: " + action);
		Log.e(TAG, "Args: " + args);

		if ("scanner.register".equals(action)) {
			
			Log.e(TAG, "In the scanner.register");
			
			if("datawedge".equalsIgnoreCase(args.getString(0))) {
				Log.e(TAG, "*********** ZEBRA ***********");
				intentHandler = new DatawedgeIntentHandler(cordova.getActivity().getBaseContext());
			}else {
				intentHandler = new OpticonIntentHandler(cordova.getActivity().getBaseContext());
			}
			
			
			intentHandler.setScanCallback(new ScanCallback<BarcodeScan>() {
				@Override
				public void execute(BarcodeScan scan) {
					Log.i(TAG, "Scan result [" + scan.LabelType + "-"
							+ scan.Barcode + "].");

					try {
						JSONObject obj = new JSONObject();
						obj.put("type", scan.LabelType);
						obj.put("barcode", scan.Barcode);
						PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, obj);
						pluginResult.setKeepCallback(true);
						callbackContext.sendPluginResult(pluginResult);
						
					} catch (JSONException e) {
						Log.e(TAG, "Error building json object", e);

					}
				}
			});
			
		} else if ("scanner.unregister".equals(action)) {
			intentHandler.setScanCallback(null);
			if (!intentHandler.hasListeners()) {
				intentHandler.stop();
			}
		} else if ("stop".equals(action)) {
			intentHandler.stop();
		} else if ("scan".equals(action)){
			intentHandler.scan();
		}

		// start plugin now if not already started
		if ("start".equals(action)) {
			intentHandler.start();
		}

		return true;
	}

}
