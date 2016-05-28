package com.example.etxeberria_vending;

import java.util.ArrayList;

import com.example.etxeberria_vending.clases.JBD;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class Ruta_dia_Activity extends Activity {
	private MiArrayAdapter mDatosAdapter;
	private ArrayList<JRuta_dia> mRuta_dia;
	private ArrayList<JPlaza> mPlazas;
	private ListView mLista;
	private EditText etFiltro;
	private int id_ruta;
	private int plaza_id;
	private int id_ruta_dia;
	private String ruta_fecha;
	private JPlaza mJPlazaSeleccionada;
	private JBD mBD;
	private String plazanombre;
	
	private Button btnVerLineas;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ruta_dia_layout);
		try {
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
			mBD = new JBD(Ruta_dia_Activity.this,getString(R.string.app_preferences),info.versionCode);
			//Cargarmos la lista de plazas:
			mPlazas = mBD.getPlazas("");
			//mRuta_dia=mBD.getRutas_dia("");
			//Para evitar que se muestre automaticamente el teclado al iniciar la activity
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
			//Asociamos la propiedad al control:
			mLista = (ListView) findViewById(R.id.lvLista);
			
			btnVerLineas =(Button)findViewById(R.id.btnVerLineas);

			id_ruta = 0;
			try {
				Bundle datos = getIntent().getExtras();
				id_ruta = datos.getInt("id_ruta",0);
				ruta_fecha=datos.getString("ruta_fecha");
				getActionBar().setTitle("ruta "+ruta_fecha);
				getActionBar().setIcon(R.drawable.icon_ruta);
			} catch (Exception e) {}

			
			
		} catch (Exception e) {
			//Log.e(JUtils.TAG,"LA:onCreate:Bundle:"+e.getMessage());
		}
		//Control de evento click sobre una fila:
		mLista.setOnItemLongClickListener(new OnItemLongClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
				//Obtenemos el id del item seleccionado y vamos a editar:
				
			}

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(Ruta_dia_Activity.this,Lineas_Activity.class);
				id_ruta_dia = mRuta_dia.get(position).getID();
				plaza_id =mRuta_dia.get(position).getID_plaza();
				mJPlazaSeleccionada=mBD.getPlaza(plaza_id);
				plazanombre=mJPlazaSeleccionada.getPlaza_empresa();
				
				intent.putExtra("id_ruta_dia",id_ruta_dia);
				intent.putExtra("plaza_id", plaza_id);
				intent.putExtra("ruta_fecha", ruta_fecha);
				intent.putExtra("plazanombre", plazanombre);
				
				
				startActivityForResult(intent,0);
				return true;
			}
		
		});
		mLista.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
				//Obtenemos el id del item seleccionado y vamos a editar:
				
				
				id_ruta_dia = mRuta_dia.get(position).getID();
				plaza_id =mRuta_dia.get(position).getID_plaza();
				mJPlazaSeleccionada=mBD.getPlaza(plaza_id);
				plazanombre=mJPlazaSeleccionada.getPlaza_empresa();
				Intent intent = new Intent(Ruta_dia_Activity.this,Maquinas_Activity.class);
				intent.putExtra("id_ruta_dia",id_ruta_dia);
				intent.putExtra("id_plaza", plaza_id);
				intent.putExtra("ruta_fecha", ruta_fecha);
				intent.putExtra("nombrePlaza", plazanombre);
				startActivityForResult(intent,0);
			}
		});
	}


	@Override
	protected void onResume() {
		String condicion=null;
		try {
			if (id_ruta>0){
				//Mostramos solo de ésta
				condicion = String.format(" and id_ruta='%s'", id_ruta);
			} else {
				//Mostramos todas
				condicion = "";
			}
			//String condicion=String.format("and producto='%s' order by unidades Asc",filtro);
			mRuta_dia = mBD.getRutas_dia(condicion);
			mDatosAdapter = new MiArrayAdapter(this); 
			mLista.setAdapter(mDatosAdapter);
		} catch (Exception e) {
			Log.e(JUtils.TAG,"LA:onResume:"+e.getMessage());
		}
		super.onResume();
	}

	private class MiArrayAdapter extends ArrayAdapter<JRuta_dia>{

		public MiArrayAdapter(Context context) {
			super(context, R.layout.ruta_dia_row_layout,mRuta_dia);
		}
		//Sobreescribimos la función getView, por la cual pasará cada vez que tenga que "pintar" una fila del ListView
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			View rowView=null;
			try {
				//"Inflamos" una fila, indicando el layout a utilizar:
				LayoutInflater inflater = Ruta_dia_Activity.this.getLayoutInflater();
				rowView= inflater.inflate(R.layout.ruta_dia_row_layout, null);

				//Rellenamos la fila con la información
				TextView tvTitle = (TextView) rowView.findViewById(R.id.tvTitle);
				TextView tvFecha = (TextView) rowView.findViewById(R.id.tvFecha);


				plaza_id = (mRuta_dia.get(position).getID_plaza());
				//Obtengo el nombre:
				String nombreplaza="";
				String hora="";
				//Busco en el arraylist mPlazas, el nombre de la plaza
				for (int i=0;i<mPlazas.size();i++){
					if (mPlazas.get(i).getID()==plaza_id){
						nombreplaza= mPlazas.get(i).getPlaza_empresa();
						break;
					}
				}


				//txtUnidades.setText("stock:"+String.valueOf(mItems.get(position).getUnidades()));
				//tvPrecio.setText(String.valueOf(mItems.get(position).getPr_venta()));
				tvTitle.setText(nombreplaza);
				tvFecha.setText(mRuta_dia.get(position).getHora());

			} catch (Exception e) {
				Log.e(JUtils.TAG,"getView:"+e.getMessage());
			}
			return rowView;
		}
	}
	public void btnVerLineas_Click(View view){
		Intent intent = new Intent(Ruta_dia_Activity.this,Lineas_Activity.class);
		intent.putExtra("ruta_fecha", ruta_fecha);
		startActivityForResult(intent,0);
	}
}