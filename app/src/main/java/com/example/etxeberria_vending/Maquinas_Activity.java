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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Maquinas_Activity extends Activity {

	private MiArrayAdapter mDatosAdapter;
	private ArrayList<JMaquina> mItems;
	private ArrayList<JPlaza>mPlazas;
	private ListView mLista;
	private EditText etFiltro;
	private int id_plaza;
	private String nombrePlaza;
	private JPlaza mJPlaza;
	private String formattedDate;
	private JBD mBD;
	
	//variables para recuperar la posicion del listview
		private int index;
		private int top;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maquinas_layout);
		try {
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
			mBD = new JBD(Maquinas_Activity.this,getString(R.string.app_preferences),info.versionCode);

			//Para evitar que se muestre automaticamente el teclado al iniciar la activity
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
			//Asociamos la propiedad al control:
			mLista = (ListView) findViewById(R.id.lvLista);

			id_plaza = 0;
			nombrePlaza=null;
			try {
				Bundle datos = getIntent().getExtras();
				id_plaza = datos.getInt("id_plaza",0);
				nombrePlaza= datos.getString("nombrePlaza",null);
			} catch (Exception e) {}
			//Control de evento click sobre una fila:
			mLista.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
					//guardamos la posicion del listview
					index = mLista.getFirstVisiblePosition();
					View v = mLista.getChildAt(0);
					//Obtenemos el id del item seleccionado y vamos a editar:
					int id = mItems.get(position).getID();
					String nombre_maquina = mItems.get(position).getMaq_nombre();
					Intent intent = new Intent(Maquinas_Activity.this,Posicion_Activity.class);
					intent.putExtra("id_maquina", id);
					intent.putExtra("nombrePlaza", nombrePlaza);
					intent.putExtra("nombre_maquina", nombre_maquina);
					
					startActivityForResult(intent,0);
				}
			});
			
			
			mLista.setOnItemLongClickListener(new OnItemLongClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
					//Obtenemos el id del item seleccionado y vamos a editar:
					
				}

				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					//guardamos la posicion del listview
					index = mLista.getFirstVisiblePosition();
					View v = mLista.getChildAt(0);
					Intent intent = new Intent(Maquinas_Activity.this,Cargando_Activity.class);
					intent.putExtra("id_maquina", mItems.get(position).getID());
					
					startActivityForResult(intent,0);
					return true;
				}
			
			});
			
			etFiltro = (EditText)findViewById(R.id.etFiltro);
			etFiltro.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					try {
						String Filtro =etFiltro.getText().toString();

						PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
						mBD = new JBD(Maquinas_Activity.this,getString(R.string.app_preferences),info.versionCode);
						String condicion="and maq_nombre like '%"+Filtro+"%'";
						//String condicion=String.format("and producto='%s' order by unidades Asc",filtro);
						mItems = mBD.getMaquinas(condicion);
						mDatosAdapter = new MiArrayAdapter(Maquinas_Activity.this); 
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
			mBD = new JBD(this,getString(R.string.app_preferences),info.versionCode);
			String condicion="and maq_nombre like '%"+Filtro+"%'";
			//String condicion=String.format("and producto='%s' order by unidades Asc",filtro);
			mItems = mBD.getMaquinas(condicion);
			mDatosAdapter = new MiArrayAdapter(this); 
			mLista.setAdapter(mDatosAdapter);
		} catch (Exception e) {
			Log.e(JUtils.TAG,"btnFiltrar_Click:"+e.getMessage());
		}
	}

	@Override
	protected void onResume() {
		String condicion=null;
		try {
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
			mBD = new JBD(this,getString(R.string.app_preferences),info.versionCode);
			
			if (id_plaza>0){
				//Mostramos las maquinas de esta plaza
				condicion = String.format(" and id_plaza='%s'", id_plaza);
				getActionBar().setTitle(nombrePlaza);
				getActionBar().setIcon(R.drawable.icon_maquina);
			} else {
				//Mostramos todas las maquinas
				condicion = "";
				getActionBar().setTitle("mis Máquinas");
				getActionBar().setIcon(R.drawable.icon_maquina);
			}
			//String condicion=String.format("and producto='%s' order by unidades Asc",filtro);
			mItems = mBD.getMaquinas(condicion);
			
			mDatosAdapter = new MiArrayAdapter(this); 
			mLista.setAdapter(mDatosAdapter);
			// restore index and position
			mLista.setSelectionFromTop(index, top);
		} catch (Exception e) {
			Log.e(JUtils.TAG,"LA:onResume:"+e.getMessage());
		}
		super.onResume();
	}

	private class MiArrayAdapter extends ArrayAdapter<JMaquina>{

		public MiArrayAdapter(Context context) {
			super(context, R.layout.maquina_row_layout,mItems);
		}
		//Sobreescribimos la función getView, por la cual pasará cada vez que tenga que "pintar" una fila del ListView
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			//"Inflamos" una fila, indicando el layout a utilizar:
			LayoutInflater inflater = Maquinas_Activity.this.getLayoutInflater();
			View rowView= inflater.inflate(R.layout.maquina_row_layout, null);

			Calendar c = Calendar.getInstance();

			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			formattedDate = df.format(c.getTime());
			    
			    int daysBetween = Days.daysBetween(new DateTime(mItems.get(position).getFecha()), new DateTime(formattedDate)).getDays();
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
			TextView txtObserv = (TextView) rowView.findViewById(R.id.tvDescripcion);
			TextView tvPlaza = (TextView) rowView.findViewById(R.id.tvPlaza);
			ImageView ivMaquina =(ImageView)rowView.findViewById(R.id.ivMaquina);
			ImageView ivSemaforo =(ImageView)rowView.findViewById(R.id.ivSemaforo);
			TextView tvFecha = (TextView) rowView.findViewById(R.id.tvFecha);
			id_plaza=mItems.get(position).getPlaza();
			mJPlaza=mBD.getPlaza(id_plaza);
			nombrePlaza=mJPlaza.getPlaza_empresa();
			String descripcion = (mItems.get(position).getMaq_descripcion().toString());
			txtTitle.setText(mItems.get(position).getMaq_nombre()+"-"+mItems.get(position).getID());
			tvPlaza.setText(nombrePlaza);
			tvFecha.setText("Última carga: "+dia);
			String maq_tipo =mItems.get(position).getMaq_tipo();
			
			if(descripcion.equals("activa")){
				ivSemaforo.setImageResource(R.drawable.verde);
			}
			if(descripcion.equals("averiada")){
				ivSemaforo.setImageResource(R.drawable.rojo);
			}
			if (maq_tipo.toUpperCase().equals("CAFE")){
				ivMaquina.setImageResource(R.drawable.icon_maquina_cafe);
			} 
			if (maq_tipo.toUpperCase().equals("LATAS")){
				ivMaquina.setImageResource(R.drawable.icon_maquina_lata);
			} 
			if (maq_tipo.toUpperCase().equals("SNACK")){
				ivMaquina.setImageResource(R.drawable.icon_maquina_snack);
			} 
			
			//txtUnidades.setText("stock:"+String.valueOf(mItems.get(position).getUnidades()));
		
			
			return rowView;
		}
	}
}