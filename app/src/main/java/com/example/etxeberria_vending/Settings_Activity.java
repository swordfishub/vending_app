package com.example.etxeberria_vending;

import java.util.ArrayList;

import com.example.etxeberria_vending.clases.JBD;
import com.example.etxeberria_vending.clases.JCargador;
import com.example.etxeberria_vending.clases.JUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

public class Settings_Activity extends Activity {
	
	private Spinner mSPCargadores;
	private String mEmailOficina;
	private String mEmailTecnico;
	private String mEmailJefe;
	private Button btnGuardar;
	private Button btnCancelar;
	private JBD mBD;
	private int id_cargador;
	private ArrayList<JCargador>mCargadores;
	private EditText ETmailOficina;
	private EditText ETMailTecnico;
	private EditText ETMailJefe;
	private String nombre_cargador;
	private RadioGroup rgSQL;
	private RadioButton rbActivarSQL;
	private RadioButton rbCancelarSQL;
	private Boolean SQL;
	private Button btnBorrar;
	
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_layout);
		try {
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
			mBD = new JBD(Settings_Activity.this,getString(R.string.app_preferences),info.versionCode);
			
			
		} catch (Exception e) {
			Log.e(JUtils.TAG,"onTextChanged:"+e.getMessage());
		}
		mCargadores = mBD.getCargadors("");
		//Para evitar que se muestre automaticamente el teclado al iniciar la activity
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		//Asociamos la propiedad al control:
		mSPCargadores = (Spinner) findViewById(R.id.SPCargadores);
		ETmailOficina = (EditText) findViewById(R.id.ETmailOficina);
		ETMailTecnico = (EditText) findViewById(R.id.ETMailTecnico);
		ETMailJefe = (EditText) findViewById(R.id.ETMailJefe);
		btnGuardar = (Button)findViewById(R.id.btnGuardar);
		btnCancelar = (Button)findViewById(R.id.btnCancelar);
		rgSQL = (RadioGroup)findViewById(R.id.rgSQL);
		rbActivarSQL = (RadioButton)findViewById(R.id.rbActivarSQL);
		rbCancelarSQL = (RadioButton)findViewById(R.id.rbCancelarSQL);
		
		btnBorrar = (Button)findViewById(R.id.btnBorrar);
		//de momento oculto el boton borrar
		btnBorrar.setVisibility(View.GONE);
		ArrayAdapter<JCargador> adapter = new ArrayAdapter<JCargador>(this, android.R.layout.simple_spinner_item, mCargadores);
		mSPCargadores.setAdapter(adapter);
		mSPCargadores.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				try {
					id_cargador = mCargadores.get(position).getID();
					nombre_cargador =mCargadores.get(position).getCargador_nombre();
					
					//AplicarFiltro();
				} catch (Exception e) {
					Log.e("_Activity", "onItemSelected:"+e.getMessage());
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
	}
	public void SalvarPreferencias(View view) {
    	try {
    		SQL = false;
    		if (rbActivarSQL.isChecked()){
    			SQL = true;
    		}
    		//Identificamos el bloque de preferencias que nos interesa (ultimo parametro: 0=Privado, 1=Lectura (otras apps), 2=Escritura (Otras apps)
    		SharedPreferences preferences = getSharedPreferences("VendingPreferencias", 0);

    		//Recogemos los datos:
    		SharedPreferences.Editor editor = preferences.edit();
    		editor.putBoolean("SQL",SQL);
    		editor.putInt("id_cargador",id_cargador);
    		editor.putString("nombre_cargador", nombre_cargador);
    		String email_oficina = ETmailOficina.getText().toString();
    		if (email_oficina.length()>0);{
    		editor.putString("email_oficina", email_oficina);
    		}
    		String email_tecnico = ETMailTecnico.getText().toString();
    		if (email_tecnico.length()>0);{
    		editor.putString("email_tecnico", email_tecnico);
    		}
    		String email_jefe = ETMailJefe.getText().toString();
    		if (email_jefe.length()>0);{
    		editor.putString("email_jefe", email_jefe);
    		}
    		
    		//Guardamos los datos:
    		editor.commit();    		
    		Toast.makeText(this, "Preferencias salvadas!", Toast.LENGTH_SHORT).show();
    	} catch (Exception e) {
			Toast.makeText(this, "No se ha podido guardar las preferencias", Toast.LENGTH_SHORT).show();
		}
    	Intent intent = new Intent(Settings_Activity.this,Main_Activity.class);
		intent.putExtra("id_cargador", id_cargador);
		intent.putExtra("nombre_cargador", nombre_cargador);
		
		startActivityForResult(intent,0);
		finish();
    }
	
	public void CancelarSettings(View view) {
    	
    		
    	Intent intent = new Intent(Settings_Activity.this,Main_Activity.class);
		intent.putExtra("id_cargador", id_cargador);
		intent.putExtra("nombre_cargador", nombre_cargador);
		
		startActivityForResult(intent,0);
		finish();
    }
	public void btnBorrar_Click(View view) {
		try {
    		//Identificamos el bloque de preferencias que nos interesa (ultimo parametro: 0=Privado, 1=Lectura (otras apps), 2=Escritura (Otras apps)
    		SharedPreferences preferences = getSharedPreferences("VendingPreferencias", 0);
    		//Obtenemos la referencia al dato que queremos eliminar de preferencias:
    		SharedPreferences.Editor editor = preferences.edit();
    		editor.remove("codigo_usuario");
    		Toast.makeText(this, "preferencias borradas", Toast.LENGTH_SHORT).show();
    		//Guardamos los cambios:
    		editor.commit();    		
    		
    	} catch (Exception e) {
			Toast.makeText(this, "No se ha podido guardar las preferencias", Toast.LENGTH_SHORT).show();
		}    	
		
    	
    }
	
}
