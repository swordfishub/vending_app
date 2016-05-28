package com.example.etxeberria_vending;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONObject;


import com.example.etxeberria_vending.clases.JBD;
import com.example.etxeberria_vending.clases.JCarga;
import com.example.etxeberria_vending.clases.JMaquina;
import com.example.etxeberria_vending.clases.JPlaza;
import com.example.etxeberria_vending.clases.JPosicion;
import com.example.etxeberria_vending.clases.JProducto;
import com.example.etxeberria_vending.clases.JRuta;
import com.example.etxeberria_vending.clases.JRuta_dia;
import com.example.etxeberria_vending.clases.JUsuario;
import com.example.etxeberria_vending.clases.JUtils;
import com.example.etxeberria_vending.track.JGPS_Posicion;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Cargando_Activity extends Comun_Activity {
	
	//Varaible para obtener mensajes del servidor, cuando algo ha ido mal
		private String mServerMsg;
		
		private final String TAG="Cargando_Activity";
	
	 private static final int CAMERA_REQUEST = 1888; 
	    private ImageView imageView;
	    private File f;
	    public File getAlbumDir()
	    {

	        File storageDir = new File(
	                Environment.getExternalStoragePublicDirectory(
	                    Environment.DIRECTORY_PICTURES
	                ), 
	                "BAC/"
	            ); 
	         // Create directories if needed
	        if (!storageDir.exists()) {
	            storageDir.mkdirs();
	        }

	        return storageDir;
	    }
	    private File createImageFile() throws IOException {
	        // Create an image file name

	        String imageFileName =getAlbumDir().toString() +"/image.jpg";
	        File image = new File(imageFileName);
	        return image;
	    }

	
	private MiArrayAdapter mDatosAdapter;
	private ArrayList<JPosicion> mItems;
	private ListView mLista;
	private EditText etFiltro;
	private int id_maquina;
	private ArrayList<JProducto> mProductos;
	private JBD mBD;
	private Button btnFin_carga;
	private EditText etContador;
	private Button btnIncidencia;
	private JPlaza mPlaza;
	private String tipoMaquina;
	private String nombreMaquina;
	private String nombrePlaza;
	private int pr_venta;
	private int producto_id;
	private int id_posicion;
	private int id_carga;
	private int ultimas;
	private int id_plaza;
	private int id_ruta;
	private String pos_fila_columna;
	private float euros; 
	private String redondear;
	private String formattedDate;
	private String formattedHora;
	private int id_cargador;
	private int contador;
	private String nombre_cargador;
	private String email_oficina;
	private String email_tecnico;
	private String email_jefe;
	private Boolean SQL;
	private CheckBox cbFoto;
	private JGPS_Posicion mJGPS_Posicion;
	private JRuta mRuta;
	
	


		
	//variables para recuperar la posicion del listview
	private int index;
	private int top;
	@Override

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cargando_layout);
		try {
			etFiltro = (EditText)findViewById(R.id.etFiltro);
			mLista = (ListView) findViewById(R.id.lvLista);
			btnFin_carga = (Button)findViewById(R.id.btnFin_carga);
			etContador =(EditText)findViewById(R.id.etContador);
			cbFoto = (CheckBox)findViewById(R.id.cbFoto);
			btnIncidencia = (Button)findViewById(R.id.btnIncidencia);
			 this.imageView = (ImageView)this.findViewById(R.id.imageView1);
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
			mBD = new JBD(Cargando_Activity.this,getString(R.string.app_preferences),info.versionCode);
			mProductos = mBD.getProductos("");
			
			//Para evitar que se muestre automaticamente el teclado al iniciar la activity
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		} catch (Exception e) {
			Log.e(JUtils.TAG,"oncreate:"+e.getMessage());
		}
		try {
			mLista.setOnItemLongClickListener(new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					//guardamos la posicion del listview
					index = mLista.getFirstVisiblePosition();
					View v = mLista.getChildAt(0);
					pos_fila_columna="";
					pos_fila_columna = mItems.get(position).getPos_fila_columna();
					
					//intentar borrar la linea que se hubiera metido antes de querer cambiar el artículo para no duplicar lineas
					int id_posicion = mItems.get(position).getID();
					mBD.deleteLinea_hoy(id_posicion, formattedDate);
					
					
					Intent intent = new Intent(Cargando_Activity.this,SeleccionProducto_Activity.class);
					intent.putExtra("posicion", mItems.get(position).getID());
					intent.putExtra("pos_fila_columna", pos_fila_columna);
					intent.putExtra("tipo_maquina", tipoMaquina);
					intent.putExtra("nombreMaquina", nombreMaquina);
					intent.putExtra("fecha", formattedDate);
					intent.putExtra("producto_old", producto_id);
					intent.putExtra("id_carga", id_carga);
					intent.putExtra("plaza_id", id_plaza);
					startActivityForResult(intent,0);
					return true;
				}

			});
		} catch (Exception e) {
			Log.e(JUtils.TAG,"LA:onResume:"+e.getMessage());
		}
		try {
			//Identificamos el bloque de preferencias que nos interesa (ultimo parametro: 0=Privado, 1=Lectura (otras apps), 2=Escritura (Otras apps)
    		SharedPreferences preferences = getSharedPreferences("VendingPreferencias", 0);
    		//Obtenemos las preferencias, indicando la clave (si no existe, valor vacio)
    		id_cargador = preferences.getInt("id_cargador", 0);
    		nombre_cargador = preferences.getString("nombre_cargador", "");
    		email_oficina = preferences.getString("email_oficina", "");
    		email_tecnico = preferences.getString("email_tecnico", "");
    		email_jefe = preferences.getString("email_jefe", "");
    		SQL =false;
    		SQL = preferences.getBoolean("SQL", SQL);
		} catch (Exception e) {
			// TODO: handle exception
		}

		mLista.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
				//guardamos la posicion del listview
				index = mLista.getFirstVisiblePosition();
				View v = mLista.getChildAt(0);
				top = (v == null) ? 0 : (v.getTop() - mLista.getPaddingTop());
				//Obtenemos el id del item seleccionado y vamos a editar:
				producto_id = mItems.get(position).getProducto_id();
				if(producto_id<2){
					Toast.makeText(Cargando_Activity.this, "selecciona un producto manteniendo pulsado", 
							Toast.LENGTH_SHORT).show();
					return;
				}
				pos_fila_columna="";
				id_posicion = mItems.get(position).getID();
				ultimas = mItems.get(position).getUltimas();
				String ultima_fecha=mItems.get(position).getFecha();
				int unidades_cargadas=0;
				if((ultima_fecha).equals(formattedDate)){
					unidades_cargadas=ultimas;
				}
				pos_fila_columna = mItems.get(position).getPos_fila_columna();
				producto_id = mItems.get(position).getProducto_id();
				pr_venta = mItems.get(position).getPr_venta();
				String nombre_producto="";
				int pack =0;
				for (int i=0;i<mProductos.size();i++){
					if (mProductos.get(i).getID()==producto_id){
						nombre_producto= mProductos.get(i).getProd_nombre();
						pack = mProductos.get(i).getProd_pack();
						break;
					}
				}
				
				Intent intent = new Intent(Cargando_Activity.this,CargandoDetalle_Activity.class);
				intent.putExtra("id_posicion", id_posicion);
				intent.putExtra("producto_id", producto_id);
				intent.putExtra("nombre_producto", nombre_producto);
				intent.putExtra("pack", pack);
				intent.putExtra("pos_fila_columna", pos_fila_columna);
				intent.putExtra("pr_venta", pr_venta);
				intent.putExtra("nombreMaquina", nombreMaquina);
				intent.putExtra("fecha", formattedDate);
				intent.putExtra("id_carga", id_carga);
				intent.putExtra("unidades_cargadas", unidades_cargadas);
				intent.putExtra("plaza_id", id_plaza);
				intent.putExtra("id_cargador", id_cargador);
				startActivityForResult(intent,0);
			}
		});
		
		try {
			Bundle datos = getIntent().getExtras();
			id_maquina = datos.getInt("id_maquina",0);
			
			//cargamos la plaza
			JMaquina m = mBD.getMaquina(id_maquina);
			mPlaza = mBD.getPlaza(m.getPlaza());
			id_plaza = mPlaza.getID();
			double Lat=mPlaza.getPlaza_latitud();
			//si no está geoposicionada la geoposicionamos
			if(Lat==0){
				mJGPS_Posicion=mBD.getUltimaPosicion();
				double mLAT=mJGPS_Posicion.getLatitud();
				double mLONG=mJGPS_Posicion.getLongitud();
				id_plaza =mPlaza.getID();
				try {
					mBD.updatePlaza_GPS(mLAT, mLONG, id_plaza);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
			}
			//y el tipo de máquina
			tipoMaquina = m.getMaq_tipo();
			cbFoto.setChecked(false);
			if (tipoMaquina.equals("snack")){
				cbFoto.setChecked(true);
			}
			nombreMaquina =m.getMaq_nombre();
			nombrePlaza =mPlaza.getPlaza_empresa();
			
			getActionBar().setTitle(nombreMaquina+"-"+nombrePlaza);
			getActionBar().setIcon(R.drawable.loguito);
			//inserto en rutas la ruta del día si es la primera.
			
			Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			formattedDate = df.format(c.getTime());
			mBD.updateMaquina_fecha(formattedDate, id_maquina);
			mBD.updatePlaza_fecha(formattedDate, id_plaza);
			JRuta r = new JRuta(id_cargador, formattedDate,0,"",0f);
			//La siguiente función ademas de añadir una nueva ruta, devuelve el id si la crea:
			id_ruta=0;
			id_ruta = mBD.addRuta(r);
			if (id_ruta>0){
				//aqui va la accion para insertar en el servidor sql
				mRuta = new JRuta(id_ruta,id_cargador, formattedDate,0,"",0f);
				Ruta_Registrar registro = new Ruta_Registrar();
				registro.execute();
				
			}
			
			//inserto en ruta_dia la plaza si es la primera maquina. 
			SimpleDateFormat df_hora = new SimpleDateFormat("HH:mm:ss");
			formattedHora = df_hora.format(c.getTime());
			
			JRuta_dia dia = new JRuta_dia(id_ruta, mPlaza.getID(),formattedHora,formattedDate);
			int id_ruta_dia = mBD.addRuta_dia(dia);
			
			// inserto en cargas la máquina
			contador =0;
			int estado =0;
			JCarga carga = new JCarga(id_ruta_dia,id_cargador,id_maquina,contador,formattedDate,estado,formattedHora,formattedHora);
			id_carga =mBD.addCarga(carga);
			
			//si no tenemos registrada la ubicacion latitud y longitud de la plaza 
			//if (mPlaza.getPlaza_latitud()==0d){
				//Toast.makeText(context, text, duration);
				//Intent intent = new Intent(Cargando_Activity.this,Locacion_Activity.class);
				//intent.putExtra("id_plaza", mPlaza.getID());
				//startActivity(intent);
			//}
		} catch (Exception e) {
			Log.e(JUtils.TAG,"oncreate:"+e.getMessage());
		}
	}
	
	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
	        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {  

	            Bitmap photo = BitmapFactory.decodeFile(f.getAbsolutePath());
	            imageView.setImageBitmap(photo);

	            Intent i = new Intent(Intent.ACTION_SEND);
	            i.setType("message/rfc822");
	            i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"icorchon@hotmail.com"});
	            i.putExtra(Intent.EXTRA_SUBJECT, "foto maquina "+id_maquina+" "+nombreMaquina+" "+nombrePlaza+" "+formattedHora);
	            i.putExtra(Intent.EXTRA_TEXT   , "foto de la maquina terminada");

	            Uri uri = Uri.fromFile(f);
	            i.putExtra(Intent.EXTRA_STREAM, uri);
	            try {
	                //startActivity(Intent.createChooser(i, "Send mail..."));
	            	startActivity(i);
	            } catch (android.content.ActivityNotFoundException ex) {
	                Toast.makeText(Cargando_Activity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
	                
	            }
	            finish();
	        } 
	        
	    } 

	@Override
	protected void onResume() {
		String condicion=null;

		try {
			//Para evitar que se muestre automaticamente el teclado al iniciar la activity
			//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			etContador.clearFocus();
			etFiltro.clearFocus();
			mLista.requestFocus();
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			
			condicion = String.format(" and id_maquina='%s'", id_maquina);
			//String condicion=String.format("and producto='%s' order by unidades Asc",filtro);
			mItems = mBD.getPosicions(condicion);
			mDatosAdapter = new MiArrayAdapter(this); 
			mLista.setAdapter(mDatosAdapter);
			// restore index and position
			mLista.setSelectionFromTop(index, top);
		} catch (Exception e) {
			Log.e(JUtils.TAG,"LA:onResume:"+e.getMessage());
		}
		super.onResume();
	}

	private class MiArrayAdapter extends ArrayAdapter<JPosicion>{

		public MiArrayAdapter(Context context) {
			super(context, R.layout.cargando_row_layout,mItems);
		}
		//Sobreescribimos la función getView, por la cual pasará cada vez que tenga que "pintar" una fila del ListView
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			View rowView=null;

			//"Inflamos" una fila, indicando el layout a utilizar:
			LayoutInflater inflater = Cargando_Activity.this.getLayoutInflater();
			rowView= inflater.inflate(R.layout.cargando_row_layout, null);

			//Rellenamos la fila con la información
			TextView tvTitle = (TextView) rowView.findViewById(R.id.tvProducto);
			TextView tvPosicion = (TextView) rowView.findViewById(R.id.tvPosicion);
			TextView tvObserv = (TextView) rowView.findViewById(R.id.tvDescripcion);
			ImageView ivProducto =(ImageView)rowView.findViewById(R.id.ivProducto);
			TextView tvFecha = (TextView) rowView.findViewById(R.id.tvFecha);
			TextView tvPrecio = (TextView) rowView.findViewById(R.id.tvPrecio);
			TextView tvUnidades= (TextView) rowView.findViewById(R.id.tvUnidades);
			TextView tvCarga= (TextView) rowView.findViewById(R.id.tvCarga);
			tvPosicion.setText(mItems.get(position).getPos_fila_columna());
			//Busco en el arraylist mConceptos, la descripción del código que tiene este movimiento
			producto_id = (mItems.get(position).getProducto_id());
			pr_venta = (mItems.get(position).getPr_venta());
			String fecha_ultima = (mItems.get(position).getFecha());
			ultimas = (mItems.get(position).getUltimas());
			String nombre_producto="";
			for (int i=0;i<mProductos.size();i++){
				if (mProductos.get(i).getID()==producto_id){
					nombre_producto= mProductos.get(i).getProd_nombre();
					break;
				}
			}
			
			if ((fecha_ultima).equals(formattedDate)){
			tvUnidades.setText(""+ultimas);	
			tvCarga.setText("Carga:");
			}
			tvTitle.setText(nombre_producto);
			int resId=getResources().getIdentifier(String.format("i%s",mItems.get(position).getProducto_id()),"drawable",getPackageName());
			if (resId==0) resId = R.drawable.loguito;
			ivProducto.setImageResource(resId);
			euros = pr_venta/100f;
			if(euros>0){
			//String precio = String.valueOf(euros);
			redondear = String.format("%.2f", euros);
			tvPrecio.setText("precio: "+redondear+" €");
			}
			//txtUnidades.setText("stock:"+String.valueOf(mItems.get(position).getUnidades()));
			//tvPrcoste.setText("precio de coste: "+String.valueOf(mItems.get(position).getPr_coste()));
			
			//tvMargen.setText("el margen es "+String.valueOf(margen));
			//tvMargen.setText("el margen es "+ redondear);


			return rowView;
		}
	}
public void Reportar_incidencia(View view){
	//Enviar por mail:
	String mensaje = "reportando averia "+ id_maquina + nombreMaquina +" " +nombrePlaza;
	Intent sendIntent = new Intent(Intent.ACTION_SEND);
	sendIntent.putExtra(Intent.EXTRA_SUBJECT, mensaje);
	sendIntent.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] { "icorchon@hotmail.com; etxebendin@gmail.com" });
	
	sendIntent.setType("text/html");
	startActivity(sendIntent);
}
public void Finalizar_Carga(View view){
	try {
		
	String contador_string=etContador.getText().toString();
	if(contador_string.length()==0){
				
			//Mostramos un alert dialog, para confirmar que quiere cerrar sin guardar
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cargando_Activity.this);
			alertDialog.setTitle("No has introducido contador");
			alertDialog.setMessage("Introducir contador?");
			alertDialog.setPositiveButton("Si",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					etContador.requestFocus();
					getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
					return;
				}
			});

			alertDialog.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					etContador.setText("0");
					Finalizar_Carga(null);
					
				}
			});
			alertDialog.show();
			return;
			
	}
		
	}catch (Exception e) {
				// TODO: handle exception
			}
	Calendar c = Calendar.getInstance();
	SimpleDateFormat df_hora = new SimpleDateFormat("HH:mm:ss");
	String formattedHora2 = df_hora.format(c.getTime());
	String contador_string=etContador.getText().toString();
	contador=0;
	if(contador_string.length()>0){
	contador = Integer.valueOf(contador_string);
	}
	try {
		mBD.finCarga(contador, formattedHora2, id_carga);
	} catch (Exception e) {
		// TODO: handle exception
	}
	if(cbFoto.isChecked()==false){
		Toast.makeText(Cargando_Activity.this, "Carga guardada", Toast.LENGTH_SHORT).show();
		finish();
		}
	
	if(cbFoto.isChecked()){
		PictureCamera();
		}
}
public void PictureCamera(){
	try {
        f = createImageFile();
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        startActivityForResult(cameraIntent, CAMERA_REQUEST); 
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
}
private void Salir(){
	try {
		//Mostramos un alert dialog, para confirmar que quiere cerrar sin guardar
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cargando_Activity.this);
		alertDialog.setTitle("Cargando máquina");
		alertDialog.setMessage("Finalizar carga?");
		alertDialog.setPositiveButton("Si",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Finalizar_Carga(null);
				Toast.makeText(Cargando_Activity.this, "Carga guardada", Toast.LENGTH_SHORT).show();
				finish();
			}
		});

		alertDialog.setNegativeButton("No",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		alertDialog.show();
	} catch (Exception e) {
		//Log.e(Cargando_Activity.this,"salir: "+e.getMessage());
	}
}

//Esta es la función automatica que busca android cuando el usuario pulsa
//el boton flecha atras del movil:
@Override
public void onBackPressed() {
	//Comentamos la llamada super.onBac... porque no quiere que automaticamente
	//se cierre esta activity, sino que haga lo indica la función Salir()
    //super.onBackPressed();
    //Llamamos a la función que pregunta al usuario si quiere salir
    Salir();
}

private class Ruta_Registrar extends AsyncTask<String,Integer,Boolean> {
	//private ProgressDialog mPD;
	//En el onPreExecute aprovechamos a cargar el progress dialog:
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		//try {
			//mPD = new ProgressDialog(Cargando_Activity.this);
			//mPD.setCancelable(false);	//El usuario no puede ocultar el progress dando a la tecla back
			//mPD.setTitle("Registrando usuario...");
			//mPD.show();
		//} catch (Exception e) {
			//Log.e(TAG,"Usuario_Registrar (onPre):"+e.getMessage());
		//}
	}
	//Lo que pongamos en la funcion doInBack, esta fuera del hilo principal de ejecución. 
	//Esto conlleva que no podemos realizar instrucciones que requieran estar en el hilo principal,
	//como mostrar mensajes en pantalla (Toast), o modificar contenidos en el layout (mETMail.setText(""))
	@Override
	protected Boolean doInBackground(String... params) {
		JSONObject jsonResponse=null;
		boolean resultado=false;
		mServerMsg="";
		try {
			JSONObject jsonRequest = new JSONObject();
			jsonRequest.put("id_ruta",id_ruta );
			jsonRequest.put("id_cargador", id_cargador);
			jsonRequest.put("fecha", formattedDate);
			String respuesta=ServerConnection(jsonRequest,"sw_rutas.php");
			if (respuesta!=null) 
				try {
					jsonResponse = new JSONObject(respuesta); 
				} catch (Exception e) {
					Log.e(TAG,"Usuario_Registrar (jsonResponse):"+e.getMessage());
					jsonResponse=null;
				}
			if (jsonResponse!=null) {	
				//Obtenemos el parametro result: true (validación correcta, false incorrecta)
				resultado = jsonResponse.getBoolean("result");
				
				
				
				
				//Si resultado es false, vamos a ller el msg que nos envia desde php
				if (resultado==false){
					mServerMsg = jsonResponse.getString("msg");
				}
			} else {
				throw new Exception("JSONResponse es nulo");
			}
		} catch (Exception e) {
			Log.e(TAG,"Ruta_Registrar (doIn):"+e.getMessage());
		}
		return resultado;
	}
	//Cuando la funcion doInBack termina, salta automaticamente a la función onPost, y vuelve al hilo principal,
	//con lo que ya podemos mostrar mensajes o hacer cambios en los contenidos de layout
	@Override
	protected void onPostExecute(Boolean resultado) {
		final String msg;
		try {
			if (resultado==true){
				msg="Registro correcto!";
				mBD.updateRuta_Estado(id_ruta);
			} else {
				msg="Registro incorrecto: "+mServerMsg;
			}
			Toast.makeText(Cargando_Activity.this, msg, Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Log.e(TAG,"Usuario_Registrar (onPost):"+e.getMessage());
		}
		//mPD.dismiss();
		//Si ha ido bien, saltamos al listado de usuarios:
		
		super.onPostExecute(resultado);
	}
}

}

