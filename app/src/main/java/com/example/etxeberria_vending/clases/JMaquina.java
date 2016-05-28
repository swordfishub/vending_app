package com.example.etxeberria_vending.clases;

public class JMaquina {
	private int mID;
	private String mMaq_nombre;
	private String mMaq_tipo;
	private String mMaq_descripcion;
	private int mMaq_filas;
	private int mMaq_columnas;		
	private int mID_Plaza;
	private String mFecha;
	private int mCargador_id;


	public int getID(){
		return mID;
	}
	public String getMaq_nombre(){
		return mMaq_nombre;
	}

	public String getMaq_tipo(){
		return mMaq_tipo;
	}
	public String getMaq_descripcion(){
		return mMaq_descripcion;
	}
	public int getFilas(){
		return mMaq_filas;
	}
	public int getColumnas(){
		return mMaq_columnas;

	}public int getPlaza(){
		return mID_Plaza;
	}
	public String getFecha(){
		return mFecha;
	}

	public String getExportar(){
		String linea=String.format("maquinas#%s#%s#%s#%s#%s#%s#%s#%s#%s", mID,mMaq_nombre,mMaq_tipo,mMaq_descripcion,mMaq_filas,mMaq_columnas,mID_Plaza,mFecha,mCargador_id);
		return linea;
	}
	//Constructor para leer de la bbdd
	public JMaquina(int id, String nombre, String tipo, String descripcion, int filas, int columnas, int id_plaza, String fecha, int cargador_id){
		mID = id;
		mMaq_nombre = nombre;
		mMaq_tipo = tipo;
		mMaq_descripcion = descripcion;
		mMaq_filas = filas;
		mMaq_columnas = columnas;
		mID_Plaza = id_plaza;
		mFecha = fecha;
		mCargador_id = cargador_id;		
	}

	//Constructor para leer de la bbdd
	public JMaquina(int id, String nombre, String tipo, String descripcion, int filas, int columnas, int id_plaza, String fecha){
		mID = id;
		mMaq_nombre = nombre;
		mMaq_tipo = tipo;
		mMaq_descripcion = descripcion;
		mMaq_filas = filas;
		mMaq_columnas = columnas;
		mID_Plaza = id_plaza;
		mFecha = fecha;

	}
	public JMaquina(String nombre, String tipo, String descripcion,int filas, int columnas, int id_plaza, String fecha){
		mMaq_nombre = nombre;
		mMaq_tipo = tipo;
		mMaq_descripcion = descripcion;
		mMaq_filas = filas;
		mMaq_columnas = columnas;
		mID_Plaza = id_plaza;
		mFecha = fecha;

	}


}
