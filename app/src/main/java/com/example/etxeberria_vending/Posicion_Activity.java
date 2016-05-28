package com.example.etxeberria_vending;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.joda.time.DateTime;
import org.joda.time.Days;

import com.example.etxeberria_vending.clases.JBD;
import com.example.etxeberria_vending.clases.JMaquina;
import com.example.etxeberria_vending.clases.JPlaza;
import com.example.etxeberria_vending.clases.JPosicion;
import com.example.etxeberria_vending.clases.JProducto;
import com.example.etxeberria_vending.clases.JUtils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Posicion_Activity extends Activity {

	private MiArrayAdapter mDatosAdapter;
	private ArrayList<JPosicion> mItems;
	private ArrayList<JMaquina>mMaquinas;
	private JMaquina mMaquina;
	private ListView mLista;
	private EditText etFiltro;
	private int id_maquina;
	private JProducto mJProductoseleccionado;
	private ArrayList<JProducto> mProductos;
	private JBD mBD;
	private String formattedDate;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.posicion_layout);
		try {
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
			mBD = new JBD(Posicion_Activity.this,getString(R.string.app_preferences),info.versionCode);
			//Cargarmos la lista de productos:
			mProductos = mBD.getProductos("");
			mMaquinas =mBD.getMaquinas("");
			//Para evitar que se muestre automaticamente el teclado al iniciar la activity
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
			//Asociamos la propiedad al control:
			mLista = (ListView) findViewById(R.id.lvLista);

			id_maquina = 0;
			try {
				Bundle datos = getIntent().getExtras();
				id_maquina = datos.getInt("id_maquina",0);
				
				
			
		} catch (Exception e) {}
				
				mMaquina = mBD.getMaquina(id_maquina);
				String nombre_maquina = mMaquina.getMaq_nombre();
				int id_plaza = mMaquina.getPlaza();
				JPlaza mPlaza=mBD.getPlaza(id_plaza);
				
				String nombrePlaza= mPlaza.getPlaza_empresa();
				if (id_maquina>0){
					getActionBar().setTitle(nombre_maquina+" "+nombrePlaza);
					getActionBar().setIcon(R.drawable.icon_maquina);
				}

			etFiltro = (EditText)findViewById(R.id.etFiltro);
			etFiltro.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					try {
						String Filtro =etFiltro.getText().toString();

						PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
						JBD db = new JBD(Posicion_Activity.this,getString(R.string.app_preferences),info.versionCode);
						String condicion="and prod_nombre like '%"+Filtro+"%'";
						//String condicion=String.format("and producto='%s' order by unidades Asc",filtro);
						mItems = db.getPosicions(condicion);
						mDatosAdapter = new MiArrayAdapter(Posicion_Activity.this); 
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


	@Override
	protected void onResume() {
		String condicion=null;
		try {
			if (id_maquina>0){
				//Mostramos solo de ésta
				condicion = String.format(" and id_maquina='%s'", id_maquina);
			} else {
				//Mostramos todas
				condicion = "";
			}
			//String condicion=String.format("and producto='%s' order by unidades Asc",filtro);
			mItems = mBD.getPosicions(condicion);
			mDatosAdapter = new MiArrayAdapter(this); 
			mLista.setAdapter(mDatosAdapter);
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
			LayoutInflater inflater = Posicion_Activity.this.getLayoutInflater();
			rowView= inflater.inflate(R.layout.cargando_row_layout, null);
			
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
			int producto_id = (mItems.get(position).getProducto_id());
			int pr_venta = (mItems.get(position).getPr_venta());
			String fecha_ultima = (mItems.get(position).getFecha());
			int ultimas = (mItems.get(position).getUltimas());
			String nombre_producto="";
			for (int i=0;i<mProductos.size();i++){
				if (mProductos.get(i).getID()==producto_id){
					nombre_producto= mProductos.get(i).getProd_nombre();
					break;
				}
			}
			
			
			tvUnidades.setText(""+ultimas);	
			tvCarga.setText("Carga:");
			tvFecha.setText("Última carga: "+dia);
			tvTitle.setText(nombre_producto);
			int resId=getResources().getIdentifier(String.format("i%s",mItems.get(position).getProducto_id()),"drawable",getPackageName());
			if (resId==0) resId = R.drawable.loguito;
			ivProducto.setImageResource(resId);
			float euros = pr_venta/100f;
			if(euros>0){
			//String precio = String.valueOf(euros);
			String redondear = String.format("%.2f", euros);
			tvPrecio.setText("precio: "+redondear+" €");
			}
			//txtUnidades.setText("stock:"+String.valueOf(mItems.get(position).getUnidades()));
			//tvPrcoste.setText("precio de coste: "+String.valueOf(mItems.get(position).getPr_coste()));
			
			//tvMargen.setText("el margen es "+String.valueOf(margen));
			//tvMargen.setText("el margen es "+ redondear);


			return rowView;
		}
	}
}