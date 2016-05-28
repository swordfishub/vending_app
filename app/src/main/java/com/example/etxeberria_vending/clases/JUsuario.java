package com.example.etxeberria_vending.clases;

public class JUsuario {
	private int mCod;
	private String mNombre;
	private String mMail;
	private String mPass;
	private String mFoto;
	
	public int getCod(){
		return mCod;
	}
	public String getNombre(){
		return mNombre;
	}
	public String getMail(){
		return mMail;
	}
	public String getPass(){
		return mPass;
	}
	public String getFoto(){
		return mFoto;
	}
	
	public JUsuario(int cod, String nombre, String mail, String pass, String foto){
		mCod = cod;
		mNombre =nombre;
		mMail = mail;
		mPass = pass;
		mFoto = foto;
	}
	
	public JUsuario(String nombre, String mail, String pass){
		mNombre =nombre;
		mMail = mail;
		mPass = pass;
	}
}