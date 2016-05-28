package com.example.etxeberria_vending.clases;

public class JMi_carga {
	private int mID;
	private int mPosicion_id;
	private int mCarga_unidades;
	private int mStock_inicial;
	private int mUnidades_retiradas;
	private int mProducto_retirado_id;
	private String mMotivo;
	private String mIncidencia;
	private String mDescripcion;
	
	public int getID(){
		return mID;
	}
	public int getPosicion_id(){
		return mPosicion_id;
	}
	public int getCarga_unidades(){
		return mCarga_unidades;
	}
	public int getStock_inicial(){
		return mStock_inicial;
	}
	public int getUnidades_retiradas(){
		return mUnidades_retiradas;
	}
	public int getProducto_retirado_id(){
		return mProducto_retirado_id;
	}
	public String getMotivo(){
		return mMotivo;
	}
	public String getIncidencia(){
		return mIncidencia;
	}
	public String getDescripcon(){
		return mDescripcion;
	}
	public JMi_carga(int id, int posicion_id, int carga_unidades, int stock_inicial, 
			int unidades_retiradas, int producto_retirado_id, String motivo, String incidencia, String descripcion){
		mID=id;
		mPosicion_id =posicion_id;
		mCarga_unidades =carga_unidades;
		mStock_inicial =stock_inicial;
		mUnidades_retiradas=unidades_retiradas;
		mProducto_retirado_id=producto_retirado_id;
		mMotivo=motivo;
		mIncidencia=incidencia;
		mDescripcion=descripcion;
		
	}
	public JMi_carga(int posicion_id, int carga_unidades, int stock_inicial, 
			int unidades_retiradas, int producto_retirado_id, String motivo, String incidencia, String descripcion){
		mPosicion_id =posicion_id;
		mCarga_unidades =carga_unidades;
		mStock_inicial =stock_inicial;
		mUnidades_retiradas=unidades_retiradas;
		mProducto_retirado_id=producto_retirado_id;
		mMotivo=motivo;
		mIncidencia=incidencia;
		mDescripcion=descripcion;
		
	}
	
}
