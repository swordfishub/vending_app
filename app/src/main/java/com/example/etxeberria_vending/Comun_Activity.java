package com.example.etxeberria_vending;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

public class Comun_Activity extends Activity{
	private final String TAG="Comun_Activity";
	//Constantes de conexion al servidor local:
	//en casa:
	//protected final String SW_ROOT = "http://192.168.1.105";
	//labat:
	//protected final String SW_ROOT = "http://192.168.0.22";
	//Si se incluye una carpeta, indicar al final la barra, por ejemplo: academia/
	//protected final String SW_FOLDER = "vending_edai/";
	//Constantes de conexion al servidor remoto:
	protected final String SW_ROOT = "http://vendingapp.es/";
	//Si se incluye una carpeta, indicar al final la barra, por ejemplo: academia/
	//protected final String SW_FOLDER = "gualapop/";
	//protected final String SW_FOLDER = "vending_edai/";
	
	//Esta es la carpeta local que crea en el movil:
	protected final String IMG_FOLDER = "fotos";

	//Esta funcion recibe como parametros, los datos a enviar en formato JSON, y el servicio web al cual enviaserselos.
	//Recibe como respuesta un fichero en formato JSON con la respuesta del servidor, y lo transforma en un String.
	protected String ServerConnection(JSONObject jsonRequest,String web_service){
		try {
			String link = String.format("%s/%s", SW_ROOT,web_service);
			URL url = new URL(link); 
			HttpURLConnection client = (HttpURLConnection) url.openConnection();                              
			client.setDoOutput(true);                                                       
			client.setDoInput(true);                                                        
			client.setRequestProperty("Content-Type", "application/json; charset=UTF-8");   
			client.setRequestMethod("POST"); 
			client.connect();  
			//Comunicación con el servidor
			OutputStreamWriter writer = new OutputStreamWriter(client.getOutputStream());   
			String output = jsonRequest.toString();                                             
			writer.write(output);                                                           
			writer.flush();                                                                 
			writer.close();
			InputStream input = client.getInputStream();                                    
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));       
			StringBuilder result = new StringBuilder();  
			//Obtenemos el String con la respuesta:
			String line;                                                                    
			while ((line = reader.readLine()) != null) result.append(line);                                                        
			return result.toString();  
		} catch (Exception e) {
			Log.e(TAG,"ServerConnection:"+e.getMessage());
			return null;
		}
	}
	
	protected boolean InternetConexion(){
		boolean result=false;
		try {
			boolean haveConnectedWifi = false;
			boolean haveConnectedMobile = false;

			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo[] netInfo = cm.getAllNetworkInfo();
			for (NetworkInfo ni : netInfo) {
				if (ni.getTypeName().equalsIgnoreCase("WIFI"))
					if (ni.isConnected())
						haveConnectedWifi = true;
				if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
					if (ni.isConnected())
						haveConnectedMobile = true;
			}
			result = haveConnectedWifi || haveConnectedMobile;
		}catch(Exception e){
			Log.e(TAG,"InternetConexion: "+e.getMessage());
		}
		return result;
	}
	
	
}
