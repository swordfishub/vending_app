package com.example.etxeberria_vending.clases;

public class JLinea {
	private int mID;
	private int mCarga_id;
	private int mPosicion_id;
	private int mProducto_id;
	private int mUnidades;
	private int mProducto_retirado_id;
	private int mUnidades_retiradas;
	private String mMotivo;
	private String mFecha;
	private int mEstado;
	private int mPlaza_id;
	private int mPr_venta;
	private int mCargador_id;


	public int getID(){
		return mID;
	}
	public int getCarga_id(){
		return mCarga_id;
	}
	public int getPosicion_id(){
		return mPosicion_id;
	}
	public int getProducto_id(){
		return mProducto_id;
	}
	public int getUnidades(){
		return mUnidades;
	}
	public int getProducto_retirado_id(){
		return mProducto_retirado_id;
	}
	public int getUnidades_retiradas(){
		return mUnidades_retiradas;
	}
	public String getMotivo(){
		return mMotivo;
	}
	public String getFecha(){
		return mFecha;
	}
	public int getEstado(){
		return mEstado;
	}
	public int getPlaza_id(){
		return mPlaza_id;
	}
	public int getPr_venta(){
		return mPr_venta;
	}
	public String getExportar(){
		String linea=String.format("lineas#%s#%s#%s#%s#%s#%s#%s#%s#%s#%s#%s#%s#%s", mID,mCarga_id,mPosicion_id,mProducto_id,mUnidades,mProducto_retirado_id,mUnidades_retiradas,mMotivo,mFecha,mEstado,mPlaza_id,mPr_venta,mCargador_id);
		return linea;
	}

	//Constructor para exportar
		public JLinea(int id, int carga_id, int posicion_id, int producto_id, int unidades, int producto_retirado_id,int unidades_retiradas, String motivo, String fecha, int estado, int plaza_id, int pr_venta, int cargador_id){
			mID = id;
			mCarga_id = carga_id;
			mPosicion_id = posicion_id;
			mProducto_id =producto_id;
			mUnidades = unidades;
			mProducto_retirado_id =producto_retirado_id;
			mUnidades_retiradas =unidades_retiradas;
			mMotivo= motivo;
			mFecha = fecha;
			mEstado = estado;
			mPlaza_id = plaza_id;
			mPr_venta = pr_venta;
			mCargador_id = cargador_id;
					

		}
	//Constructor para leer de la bbdd
	public JLinea(int id, int carga_id, int posicion_id, int producto_id, int unidades, int producto_retirado_id,int unidades_retiradas, String motivo, String fecha, int estado, int plaza_id, int pr_venta){
		mID = id;
		mCarga_id = carga_id;
		mPosicion_id = posicion_id;
		mProducto_id =producto_id;
		mUnidades = unidades;
		mProducto_retirado_id =producto_retirado_id;
		mUnidades_retiradas =unidades_retiradas;
		mMotivo= motivo;
		mFecha = fecha;
		mEstado = estado;
		mPlaza_id = plaza_id;
		mPr_venta = pr_venta;
				

	}
	//Constructor para leer de la bbdd
	public JLinea(int carga_id, int posicion_id, int producto_id, int unidades, int producto_retirado_id,int unidades_retiradas, String motivo, String fecha, int estado, int plaza_id, int pr_venta){

		mCarga_id = carga_id;
		mPosicion_id = posicion_id;
		mProducto_id =producto_id;
		mUnidades = unidades;
		mProducto_retirado_id =producto_retirado_id;
		mUnidades_retiradas =unidades_retiradas;
		mMotivo= motivo;
		mFecha = fecha;
		mEstado = estado;
		mPlaza_id = plaza_id;
		mPr_venta = pr_venta;

	}
}
