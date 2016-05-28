package com.example.etxeberria_vending;

import java.util.ArrayList;

import com.example.etxeberria_vending.clases.JBD;
import com.example.etxeberria_vending.clases.JProducto;
import com.example.etxeberria_vending.clases.JUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;



public class SeleccionProducto_Activity extends Activity {

	private MiArrayAdapter mDatosAdapter;
	private ArrayList<JProducto> mItems;
	private ListView mLista;
	private EditText etFiltro;
	private ImageView ivProducto;
	private int id_posicion;
	private int id_carga;
	private int id_producto;
	private int plaza_id;
	private String tipoMaquina;
	private String pos_fila_columna;
	private String nombreMaquina;
	private String fecha;
	private int producto_old;
	private JBD mBD;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seleccion_producto_layout);
		
		tipoMaquina = "";
		Bundle datos = getIntent().getExtras();
		id_posicion = datos.getInt("posicion");
		producto_old = datos.getInt("producto_old",0);
		id_carga = datos.getInt("id_carga");
		plaza_id = datos.getInt("plaza_id");
		tipoMaquina = datos.getString("tipo_maquina");
		fecha = datos.getString("fecha");
		pos_fila_columna = datos.getString("pos_fila_columna");
		nombreMaquina = datos.getString("nombreMaquina");
		getActionBar().setTitle("Posicion"+"-"+pos_fila_columna+"-"+nombreMaquina);
		getActionBar().setIcon(R.drawable.loguito);
		PackageInfo info;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
			mBD = new JBD(SeleccionProducto_Activity.this,getString(R.string.app_preferences),info.versionCode);
		} catch (NameNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//Para evitar que se muestre automaticamente el teclado al iniciar la activity
				getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
					mLista = (ListView) findViewById(R.id.lvLista);
					//Asociamos la propiedad al control:
					//Control de evento click sobre una fila:
					mLista.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3) {
					//Obtenemos el id del item seleccionado y vamos a editar:
					id_producto = mItems.get(position).getID();
					int pack = mItems.get(position).getProd_pack();
					String prod_nombre = mItems.get(position).getProd_nombre();
					mBD.updatePosicionProducto(id_posicion, id_producto);
					
					Intent intent = new Intent(SeleccionProducto_Activity.this,CargandoDetalle_Activity.class);
					intent.putExtra("id_posicion", id_posicion);
					intent.putExtra("pos_fila_columna", pos_fila_columna);
					intent.putExtra("tipo_maquina", tipoMaquina);
					intent.putExtra("nombreMaquina", nombreMaquina);
					intent.putExtra("pack", pack);
					intent.putExtra("nombre_producto", prod_nombre);
					intent.putExtra("producto_id", id_producto);
					intent.putExtra("fecha", fecha);
					intent.putExtra("producto_old", producto_old);
					intent.putExtra("id_carga", id_carga);
					intent.putExtra("plaza_id", plaza_id);
					startActivityForResult(intent,0);
					finish();
					}
				});
				etFiltro = (EditText)findViewById(R.id.etFiltro);
				etFiltro.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						try {
							String Filtro =etFiltro.getText().toString();

							PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
							JBD db = new JBD(SeleccionProducto_Activity.this,getString(R.string.app_preferences),info.versionCode);
							String condicion="and prod_nombre like '%"+Filtro+"%'";
							//String condicion=String.format("and producto='%s' order by unidades Asc",filtro);
							mItems = db.getProductos(condicion);
							mDatosAdapter = new MiArrayAdapter(SeleccionProducto_Activity.this); 
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
			//} catch (Exception e) {
				//Log.e(JUtils.TAG,"LA:onCreate:Bundle:"+e.getMessage());
			//}
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
				String condicion="and producto like '%"+Filtro+"%'";
				//String condicion=String.format("and producto='%s' order by unidades Asc",filtro);
				mItems = db.getProductos(condicion);
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
				//String condicion="";
				String condicion=String.format("and prod_tipo='%s'",tipoMaquina);
				mItems = db.getProductos(condicion);
				mDatosAdapter = new MiArrayAdapter(this); 
				mLista.setAdapter(mDatosAdapter);
			} catch (Exception e) {
				Log.e(JUtils.TAG,"LA:onResume:"+e.getMessage());
			}
			super.onResume();
		}

		private class MiArrayAdapter extends ArrayAdapter<JProducto>{

			public MiArrayAdapter(Context context) {
				super(context, R.layout.seleccion_productos_row_layout,mItems);
			}
			//Sobreescribimos la función getView, por la cual pasará cada vez que tenga que "pintar" una fila del ListView
			@Override
			public View getView(int position, View view, ViewGroup parent) {
				//"Inflamos" una fila, indicando el layout a utilizar:
				LayoutInflater inflater = SeleccionProducto_Activity.this.getLayoutInflater();
				View rowView= inflater.inflate(R.layout.seleccion_productos_row_layout, null);

				//Rellenamos la fila con la información (TextView con la ciudad e ImageView con la imagen)
				TextView txtTitle = (TextView) rowView.findViewById(R.id.tvTitle);
				TextView txtUnidades = (TextView) rowView.findViewById(R.id.tvTipo);
				TextView txtObserv = (TextView) rowView.findViewById(R.id.tvDescripcion);
				ivProducto = (ImageView) rowView.findViewById(R.id.ivProducto);
				
				txtTitle.setText(mItems.get(position).getProd_nombre());
				//txtUnidades.setText("stock:"+String.valueOf(mItems.get(position).getUnidades()));
				txtObserv.setText(mItems.get(position).getProd_tipo());
				txtUnidades.setText(String.valueOf((mItems.get(position).getProd_pack())));
				int resId=getResources().getIdentifier(String.format("i%s",mItems.get(position).getID()),"drawable",getPackageName());
				if (resId==0) resId = R.drawable.ic_launcher;
				ivProducto.setImageResource(resId);
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
