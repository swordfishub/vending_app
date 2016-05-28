package com.example.etxeberria_vending;



import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.example.etxeberria_vending.clases.JBD;
import com.example.etxeberria_vending.clases.JCarga;
import com.example.etxeberria_vending.clases.JLinea;
import com.example.etxeberria_vending.clases.JMaquina;
import com.example.etxeberria_vending.clases.JPlaza;
import com.example.etxeberria_vending.clases.JPosicion;
import com.example.etxeberria_vending.clases.JRuta;
import com.example.etxeberria_vending.clases.JRuta_dia;
import com.example.etxeberria_vending.track.Alarm_Receiver;
import com.example.etxeberria_vending.track.JGPS_Posicion;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.opencsv.CSVWriter;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Main_Activity extends Activity {

	private final String TAG="Main_Activity";

	//Numero para identificar el servicio que lanzaremos en segundo plano (background)
	private final int MI_SERVICIO=1234;
	//Cuando esté en marcha, lanzaremos una notificación en la barra superior de Android
	private final int NOTIFICATION_ID=1;

	//Gestor de alarmas de Android:
	private AlarmManager mAlarmas;
	//Intent para añadir una nueva alarma y configurar la acción a realizar:
	private PendingIntent mIniciarAlarma;

	//En milisegundos, indica cada cuanto tiempo se tiene que lanzar la alarma:
	private long UPDATE_INTERVAL = 20000;
	//Tiempo inicial que esperamos en el primer lanzamiento (en segundos):
	private int START_DELAY = 3;
	//Variable para controlar si el servicio esta en marcha o detenido:
	private boolean mEstado;

	private ImageView ivFamilia;
	private TextView rowTextView;
	private String[] mmenu_inicial;
	private int[] iconos ={R.drawable.icon_ruta,R.drawable.icon_plaza,R.drawable.icon_maquina, R.drawable.icon_producto, R.drawable.icon_carga, R.drawable.icon_settings };
	private ListView lista;
	private String nombre_cargador;
	private int id_cargador;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		try {
			lista = (ListView) findViewById(R.id.lvMenu_inicio);

			mmenu_inicial = getResources().getStringArray(R.array.menu_inicial);

			MiArrayAdapter datosAdapter = new MiArrayAdapter(this); 
			lista.setAdapter(datosAdapter);

			lista.setOnItemLongClickListener(new OnItemLongClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
					//Obtenemos el id del item seleccionado y vamos a editar:

				}

				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					if (position==1){
						Intent intent = new Intent(Main_Activity.this,Mapa_Ruta_Activity.class);
						int id_ruta=0;
						intent.putExtra("id_ruta",id_ruta);
						startActivityForResult(intent,0);
						return true;
					}
					else{
						return false;
					}
				}

			});


			lista.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int posicion, long arg3) {
					String top = mmenu_inicial[posicion];
					//Toast.makeText(Main_Activity.this, top, Toast.LENGTH_SHORT).show();
					if (posicion ==0){
						Intent intent = new Intent(Main_Activity.this,Rutas_Activity.class);
						startActivity(intent);
					}
					if (posicion ==1){
						Intent intent = new Intent(Main_Activity.this,Plazas_Activity.class);
						startActivity(intent);
					}
					if (posicion ==2){
						Intent intent = new Intent(Main_Activity.this,Maquinas_Activity.class);
						startActivity(intent);
					}
					if (posicion ==3){
						Intent intent = new Intent(Main_Activity.this,Productos_Activity.class);
						startActivity(intent);
					}
					if (posicion ==4){
						//Intent intent = new Intent(Main_Activity.this,Lineas_Activity.class);
						//startActivity(intent);
						Toast.makeText(Main_Activity.this, top, Toast.LENGTH_SHORT).show();
					}

					if (posicion ==5){
						Intent intent = new Intent(Main_Activity.this,Settings_Activity.class);
						startActivity(intent);
					}
				}
			});
		} catch (Exception e) {
			Log.e("MainActivity","onCreate: "+e.getMessage());
		}
		try {
			//Identificamos el bloque de preferencias que nos interesa (ultimo parametro: 0=Privado, 1=Lectura (otras apps), 2=Escritura (Otras apps)
			SharedPreferences preferences = getSharedPreferences("VendingPreferencias", 0);
			//Obtenemos las preferencias, indicando la clave (si no existe, valor vacio)

			nombre_cargador = preferences.getString("nombre_cargador", "");
			getActionBar().setTitle("Hola "+nombre_cargador);
			getActionBar().setIcon(R.drawable.loguito);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}



	private class MiArrayAdapter extends ArrayAdapter<String>{

		public MiArrayAdapter(Context context) {
			super(context, R.layout.main_row_layout,mmenu_inicial);
		}
		//Sobreescribimos la función getView, por la cual pasará cada vez que tenga que "pintar" una fila del ListView
		@Override
		public View getView(int posicion, View view, ViewGroup parent) {
			//"Inflamos" una fila, indicando el layout a utilizar:
			LayoutInflater inflater = Main_Activity.this.getLayoutInflater();
			View rowView= inflater.inflate(R.layout.main_row_layout, null);

			//Rellenamos la fila con la información (TextView con la ciudad e ImageView con la imagen)
			TextView txtTitle = (TextView) rowView.findViewById(R.id.rowTextView);

			ImageView imageView = (ImageView) rowView.findViewById(R.id.ivFamilia);
			txtTitle.setText(mmenu_inicial[posicion]);

			imageView.setImageResource(iconos[posicion]);
			return rowView;
		}
	}




	public void btnExportar_Click(View view){
		CSVWriter writer = null;
		try {
			//Conectar con la bbdd
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
			JBD bd = new JBD(Main_Activity.this,"vendingmovil",info.versionCode);

			Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String fecha = df.format(c.getTime());

			try {
				//Identificamos el bloque de preferencias que nos interesa (ultimo parametro: 0=Privado, 1=Lectura (otras apps), 2=Escritura (Otras apps)
				SharedPreferences preferences = getSharedPreferences("VendingPreferencias", 0);
				//Obtenemos las preferencias, indicando la clave (si no existe, valor vacio)
				id_cargador = preferences.getInt("id_cargador", 0);

			} catch (Exception e) {
				// TODO: handle exception
			}

			int cargador_id=id_cargador;
			
			//exportamos el contenido de las tablas en formato csv y enviar por mail
			File root   = Environment.getExternalStorageDirectory();
			if (root.canWrite()){
				File dir    =   new File (root.getAbsolutePath() + "/VendingData");
				if (!dir.exists()) dir.mkdirs();
				File file   =   new File(dir, "Datos_dia.csv");
				writer = new CSVWriter(new FileWriter(file.getAbsolutePath()), ';');
				// le meto un encabezado
				String [] encabezado = {"encabezado",String.valueOf(id_cargador),nombre_cargador,fecha};
				writer.writeNext(encabezado);
				//leo las cargas:

				ArrayList<JCarga> cargas = bd.exportarCargas(fecha, cargador_id);
				//Generamos un String con todos los datos, separados por #
				for (int i=0;i<cargas.size();i++){
					String[] entries = cargas.get(i).getExportar().split("#");
					writer.writeNext(entries);
				}
				ArrayList<JLinea> lineas = bd.exportarLineas(fecha, cargador_id);
				//Generamos un String con todos los datos, separados por #
				for (int i=0;i<lineas.size();i++){
					String[] entries = lineas.get(i).getExportar().split("#");
					writer.writeNext(entries);
				}
				
				ArrayList<JGPS_Posicion> gps_posicion = bd.exportarPosicionesGPS("",cargador_id);
				//Generamos un String con todos los datos, separados por #
				for (int i=0;i<gps_posicion.size();i++){
					String[] entries = gps_posicion.get(i).getExportar().split("#");
					writer.writeNext(entries);
				}
				
				ArrayList<JPosicion> posiciones = bd.exportarPosicion(fecha, cargador_id);
				//Generamos un String con todos los datos, separados por #
				for (int i=0;i<posiciones.size();i++){
					String[] entries = posiciones.get(i).getExportar().split("#");
					writer.writeNext(entries);
				}
				ArrayList<JPlaza> plazas = bd.exportarPlazas(fecha, cargador_id);
				//Generamos un String con todos los datos, separados por #
				for (int i=0;i<plazas.size();i++){
					String[] entries = plazas.get(i).getExportar().split("#");
					writer.writeNext(entries);
				}
				ArrayList<JMaquina> maquinas = bd.exportarMaquinas(fecha, cargador_id);
				//Generamos un String con todos los datos, separados por #
				for (int i=0;i<maquinas.size();i++){
					String[] entries = maquinas.get(i).getExportar().split("#");
					writer.writeNext(entries);
				}
				ArrayList<JRuta_dia> rutas_dia = bd.exportarRutas_dia(fecha, cargador_id);
				//Generamos un String con todos los datos, separados por #
				for (int i=0;i<rutas_dia.size();i++){
					String[] entries = rutas_dia.get(i).getExportar().split("#");
					writer.writeNext(entries);
				}
				ArrayList<JRuta> rutas = bd.exportarRutas(fecha, cargador_id);
				//Generamos un String con todos los datos, separados por #
				for (int i=0;i<rutas.size();i++){
					String[] entries = rutas.get(i).getExportar().split("#");
					writer.writeNext(entries);
				}

				writer.close();
				//Enviar por mail:
				Uri u1= Uri.fromFile(file);
				Intent sendIntent = new Intent(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Reportando actividad");
				sendIntent.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] { "icorchon@hotmail.com; ignaciobti@gmail.com" });
				sendIntent.putExtra(Intent.EXTRA_STREAM, u1);
				sendIntent.setType("text/html");
				startActivity(sendIntent);
			}
		} catch (Exception e) {
			Log.e("MainActivity","btnExportar_Click: "+e.getMessage());
		}
		Alarm_Stop();
	}
	public void Escanear (View v) {
		try {
			checkAlarmRuning();
			if (mEstado==false){
				Alarm_Start();
			}
		} catch (Exception e) {
			Log.e(TAG, "btnOp_Click:"+e.getMessage());
		}
		IntentIntegrator scanIntegrator= new IntentIntegrator(this);
		scanIntegrator.initiateScan();

	}

	//Esta funcion comprueba si nuestra alarma ya está en la lista del alarm Manager:
	private void checkAlarmRuning() {
		try {
			boolean alarmUp = (PendingIntent.getBroadcast(getBaseContext(), MI_SERVICIO, 
					new Intent(getBaseContext(), Alarm_Receiver.class), 
					PendingIntent.FLAG_NO_CREATE) != null);
			if (alarmUp) {
				mEstado = true;
				Log.e(TAG, "Alarm is already active");
			} else {
				mEstado = false;
				Log.e(TAG, "Alarm is not active");
			}
		} catch (Exception e) {
			Log.e(TAG, "checkAlarmRuning:"+e.getMessage());
		}
	}
	//Creamos una nueva alarma, que llame al servicio en segundo plano cada X tiempo:
	private void Alarm_Start(){
		try {
			//Arrancamos el servicio para ir obteniendo posiciones:
			mAlarmas = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

			// get a Calendar object with current time
			Calendar cal = Calendar.getInstance();
			// add delay seconds to the calendar object
			cal.add(Calendar.SECOND, START_DELAY);
			//Creamos la alarma, que iniciará el servicio en segundo plano:
			Intent intent = new Intent(getBaseContext(), Alarm_Receiver.class);
			mIniciarAlarma = PendingIntent.getBroadcast(getBaseContext(), MI_SERVICIO, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			mAlarmas.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), UPDATE_INTERVAL, mIniciarAlarma);
			Log.e(TAG, "->Alarm_Start");
			checkAlarmRuning();
		} catch (Exception e) {
			Log.e(TAG, "Alarm_Start:"+e.getMessage());
		}
	}
	
	private void Alarm_Stop(){
		try {
			Intent intent = new Intent(getBaseContext(), Alarm_Receiver.class);
			mIniciarAlarma = PendingIntent.getBroadcast(getBaseContext(), MI_SERVICIO, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			mAlarmas = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
			mAlarmas.cancel(mIniciarAlarma);
			if (mIniciarAlarma!=null) mIniciarAlarma.cancel();
			Log.e(TAG, "->Alarm_Stop");
			//Comprobamos q se ha parado ok:
			checkAlarmRuning();
		} catch (Exception e) {
			Log.e(TAG, "Alarm_Stop:"+e.getMessage());
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent){
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanningResult != null){

			String scanContent = scanningResult.getContents();
			int id_maquina = Integer.valueOf(scanContent);
			Intent intent1 = new Intent(Main_Activity.this,Cargando_Activity.class);
			intent1.putExtra("id_maquina", id_maquina);
			startActivityForResult(intent1,0);
		}
	}

}
