package com.example.etxeberria_vending.clases;

public class JProducto {

	private int mID;
	private String mProd_nombre;
	private String mProd_tipo;
	private int mProd_pack;
	private String mFecha;
	private int mCargador_id;
	
	public int getID(){
		return mID;
	}
	public String getProd_nombre(){
		return mProd_nombre;
	}
	public String getProd_tipo(){
		return mProd_tipo;
	}
	public int getProd_pack(){
		return mProd_pack;
	}
	public String getFecha(){
		return mFecha;
	}
	public String getExportar(){
		String linea=String.format("productos#%s#%s#%s#%s#%s#%s", mID,mProd_nombre,mProd_tipo,mProd_pack,mFecha,mCargador_id);
		return linea;
	}
	//Constructor para exportar
			public JProducto(int id, String nombre, String tipo, int pack, String fecha, int cargador_id ){
			mID = id;
			mProd_nombre = nombre;
			mProd_tipo = tipo;
			mProd_pack = pack;
			mFecha = fecha;
			mCargador_id = cargador_id;
			
			}
	
	//Constructor para leer de la bbdd
	public JProducto(int id, String nombre, String tipo, int pack, String fecha ){
	mID = id;
	mProd_nombre = nombre;
	mProd_tipo = tipo;
	mProd_pack = pack;
	mFecha = fecha;
	
	}
	
	
	public JProducto(String nombre, String tipo, int pack, String fecha ){
		
		mProd_nombre = nombre;
		mProd_tipo = tipo;
		mProd_pack = pack;
		mFecha = fecha;
		}
}
