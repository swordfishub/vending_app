package com.example.etxeberria_vending;

import java.util.ArrayList;

import com.example.etxeberria_vending.clases.JBD;
import com.example.etxeberria_vending.clases.JCarga;
import com.example.etxeberria_vending.clases.JMaquina;
import com.example.etxeberria_vending.clases.JPlaza;
import com.example.etxeberria_vending.clases.JRuta;
import com.example.etxeberria_vending.clases.JUtils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;





public class Cargas_Activity extends Activity {
	
	private MiArrayAdapter mDatosAdapter;
	private ArrayList<JMaquina> mMaquinas;
	private ArrayList<JPlaza> mPlazas;
	private ArrayList<JCarga>mCargas;
	private ListView mLista;
	private EditText etFiltro;
	private int id_ruta;
	private int id_ruta_dia;
	private String ruta_fecha;
	private JRuta mJRutaSeleccionada;
	private JBD mBD;
	private String plazanombre;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cargas_layout);
		try {
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
			mBD = new JBD(Cargas_Activity.this,getString(R.string.app_preferences),info.versionCode);
			//Cargarmos las:
			mPlazas = mBD.getPlazas("");
			mMaquinas = mBD.getMaquinas("");
			mCargas= mBD.getCargas("");
			//Para evitar que se muestre automaticamente el teclado al iniciar la activity
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
			//Asociamos la propiedad al control:
			mLista = (ListView) findViewById(R.id.lvLista);

			id_ruta_dia = 0;
			
				Bundle datos = getIntent().getExtras();
				id_ruta_dia = datos.getInt("id_ruta_dia",0);
				ruta_fecha=datos.getString("ruta_fecha");
				plazanombre=datos.getString("plazanombre");
				getActionBar().setTitle("ruta "+ruta_fecha);
				getActionBar().setIcon(R.drawable.icon_ruta);
			

			
		} catch (Exception e) {
			Log.e(JUtils.TAG,"LA:onCreate:Bundle:"+e.getMessage());
		}
	}


	@Override
	protected void onResume() {
		String condicion=null;
		try {
			if (id_ruta_dia>0){
				//Mostramos solo de ésta
				condicion = String.format(" and id_ruta_dia='%s'", id_ruta_dia);
			} else {
				//Mostramos todas
				condicion = "";
			}
			//String condicion=String.format("and producto='%s' order by unidades Asc",filtro);
			mCargas = mBD.getCargas(condicion);
			mDatosAdapter = new MiArrayAdapter(this); 
			mLista.setAdapter(mDatosAdapter);
		} catch (Exception e) {
			Log.e(JUtils.TAG,"LA:onResume:"+e.getMessage());
		}
		super.onResume();
	}

	private class MiArrayAdapter extends ArrayAdapter<JCarga>{

		public MiArrayAdapter(Context context) {
			super(context, R.layout.cargas_row_layout,mCargas);
		}
		//Sobreescribimos la función getView, por la cual pasará cada vez que tenga que "pintar" una fila del ListView
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			View rowView=null;
			try {
				//"Inflamos" una fila, indicando el layout a utilizar:
				LayoutInflater inflater = Cargas_Activity.this.getLayoutInflater();
				rowView= inflater.inflate(R.layout.cargas_row_layout, null);

				//Rellenamos la fila con la información
				TextView tvMaquina = (TextView) rowView.findViewById(R.id.tvMaquina);
				TextView tvFecha = (TextView) rowView.findViewById(R.id.tvFecha);
				TextView tvProductos = (TextView) rowView.findViewById(R.id.tvProductos);
				TextView tvUnidades = (TextView) rowView.findViewById(R.id.tvUnidades);
				int id_maquina = (mCargas.get(position).getMaquina_id());
				String nombremaquina = "";
				String fecha="";
				//Busco en el arraylist mConceptos, la descripción del código que tiene este movimiento
				for (int i=0;i<mMaquinas.size();i++){
					if (mMaquinas.get(i).getID()==id_maquina){
						nombremaquina= mMaquinas.get(i).getMaq_nombre();
						break;
					}
				}


				//txtUnidades.setText("stock:"+String.valueOf(mItems.get(position).getUnidades()));
				//tvPrecio.setText(String.valueOf(mItems.get(position).getPr_venta()));
				tvMaquina.setText(nombremaquina);
				tvFecha.setText(mCargas.get(position).getFecha());

			} catch (Exception e) {
				Log.e(JUtils.TAG,"getView:"+e.getMessage());
			}
			return rowView;
		}
	}
}