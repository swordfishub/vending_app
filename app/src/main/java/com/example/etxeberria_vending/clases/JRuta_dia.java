package com.example.etxeberria_vending.clases;

public class JRuta_dia {

	private int mID;
	private int mID_ruta;
	private int mID_plaza;
	private String mHora;
	private String mFecha;
	private int mCargador_id;

	public int getID(){
		return mID;
	}
	public int getID_ruta(){
		return mID_ruta;
	}
	public int getID_plaza(){
		return mID_plaza;
	}
	public String getHora(){
		return mHora;
	}
	public String getFecha(){
		return mFecha;
	}
	public int getCargador_id(){
		return mCargador_id;
	}
	
	
	public String getExportar(){
		String linea=String.format("rutas_dia#%s#%s#%s#%s#%s#%s", mID,mID_ruta,mID_plaza,mHora,mFecha,mCargador_id);
		return linea;
	}
	//Constructor para exportar
		public JRuta_dia(int id, int id_ruta, int id_plaza, String ruta_hora, String ruta_fecha, int cargador_id){
			mID = id;
			mID_ruta = id_ruta;
			mID_plaza = id_plaza;
			mHora = ruta_hora;
			mFecha = ruta_fecha;
			mCargador_id= cargador_id;
		}

	//Constructor para leer de la bbdd
	public JRuta_dia(int id, int id_ruta, int id_plaza, String ruta_hora, String ruta_fecha){
		mID = id;
		mID_ruta = id_ruta;
		mID_plaza = id_plaza;
		mHora = ruta_hora;
		mFecha = ruta_fecha;
		

	}

	//Constructor para leer de la bbdd
	public JRuta_dia(int id_ruta, int id_plaza, String ruta_hora, String ruta_fecha){
		
		mID_ruta = id_ruta;
		mID_plaza = id_plaza;
		mHora = ruta_hora;
		mFecha = ruta_fecha;

	}
}
