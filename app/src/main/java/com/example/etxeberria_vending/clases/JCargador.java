package com.example.etxeberria_vending.clases;

public class JCargador {
	private int mID;
	private String mCargador_nombre;
	private String mCargador_password;
	private String mCargador_email;
	private String mCargador_telf;
	
	public int getID(){
		return mID;
	}
	public String getCargador_nombre(){
		return mCargador_nombre;
	}
	
	public String getCargador_password(){
		return mCargador_password;
	}
	public String getCargador_email(){
		return mCargador_email;
	}
	public String getCargador_telf(){
		return mCargador_telf;
	}

	//Constructor para leer de la bbdd
		public JCargador(int id, String nombre, String password,String email, String telf){
			mID = id;
			mCargador_nombre = nombre;
			mCargador_password = password;
			mCargador_email = email;
			mCargador_telf = telf;
					}
		
		public JCargador(String nombre, String password,String email, String telf){
			mCargador_nombre = nombre;
			mCargador_password = password;
			mCargador_email = email;
			mCargador_telf = telf;
			
		}
		
		@Override
		public String toString() {
			return mCargador_nombre;
		}
}
