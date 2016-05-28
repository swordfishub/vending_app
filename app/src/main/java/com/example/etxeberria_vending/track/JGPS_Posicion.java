package com.example.etxeberria_vending.track;

public class JGPS_Posicion {
	private int mID;
	private double mLatitud;
	private double mLongitud;
	private String mFecha;
	private String mHora;
	private int mCargador_id;
	private int mLongInt;
	private int mLatInt;
	
	
	public int getID(){
		return mID;
	}
	public double getLatitud(){
		return mLatitud;
	}
	public double getLongitud(){
		return mLongitud;
	}
	public String getFecha(){
		return mFecha;
	}
	public String getHora(){
		return mHora;
	}
	public int getLongInt(){
		return mLongInt;
	}
	public int getLatInt(){
		return mLatInt;
	}
	
	public JGPS_Posicion(double lat, double lon, String fecha, String hora, int longint, int latint){
		mLatitud = lat;
		mLongitud = lon;
		mFecha = fecha;
		mHora= hora;
		mLongInt =longint;
		mLatInt = latint;
	}
	public JGPS_Posicion (int id,double lat, double lon, String fecha, String hora, int longint, int latint, int cargador_id){
		mID = id;
		mLatitud = lat;
		mLongitud = lon;
		mFecha = fecha;
		mHora= hora;
		mLongInt =longint;
		mLatInt = latint;
		mCargador_id = cargador_id;
	}
	
	public JGPS_Posicion (int id,double lat, double lon, String fecha, String hora, int longint, int latint){
		mID = id;
		mLatitud = lat;
		mLongitud = lon;
		mFecha = fecha;
		mHora= hora;
		mLongInt =longint;
		mLatInt = latint;
		
	}
	
	public String getExportar(){
		String gps_posicion=String.format("gps_posicion#%s#%s#%s#%s#%s#%s#%s#%s", mID,mLatitud,mLongitud,mFecha,mHora,mLongInt,mLatInt,mCargador_id);
		return gps_posicion;
	}
}
