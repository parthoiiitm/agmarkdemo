package com.pik.agmarkiiitm;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;



public class Results extends Activity  {

    String commodity,market,variety,date,datedb, URL;
    ProgressDialog pd;
    TextView resultOptions;
    TextView resultAnswers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    		
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_result);
	        
	        Drawable rightArrow = getResources().getDrawable(R.drawable.cornwall);

			// setting the opacity (alpha)
			rightArrow.setAlpha(10);
			
	        Intent intent = getIntent();
	        commodity = intent.getExtras().getString("commo");
	        variety = intent.getExtras().getString("varie");
	        market = intent.getExtras().getString("markt");
	        date = intent.getExtras().getString("date");
	        datedb = intent.getExtras().getString("datedb");
	        
	        resultOptions = (TextView) findViewById(R.id.resultOptions);
	        resultAnswers = (TextView) findViewById(R.id.resultAnswers); 
	        
	        
	        
	        URL = "http://iiitmagmark.3eeweb.com/response.php";
			new StockServerTask().execute(URL);
    }
    
    private class StockServerTask extends AsyncTask<String,Void,JSONObject>
	{
       
		 @Override
	        protected void onPreExecute() {
			 
			  
	            pd = new ProgressDialog(Results.this);
	            pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	            pd.setMessage("Loading Results...");
	            pd.show();
	            
			}
			
		 

		protected JSONObject doInBackground(String... urls) {
			// TODO Auto-generated method stub
			
			String URLINPUT = urls[0];
		
			JSONObject jsonObj=null;
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(URLINPUT);
	
	        try {
	        	
	        	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
	    		nameValuePairs.add(new BasicNameValuePair("commodity", commodity));
	    		nameValuePairs.add(new BasicNameValuePair("variety", variety));
	    		nameValuePairs.add(new BasicNameValuePair("market",market));
	    		nameValuePairs.add(new BasicNameValuePair("datedb", datedb));

	    		httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

	    		// Execute HTTP Post Request

	    		ResponseHandler<String> responseHandler = new BasicResponseHandler();
	    		String response = httpclient.execute(httppost, responseHandler);
	    		final String json = response;
		    	Log.d("STOCKjson",json);
		        jsonObj = new JSONObject(json);
		    	
	        }
		    catch(Exception e)
		    {
	    		Log.e("Error processing agmark URL", e.getMessage());
	            e.printStackTrace();
		    }
			
			return jsonObj;
			
		}
		
		protected void onPostExecute(JSONObject jsonObj) {
			
		  	try 
		  	{	  		
		  		String success = jsonObj.getString("success").toString();
		  		if(success.compareTo("1") == 0)
		  		{
		  			JSONArray stockJsonArray = jsonObj.getJSONArray("details");
			  		resultOptions.setText(
			        		"  Date: \n\n"+
			     			"  Commodity: \n\n"+
			     			"  Variety: \n\n"+
			     			"  Market: \n\n"+
			     			"  Minimum Price: \n\n"+
			     			"  Maximum Price: \n\n"+
			     			"  Modal Price: \n\n"+
			     		    "  Unit of Price: "
			     			);
			  		
		    		resultAnswers.setText(
			  				date + "\n\n"+
			  		     	commodity +"\n\n"+
			  		     	variety +"\n\n"+
			  		     	market + "\n\n"+
			  		        stockJsonArray.getJSONObject(0).getString("min") + "\n\n" +
			  		        stockJsonArray.getJSONObject(0).getString("max")+ "\n\n" +
			  		        stockJsonArray.getJSONObject(0).getString("mod")+ "\n\n" +
			  		        stockJsonArray.getJSONObject(0).getString("upr"));
	    		}  
		  		else
		  			Toast.makeText(getApplicationContext(), jsonObj.getString("message").toString(), Toast.LENGTH_LONG).show();
		  		
			} 
		  	catch (JSONException e)
		  	{
		  		Log.e("Error printing agmark data", e.getMessage());
				e.printStackTrace();
			}
			
		  	pd.dismiss();
        }
		 
	}
    
}
