package com.example.etxeberria_vending;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;

import com.example.etxeberria_vending.clases.JBD;
import com.example.etxeberria_vending.clases.JCarga;
import com.example.etxeberria_vending.clases.JCargador;
import com.example.etxeberria_vending.clases.JLinea;
import com.example.etxeberria_vending.clases.JMaquina;
import com.example.etxeberria_vending.clases.JPlaza;
import com.example.etxeberria_vending.clases.JPosicion;
import com.example.etxeberria_vending.clases.JProducto;
import com.example.etxeberria_vending.clases.JRuta;
import com.example.etxeberria_vending.clases.JRuta_dia;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class LeerDatos_Activity extends Activity{
	private final String TAG="LeerDatos_Activity";
	private ListView lista;
	private String path;
	private final String SEP=";";
	private ArrayList<JTabla> mTablas;
	private ArrayList<JCarga> mListaCargas;
	private ArrayList<JCargador> mListaCargadores;
	private ArrayList<JLinea> mListaLineas;
	private ArrayList<JMaquina> mListaMaquinas;
	//private ArrayList<JMi_carga> mListaMis_cargas;
	private ArrayList<JPlaza> mListaPlazas;
	private ArrayList<JPosicion> mListaPosiciones;
	private ArrayList<JProducto> mListaProductos;
	private ArrayList<JRuta> mListaRutas;
	private ArrayList<JRuta_dia> mListaRutas_dia;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.leerdatos_layout);
		try {
			Log.e("VendingCSV", "abriendo .vending");
			//Asociamos la propiedad al control:
			lista = (ListView) findViewById(R.id.lvDatosImportar);


		} catch (Exception e) {
			Log.e("VENDING", "onCreate:"+e.getMessage());
		}
		boolean leer_datos = false;
		try {
			Uri uri = getIntent().getData();
			if (uri != null) {
				InputStream attachment = getContentResolver().openInputStream(uri);
				if (attachment == null)
					Log.e("GMAIL ATTACHMENT", "Mail attachment failed to resolve");
				else {

					FileOutputStream tmp = new FileOutputStream(getCacheDir().getPath() + "/temp.myfile");
					byte[] buffer = new byte[1024];
					while (attachment.read(buffer) > 0)
						tmp.write(buffer);
					tmp.close();
					attachment.close();
					path= getCacheDir().getPath() + "/temp.myfile";
					leer_datos = true;
				}
			}
		}catch(Exception e){
			Log.e(TAG, "onCreate (llerdatos)"+e.getMessage());
		}
		if (leer_datos){
			Log.e("VendingCSV", "detectado .vending");
			LeerCSV();
		} else {
			Log.e("VendingCSV", "no detectado .vending");
			Toast.makeText(LeerDatos_Activity.this, "no detectado .vending", Toast.LENGTH_SHORT).show();
		}
	}

	private void LeerCSV(){
		try {
			Log.e("VENDINGCSV", path);
			Uri uri = Uri.parse(path);
			File file = new File(uri.getPath());
			BufferedReader buffer = new BufferedReader(new FileReader(file));

			//saltamos la primera fila que es la cabecera
			//String line = buffer.readLine();
			String line = "";
			mTablas = new ArrayList<LeerDatos_Activity.JTabla>();
			mListaCargas=new ArrayList<JCarga>();
			mListaCargadores = new ArrayList<JCargador>();
			mListaLineas = new ArrayList<JLinea>();
			mListaMaquinas = new ArrayList<JMaquina>();
			//la de mi carga no hace falta
			mListaPlazas = new ArrayList<JPlaza>();
			mListaPosiciones = new ArrayList<JPosicion>();
			mListaProductos=new ArrayList<JProducto>();
			mListaRutas=new ArrayList<JRuta>();
			mListaRutas_dia=new ArrayList<JRuta_dia>();
			int num_linea=0;
			while ((line = buffer.readLine()) != null) {
				try{
					num_linea++;
					String[] str = line.split(SEP);
					//La primera columna indica la tabla:
					if (str[0].toUpperCase().equals("CARGAS")){
						JCarga item = new JCarga(Integer.valueOf(str[1]),Integer.valueOf(str[2]),Integer.valueOf(str[3]),Integer.valueOf(str[4]),str[5],Integer.valueOf(str[6]),str[7],str[8]);
						mListaCargas.add(item);
					}

					if (str[0].toUpperCase().equals("CARGADORES")){
						JCargador item = new JCargador(Integer.valueOf(str[1]),str[2],str[3],str[4],str[5]);
						mListaCargadores.add(item);
					}
					if (str[0].toUpperCase().equals("LINEAS")){
						JLinea item = new JLinea(Integer.valueOf(str[1]),Integer.valueOf(str[2]),Integer.valueOf(str[3]),Integer.valueOf(str[4]),Integer.valueOf(str[5]),Integer.valueOf(str[6]),Integer.valueOf(str[7]),str[8],str[9],Integer.valueOf(str[10]),Integer.valueOf(str[11]),Integer.valueOf(str[12]));
						mListaLineas.add(item);
					}
					if (str[0].toUpperCase().equals("MAQUINAS")){
						JMaquina item = new JMaquina(Integer.valueOf(str[1]),str[2],str[3],str[4],Integer.valueOf(str[5]),Integer.valueOf(str[6]),Integer.valueOf(str[7]),str[8]);
						mListaMaquinas.add(item);
					}
					if (str[0].toUpperCase().equals("PLAZAS")){
						double lat = Double.valueOf(str[6])/10000000d;
						double lon = Double.valueOf(str[7])/10000000d;
						JPlaza item = new JPlaza(Integer.valueOf(str[1]),str[2],str[3],str[4],str[5],lat,lon,Integer.valueOf(str[8]),str[9]);
						mListaPlazas.add(item);
					}
					if (str[0].toUpperCase().equals("POSICIONES")){
						JPosicion item = new JPosicion(Integer.valueOf(str[1]),Integer.valueOf(str[2]),Integer.valueOf(str[3]),str[4],Integer.valueOf(str[5]),Integer.valueOf(str[6]),Integer.valueOf(str[7]),str[8],Integer.valueOf(str[9]));
						mListaPosiciones.add(item);
					}
					if (str[0].toUpperCase().equals("PRODUCTOS")){
						JProducto item = new JProducto(Integer.valueOf(str[1]),str[2],str[3],Integer.valueOf(str[4]),str[5]);
						mListaProductos.add(item);
					}
					if (str[0].toUpperCase().equals("RUTAS")){
						JRuta item = new JRuta(Integer.valueOf(str[1]),Integer.valueOf(str[2]),str[3],Integer.valueOf(str[4]),str[4],Float.valueOf(str[5]));
						mListaRutas.add(item);
					}
					if (str[0].toUpperCase().equals("RUTAS_DIA")){
						JRuta_dia item = new JRuta_dia(Integer.valueOf(str[1]),Integer.valueOf(str[2]),Integer.valueOf(str[3]),str[4],str[5]);
						mListaRutas_dia.add(item);
					}
				}catch(Exception e){
					Log.e("VENDING", String.format("LeerCSV (while) Line(%s):%s",num_linea,line));
				}
			}
			buffer.close();

			//Recorremos los arraysList para saber cuantos datos vamos a cargar:
			JTabla tabla = new JTabla("CARGAS", mListaCargas.size());
			mTablas.add(tabla);
			tabla = new JTabla("CARGADORES", mListaCargadores.size());
			mTablas.add(tabla);
			tabla = new JTabla("LINEAS", mListaLineas.size());
			mTablas.add(tabla);
			tabla = new JTabla("MAQUINAS", mListaMaquinas.size());
			mTablas.add(tabla);
			tabla = new JTabla("PLAZAS", mListaPlazas.size());
			mTablas.add(tabla);
			tabla = new JTabla("POSICIONES", mListaPosiciones.size());
			mTablas.add(tabla);
			tabla = new JTabla("PRODUCTOS", mListaProductos.size());
			mTablas.add(tabla);
			tabla = new JTabla("RUTAS", mListaRutas.size());
			mTablas.add(tabla);
			tabla = new JTabla("RUTAS_DIA", mListaRutas_dia.size());
			mTablas.add(tabla);

			//Creamos un objeto adapter básico, al que le indicamos el layout de la fila, y los datos a mostrar:
			MiArrayAdapter datosAdapter = new MiArrayAdapter(this); 
			//Asignamos el adapter al contenedor lista:
			lista.setAdapter(datosAdapter);
		} catch (Exception e) {
			Log.e("VENDING", "LeerCSV:"+e.getMessage());
		}
	}

	public void btnImportar_Click(View view){
		ImportarDatos importar = new ImportarDatos();
		importar.execute();
		Intent intent = new Intent(LeerDatos_Activity.this,Main_Activity.class);
		startActivity(intent);
	}

	private class MiArrayAdapter extends ArrayAdapter<JTabla>{

		public MiArrayAdapter(Context context) {
			super(context, R.layout.leerdatos_row_layout,mTablas);
		}
		//Sobreescribimos la función getView, por la cual pasará cada vez que tenga que "pintar" una fila del ListView
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			//"Inflamos" una fila, indicando el layout a utilizar:
			LayoutInflater inflater = LeerDatos_Activity.this.getLayoutInflater();
			View rowView= inflater.inflate(R.layout.leerdatos_row_layout, null);

			//Rellenamos la fila con la información (TextView con la ciudad e ImageView con la imagen)
			TextView tvTabla = (TextView) rowView.findViewById(R.id.tvTabla);
			tvTabla.setText(String.format("%s: %s", mTablas.get(position).mTabla,mTablas.get(position).mNumRegistros));
			return rowView;
		}
	}

	private class JTabla {
		public String mTabla;
		public int mNumRegistros;
		public JTabla(String tabla, int numRegistros){
			mTabla = tabla;
			mNumRegistros = numRegistros;
		}
	}

	private class ImportarDatos extends AsyncTask <Void, Void, Void> {
		private ProgressDialog dialog;

		@Override
		protected void onPreExecute() {
			dialog = new ProgressDialog(LeerDatos_Activity.this);
			dialog.setMessage("Importando datos....");
			dialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				//Conectar con la bbdd
				PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
				JBD bd = new JBD(LeerDatos_Activity.this,"vendingmovil",info.versionCode);

				if (mListaCargas.size()>0){
					bd.vaciarCargas();
					for (int i=0;i<mListaCargas.size();i++){
						bd.addCarga(mListaCargas.get(i));
					}
				}
				if (mListaCargadores.size()>0){
					bd.vaciarCargadores();
					for (int i=0;i<mListaCargadores.size();i++){
						bd.addCargador(mListaCargadores.get(i));
					}
				}
				if (mListaLineas.size()>0){
					bd.vaciarLineas();
					for (int i=0;i<mListaLineas.size();i++){
						bd.addLinea(mListaLineas.get(i));
					}
				}
				if (mListaMaquinas.size()>0){
					bd.vaciarMaquinas();
					for (int i=0;i<mListaMaquinas.size();i++){
						bd.addMaquina(mListaMaquinas.get(i));
					}
				}
				if (mListaPlazas.size()>0){
					bd.vaciarPlazas();
					for (int i=0;i<mListaPlazas.size();i++){
						bd.addPlaza(mListaPlazas.get(i));
					}
				}
				if (mListaPosiciones.size()>0){
					bd.vaciarPosiciones();
					for (int i=0;i<mListaPosiciones.size();i++){
						bd.addPosicion(mListaPosiciones.get(i));
					}
				}
				if (mListaProductos.size()>0){
					bd.vaciarProductos();
					for (int i=0;i<mListaProductos.size();i++){
						bd.addProducto(mListaProductos.get(i));
					}
					String fecha_inicial ="2015-01-01";
					bd.vaciarProductos_inicial(fecha_inicial);
				}
				if (mListaRutas.size()>0){
					bd.vaciarRutas();
					for (int i=0;i<mListaRutas.size();i++){
						bd.addRuta(mListaRutas.get(i));
					}
				}
			} catch (Exception e) {
				Log.e("VENDING", "btnImportar_Click:"+e.getMessage());
			}
			return null;
		}
		/*
	    @Override
	    protected void onProgressUpdate(Void... values) {
	    	// TODO Auto-generated method stub
	    	super.onProgressUpdate(values);
	    }
		 */
		@Override
		protected void onPostExecute(Void result) {
			try {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			Toast.makeText(LeerDatos_Activity.this, "Datos importados", Toast.LENGTH_SHORT).show();
		}     
	}
}
