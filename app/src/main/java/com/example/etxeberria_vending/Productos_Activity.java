package com.example.etxeberria_vending;

import java.util.ArrayList;
import java.util.Calendar;

import com.example.etxeberria_vending.clases.JBD;
import com.example.etxeberria_vending.clases.JProducto;
import com.example.etxeberria_vending.clases.JUtils;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class Productos_Activity extends Activity {

	private MiArrayAdapter mDatosAdapter;
	private ArrayList<JProducto> mItems;
	private ListView mLista;
	private EditText etFiltro;
	private EditText etUltimos;
	private TextView tvFiltro;
	private ImageView ivProducto;
	private JBD mBD;
	private LinearLayout llCalendario;
	private ImageButton btnSettings;
	private ImageButton btnCalendariodesde;
	private ImageButton btnCalendariohasta;
	private Boolean llOculto = true;
	private CheckBox chekBox1;
	private CheckBox chekBox2;
	private TextView tvfecha1;
	private TextView tvFecha2;
	private RadioGroup rgUso;
	private RadioButton rbTodos;
	private RadioButton rbActivos;
	private RadioGroup rgTipo;
	private RadioButton rbAll;
	private RadioButton rbCafe;
	private RadioButton rbLatas;
	private RadioButton rbSnack;
	
	private String ParteTipo;
	private String ParteUso;
	private String parteFecha;
	private String titulo;
	private String tit_fecha;
	private String tit_tipo;
	private String tit_uso;
	
	private String fecha1;
	private String fecha2;
	private int ultimos;
	private String condicion="";
	
	
	//variables para recuperar la posicion del listview
		private int index;
		private int top;
	//controles para la seleccion de la fecha	
		private Calendar cal;
		private int day;
		private int month;
		private int year;
		private DatePickerDialog mDatePickerDialog1;
		private DatePickerDialog mDatePickerDialog2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.productos_layout);
		getActionBar().setTitle("Mis productos");
		tit_fecha="- fechas:todas - ";
		tit_uso="";
		tit_tipo="- tipos:todos -";
		//try {
		
		//Para evitar que se muestre automaticamente el teclado al iniciar la activity
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
			mLista = (ListView) findViewById(R.id.lvLista);
			llCalendario = (LinearLayout)findViewById(R.id.llCalendario);
			chekBox1 =(CheckBox)findViewById(R.id.chekBox1);
			chekBox2 =(CheckBox)findViewById(R.id.checkBox2);
			tvfecha1 =(TextView)findViewById(R.id.tvfecha1);
			tvFecha2 =(TextView)findViewById(R.id.tvFecha2);
			rgUso =(RadioGroup)findViewById(R.id.rgUso);
			rgTipo =(RadioGroup)findViewById(R.id.rgTipo);
			rbTodos=(RadioButton)findViewById(R.id.rbTodos);
			rbActivos=(RadioButton)findViewById(R.id.rbActivos);
			rbAll=(RadioButton)findViewById(R.id.rbAll);
			rbCafe=(RadioButton)findViewById(R.id.rbCafe);
			rbLatas=(RadioButton)findViewById(R.id.rbLatas);
			rbSnack=(RadioButton)findViewById(R.id.rbSnack);
			
			etUltimos =(EditText)findViewById(R.id.etUltimos);
			btnSettings =(ImageButton)findViewById(R.id.btnSettings);
			btnCalendariodesde =(ImageButton)findViewById(R.id.btnCalendariodesde);
			btnCalendariohasta =(ImageButton)findViewById(R.id.btnCalendariohasta);
			tvFiltro =(TextView)findViewById(R.id.tvfiltro);
			llCalendario.setVisibility(View.GONE);
			cal = Calendar.getInstance();
			day = cal.get(Calendar.DAY_OF_MONTH);
			month = cal.get(Calendar.MONTH);
			year = cal.get(Calendar.YEAR);
			
			parteFecha="";
			fecha1="";
			fecha2="";
			
			
			btnSettings.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					MostrarOcultarLL();
				}
			});
			btnCalendariodesde.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					MostrarCalendario1();
				}
			});
			btnCalendariohasta.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					MostrarCalendario2();
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
					int id_producto= mItems.get(position).getID();
					Intent intent = new Intent(Productos_Activity.this,Lineas_Activity.class);
					intent.putExtra("id_producto", id_producto);
					intent.putExtra("fecha1", fecha1);
					intent.putExtra("fecha2", fecha2);
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
					mBD = new JBD(Productos_Activity.this,getString(R.string.app_preferences),info.versionCode);
					String condicion="and prod_nombre like '%"+Filtro+"%'";
					//String condicion=String.format("and producto='%s' order by unidades Asc",filtro);
					mItems = mBD.getProductos(condicion);
					mDatosAdapter = new MiArrayAdapter(Productos_Activity.this); 
					mLista.setAdapter(mDatosAdapter);
					tit_fecha="- fechas:todas - ";
					tit_uso="";
					tit_tipo="- tipos:todos -";
					titulo=tit_fecha+tit_tipo;
					tvFiltro.setText(titulo);
					
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
		
		etUltimos.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				try {
					String ultim = etUltimos.getText().toString();
					if(ultim.length()==0){
						
						return;
					}else{
						
					ultimos =Integer.valueOf(etUltimos.getText().toString());
					if(month<10){
						if(day-ultimos<10){
							fecha1=(""+ year +"-0" +(month + 1) + "-0" +(day-ultimos));
							//fecha2=(""+ year +"-0" +(month + 1) + "-0" +day);
						}else{
							fecha1=(""+ year +"-0" +(month + 1) + "-" +(day-ultimos));
							//fecha2=(""+ year +"-0" +(month + 1) + "-" +day);
						}
						if (month>9){
							if (day-ultimos>9){
								fecha1=(""+ year +"-" +(month + 1) + "-" +(day-ultimos));
								//fecha2=(""+ year +"-" +(month + 1) + "-" +day);
							}
							else{
								fecha1=(""+ year +"-" +(month + 1) + "-0" +(day-ultimos));
								//fecha2=(""+ year +"-" +(month + 1) + "-0" +day);
							}
						}
					}
					if(month<10){
						if(day<10){
							//fecha1=(""+ year +"-0" +(month + 1) + "-0" +(day-ultimos));
							fecha2=(""+ year +"-0" +(month + 1) + "-0" +day);
						}else{
							//fecha1=(""+ year +"-0" +(month + 1) + "-" +(day-ultimos));
							fecha2=(""+ year +"-0" +(month + 1) + "-" +day);
						}
						if (month>9){
							if (day>9){
								//fecha1=(""+ year +"-" +(month + 1) + "-" +(day-ultimos));
								fecha2=(""+ year +"-" +(month + 1) + "-" +day);
							}
							else{
								//fecha1=(""+ year +"-" +(month + 1) + "-0" +(day-ultimos));
								fecha2=(""+ year +"-" +(month + 1) + "-0" +day);
							}
						}
					}
					}
					
					
				} catch (Exception e) {
					Log.e(JUtils.TAG,"onTextChanged:"+e.getMessage());
				}
				
				tit_fecha="de "+fecha1+" a "+fecha2+" ";
				chekBox2.setChecked(true);
				chekBox1.setChecked(false);
				tvfecha1.setText("Fecha \n desde:");
				tvFecha2.setText("Hasta");
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
	
}
	private void MostrarCalendario1() {
		mDatePickerDialog1 = new DatePickerDialog(this, datePickerListener1, year, month, day);
		mDatePickerDialog1.show();
	}
	private DatePickerDialog.OnDateSetListener datePickerListener1 = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int selectedYear1,int selectedMonth1, int selectedDay1) {
			tvfecha1.setText(selectedDay1 + "-" + (selectedMonth1 + 1) + "-" + selectedYear1);
			if(selectedMonth1<10){
				if(selectedDay1>9){
			fecha1=(""+ selectedYear1 +"-0" +(selectedMonth1 + 1) + "-" +selectedDay1);
				}else{
					fecha1=(""+ selectedYear1 +"-0" +(selectedMonth1 + 1) + "-0" +selectedDay1);
				}
			}else{
				if(selectedDay1>9){
				fecha1=(""+ selectedYear1 +"-" +(selectedMonth1 + 1) + "-" +selectedDay1);
				}else{
					fecha1=(""+ selectedYear1 +"-" +(selectedMonth1 + 1) + "-0" +selectedDay1);
				}
			}
			chekBox1.setChecked(true);
			chekBox2.setChecked(false);
			etUltimos.setText("");
			
			
		}
	};
	private void MostrarCalendario2() {
		mDatePickerDialog2 = new DatePickerDialog(this, datePickerListener2, year, month, day);
		mDatePickerDialog2.show();
	}
	private DatePickerDialog.OnDateSetListener datePickerListener2 = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int selectedYear2,int selectedMonth2, int selectedDay2) {
			tvFecha2.setText(selectedDay2 + "-" + (selectedMonth2 + 1) + "-"+ selectedYear2);
			if(selectedMonth2<10){
				if(selectedDay2>9){
			fecha2=(""+ selectedYear2 +"-0" +(selectedMonth2 + 1) + "-" +selectedDay2);
				}else{
					fecha2=(""+ selectedYear2 +"-0" +(selectedMonth2 + 1) + "-0" +selectedDay2);
				}
			}else{
				if(selectedDay2>9){
				fecha2=(""+ selectedYear2 +"-" +(selectedMonth2 + 1) + "-" +selectedDay2);
				}else{
					fecha2=(""+ selectedYear2 +"-" +(selectedMonth2 + 1) + "-0" +selectedDay2);
				}
			}
			chekBox1.setChecked(true);
			chekBox2.setChecked(false);
			etUltimos.setText("");
			tit_fecha=fecha1+"-"+fecha2;
		}
	};
	private void MostrarOcultarLL() {
		
		if (llOculto == true){
			llCalendario.setVisibility(View.VISIBLE);
			llOculto = false;
		}else{
			llCalendario.setVisibility(View.GONE);
			llOculto = true;
			
			
			Filtrar();
			titulo=tit_fecha+tit_tipo+tit_uso;
			tvFiltro.setText(titulo);
		}
		
	}
	

public void Filtrar (){
	try {

		if( rbTodos.isChecked()==true){
			ParteUso ="";
		}
		if( rbActivos.isChecked()==true){
			ParteUso =" and fecha>'2016-01-01' ";
			tit_uso=" activos ";
		}
		
		if( rbAll.isChecked()==true){
			ParteTipo ="";
		}
		if( rbCafe.isChecked()==true){
			ParteTipo =" and prod_tipo = 'cafe' ";
			tit_tipo=" cafe ";
		}
		if( rbLatas.isChecked()==true){
			ParteTipo =" and prod_tipo = 'latas' ";
			tit_tipo=" latas ";
		}
		if( rbSnack.isChecked()==true){
			ParteTipo = (" and prod_tipo = 'snack' ");
			tit_tipo=" snack ";
		}

		PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
		mBD = new JBD(this,getString(R.string.app_preferences),info.versionCode);
		if(fecha1.length()==0){
			parteFecha="";
		}else{
			ParteUso="";
			String pf1="and fecha >= '";
			String pf2="' and fecha <= '";
			String pf3="'";
			parteFecha=pf1+fecha1+pf2+fecha2+pf3;
		}
		
		condicion=" "+ParteUso+parteFecha+ParteTipo;
		
		
		mItems = mBD.getProductos(condicion);
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
		mBD= new JBD(this,getString(R.string.app_preferences),info.versionCode);
		String condicion="";
		titulo=tit_fecha+tit_tipo+tit_uso;
		tvFiltro.setText(titulo);
		//String condicion=String.format("and producto='%s' order by unidades Asc",filtro);
		mItems = mBD.getProductos2(condicion);
		mDatosAdapter = new MiArrayAdapter(this); 
		mLista.setAdapter(mDatosAdapter);
		// restore index and position
		mLista.setSelectionFromTop(index, top);
	} catch (Exception e) {
		Log.e(JUtils.TAG,"LA:onResume:"+e.getMessage());
	}
	super.onResume();
}

private class MiArrayAdapter extends ArrayAdapter<JProducto>{

	public MiArrayAdapter(Context context) {
		super(context, R.layout.productos_row_layout,mItems);
	}
	//Sobreescribimos la función getView, por la cual pasará cada vez que tenga que "pintar" una fila del ListView
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		//"Inflamos" una fila, indicando el layout a utilizar:
		LayoutInflater inflater = Productos_Activity.this.getLayoutInflater();
		View rowView= inflater.inflate(R.layout.productos_row_layout, null);

		//Rellenamos la fila con la información (TextView con la ciudad e ImageView con la imagen)
		TextView txtTitle = (TextView) rowView.findViewById(R.id.tvTitle);
		TextView txtUnidades = (TextView) rowView.findViewById(R.id.tvTipo);
		TextView txtObserv = (TextView) rowView.findViewById(R.id.tvDescripcion);
		ivProducto = (ImageView) rowView.findViewById(R.id.ivProducto);
		int unidades= 0;
		
		//try {
		String condicionFiltro="";
		if(chekBox1.isChecked()==true){
			condicionFiltro=" and fecha>='"+fecha1+"' and fecha<='"+fecha2+"'";
		}
		if(chekBox2.isChecked()==true){
			condicionFiltro=" and fecha>='"+fecha1+"' and fecha<='"+fecha2+"'";
		}
			unidades =mBD.cuentaProductos_old2(mItems.get(position).getID(),condicionFiltro);
		//} catch (Exception e) {
			// TODO: handle exception
		//}
		if (unidades>0){
			txtUnidades.setText("Total unidades distribuidas: "+unidades);
		}
		txtTitle.setText(mItems.get(position).getProd_nombre());
	
		txtObserv.setText("Tipo: "+mItems.get(position).getProd_tipo()+" - pack: "+mItems.get(position).getProd_pack());
		
		int resId=getResources().getIdentifier(String.format("i%s",mItems.get(position).getID()),"drawable",getPackageName());
		if (resId==0) resId = R.drawable.etxebendin;
		ivProducto.setImageResource(resId);
		
		return rowView;
	}
	
}
public void bntFiltrar_Click (View view){
	try {


		String Filtro =etFiltro.getText().toString();

		PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
		mBD = new JBD(this,getString(R.string.app_preferences),info.versionCode);
		String condicion="and producto like '%"+Filtro+"%'";
		//String condicion=String.format("and producto='%s' order by unidades Asc",filtro);
		mItems = mBD.getProductos(condicion);
		mDatosAdapter = new MiArrayAdapter(this); 
		mLista.setAdapter(mDatosAdapter);
	} catch (Exception e) {
		Log.e(JUtils.TAG,"btnFiltrar_Click:"+e.getMessage());
	}
}
}

