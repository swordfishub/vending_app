package com.example.etxeberria_vending.clases;

public class JLinea_dia {
	private int mID;
	private int mCarga_id;
	private int mPosicion_id;
	private int mProducto_id;
	private int mUnidades;
	private int mProducto_retirado_id;
	private int mUnidades_retiradas;
	private String mMotivo;


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

	//Constructor para leer de la bbdd
	public JLinea_dia(int id, int carga_id, int posicion_id, int producto_id, int unidades, int producto_retirado_id,int unidades_retiradas, String motivo){
		mID = id;
		mCarga_id = carga_id;
		mPosicion_id = posicion_id;
		mProducto_id =producto_id;
		mUnidades = unidades;
		mProducto_retirado_id =producto_retirado_id;
		mUnidades_retiradas =unidades_retiradas;
		mMotivo= motivo;

	}
	//Constructor para leer de la bbdd
	public JLinea_dia(int carga_id, int posicion_id, int producto_id, int unidades, int producto_retirado_id,int unidades_retiradas, String motivo){

		mCarga_id = carga_id;
		mPosicion_id = posicion_id;
		mProducto_id =producto_id;
		mUnidades = unidades;
		mProducto_retirado_id =producto_retirado_id;
		mUnidades_retiradas =unidades_retiradas;
		mMotivo= motivo;

	}
}
