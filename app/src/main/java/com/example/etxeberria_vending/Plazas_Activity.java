package com.example.etxeberria_vending;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Days;

import com.example.etxeberria_vending.clases.JBD;
import com.example.etxeberria_vending.clases.JPlaza;
import com.example.etxeberria_vending.clases.JRuta;
import com.example.etxeberria_vending.clases.JRuta_dia;
import com.example.etxeberria_vending.clases.JUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Color;
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

public class Plazas_Activity extends Activity {

	private MiArrayAdapter mDatosAdapter;
	private ArrayList<JPlaza> mItems;
	private ArrayList<JRuta_dia> mRuta;
	private ListView mLista;
	private EditText etFiltro;
	private int id_ruta;
	private JRuta mJRutaSeleccionada;
	private JBD mBD;
	private String formattedDate;
	//variables para recuperar la posicion del listview
		private int index;
		private int top;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.plazas_layout);
		try {
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
			mBD = new JBD(Plazas_Activity.this,getString(R.string.app_preferences),info.versionCode);

			//Para evitar que se muestre automaticamente el teclado al iniciar la activity
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
			//Asociamos la propiedad al control:
			mLista = (ListView) findViewById(R.id.lvLista);

			id_ruta = 0;
			try {
				Bundle datos = getIntent().getExtras();
				id_ruta = datos.getInt("id_ruta",0);
			} catch (Exception e) {}
			//Control de evento click sobre una fila:
			getActionBar().setTitle("Mis Plazas");
			getActionBar().setIcon(R.drawable.icon_plaza);
			mLista.setOnItemLongClickListener(new OnItemLongClickListener() {
				public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
					//Obtenemos el id del item seleccionado y vamos a editar:
					
				}

				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
					//guardamos la posicion del listview
					index = mLista.getFirstVisiblePosition();
					View v = mLista.getChildAt(0);
					Intent intent = new Intent(Plazas_Activity.this,MapsActivity.class);
					intent.putExtra("nombrePlaza", mItems.get(position).getPlaza_empresa());
					intent.putExtra("latitud", mItems.get(position).getPlaza_latitud());
					intent.putExtra("longitud", mItems.get(position).getPlaza_longitud());
					startActivityForResult(intent,0);
					return true;
				}
			
			});
			
			mLista.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
					//guardamos la posicion del listview
					index = mLista.getFirstVisiblePosition();
					View v = mLista.getChildAt(0);
					//Obtenemos el id del item seleccionado y vamos a editar:
					int id = mItems.get(position).getID();
					String nombrePlaza = mItems.get(position).getPlaza_empresa();
					Intent intent = new Intent(Plazas_Activity.this,Maquinas_Activity.class);
					intent.putExtra("id_plaza", id);
					intent.putExtra("nombrePlaza", nombrePlaza);
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
						JBD db = new JBD(Plazas_Activity.this,getString(R.string.app_preferences),info.versionCode);
						String condicion="and plaza_empresa like '%"+Filtro+"%'";
						//String condicion=String.format("and producto='%s' order by unidades Asc",filtro);
						mItems = db.getPlazas(condicion);
						mDatosAdapter = new MiArrayAdapter(Plazas_Activity.this); 
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
			String condicion="and plaza_empresa like '%"+Filtro+"%'";
			//String condicion=String.format("and producto='%s' order by unidades Asc",filtro);
			mItems = db.getPlazas(condicion);
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
			JBD db = new JBD(this,getString(R.string.app_preferences),info.versionCode);
			
			if (id_ruta>0){
				//Mostramos las plazas solo de ésta ruta
				condicion = String.format(" and id_ruta='%s'", id_ruta);
			} else {
				//Mostramos todas las plazas
				condicion = "";
			}
			//String condicion=String.format("and producto='%s' order by unidades Asc",filtro);
			mItems = db.getPlazas(condicion);
			mRuta =db.getRutas_dia("");
			mDatosAdapter = new MiArrayAdapter(this); 
			mLista.setAdapter(mDatosAdapter);
			// restore index and position
			mLista.setSelectionFromTop(index, top);
		} catch (Exception e) {
			Log.e(JUtils.TAG,"LA:onResume:"+e.getMessage());
		}
		super.onResume();
	}

	private class MiArrayAdapter extends ArrayAdapter<JPlaza>{

		public MiArrayAdapter(Context context) {
			super(context, R.layout.plazas_row_layout,mItems);
		}
		//Sobreescribimos la función getView, por la cual pasará cada vez que tenga que "pintar" una fila del ListView
		@Override
		public View getView(int position, View view, ViewGroup parent) {
			
			
			
			//"Inflamos" una fila, indicando el layout a utilizar:
			LayoutInflater inflater = Plazas_Activity.this.getLayoutInflater();
			View rowView= inflater.inflate(R.layout.plazas_row_layout, null);

			//Rellenamos la fila con la información (TextView con la ciudad e ImageView con la imagen)
			TextView txtTitle = (TextView) rowView.findViewById(R.id.tvTitle);
			TextView txtObserv = (TextView) rowView.findViewById(R.id.tvDescripcion);
			//ImageView ivMapicon =(ImageView)rowView.findViewById(R.id.ivMapicon);
			TextView tvFecha = (TextView) rowView.findViewById(R.id.tvFecha);
			//TextView tvPrcoste = (TextView) rowView.findViewById(R.id.tvPrcoste);
			//TextView tvPrventa = (TextView) rowView.findViewById(R.id.tvPrventa);
			//TextView tvMargen = (TextView) rowView.findViewById(R.id.tvMargen);
			txtTitle.setText(mItems.get(position).getPlaza_empresa());
			int id_plaza= mItems.get(position).getID();
			String fecha_registrada=mItems.get(position).getFecha();
			//String fecha_registrada=mItems.get(position).getFecha();
			
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

			
			tvFecha.setText(dia);
			if(daysBetween<=3){
				tvFecha.setTextColor(Color.parseColor("#17A803"));
			}
			if(daysBetween>3){
				tvFecha.setTextColor(Color.parseColor("#FA0C07"));
			}
			if(fecha_registrada.equals("2016-01-01")){
				tvFecha.setVisibility(View.INVISIBLE);
			}
			//txtUnidades.setText("stock:"+String.valueOf(mItems.get(position).getUnidades()));
			//txtObserv.setText(mItems.get(position).getProd_tipo());
			//txtUnidades.setText(mItems.get(position).getProd_descripcion());
			//tvPrcoste.setText("precio de coste: "+String.valueOf(mItems.get(position).getPr_coste()));
			//tvPrventa.setText("precio de venta: "+String.valueOf(mItems.get(position).getPr_venta()));
			//float margen = mItems.get(position).getPr_venta()-mItems.get(position).getPr_coste();
			//String redondear = String.format("%.2f", margen);
			//tvMargen.setText("el margen es "+String.valueOf(margen));
			//tvMargen.setText("el margen es "+ redondear);
			return rowView;
		}
	}
	/**
	 * Calcula la diferencia entre dos fechas. Devuelve el resultado en días, meses o años según sea el valor del parámetro 'tipo'
	 * @param fechaInicio Fecha inicial
	 * @param fechaFin Fecha final
	 * @param tipo 0=TotalAños; 1=TotalMeses; 2=TotalDías; 3=MesesDelAnio; 4=DiasDelMes
	 * @return numero de días, meses o años de diferencia
	 */
	public long getDiffDates(Date fechaInicio, Date fechaFin, int tipo) {
		// Fecha inicio
		Calendar calendarInicio = Calendar.getInstance();
		calendarInicio.setTime(fechaInicio);
		int diaInicio = calendarInicio.get(Calendar.DAY_OF_MONTH);
		int mesInicio = calendarInicio.get(Calendar.MONTH) + 1; // 0 Enero, 11 Diciembre
		int anioInicio = calendarInicio.get(Calendar.YEAR);
	 
		// Fecha fin
		Calendar calendarFin = Calendar.getInstance();
		calendarFin.setTime(fechaFin);
		int diaFin = calendarFin.get(Calendar.DAY_OF_MONTH);
		int mesFin = calendarFin.get(Calendar.MONTH) + 1; // 0 Enero, 11 Diciembre
		int anioFin = calendarFin.get(Calendar.YEAR);
	 
		int anios = 0;
		int mesesPorAnio = 0;
		int diasPorMes = 0;
		int diasTipoMes = 0;
	 
		//
		// Calculo de días del mes
		//
		if (mesInicio == 2) {
			// Febrero
			if ((anioFin % 4 == 0) && ((anioFin % 100 != 0) || (anioFin % 400 == 0))) {
				// Bisiesto
				diasTipoMes = 29;
			} else {
				// No bisiesto
				diasTipoMes = 28;
			}
		} else if (mesInicio <= 7) {
			// De Enero a Julio los meses pares tienen 30 y los impares 31
			if (mesInicio % 2 == 0) {
				diasTipoMes = 30;
			} else {
				diasTipoMes = 31;
			}
		} else if (mesInicio > 7) {
			// De Julio a Diciembre los meses pares tienen 31 y los impares 30
			if (mesInicio % 2 == 0) {
				diasTipoMes = 31;
			} else {
				diasTipoMes = 30;
			}
		}
	 
	 
		//
		// Calculo de diferencia de año, mes y dia
		//
		if ((anioInicio > anioFin) || (anioInicio == anioFin && mesInicio > mesFin)
				|| (anioInicio == anioFin && mesInicio == mesFin && diaInicio > diaFin)) {
			// La fecha de inicio es posterior a la fecha fin
			// System.out.println("La fecha de inicio ha de ser anterior a la fecha fin");
			return -1;
		} else {
			if (mesInicio <= mesFin) {
				anios = anioFin - anioInicio;
				if (diaInicio <= diaFin) {
					mesesPorAnio = mesFin - mesInicio;
					diasPorMes = diaFin - diaInicio;
				} else {
					if (mesFin == mesInicio) {
						anios = anios - 1;
					}
					mesesPorAnio = (mesFin - mesInicio - 1 + 12) % 12;
					diasPorMes = diasTipoMes - (diaInicio - diaFin);
				}
			} else {
				anios = anioFin - anioInicio - 1;
				System.out.println(anios);
				if (diaInicio > diaFin) {
					mesesPorAnio = mesFin - mesInicio - 1 + 12;
					diasPorMes = diasTipoMes - (diaInicio - diaFin);
				} else {
					mesesPorAnio = mesFin - mesInicio + 12;
					diasPorMes = diaFin - diaInicio;
				}
			}
		}
		//System.out.println("Han transcurrido " + anios + " Años, " + mesesPorAnio + " Meses y " + diasPorMes + " Días.");		
	 
		//
		// Totales
		//
		long returnValue = -1;
	 
		switch (tipo) {
			case 0:
				// Total Años
				returnValue = anios;
				// System.out.println("Total años: " + returnValue + " Años.");
				break;
	 
			case 1:
				// Total Meses
				returnValue = anios * 12 + mesesPorAnio;
				// System.out.println("Total meses: " + returnValue + " Meses.");
				break;
	 
			case 2:
				// Total Dias (se calcula a partir de los milisegundos por día)
				long millsecsPerDay = 86400000; // Milisegundos al día
				returnValue = (fechaFin.getTime() - fechaInicio.getTime()) / millsecsPerDay;
				// System.out.println("Total días: " + returnValue + " Días.");
				break;
	 
			case 3:
				// Meses del año
				returnValue = mesesPorAnio;
				// System.out.println("Meses del año: " + returnValue);
				break;
	 
			case 4:
				// Dias del mes
				returnValue = diasPorMes;
				// System.out.println("Dias del mes: " + returnValue);
				break;
	 
			default:
				break;
		}
	 
		return returnValue;
	}
}
