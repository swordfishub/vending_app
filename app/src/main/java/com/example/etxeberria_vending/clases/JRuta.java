package com.example.etxeberria_vending.clases;

public class JRuta {
	
	private int mID;
	private int mID_cargador;
	private String mRuta_fecha;
	private int mCargador_id;
	private int mEstado;
	private String mRuta_nombre;
	private float mDistancia;
		
	public int getID(){
		return mID;
	}
	public int getID_cargador(){
		return mID_cargador;
	}
	public String getRuta_fecha(){
		return mRuta_fecha;
	}
	
	public int getEstado(){
		return mEstado;
	}
	public String getRuta_nombre(){
		return mRuta_nombre;
	}
	public float getDistancia(){
		return mDistancia;
	}
	
	public String getExportar(){
		String linea=String.format("rutas#%s#%s#%s#%s#%s#%s#%s", mID,mID_cargador,mRuta_fecha,mCargador_id,mEstado, mRuta_nombre,mDistancia);
		return linea;
	}
	//Constructor para exportar
		public JRuta(int id, int id_cargador, String ruta_fecha, int cargador_id, int estado, String ruta_nombre, float distancia){
		mID = id;
		mID_cargador = id_cargador;
		mRuta_fecha = ruta_fecha;
		mCargador_id= cargador_id;
		mEstado = estado;
		mRuta_nombre = ruta_nombre;
		mDistancia = distancia;
		
		
		}
	
	
	//Constructor para leer de la bbdd
	public JRuta(int id, int id_cargador, String ruta_fecha, int estado, String ruta_nombre, float distancia){
	mID = id;
	mID_cargador = id_cargador;
	mRuta_fecha = ruta_fecha;
	mEstado = estado;
	mRuta_nombre = ruta_nombre;
	mDistancia = distancia;
	
	}
	
	//Constructor para leer de la bbdd
	public JRuta(int id_cargador,String ruta_fecha, int estado, String ruta_nombre, float distancia){
		
		mID_cargador = id_cargador;
		mRuta_fecha = ruta_fecha;
		mEstado = estado;
		mRuta_nombre = ruta_nombre;
		mDistancia = distancia;
		
		
		}
}
