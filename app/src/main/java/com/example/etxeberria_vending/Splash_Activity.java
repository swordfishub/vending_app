package com.example.etxeberria_vending;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class Splash_Activity extends Activity {
	
	private int codigo_usuario;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_layout);
		
		try {
			//Identificamos el bloque de preferencias que nos interesa (ultimo parametro: 0=Privado, 1=Lectura (otras apps), 2=Escritura (Otras apps)
    		SharedPreferences preferences = getSharedPreferences("VendingPreferencias", 0);
    		//Obtenemos las preferencias, indicando la clave (si no existe, valor vacio)
    		codigo_usuario = preferences.getInt("codigo_usuario", 0);
    		
		} catch (Exception e) {
			// TODO: handle exception
		}


	//temporizador para que en 2 segundos se cierre el activity
	  Handler handler = new Handler();

      handler.postDelayed(new Runnable() {
          public void run() {
              finish();
              if(codigo_usuario>0){
              Intent nueva_pantalla = new Intent(Splash_Activity.this, Main_Activity.class);
				startActivity(nueva_pantalla);
             }
             else{
            	  Intent nueva_pantalla = new Intent(Splash_Activity.this, Alta_Activity.class);
  				startActivity(nueva_pantalla);
              }
          }
      }, 500);
}

}
