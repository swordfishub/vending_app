package com.example.etxeberria_vending;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.joda.time.DateTime;
import org.joda.time.Days;

import com.example.etxeberria_vending.clases.JBD;
import com.example.etxeberria_vending.clases.JMaquina;
import com.example.etxeberria_vending.clases.JPlaza;
import com.example.etxeberria_vending.clases.JRuta;
import com.example.etxeberria_vending.clases.JRuta_dia;
import com.example.etxeberria_vending.clases.JUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class Rutas_Activity extends Activity {

	private MiArrayAdapter mDatosAdapter;
	private ArrayList<JRuta> mItems;

	private ListView mLista;
	private EditText etFiltro;
	private int id_ruta;
	
	private JBD mBD;
	private String formattedDate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rutas_layout);
		getActionBar().setTitle("Mis rutas");
		getActionBar().setIcon(R.drawable.icon_ruta);
		try {
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
			JBD db = new JBD(Rutas_Activity.this,getString(R.string.app_preferences),info.versionCode);
			//cargo el arraylist de las plazas
			
			//Para evitar que se muestre automaticamente el teclado al iniciar la activity
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
			//Asociamos la propiedad al control:
			mLista = (ListView) findViewById(R.id.lvLista);
			
			
			//Control de evento click sobre una fila:
			mLista.setOnItemLongClickListener(new OnItemLongClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
					//Obtenemos el id del item seleccionado y vamos a editar:
					
				}

				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					Intent intent = new Intent(Rutas_Activity.this,Mapa_Ruta_Activity.class);
					id_ruta= mItems.get(position).getID();
					intent.putExtra("id_ruta", id_ruta);
					intent.putExtra("ruta_fecha", mItems.get(position).getRuta_fecha());
					
					startActivityForResult(intent,0);
					return true;
				}
			
			});
			mLista.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
					//Obtenemos el id del item seleccionado y vamos a editar:
					id_ruta = mItems.get(position).getID();
					Intent intent = new Intent(Rutas_Activity.this,Ruta_dia_Activity.class);
					intent.putExtra("id_ruta", id_ruta);
					intent.putExtra("ruta_fecha", mItems.get(position).getRuta_fecha());
					startActivityForResult(intent,0);
				}
			});
			etFiltro = (EditText)findViewById(R.id.etFiltro);
			etFiltro.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					try {
						String Filtro =etFiltro.getText().toString();

						PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
						JBD db = new JBD(Rutas_Activity.this,getString(R.string.app_preferences),info.versionCode);
						String condicion="and fecha like '%"+Filtro+"%'";
						//String condicion=String.format("and producto='%s' order by unidades Asc",filtro);
						
						mItems = db.getRutas(condicion);
						mDatosAdapter = new MiArrayAdapter(Rutas_Activity.this); 
						mLista.setAdapter(mDatosAdapter);
					} catch (Exception e) {
						Log.e(JUtils.TAG,"onTextChanged:"+e.getMessage());
					}
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					// TODO Auto-generated method stub

				}

				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub

				}
			});
		} catch (Exception e) {
			//Log.e(JUtils.TAG,"LA:onCreate:Bundle:"+e.getMessage());
		}
	}
	//public void btnAdd_Click(View view){
	//Intent intent = new Intent(Lista_Activity.this,Ficha_Activity.class);
	//startActivityForResult(intent,0);
	//}

	public void bntFiltrar_Click (View view){
		try {


			String Filtro =etFiltro.getText().toString();

			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
			JBD db = new JBD(this,getString(R.string.app_preferences),info.versionCode);
			String condicion="and fecha like '%"+Filtro+"%'";
			//String condicion=String.format("and producto='%s' order by unidades Asc",filtro);
			mItems = db.getRutas(condicion);
			mDatosAdapter = new MiArrayAdapter(this); 
			mLista.setAdapter(mDatosAdapter);
		} catch (Exception e) {
			Log.e(JUtils.TAG,"btnFiltrar_Click:"+e.getMessage());
		}
	}

	@Override
	protected void onResume() {
		try {
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
			JBD db = new JBD(this,getString(R.string.app_preferences),info.versionCode);
			String condicion="";
			//String condicion=String.format("and producto='%s' order by unidades Asc",filtro);
			mItems = db.getRutas(condicion);
			
			mDatosAdapter = new MiArrayAdapter(this); 
			mLista.setAdapter(mDatosAdapter);
		} catch (Exception e) {
			Log.e(JUtils.TAG,"LA:onResume:"+e.getMessage());
		}
		super.onResume();
	}

	private class MiArrayAdapter extends ArrayAdapter<JRuta>{

		public MiArrayAdapter(Context context) {
			super(context, R.layout.rutas_row_layout,mItems);
		}
		//Sobreescribimos la función getView, por la cual pasará cada vez que tenga que "pintar" una fila del ListView
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			View rowView=null;
			try {
				//"Inflamos" una fila, indicando el layout a utilizar:
				LayoutInflater inflater = Rutas_Activity.this.getLayoutInflater();
				rowView= inflater.inflate(R.layout.rutas_row_layout, null);
				
				Calendar c = Calendar.getInstance();

				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				formattedDate = df.format(c.getTime());
				    
				    int daysBetween = Days.daysBetween(new DateTime(mItems.get(position).getRuta_fecha()), new DateTime(formattedDate)).getDays();
				    String dia ="";
				if(daysBetween==0){
					dia ="HOY";
				} 
				if(daysBetween==1){
					dia ="AYER";
				}    
				if(daysBetween>1){
					dia ="Hace "+daysBetween+" días";
				}    


				//Rellenamos la fila con la información (TextView con la ciudad e ImageView con la imagen)
				TextView txtTitle = (TextView) rowView.findViewById(R.id.tvTitle);
				TextView tvPlazas =(TextView) rowView.findViewById(R.id.tvPlazas);
				TextView tvMaquinas =(TextView) rowView.findViewById(R.id.tvMaquinas);
				TextView tvProductos =(TextView) rowView.findViewById(R.id.tvProductos);
				txtTitle.setText(dia);
				PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
				mBD = new JBD(Rutas_Activity.this,getString(R.string.app_preferences),info.versionCode);
								
				int numplazas=mBD.cuentaRutaPlazas(mItems.get(position).getRuta_fecha());
				int numlineas=mBD.cuentaRutaLineas(mItems.get(position).getRuta_fecha());
				int numproductos=mBD.cuentaRutaProductos(mItems.get(position).getRuta_fecha());
				tvPlazas.setText(""+numplazas+" plazas visitadas");
				tvMaquinas.setText(""+numlineas+" posiciones atendidas");
				tvProductos.setText(""+numproductos+" unidades cargadas");
			} catch (Exception e) {
				Log.e(JUtils.TAG,"getView:"+e.getMessage());
			}

			return rowView;
		}
	}
}
