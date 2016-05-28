package com.example.etxeberria_vending.clases;

public class JCarga {
	private int mID;
	private int mRuta_id;
	private int mMaquina_id;
	private int mContador;
	private String mFecha;
	private int mEstado;
	private int mCargador_id;
	private String mHora_ini;
	private String mHora_fin;
	
	
	public int getID(){
		return mID;
	}
	public int getRuta_id(){
		return mRuta_id;
	}
	public int getMaquina_id(){
		return mMaquina_id;
	}
	public int getContador(){
		return mContador;
	}
	public String getFecha(){
		return mFecha;
	}
	public int getEstado(){
		return mEstado;
	}
	public String getHora_ini(){
		return mHora_ini;
	}
	public String getHora_fin(){
		return mHora_fin;
	}
	
	public String getExportar(){
		String linea=String.format("cargas#%s#%s#%s#%s#%s#%s#%s#%s#%s", mID,mRuta_id,mMaquina_id,mContador,mFecha,mEstado,mHora_ini,mHora_fin,mCargador_id);
		return linea;
	}
	//Constructor para exportar
			public JCarga(int id, int ruta_id, int maquina_id, int contador, String fecha, int estado, String hora_ini, String hora_fin,int cargador_id){
			mID = id;
			mRuta_id = ruta_id;
			mMaquina_id = maquina_id;
			mContador = contador;
			mFecha = fecha;
			mEstado = estado;
			mHora_ini = hora_ini;
			mHora_fin = hora_fin;
			mCargador_id= cargador_id;
			
			}
	//Constructor para leer de la bbdd
		public JCarga(int id, int ruta_id, int maquina_id, int contador, String fecha, int estado, String hora_ini, String hora_fin){
		mID = id;
		mRuta_id = ruta_id;
		mMaquina_id = maquina_id;
		mContador = contador;
		mFecha = fecha;
		mEstado = estado;
		mHora_ini= hora_ini;
		mHora_fin = hora_fin;

		
		}
		public JCarga(int ruta_id, int maquina_id, int contador, String fecha, int estado, String hora_ini, String hora_fin){
			mRuta_id = ruta_id;
			mMaquina_id = maquina_id;
			mContador = contador;
			mFecha = fecha;
			mEstado = estado;
			mHora_ini= hora_ini;
			mHora_fin = hora_fin;
			
			}

}
