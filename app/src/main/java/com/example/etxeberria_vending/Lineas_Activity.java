package com.example.etxeberria_vending;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.joda.time.DateTime;
import org.joda.time.Days;

import com.example.etxeberria_vending.clases.JBD;
import com.example.etxeberria_vending.clases.JLinea;
import com.example.etxeberria_vending.clases.JMaquina;
import com.example.etxeberria_vending.clases.JPlaza;
import com.example.etxeberria_vending.clases.JPosicion;
import com.example.etxeberria_vending.clases.JProducto;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Lineas_Activity extends Activity {
	
	private MiArrayAdapter mDatosAdapter;
	private ArrayList<JLinea> mLineas;
	private ArrayList<JPosicion>mPosiciones;
	private ArrayList<JProducto>mProductos;
	private ArrayList<JPlaza>mPlazas;
	private ArrayList<JMaquina>mMaquinas;
	private JProducto mProducto;
	private JPosicion mPosicion;
	private JMaquina mMaquina;
	private JPlaza mPlaza;
	private ListView mLista;
	private ImageView ivProducto;
	private JBD mBD;
	private int plaza_id;
	private String ruta_fecha;
	private String condicion;
	private String formattedDate;
	private int id_producto;
	private String fecha1;
	private String fecha2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lineas_layout);
		plaza_id=0;
		ruta_fecha="";
		id_producto=0;
		fecha1="";
		fecha2="";
		getActionBar().setTitle("Mis cargas");
		try {
			
			Bundle datos = getIntent().getExtras();
			plaza_id = datos.getInt("plaza_id",0);
			ruta_fecha = datos.getString("ruta_fecha","");
		} catch (Exception e) {
			Log.e(JUtils.TAG,"LA:onCreate:"+e.getMessage());
		}
		
try {
			
			Bundle datos = getIntent().getExtras();
			id_producto = datos.getInt("id_producto",0);
			fecha1=datos.getString("fecha1",null);
			fecha2=datos.getString("fecha2",null);
			
		} catch (Exception e) {
			Log.e(JUtils.TAG,"LA:onCreate:"+e.getMessage());
		}
		
try {
			
			Bundle datos = getIntent().getExtras();
			
			ruta_fecha = datos.getString("ruta_fecha","");
		} catch (Exception e) {
			Log.e(JUtils.TAG,"LA:onCreate:"+e.getMessage());
		}
			//Para evitar que se muestre automaticamente el teclado al iniciar la activity
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
				mLista = (ListView) findViewById(R.id.lvLista);
		try {
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
			mBD = new JBD(this,getString(R.string.app_preferences),info.versionCode);
		} catch (Exception e) {
			Log.e(JUtils.TAG,"LA:onCreate:"+e.getMessage());
		}
			
			mProductos=mBD.getProductos("");
			mPosiciones=mBD.getPosicions("");
			mMaquinas=mBD.getMaquinas("");
			mPlazas=mBD.getPlazas("");
			condicion="";
			if(plaza_id>0){
				condicion = String.format(" and id_plaza='%s' and fecha='%s'", plaza_id, ruta_fecha);
			}
			else{
				if(ruta_fecha.length()>0){
					condicion = String.format(" and fecha='%s'", ruta_fecha);
				}
				else{ 
					if(id_producto>0){
						if(fecha1.length()>0){
							condicion = String.format(" and id_producto='%s' and fecha>='%s' and fecha<='%s'", id_producto, fecha1, fecha2);
						}else{
							condicion = String.format(" and id_producto='%s'", id_producto);
						}
						
					}else{
						condicion="";	
					}
					
						
				}
				
			}
			mLineas = mBD.getLineas(condicion);
			mDatosAdapter = new MiArrayAdapter(this); 
			mLista.setAdapter(mDatosAdapter);
		
		
			
	}

@Override
protected void onResume() {

	super.onResume();
}

private class MiArrayAdapter extends ArrayAdapter<JLinea>{

	public MiArrayAdapter(Context context) {
		super(context, R.layout.lineas_row_layout,mLineas);
	}
	//Sobreescribimos la función getView, por la cual pasará cada vez que tenga que "pintar" una fila del ListView
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		//"Inflamos" una fila, indicando el layout a utilizar:
		LayoutInflater inflater = Lineas_Activity.this.getLayoutInflater();
		View rowView= inflater.inflate(R.layout.lineas_row_layout, null);
		
		Calendar c = Calendar.getInstance();

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		formattedDate = df.format(c.getTime());
		    
		    int daysBetween = Days.daysBetween(new DateTime(mLineas.get(position).getFecha()), new DateTime(formattedDate)).getDays();
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
		TextView tvPlaza = (TextView) rowView.findViewById(R.id.tvPlaza);
		TextView tvFecha = (TextView) rowView.findViewById(R.id.tvFecha);
		TextView tvMaquina = (TextView) rowView.findViewById(R.id.tvMaquina);
		TextView tvImporte = (TextView) rowView.findViewById(R.id.tvImporte);
		TextView tvProdcuto = (TextView) rowView.findViewById(R.id.tvProdcuto);
		TextView tvUnidades = (TextView) rowView.findViewById(R.id.tvUnidades);
		ivProducto = (ImageView) rowView.findViewById(R.id.ivProducto);
		
		int id_producto = mLineas.get(position).getProducto_id();
		int id_posicion = mLineas.get(position).getPosicion_id();
		int unidades=mLineas.get(position).getUnidades();//1
		String fecha=mLineas.get(position).getFecha();//2
		mProducto = mBD.getProducto(id_producto);
		mPosicion =mBD.getPosicion(id_posicion);
		int centimos=mPosicion.getPr_venta();
		float euros=centimos/100f;
		int id_maquina = mPosicion.getMaquina_id();
		mMaquina =mBD.getMaquina(id_maquina);
		String maquina_nombre=mMaquina.getMaq_nombre();//3
		int id_plaza=mMaquina.getPlaza();
		String nombre_producto =mProducto.getProd_nombre();//4
		mPlaza=mBD.getPlaza(id_plaza);
		String plaza_nombre=mPlaza.getPlaza_empresa();//5
		String redondear = String.format("%.2f", euros);//6
		//le doy 25 caracteres de maximo para que no solape el texto de la derecha
		String plaza = String.format("%3.25s", plaza_nombre);
		tvPlaza.setText(plaza);
		tvFecha.setText(dia);
		tvMaquina.setText(maquina_nombre+"-"+id_maquina);
		tvImporte.setText(redondear+" €");
		if(centimos==0){
			tvImporte.setVisibility(view.INVISIBLE);
		}
		tvProdcuto.setText(nombre_producto);
		tvUnidades.setText(""+unidades+" unidades");
		
		int resId=getResources().getIdentifier(String.format("i%s",mProducto.getID()),"drawable",getPackageName());
		if (resId==0) resId = R.drawable.etxebendin;
		ivProducto.setImageResource(resId);
		//txtUnidades.setText("stock:"+String.valueOf(mItems.get(position).getUnidades()));
		//tvPrcoste.setText("precio de coste: "+String.valueOf(mItems.get(position).getPr_coste()));
		//tvPrventa.setText("precio de venta: "+String.valueOf(mItems.get(position).getPr_venta()));
		//float margen = mItems.get(position).getPr_venta()-mItems.get(position).getPr_coste();
		//String redondear = String.format("%.2f", margen);
		//tvMargen.setText("el margen es "+String.valueOf(margen));
		//tvMargen.setText("el margen es "+ redondear);
		return rowView;
	}
	
}
}
