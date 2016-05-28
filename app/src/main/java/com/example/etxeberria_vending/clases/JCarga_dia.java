package com.example.etxeberria_vending.clases;

public class JCarga_dia {
	private int mID;
	private int mRuta_id;
	private int mMaquina_id;
	private int mContador;
	
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
	//Constructor para leer de la bbdd
		public JCarga_dia(int id, int cargador_id, int maquina_id, int contador){
		mID = id;
		mRuta_id = cargador_id;
		mMaquina_id = maquina_id;
		mContador = contador;
		
		}
		public JCarga_dia(int cargador_id, int maquina_id, int contador){
			mRuta_id = cargador_id;
			mMaquina_id = maquina_id;
			mContador = contador;
			
			}

}


