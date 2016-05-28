package com.example.etxeberria_vending.clases;

public class JPosicion {
	private int mID;
	private int mMaquina_id;
	private int mProducto_id;
	private String mPos_fila_columna;
	private int mPr_venta;
	private int mCapacidad;
	private int mUltimas;
	private String mFecha;
	private int mEstado;
	private int mCargador_id;
	
	public int getID(){
		return mID;
	}
	public int getMaquina_id(){
		return mMaquina_id;
	}
	public int getProducto_id(){
		return mProducto_id;
	}
	public String getPos_fila_columna(){
		return mPos_fila_columna;
	}
	public int getPr_venta(){
		return mPr_venta;
	}
	public int getCapacidad(){
		return mCapacidad;
	}
	public int getUltimas(){
		return mUltimas;
	}
	public String getFecha(){
		return mFecha;
	}
	public int getEstado(){
		return mEstado;
	}
	
	public String getExportar(){
		String linea=String.format("posiciones#%s#%s#%s#%s#%s#%s#%s#%s#%s#%s", mID,mMaquina_id,mProducto_id,mPos_fila_columna,mPr_venta,mCapacidad,mUltimas,mFecha,mEstado,mCargador_id);
		return linea;
	}
	
	//Constructor para exportar
	public JPosicion(int id, int maquina_id, int producto_id, String fila_columna, int pr_venta, int capacidad, int ultimas, String fecha, int estado, int cargador_id){
	mID = id;
	mMaquina_id = maquina_id;
	mProducto_id = producto_id;
	mPos_fila_columna = fila_columna;
	mPr_venta = pr_venta;
	mCapacidad = capacidad;
	mUltimas = ultimas;
	mFecha = fecha;
	mEstado = estado;
	mCargador_id = cargador_id;
	}
	
	//Constructor para leer de la bbdd
			public JPosicion(int id, int maquina_id, int producto_id, String fila_columna, int pr_venta, int capacidad, int ultimas, String fecha, int estado){
			mID = id;
			mMaquina_id = maquina_id;
			mProducto_id = producto_id;
			mPos_fila_columna = fila_columna;
			mPr_venta = pr_venta;
			mCapacidad = capacidad;
			mUltimas = ultimas;
			mFecha = fecha;
			mEstado = estado;
			}
			
			//Constructor para leer de la bbdd
			public JPosicion(int maquina_id, int producto_id, String fila_columna, int pr_venta, int capacidad, int ultimas, String fecha, int estado){
			
			mMaquina_id = maquina_id;
			mProducto_id = producto_id;
			mPos_fila_columna = fila_columna;
			mPr_venta = pr_venta;
			mCapacidad = capacidad;
			mUltimas = ultimas;
			mFecha = fecha;
			mEstado = estado;
			}

}
