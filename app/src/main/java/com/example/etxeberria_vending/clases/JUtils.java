package com.example.etxeberria_vending.clases;

import com.example.etxeberria_vending.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

public class JUtils {
	public final static String TAG="SSFXML";
	public String PATH;
	public String PREFERENCES;
	private Context mContext;
	public JUtils(Context context){
		mContext = context;
		PREFERENCES = mContext.getString(R.string.app_preferences);
		//Establecemos el path de la carpeta de datos, por defecto, almacenamiento externo y carpeta con el nombre de las preferencias:
    	String defaultPath = String.format("%s/%s", Environment.getExternalStorageDirectory().getPath(),PREFERENCES);
    	SharedPreferences prefs = mContext.getSharedPreferences(PREFERENCES, 0);
    	PATH = prefs.getString("path", defaultPath);
	}
}
