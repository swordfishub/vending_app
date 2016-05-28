package com.example.etxeberria_vending;

import org.json.JSONObject;

import com.example.etxeberria_vending.clases.JUsuario;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Alta_Activity extends Comun_Activity {
	private final String TAG="Main_Activity";
	
	
	private EditText mETMail;
	private EditText mETPass;
	private JUsuario mUsuario;
	private int mCod;
	
	//Funcionalidad de registro
	private LinearLayout mLLRegistro;
	private EditText mETNombre;
	//Varaible para obtener mensajes del servidor, cuando algo ha ido mal
	private String mServerMsg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alta_layout);
		try {
			mETMail = (EditText)findViewById(R.id.etMail);
			mETPass = (EditText)findViewById(R.id.etPass);
			
			mLLRegistro = (LinearLayout)findViewById(R.id.llRegistro);
			mETNombre = (EditText)findViewById(R.id.etNombre);
			//Si en las preferencias tenemos guardado un mail, lo cargamos:
			SharedPreferences prefs = getSharedPreferences("gualapop", 0);
			mETMail.setText(prefs.getString("u_mail", ""));
		} catch (Exception e) {
			Log.e(TAG, "onCreate:"+e.getMessage());
		}
	}

	public void btnRegistro_Click(View view){
		mLLRegistro.setVisibility(View.VISIBLE);
	}
	public void btnCancelar_Click(View view){
		mLLRegistro.setVisibility(View.GONE);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	//Mostramos un cuadro de dialogo para pedir confirmación de eliminar:
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(Alta_Activity.this);
			alertDialog.setTitle("VENDING");
			alertDialog.setMessage("¿Quieres salir de la app?");
			alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});

			alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			alertDialog.show();
	    }
	    return true;
	}
	
	public void btnRegenerarPass_Click(View view){
		try {
			//Recogemos los datos del layout, comprobamos que no sean nulos:
			String mail = mETMail.getText().toString();
			if (mail.length()==0) {
				Toast.makeText(Alta_Activity.this, "Mail requerido!", Toast.LENGTH_SHORT).show();
				return;
			}
			//Si llegamos hasta aqui, tenemos mail 
			//ya podemos llamar al servicio web regenerar pass:
			mUsuario = new JUsuario("",mail, "");
			Usuario_RegenerarPass regenerar = new Usuario_RegenerarPass();
			regenerar.execute();
		} catch (Exception e) {
			Log.e(TAG, "btnRegenerarPass_Click:"+e.getMessage());
		}
	}
	
	public void btnRegistrar_Click(View view){
		try {
			//Recogemos los datos del layout, comprobamos que no sean nulos:
			String mail = mETMail.getText().toString();
			if (mail.length()==0) {
				Toast.makeText(Alta_Activity.this, "Mail requerido!", Toast.LENGTH_SHORT).show();
				return;
			}
			//Comprobar que el mail tenga formato correcto:
			if (android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()==false){
				Toast.makeText(Alta_Activity.this, "El mail no es válido", Toast.LENGTH_SHORT).show();
				return;
			}
			String pass = mETPass.getText().toString();
			if (pass.length()==0) {
				Toast.makeText(Alta_Activity.this, "Password requerido!", Toast.LENGTH_SHORT).show();
				return;
			}
			String nombre = mETNombre.getText().toString();
			if (nombre.length()==0) {
				Toast.makeText(Alta_Activity.this, "Nombre requerido!", Toast.LENGTH_SHORT).show();
				return;
			}
			//Si llegamos hasta aqui, tenemos mail, password y nombre, 
			//ya podemos llamar al servicio web registrar:
			mUsuario = new JUsuario(nombre,mail, pass);
			Usuario_Registrar registro = new Usuario_Registrar();
			registro.execute();
		} catch (Exception e) {
			Log.e(TAG, "btnRegistrar_Click:"+e.getMessage());
		}
	}

	public void btnValidar_Click(View view){
		try {
			//Recogemos los datos del layout, comprobamos que no sean nulos:
			String mail = mETMail.getText().toString();
			if (mail.length()==0) {
				Toast.makeText(Alta_Activity.this, "Mail requerido!", Toast.LENGTH_SHORT).show();
				return;
			}
			//Comprobar que el mail tenga formato correcto:
			if (android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()==false){
				Toast.makeText(Alta_Activity.this, "El mail no es válido", Toast.LENGTH_SHORT).show();
				return;
			}
			String pass = mETPass.getText().toString();
			if (pass.length()==0) {
				Toast.makeText(Alta_Activity.this, "Password requerido!", Toast.LENGTH_SHORT).show();
				return;
			}
			//Si llegamos hasta aqui, tenemos mail y password, podemos llamar al servicio web validar:
			mUsuario = new JUsuario("",mail, pass);
			Usuario_Validar validar = new Usuario_Validar();
			validar.execute();
		} catch (Exception e) {
			Log.e(TAG, "btnValidar_Click:"+e.getMessage());
		}
	}
	private class Usuario_Validar extends AsyncTask<String,Integer,Boolean> {
		private ProgressDialog mPD;
		//En el onPreExecute aprovechamos a cargar el progress dialog:
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			try {
				mPD = new ProgressDialog(Alta_Activity.this);
				mPD.setCancelable(false);	//El usuario no puede ocultar el progress dando a la tecla back
				mPD.setTitle("Validando usuario...");
				mPD.show();
			} catch (Exception e) {
				Log.e(TAG,"Usuario_Validar (onPre):"+e.getMessage());
			}
		}
		//Lo que pongamos en la funcion doInBack, esta fuera del hilo principal de ejecución. 
		//Esto conlleva que no podemos realizar instrucciones que requieran estar en el hilo principal,
		//como mostrar mensajes en pantalla (Toast), o modificar contenidos en el layout (mETMail.setText(""))
		@Override
		protected Boolean doInBackground(String... params) {
			JSONObject jsonResponse=null;
			boolean resultado=false;
			try {
				JSONObject jsonRequest = new JSONObject();
				jsonRequest.put("mail", mUsuario.getMail());
				jsonRequest.put("pass", mUsuario.getPass());
				String respuesta=ServerConnection(jsonRequest,"sw_validar.php");
				if (respuesta!=null) 
					try {
						jsonResponse = new JSONObject(respuesta); 
					} catch (Exception e) {
						Log.e(TAG,"Usuario_Validar (jsonResponse):"+e.getMessage());
						jsonResponse=null;
					}
				if (jsonResponse!=null) {	
					//Obtenemos el parametro result: true (validación correcta, false incorrecta)
					resultado = jsonResponse.getBoolean("result");
					//También recibo el codigo de usuario, 0 (usuario no existe) >0, 
					//el usuario existe y es su codigo remoto:
					mCod = jsonResponse.getInt("cod");
				} else {
					throw new Exception("JSONResponse es nulo");
				}
			} catch (Exception e) {
				Log.e(TAG,"Usuario_Validar (doIn):"+e.getMessage());
			}
			return resultado;
		}
		//Cuando la funcion doInBack termina, salta automaticamente a la función onPost, y vuelve al hilo principal,
		//con lo que ya podemos mostrar mensajes o hacer cambios en los contenidos de layout
		@Override
		protected void onPostExecute(Boolean resultado) {
			try {
				if (resultado==false){
					Toast.makeText(Alta_Activity.this, "Acceso incorrecto", Toast.LENGTH_SHORT).show();
				} 
				
			} catch (Exception e) {
				Log.e(TAG,"Usuario_Validar (onPost):"+e.getMessage());
			}
			mPD.dismiss();
			//Si ha ido bien, saltamos al listado de usuarios:
			if (resultado==true) {
				//Guardamos en las preferencias el mail:
				SharedPreferences prefs = getSharedPreferences("gualapop", 0);
				SharedPreferences.Editor editor = prefs.edit();
					editor.putString("u_mail", mUsuario.getMail());
				editor.commit();
				Intent nueva_pantalla = new Intent(Alta_Activity.this,Main_Activity.class);
				nueva_pantalla.putExtra("usu_cod", mCod);
				startActivity(nueva_pantalla);
				finish();
			}
			super.onPostExecute(resultado);
		}
	}
	
	private class Usuario_Registrar extends AsyncTask<String,Integer,Boolean> {
		private ProgressDialog mPD;
		//En el onPreExecute aprovechamos a cargar el progress dialog:
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			try {
				mPD = new ProgressDialog(Alta_Activity.this);
				mPD.setCancelable(false);	//El usuario no puede ocultar el progress dando a la tecla back
				mPD.setTitle("Registrando usuario...");
				mPD.show();
			} catch (Exception e) {
				Log.e(TAG,"Usuario_Registrar (onPre):"+e.getMessage());
			}
		}
		//Lo que pongamos en la funcion doInBack, esta fuera del hilo principal de ejecución. 
		//Esto conlleva que no podemos realizar instrucciones que requieran estar en el hilo principal,
		//como mostrar mensajes en pantalla (Toast), o modificar contenidos en el layout (mETMail.setText(""))
		@Override
		protected Boolean doInBackground(String... params) {
			JSONObject jsonResponse=null;
			boolean resultado=false;
			mServerMsg="";
			try {
				JSONObject jsonRequest = new JSONObject();
				jsonRequest.put("mail", mUsuario.getMail());
				jsonRequest.put("pass", mUsuario.getPass());
				jsonRequest.put("nombre", mUsuario.getNombre());
				String respuesta=ServerConnection(jsonRequest,"sw_registrar.php");
				if (respuesta!=null) 
					try {
						jsonResponse = new JSONObject(respuesta); 
					} catch (Exception e) {
						Log.e(TAG,"Usuario_Registrar (jsonResponse):"+e.getMessage());
						jsonResponse=null;
					}
				if (jsonResponse!=null) {	
					//Obtenemos el parametro result: true (validación correcta, false incorrecta)
					resultado = jsonResponse.getBoolean("result");
					//También recibo el codigo de usuario, 0 (usuario no existe) >0, 
					//el usuario existe y es su codigo remoto:
					mCod = jsonResponse.getInt("cod");
					
					try {
						SharedPreferences preferences = getSharedPreferences("VendingPreferencias", 0);

			    		//Recogemos los datos:
			    		SharedPreferences.Editor editor = preferences.edit();
			    		;
			    		editor.putInt("codigo_usuario",mCod);
			    		
			    		//Guardamos los datos:
			    		editor.commit();    
					} catch (Exception e) {
						// TODO: handle exception
					}
					
					
					//Si resultado es false, vamos a ller el msg que nos envia desde php
					if (resultado==false){
						mServerMsg = jsonResponse.getString("msg");
					}
				} else {
					throw new Exception("JSONResponse es nulo");
				}
			} catch (Exception e) {
				Log.e(TAG,"Usuario_Registrar (doIn):"+e.getMessage());
			}
			return resultado;
		}
		//Cuando la funcion doInBack termina, salta automaticamente a la función onPost, y vuelve al hilo principal,
		//con lo que ya podemos mostrar mensajes o hacer cambios en los contenidos de layout
		@Override
		protected void onPostExecute(Boolean resultado) {
			final String msg;
			try {
				if (resultado==true){
					msg="Registro correcto!";
				} else {
					msg="Registro incorrecto: "+mServerMsg;
				}
				Toast.makeText(Alta_Activity.this, msg, Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				Log.e(TAG,"Usuario_Registrar (onPost):"+e.getMessage());
			}
			mPD.dismiss();
			//Si ha ido bien, saltamos al listado de usuarios:
			if (resultado==true) {
				//Guardamos en las preferencias el mail:
				SharedPreferences prefs = getSharedPreferences("gualapop", 0);
				SharedPreferences.Editor editor = prefs.edit();
					editor.putString("u_mail", mUsuario.getMail());
				editor.commit();
				Intent nueva_pantalla = new Intent(Alta_Activity.this,Main_Activity.class);
				nueva_pantalla.putExtra("usu_cod", mCod);
				startActivity(nueva_pantalla);
				finish();
			}
			super.onPostExecute(resultado);
		}
	}
	private class Usuario_RegenerarPass extends AsyncTask<String,Integer,Boolean> {
		private ProgressDialog mPD;
		//En el onPreExecute aprovechamos a cargar el progress dialog:
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			try {
				mPD = new ProgressDialog(Alta_Activity.this);
				mPD.setCancelable(false);	//El usuario no puede ocultar el progress dando a la tecla back
				mPD.setTitle("Generando nueva clave...");
				mPD.show();
			} catch (Exception e) {
				Log.e(TAG,"Usuario_RegenerarPass (onPre):"+e.getMessage());
			}
		}
		//Lo que pongamos en la funcion doInBack, esta fuera del hilo principal de ejecución. 
		//Esto conlleva que no podemos realizar instrucciones que requieran estar en el hilo principal,
		//como mostrar mensajes en pantalla (Toast), o modificar contenidos en el layout (mETMail.setText(""))
		@Override
		protected Boolean doInBackground(String... params) {
			JSONObject jsonResponse=null;
			boolean resultado=false;
			try {
				JSONObject jsonRequest = new JSONObject();
				jsonRequest.put("mail", mUsuario.getMail());
				String respuesta=ServerConnection(jsonRequest,"sw_regenerar.php");
				if (respuesta!=null) 
					try {
						jsonResponse = new JSONObject(respuesta); 
					} catch (Exception e) {
						Log.e(TAG,"Usuario_RegenerarPass (jsonResponse):"+e.getMessage());
						jsonResponse=null;
					}
				if (jsonResponse!=null) {	
					//Obtenemos el parametro result: true (validación correcta, false incorrecta)
					resultado = jsonResponse.getBoolean("result");
				} else {
					throw new Exception("JSONResponse es nulo");
				}
			} catch (Exception e) {
				Log.e(TAG,"Usuario_RegenerarPass (doIn):"+e.getMessage());
			}
			return resultado;
		}
		//Cuando la funcion doInBack termina, salta automaticamente a la función onPost, y vuelve al hilo principal,
		//con lo que ya podemos mostrar mensajes o hacer cambios en los contenidos de layout
		@Override
		protected void onPostExecute(Boolean resultado) {
			final String msg;
			try {
				if (resultado==true){
					msg="Clave generada, consulta tu email";
				} else {
					msg="No he encontrado el mail";
				}
				Toast.makeText(Alta_Activity.this, msg, Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
				Log.e(TAG,"Usuario_RegenerarPass (onPost):"+e.getMessage());
			}
			mPD.dismiss();
			super.onPostExecute(resultado);
		}
	}
}