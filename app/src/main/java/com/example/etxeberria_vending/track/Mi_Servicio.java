package com.example.etxeberria_vending.track;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONObject;

import com.example.etxeberria_vending.clases.JBD;
import com.example.etxeberria_vending.clases.JRuta;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class Mi_Servicio extends Service implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,LocationListener{
	private final String TAG="Mi_Servicio";
	//Identificador para la señal de broadcast que emitira el servicio, cuando tenga una nueva posición:
	public final static String BC_NUEVA_POSICION = "NUEVA_POSICION";
	
	private Looper mServiceLooper;
	private ServiceHandler mServiceHandler;
	
	//Objetos para localizar la posición del usuario:
	private GoogleApiClient mGoogleApiClient;
	private LocationRequest mLocationRequest;
	
	private int mStartId;
	private double mLat;
	private double mLon;
	private int mLongInt;
	private int mLatInt;
	private int codigo_usuario;
	
	//variables de comun
	
	//Constantes de conexion al servidor local:
	//en casa:
	protected final String SW_ROOT = "http://192.168.1.105";
	//labat:
	//protected final String SW_ROOT = "http://192.168.0.22";
	//Si se incluye una carpeta, indicar al final la barra, por ejemplo: academia/
	protected final String SW_FOLDER = "vending_edai/";
	//Constantes de conexion al servidor remoto:
	//protected final String SW_ROOT = "http://bymcarlos.esy.es";
	//Si se incluye una carpeta, indicar al final la barra, por ejemplo: academia/
	//protected final String SW_FOLDER = "gualapop/";
	
	//Esta es la carpeta local que crea en el movil:
	protected final String IMG_FOLDER = "fotos";
	
	//Varaible para obtener mensajes del servidor, cuando algo ha ido mal
		private String mServerMsg;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		try {
			//Identificamos el bloque de preferencias que nos interesa (ultimo parametro: 0=Privado, 1=Lectura (otras apps), 2=Escritura (Otras apps)
    		SharedPreferences preferences = getSharedPreferences("VendingPreferencias", 0);
    		//Obtenemos las preferencias, indicando la clave (si no existe, valor vacio)
    		codigo_usuario = preferences.getInt("codigo_usuario", 0);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		try {
			Log.e(TAG, "onCreate");
			HandlerThread thread = new HandlerThread("ServiceStartArguments",android.os.Process.THREAD_PRIORITY_BACKGROUND);
			thread.start();
			// Get the HandlerThread's Looper and use it for our Handler 
			mServiceLooper = thread.getLooper();
			mServiceHandler = new ServiceHandler(mServiceLooper);
			
			//Inicializamos los objetos para encontrar la posición del usuario:
			mGoogleApiClient = new GoogleApiClient.Builder(this)
	                .addApi(LocationServices.API)
	                .addConnectionCallbacks(this)
	                .addOnConnectionFailedListener(this)
	                .build();
			mLocationRequest = LocationRequest.create()
					.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
					.setInterval(10 * 1000)        // 10 seconds, in milliseconds
					.setFastestInterval(1 * 1000); // 1 second, in milliseconds
		} catch (Exception e) {
			Log.e(TAG, "onCreate:"+e.getMessage());
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			Log.e(TAG, "onStartCommand");
			mStartId = startId;
			
			//Iniciamos los servicios de ubicación:
			mGoogleApiClient.connect();
			
			//Se ha iniciado el servicio, esperamos 5 segundos para dar tiempo a localizar la posición
			//y llama a una función que registre la posición actual:
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					Message msg = mServiceHandler.obtainMessage();
					msg.arg1 = mStartId;
					mServiceHandler.sendMessage(msg);
				}
			}, 5000);
			Log.e(TAG, "onStartCommand: connect");
		} catch (Exception e) {
			Log.e(TAG, "onCreate:"+e.getMessage());
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		Log.e(TAG, "onDestroy");
		//Detenemos los servicios de ubicación:
		if (mGoogleApiClient.isConnected()) {
			LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
			mGoogleApiClient.disconnect();
			Log.e(TAG, "onDestroy: disconnect");
		}
		super.onDestroy();
	}
	
	private final class ServiceHandler extends Handler {
		public ServiceHandler(Looper looper) {
			super(looper);
		}
		//A la siguiente función se llega a los 5 segundos de haber iniciado el servicio
		@Override
		public void handleMessage(Message msg) {
			try {
				//Vamos a obtener la ultima posición conseguida del usuario, comprobar
				//si ha variado algo (al menos 10 metros desde la ultima) y si es así
				//la registramos en la bbdd y emitimos una señal de broadcast
				//por si Main_Activity está en en primer plano, que se actualice el listView:
				Log.e(TAG, String.format("handleMessage: Lat: %s Lon: %s",mLat,mLon));
				
				Calendar c = Calendar.getInstance();
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				String formattedDate = df.format(c.getTime());
				
				SimpleDateFormat df_hora = new SimpleDateFormat("HH:mm:ss");
				String formattedHora = df_hora.format(c.getTime());
				
				
				JGPS_Posicion gps_posicion = new JGPS_Posicion(mLat, mLon, formattedDate, formattedHora, mLongInt,mLatInt);
				PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
				JBD bd = new JBD(getApplicationContext(),"vendingmovil",info.versionCode);
				//Obtenemos la ultima posición guardada en la bbdd:
				JGPS_Posicion ultimaPosicionGuardada = bd.getUltimaPosicion();
				//Si es la primera posición que registramos (ultimaPosicion es null)
				//o la distancia de la nueva posición con respecto a la ultima es mayor
				//de un minimo (en este caso 10 metros), añadimos a la BBDD:
				boolean agregarPosicion = true;
				if (ultimaPosicionGuardada!=null) {
					//Creamos un par de objetos Location con la ultima posición registrada
					//y la nueva, para calcular la distancia entre ambas:
					Location locLast = new Location("last");
					locLast.setLatitude(ultimaPosicionGuardada.getLatitud());
					locLast.setLongitude(ultimaPosicionGuardada.getLongitud());
					Location locNew = new Location("new");
					locNew.setLatitude(mLat);
					locNew.setLongitude(mLon);
					//Obtenemos la distancia lineal (en metros) entre ambas posiciones:
					float distance = locLast.distanceTo(locNew);
					//Si no hay al menos 10 metros de distancia, no la añadimos:
					if (distance<10f) agregarPosicion = false;
				} 
				if (agregarPosicion){
					//Guardamos la nueva posición:
					bd.addGPS_Posicion(gps_posicion);
					//aqui tengo que meter
					//la conexion remota
					//copio y pego de comun activity, porque no se si puedo extends comun, porque esta extends service
					
					
					JSONObject jsonResponse=null;
					boolean resultado=false;
					mServerMsg="";
					
						JSONObject jsonRequest = new JSONObject();
						jsonRequest.put("codigo_usuario", codigo_usuario);
						jsonRequest.put("lat", mLat);
						jsonRequest.put("longitud", mLon);
						String respuesta=ServerConnection(jsonRequest,"sw_gps.php");
					
					
					
					
					//he desconectado el brodcast porque no se tiene que actualizar nada en el movil
					//Enviamos la señal de broadcast para que la activity Main se actualice
					//en caso de que este en estos momentos en primer plano en pantalla:
					//Intent intent = new Intent();
					//intent.setAction(BC_NUEVA_POSICION);
					//sendBroadcast(intent);
				}
				
				// Stop the service using the startId, so that we don't stop
				// the service in the middle of handling another job
				Log.e(TAG, "handleMessage: stopSelf");
				//Stop the service:
				stopSelf(msg.arg1);
			} catch (Exception e) {
				Log.e(TAG, "handleMessage:"+e.getMessage());
			}
			
		}
	}
	
	//Función perteneciente a la clase Service y de obligada implementación aunque no se use:
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	//Funciones asociadas a la implementación de los listeners de ubicación (implements ...)
	@Override
	public void onConnected(Bundle connectionHint) {
		//Intentamos obtener la última ubicación conocida:
		Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		if (location != null) {
			Log.e(TAG, "onConnected: Location NOT null!!");
			mLat = location.getLatitude();
			mLon = location.getLongitude();
			mLatInt = Integer.valueOf((int) (mLat * 10000000));
			mLongInt = Integer.valueOf((int) (mLon * 10000000));
		}
		//Solicitamos que se refesque la ubicación actual:
		LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
	}
	@Override
	public void onLocationChanged(Location location) {
		Log.e(TAG, "onLocationChanged!!");
		//Esta función se ejecuta automaticamente cuando se detecta una nueva ubicación:
		if (location !=null) {
			//Actualizamos los atributos de la clase donde guardamos lat y lon actuales:
			mLat = location.getLatitude();
			mLon = location.getLongitude();
			mLatInt = Integer.valueOf((int) (mLat * 10000000));
			mLongInt = Integer.valueOf((int) (mLon * 10000000));
		}
	}
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
		
	}
	
	//Esta funcion recibe como parametros, los datos a enviar en formato JSON, y el servicio web al cual enviaserselos.
		//Recibe como respuesta un fichero en formato JSON con la respuesta del servidor, y lo transforma en un String.
		protected String ServerConnection(JSONObject jsonRequest,String web_service){
			try {
				String link = String.format("%s/%s%s", SW_ROOT,SW_FOLDER,web_service);
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
