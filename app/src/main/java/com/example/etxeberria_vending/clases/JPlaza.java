package com.example.etxeberria_vending.clases;

public class JPlaza {
	
	private int mID;
	private String mPlaza_empresa;
	private String mPlaza_direccion;
	private String mPlaza_poblacion;
	private String mPlaza_provincia;
	private double mPlaza_latitud;
	private double mPlaza_longitud;
	private int mPlaza_ruta_id;
	private String mFecha;
	private int mCargador_id;
	
	public int getID(){
		return mID;
	}
	public String getPlaza_empresa(){
		return mPlaza_empresa;
	}
	
	public String getPlaza_direccion(){
		return mPlaza_direccion;
	}
	public String getPlaza_poblacion(){
		return mPlaza_poblacion;
	}
	public String getPlaza_provincia(){
		return mPlaza_provincia;
	}
	public double getPlaza_latitud(){
		return mPlaza_latitud;
	}
	public double getPlaza_longitud(){
		return mPlaza_longitud;
	}
	public int getPlaza_ruta_id(){
		return mPlaza_ruta_id;
	}
	public String getFecha(){
		return mFecha;
	}
	public String getExportar(){
		String linea=String.format("plazas#%s#%s#%s#%s#%s#%s#%s#%s#%s#%s", mID,mPlaza_empresa,mPlaza_direccion,mPlaza_poblacion,mPlaza_provincia,mPlaza_latitud,mPlaza_longitud,mPlaza_ruta_id,mFecha,mCargador_id);
		return linea;
	}
	
	//Constructor para leer de la bbdd
		public JPlaza(int id, String empresa, String direccion, String poblacion, String provincia, double latitud, double longitud, int ruta_id, String fecha, int cargador_id){
		mID = id;
		mPlaza_empresa = empresa;
		mPlaza_direccion = direccion;
		mPlaza_poblacion = poblacion;
		mPlaza_provincia = provincia;
		mPlaza_latitud = latitud;
		mPlaza_longitud = longitud;
		mPlaza_ruta_id = ruta_id;
		mFecha = fecha;
		mCargador_id = cargador_id;
				
		}
		public JPlaza(String empresa, String direccion, String poblacion, String provincia, double latitud, double longitud, int ruta_id, String fecha){
			mPlaza_empresa = empresa;
			mPlaza_direccion = direccion;
			mPlaza_poblacion = poblacion;
			mPlaza_provincia = provincia;
			mPlaza_latitud = latitud;
			mPlaza_longitud = longitud;
			mPlaza_ruta_id = ruta_id;
			mFecha = fecha;
					
			}
		//Constructor para exportar
				public JPlaza(int id, String empresa, String direccion, String poblacion, String provincia, double latitud, double longitud, int ruta_id, String fecha){
				mID = id;
				mPlaza_empresa = empresa;
				mPlaza_direccion = direccion;
				mPlaza_poblacion = poblacion;
				mPlaza_provincia = provincia;
				mPlaza_latitud = latitud;
				mPlaza_longitud = longitud;
				mPlaza_ruta_id = ruta_id;
				mFecha = fecha;
						
				}

}
