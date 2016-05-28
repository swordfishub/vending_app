package com.example.etxeberria_vending;

import com.example.etxeberria_vending.clases.JBD;
import com.example.etxeberria_vending.clases.JLinea;
import com.example.etxeberria_vending.clases.JUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CargandoDetalle_Activity extends Activity {
	private int id_posicion;
	private int producto_id;
	private int producto_old;
	private int id_carga;
	private int pr_venta;
	private int plaza_id;
	private String nombre_producto;
	private String pos_fila_columna;
	private String nombreMaquina;
	private int pack;
	private TextView tvPosicion;
	private TextView tvProducto;
	private EditText etUnidades;
	private EditText etPrecio;
	private Button btnPack;
	private Button btnGuardar;
	private EditText etUnidades_retiradas;
	private EditText etMotivo;
	private String fecha;
	private int cantidad_retirada;
	private String motivo;
	private JBD mBD;
	private JLinea mlinea;
	private int unidades;
	private int unidades_cargadas;
	//el importe para el pr venta de las lineas
	private int imp;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cargando_detalle_layout);
		try {
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
			mBD = new JBD(CargandoDetalle_Activity.this,getString(R.string.app_preferences),info.versionCode);
			btnPack = (Button)findViewById(R.id.btnPack);
			btnGuardar = (Button)findViewById(R.id.btnGuardar);
			etUnidades = (EditText)findViewById(R.id.etUnidades);
			etPrecio = (EditText)findViewById(R.id.etPrecio);
			etUnidades_retiradas = (EditText)findViewById(R.id.etUnidades_retiradas);
			etMotivo = (EditText)findViewById(R.id.etMotivo);
			tvPosicion = (TextView)findViewById(R.id.tvPosicion);
			tvProducto = (TextView)findViewById(R.id.tvProducto);
			
			Bundle datos = getIntent().getExtras();
			id_posicion = datos.getInt("id_posicion");
			producto_id = datos.getInt("producto_id");
			producto_old = datos.getInt("producto_old");
			unidades_cargadas = datos.getInt("unidades_cargadas",0);
			nombre_producto = datos.getString("nombre_producto");
			fecha = datos.getString("fecha");
			nombreMaquina = datos.getString("nombreMaquina");
			pr_venta = datos.getInt("pr_venta",0);
			pos_fila_columna = datos.getString("pos_fila_columna");
			pack = datos.getInt("pack");
			plaza_id = datos.getInt("plaza_id");
			id_carga = datos.getInt("id_carga");
			getActionBar().setTitle("cargando "+nombreMaquina);
			getActionBar().setIcon(R.drawable.loguito);
			tvPosicion.setText(String.valueOf(pos_fila_columna));
			tvProducto.setText(nombre_producto);
			float euros = pr_venta/100f;
			//String precio = String.valueOf(euros);
			String redondear = String.format("%.2f", euros);
			if (euros>0){
			etPrecio.setText(redondear);
			}else{
				etPrecio.setText("");
			}
			if(unidades_cargadas>0){
				etUnidades.setText(""+unidades_cargadas);
			}
			
			
			btnPack.setText("pack: "+pack);
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
			
			
		
		} catch (Exception e) {
			Log.e(JUtils.TAG,"oncreate:"+e.getMessage());
		}
	}

	public void btnPack_click(View view){
		String unidades_iniciales = etUnidades.getText().toString();
		if (unidades_iniciales.length()==0){
			int cantidad_inicial =0;
		etUnidades.setText(""+pack);	
		}else{
			int unid_ini= Integer.valueOf(unidades_iniciales);
			int unidades_finales = unid_ini+pack;
			String uni_final = String.valueOf(unidades_finales).toString();
			etUnidades.setText(uni_final);
		}
		
	}
	public void btnGuardar_click(View view){
		float importe =0f;
		try {
			String string_precio=etPrecio.getText().toString();
			if(string_precio.length()==0){
				importe=0f;
			}else{
			String str=string_precio.replace(',', '.');
			importe = Float.valueOf(str);
			}
		} catch (Exception e) {
			Toast.makeText(CargandoDetalle_Activity.this, "Introduce un importe válido", 
					Toast.LENGTH_SHORT).show();
			return;
		}
		imp =(int)(importe*100f);
		
	
		if(pr_venta==imp){
			pr_venta=imp;
		}else{
			mBD.updatePosicionPrecio(id_posicion, imp);
		}
		String unidades_retiradas = etUnidades_retiradas.getText().toString();
		if (unidades_retiradas.length()==0){
			cantidad_retirada =0;
			producto_old =0;
			motivo=null;
			
			}else{
			cantidad_retirada= Integer.valueOf(unidades_retiradas);
			
			motivo = etMotivo.getText().toString();
			
		}
		try {
			unidades = Integer.valueOf(etUnidades.getText().toString());
		} catch (Exception e) {
			Toast.makeText(CargandoDetalle_Activity.this, "Introduce las unidades", 
					Toast.LENGTH_SHORT).show();
			return;
		}
		
		int estado=0;
		mlinea = new JLinea(id_carga,id_posicion,producto_id,unidades,producto_old,cantidad_retirada,motivo,fecha,estado, plaza_id, imp);
		mBD.addLinea(mlinea);
		mBD.updatePosicionUltimas(id_posicion, unidades);
		mBD.updatePosicionFecha(id_posicion, fecha);
		mBD.updateProductoFecha(fecha, producto_id);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		finish();
	}
	private void Salir(){
		try {
			//Mostramos un alert dialog, para confirmar que quiere cerrar sin guardar
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(CargandoDetalle_Activity.this);
			alertDialog.setTitle("Cargando producto");
			alertDialog.setMessage("Sales sin guardar?");
			alertDialog.setPositiveButton("Si",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});

			alertDialog.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			alertDialog.show();
		} catch (Exception e) {
			Log.e("Salir:"+e.getMessage(), fecha);
		}
	}
	
	//Esta es la función automatica que busca android cuando el usuario pulsa
	//el boton flecha atras del movil:
	@Override
	public void onBackPressed() {
		//Comentamos la llamada super.onBac... porque no quiere que automaticamente
		//se cierre esta activity, sino que haga lo indica la función Salir()
	    //super.onBackPressed();
	    //Llamamos a la función que pregunta al usuario si quiere salir
	    Salir();
	}
}
