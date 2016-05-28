package com.example.etxeberria_vending.clases;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.example.etxeberria_vending.track.JGPS_Posicion;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class JBD extends SQLiteOpenHelper {
	private Context mContext;
	private int DATABASE_VERSION;
	private String DATABASE_ASSETS_FILE;
	private String DATABASE_NAME;
	private String DATABASE_PATH;


	public JBD(Context context,String bdname, int version) {
		super(context, bdname, null, version);
		mContext = context;
		DATABASE_NAME = bdname;
		DATABASE_ASSETS_FILE = String.format("%s.sqlite",DATABASE_NAME);
		DATABASE_VERSION = version;

		//He leido q mejor así, por si cambia la ruta según el SDK de Android:
		DATABASE_PATH = mContext.getApplicationInfo().dataDir +"/databases/";
	}
	@Override
	public void onCreate(SQLiteDatabase db) {}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

	@Override
	public void onOpen(SQLiteDatabase db) {
		boolean copiarBD = false;
		int version = 0;
		try {
			//Obtenemos el numero de videos, mediante un nuevo cursor:
			String sql = "SELECT max(_id) as VERSION FROM versiones";
			Cursor c = db.rawQuery(sql,null);
			if (c.moveToFirst()) version = c.getInt(0);
			c.close();
			//Comprobamos las versiones:
			if (version<DATABASE_VERSION) copiarBD = true;
		}catch(Exception e){
			//Si entra por aqui, la bd está mal, o quizás ni siquiera estaba copiada aun (primera ejecución)
			copiarBD = true;
			Log.e("JItemsBD","onOpen: "+e.getMessage());
		}
		try {
			if (copiarBD) copyDB();
		} catch (Exception e) {
			Log.e("JItemsBD","onOpen-copiarBD: "+e.getMessage());
		}
		super.onOpen(db);
	}

	//Copia nuestra base de datos desde la carpeta assets a la carpeta de sistema
	private void copyDB() throws IOException{
		try {
			//Abrimos el fichero de base de datos como entrada
			InputStream myInput = mContext.getAssets().open(DATABASE_ASSETS_FILE);

			//Ruta a la base de datos vacía q vamos a crear
			String outFileName = DATABASE_PATH + DATABASE_NAME;

			//Abrimos la base de datos vacía como salida
			OutputStream myOutput = new FileOutputStream(outFileName);

			//Transferimos los bytes desde el fichero de entrada al de salida
			byte[] buffer = new byte[1024];
			int length;
			while ((length = myInput.read(buffer))>0) myOutput.write(buffer, 0, length);

			//Liberamos los streams
			myOutput.flush();
			myOutput.close();
			myInput.close();
		} catch (Exception e){
			Log.e("JItemsBD","copyDB: "+e.getMessage());
		}
	}
	// desde aqui JCarga_dia
	public ArrayList<JCarga_dia> getCargas_dia(String condicion){
		ArrayList<JCarga_dia> recordList = new ArrayList<JCarga_dia>();
		try {
			String selectQuery = String.format("SELECT * FROM cargas_dia where _id>=0 %s",condicion);

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					JCarga_dia Carga_dia = new JCarga_dia(cursor.getInt(0),cursor.getInt(1),cursor.getInt(2),cursor.getInt(3));
					recordList.add(Carga_dia);
				} while (cursor.moveToNext());
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","getCargas: "+e.getMessage());
		}
		return recordList;
	}

	public JCarga_dia getCarga_dia(int cod) {
		JCarga_dia Carga_dia=null;
		try {
			String selectQuery = "SELECT * FROM carga_dia where _id="+cod;

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				Carga_dia = new JCarga_dia(cursor.getInt(0),cursor.getInt(1),cursor.getInt(2),cursor.getInt(3));
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","getCarga: "+e.getMessage());
		}
		return Carga_dia;
	}

	public int addCarga_dia(JCarga_dia Carga_dia){
		int id_carga_dia=0;
		try {
			
			String selectQuery = String.format("insert into carga_dia(id_ruta_dia,id_maquina,contador) values ('%s','%s','%s')",
					Carga_dia.getRuta_id(),Carga_dia.getMaquina_id(),Carga_dia.getContador());
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			//Ahora obtengo el id de carga_dia, creado por la bbdd:
			selectQuery = String.format("Select max(_id) from carga_dia where id_maquina='%s'",Carga_dia.getMaquina_id());
			Cursor cursor = db.rawQuery(selectQuery, null);
			cursor = db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				id_carga_dia = cursor.getInt(0);
			}
			cursor.close();

			
			db.close();
		} catch (Exception e) {
			Log.e("JBD","addCarga_dia: "+e.getMessage());
		}
		return id_carga_dia;
	}
	public void updateCarga_dia(JCarga_dia Carga_dia){
		try {
			String selectQuery = String.format("update carga_dia set id_ruta_dia='%s',id_maquina='%s',contador='%s' where _id='%s'",
					Carga_dia.getRuta_id(),Carga_dia.getMaquina_id(),Carga_dia.getContador());
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","updateCarga_dia: "+e.getMessage());
		}
	}
	public void deleteCarga_dia(int id){
		try {
			String selectQuery = String.format("delete from carga_dia where _id='%s'",id);
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","deleteCarga_dia: "+e.getMessage());
		}
	}
	public void vaciarCargas_dia(){
		try {
			String selectQuery = "delete from carga_dia";
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","vaciarCargas_dia: "+e.getMessage());
		}
	}
	//hasta aqui
	// desde aqui JCarga
	public ArrayList<JCarga> getCargas(String condicion){
		ArrayList<JCarga> recordList = new ArrayList<JCarga>();
		try {
			String selectQuery = String.format("SELECT * FROM cargas where _id>=0 %s",condicion);

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					JCarga Carga = new JCarga(cursor.getInt(0),cursor.getInt(1),cursor.getInt(2),cursor.getInt(3), cursor.getString(4), cursor.getInt(5), cursor.getString(6),cursor.getString(7));
					recordList.add(Carga);
				} while (cursor.moveToNext());
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","getCargas: "+e.getMessage());
		}
		return recordList;
	}

	public JCarga getCarga(int cod) {
		JCarga Carga=null;
		try {
			String selectQuery = "SELECT * FROM cargas where _id="+cod;

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				Carga = new JCarga(cursor.getInt(0),cursor.getInt(1),cursor.getInt(2),cursor.getInt(3),cursor.getString(4),cursor.getInt(5),cursor.getString(6),cursor.getString(7));
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","getCarga: "+e.getMessage());
		}
		return Carga;
	}
	

	public int addCarga(JCarga Carga){
		int id_carga=0;
		try {
			
			String selectQuery = String.format("insert into cargas(id_ruta_dia,id_maquina,contador,fecha,estado, hora_ini, hora_fin) values ('%s','%s','%s','%s','%s','%s','%s')",
					Carga.getRuta_id(),Carga.getMaquina_id(),Carga.getContador(),Carga.getFecha(),Carga.getEstado(), Carga.getHora_ini(), Carga.getHora_fin());
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			//Ahora obtengo el id de carga, creado por la bbdd:
			selectQuery = String.format("Select max(_id) from cargas where id_maquina='%s'",Carga.getMaquina_id());
			Cursor cursor = db.rawQuery(selectQuery, null);
			cursor = db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				id_carga = cursor.getInt(0);
			}
			cursor.close();

			
			db.close();
		} catch (Exception e) {
			Log.e("JBD","addCarga: "+e.getMessage());
		}
		return id_carga;
	}
	public void updateCarga(JCarga Carga){
		try {
			String selectQuery = String.format("update cargas set id_ruta_dia='%s',id_maquina='%s',contador='%s', fecha='%s', estado='%s', hora_ini='%s', hora_fin='%s', where _id='%s'",
					Carga.getRuta_id(),Carga.getMaquina_id(),Carga.getContador(), Carga.getFecha(), Carga.getEstado(), Carga.getHora_ini(), Carga.getHora_fin() );
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","updateCarga: "+e.getMessage());
		}
	}
	public void finCarga(int contador, String hora, int id_carga){
		try {
			String selectQuery = String.format("update cargas set contador='%s', hora_fin='%s' where _id='%s'", contador, hora, id_carga);
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","finCarga: "+e.getMessage());
		}
	}
	public void deleteCarga(int id){
		try {
			String selectQuery = String.format("delete from cargas where _id='%s'",id);
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","deleteCarga: "+e.getMessage());
		}
	}
	public void vaciarCargas(){
		try {
			String selectQuery = "delete from cargas";
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","vaciarCargas: "+e.getMessage());
		}
	}
	//hasta aqui

	// desde aqui JCargador
	public ArrayList<JCargador> getCargadors(String condicion){
		ArrayList<JCargador> recordList = new ArrayList<JCargador>();
		try {
			String selectQuery = String.format("SELECT * FROM cargadores where _id>=0 %s",condicion);

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					JCargador Cargador = new JCargador(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4));
					recordList.add(Cargador);
				} while (cursor.moveToNext());
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","getCargadors: "+e.getMessage());
		}
		return recordList;
	}

	public JCargador getCargador(int cod) {
		JCargador Cargador=null;
		try {
			String selectQuery = "SELECT * FROM cargadores where _id="+cod;

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				Cargador = new JCargador(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3), cursor.getString(4));
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","getCargador: "+e.getMessage());
		}
		return Cargador;
	}

	public void addCargador(JCargador Cargador){
		try {
			String selectQuery = String.format("insert into cargadores(cargador_nombre,cargador_password,cargador_email, cargador_telf) values ('%s','%s','%s','%s')",
					Cargador.getCargador_nombre(),Cargador.getCargador_password(),Cargador.getCargador_email(), Cargador.getCargador_telf());
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","addCargador: "+e.getMessage());
		}
	}
	public void updateCargador(JCargador Cargador){
		try {
			String selectQuery = String.format("update cargadores set cargador_nombre='%s',cargador_password='%s',cargador_email='%s',cargador_telf='%s' where _id='%s'",
					Cargador.getCargador_nombre(),Cargador.getCargador_password(),Cargador.getCargador_email(),Cargador.getCargador_telf(),Cargador.getID());
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","updateCargador: "+e.getMessage());
		}
	}
	public void deleteCargador(int id){
		try {
			String selectQuery = String.format("delete from cargadores where _id='%s'",id);
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","deleteCargador: "+e.getMessage());
		}
	}
	public void vaciarCargadores(){
		try {
			String selectQuery = "delete from cargadores";
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","vaciarCargadores: "+e.getMessage());
		}
	}
	//hasta aqui

	// desde aqui JLinea
	public ArrayList<JLinea> getLineas(String condicion){
		ArrayList<JLinea> recordList = new ArrayList<JLinea>();
		try {
			String selectQuery = String.format("SELECT * FROM lineas where _id>=0 %s order by fecha desc",condicion);

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					JLinea Linea = new JLinea(cursor.getInt(0),cursor.getInt(1),cursor.getInt(2),cursor.getInt(3),cursor.getInt(4),cursor.getInt(5),cursor.getInt(6),cursor.getString(7),cursor.getString(8),cursor.getInt(9),cursor.getInt(10),cursor.getInt(11));
					recordList.add(Linea);
				} while (cursor.moveToNext());
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","getLinea_cargas: "+e.getMessage());
		}
		return recordList;
	}
	
	public JLinea getLinea(int cod) {
		JLinea Linea=null;
		try {
			String selectQuery = "SELECT * FROM lineas where _id="+cod;

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				Linea = new JLinea(cursor.getInt(0),cursor.getInt(1),cursor.getInt(2),cursor.getInt(3),cursor.getInt(4),cursor.getInt(5),cursor.getInt(6),cursor.getString(7),cursor.getString(8),cursor.getInt(9),cursor.getInt(10),cursor.getInt(11));
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","getLinea: "+e.getMessage());
		}
		return Linea;
	}
	//Tiene que comprobar si ya existe una linea creada con esta fecha y en esta posicion y producto hace update, sino la crea
		public void addLinea(JLinea Linea){
			int id_linea=0;
			try {
				SQLiteDatabase db = this.getReadableDatabase();
				//Comprobamos si ya existe en la bbdd una linea creada con esta fecha, en esta posicion y con eete producto
				String selectQuery=String.format("Select _id from lineas where fecha='%s' and id_posicion='%s' and id_producto='%s'",Linea.getFecha(), Linea.getPosicion_id(), Linea.getProducto_id());
				Cursor cursor = db.rawQuery(selectQuery, null);
				if (cursor.moveToFirst()) {
					id_linea = cursor.getInt(0);
				}
				cursor.close();
				if (id_linea==0){
					//Insertamos la linea:
					selectQuery = String.format("insert into lineas(id_carga_dia, id_posicion, id_producto, unidades, id_producto_retirado, unidades_retiradas, motivo, fecha, estado, id_plaza, pr_venta) values ('%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s')",
							Linea.getCarga_id(),Linea.getPosicion_id(),Linea.getProducto_id(),Linea.getUnidades(), Linea.getProducto_retirado_id(),Linea.getUnidades_retiradas(), Linea.getMotivo(), Linea.getFecha(), Linea.getEstado(), Linea.getPlaza_id(), Linea.getPr_venta());
					db.execSQL(selectQuery);
				
				}else{
					//hacemos update de la linea
					selectQuery = String.format("update lineas set id_carga_dia='%s',id_posicion='%s',id_producto='%s',unidades='%s',id_producto_retirado='%s',unidades_retiradas='%s',motivo='%s',fecha='%s',estado='%s',id_plaza='%s',pr_venta='%s' where _id='%s'",
							Linea.getCarga_id(),Linea.getPosicion_id(),Linea.getProducto_id(),Linea.getUnidades(),Linea.getProducto_retirado_id(),Linea.getUnidades_retiradas(), Linea.getMotivo(), Linea.getFecha(),Linea.getEstado(), Linea.getPlaza_id(),Linea.getPr_venta(), id_linea);
					db.execSQL(selectQuery);
				}
				db.close();
			} catch (Exception e) {
				Log.e("JBD","addRuta: "+e.getMessage());
			}
			
		}
	
	public void updateLinea(JLinea Linea){
		try {
			String selectQuery = String.format("update lineas set id_carga_dia='%s',id_posicion='%s',id_producto='%s',unidades='%s',id_producto_retirado='%s',unidades_retiradas='%s',motivo='%s',fecha='%s',estado='%s',id_plaza='%s',pr_venta='%s' where _id='%s'",
					Linea.getCarga_id(),Linea.getPosicion_id(),Linea.getProducto_id(),Linea.getUnidades(),Linea.getProducto_retirado_id(),Linea.getUnidades_retiradas(), Linea.getMotivo(), Linea.getFecha(),Linea.getEstado(), Linea.getPlaza_id(),Linea.getPr_venta(),Linea.getID());
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","updateLinea: "+e.getMessage());
		}
	}
	public void deleteLinea(int id){
		try {
			String selectQuery = String.format("delete from lineas where _id='%s'",id);
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","deleteLinea: "+e.getMessage());
		}
	}
	public void vaciarLineas(){
		try {
			String selectQuery = "delete from lineas";
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","vaciarLineas: "+e.getMessage());
		}
	}

	//hasta aqui

	// desde aqui JLinea_dia
	public ArrayList<JLinea_dia> getLineas_dia(String condicion){
		ArrayList<JLinea_dia> recordList = new ArrayList<JLinea_dia>();
		try {
			String selectQuery = String.format("SELECT * FROM lineas_dia where _id>=0 %s",condicion);

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					JLinea_dia Linea_dia = new JLinea_dia(cursor.getInt(0),cursor.getInt(1),cursor.getInt(2),cursor.getInt(3),cursor.getInt(4),cursor.getInt(5),cursor.getInt(6),cursor.getString(7));
					recordList.add(Linea_dia);
				} while (cursor.moveToNext());
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","getLinea_dia: "+e.getMessage());
		}
		return recordList;
	}

	public JLinea_dia getLinea_dia(int cod) {
		JLinea_dia Linea_dia=null;
		try {
			String selectQuery = "SELECT * FROM lineas_dia where _id="+cod;

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				Linea_dia = new JLinea_dia(cursor.getInt(0),cursor.getInt(1),cursor.getInt(2),cursor.getInt(3),cursor.getInt(4),cursor.getInt(5),cursor.getInt(6),cursor.getString(7));
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","getLinea_dia: "+e.getMessage());
		}
		return Linea_dia;
	}

	public void addLinea_dia(JLinea_dia Linea_dia){
		try {
			String selectQuery = String.format("insert into lineas_dia(id_carga_dia,id_posicion,id_producto,unidades, id_producto_retirado,unidades_retiradas,motivo) values ('%s','%s','%s','%s','%s','%s','%s')",
					Linea_dia.getCarga_id(),Linea_dia.getPosicion_id(),Linea_dia.getProducto_id(),Linea_dia.getUnidades(), Linea_dia.getProducto_retirado_id(),Linea_dia.getUnidades_retiradas(), Linea_dia.getMotivo());
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","addLinea_dia: "+e.getMessage());
		}
	}
	public void updateLinea_dia(JLinea_dia Linea_dia){
		try {
			String selectQuery = String.format("update lineas_dia set id_carga_dia='%s',id_posicion='%s',id_producto='%s',unidades='%s',id_producto_retirado='%s',unidades_retiradas='%s',,motivo='%s' where _id='%s'",
					Linea_dia.getCarga_id(),Linea_dia.getPosicion_id(),Linea_dia.getProducto_id(),Linea_dia.getUnidades(),Linea_dia.getProducto_retirado_id(),Linea_dia.getUnidades_retiradas(), Linea_dia.getMotivo(), Linea_dia.getID());
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","updateLinea_dia: "+e.getMessage());
		}
	}
	public void deleteLinea_dia(int id){
		try {
			String selectQuery = String.format("delete from lineas_dia where _id='%s'",id);
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","deleteLinea_dia: "+e.getMessage());
		}
	}
	
	public void deleteLinea_hoy(int id_posicion, String fecha){
		try {
			String selectQuery = String.format("delete from lineas where id_posicion='%s' and fecha='%s'",id_posicion,fecha);
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","deleteLinea: "+e.getMessage());
		}
	}
	
	public void vaciarLineas_dia(){
		try {
			String selectQuery = "delete from lineas_dia";
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","vaciarLineas_dia: "+e.getMessage());
		}
	}

	//hasta aqui

	// desde aqui JMaquina
	public ArrayList<JMaquina> getMaquinas(String condicion){
		ArrayList<JMaquina> recordList = new ArrayList<JMaquina>();
		try {
			String selectQuery = String.format("SELECT * FROM maquinas where _id>=0 %s",condicion);

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					JMaquina Maquina = new JMaquina(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getInt(4),cursor.getInt(5),cursor.getInt(6),cursor.getString(7));
					recordList.add(Maquina);
				} while (cursor.moveToNext());
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","getMaquinas: "+e.getMessage());
		}
		return recordList;
	}

	public JMaquina getMaquina(int cod) {
		JMaquina Maquina=null;
		try {
			String selectQuery = "SELECT * FROM maquinas where _id="+cod;

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				Maquina = new JMaquina(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6), cursor.getString(7));
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","getMaquina: "+e.getMessage());
		}
		return Maquina;
	}

	public void addMaquina(JMaquina Maquina){
		try {
			String selectQuery = String.format("insert into maquinas(_id,maq_nombre,maq_tipo,maq_descripcion,filas,columnas,id_plaza,fecha) values ('%s','%s','%s','%s','%s','%s','%s','%s')",
					Maquina.getID(),Maquina.getMaq_nombre(),Maquina.getMaq_tipo(),Maquina.getMaq_descripcion(), Maquina.getFilas(), Maquina.getColumnas(),Maquina.getPlaza(),Maquina.getFecha());
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","addMaquina: "+e.getMessage());
		}
	}
	public void updateMaquina(JMaquina Maquina){
		try {
			String selectQuery = String.format("update maquinas set maq_nombre='%s',maq_tipo='%s',maq_descripcion='%s',filas='%s', columnas='%s',id_plaza='%s',fecha='%s' where _id='%s'",
					Maquina.getMaq_nombre(),Maquina.getMaq_tipo(),Maquina.getMaq_descripcion(),Maquina.getPlaza(),Maquina.getFilas(), Maquina.getColumnas(), Maquina.getFecha(),Maquina.getID());
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","updateMaquina: "+e.getMessage());
		}
	}
	public void updateMaquina_fecha(String Fecha, int id_maquina){
		try {
			String selectQuery = String.format("update maquinas set fecha='%s' where _id='%s'",Fecha ,id_maquina);
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","updateMaquina: "+e.getMessage());
		}
	}
	public void deleteMaquina(int id){
		try {
			String selectQuery = String.format("delete from maquinas where _id='%s'",id);
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","deleteMaquina: "+e.getMessage());
		}
	}
	public void vaciarMaquinas(){
		try {
			String selectQuery = "delete from maquinas";
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","vaciarMaquinas: "+e.getMessage());
		}
	}
	//hasta aqui

	// desde aqui JMi_carga
	public ArrayList<JMi_carga> getMi_cargas(String condicion){
		ArrayList<JMi_carga> recordList = new ArrayList<JMi_carga>();
		try {
			String selectQuery = String.format("SELECT * FROM mi_carga where _id>=0 %s",condicion);

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					JMi_carga Mi_carga = new JMi_carga(cursor.getInt(0),cursor.getInt(1),cursor.getInt(2),cursor.getInt(3),cursor.getInt(4),cursor.getInt(5),cursor.getString(6),cursor.getString(7),cursor.getString(8));
					recordList.add(Mi_carga);
				} while (cursor.moveToNext());
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","getMi_cargas: "+e.getMessage());
		}
		return recordList;
	}

	public JMi_carga getMi_carga(int cod) {
		JMi_carga Mi_carga=null;
		try {
			String selectQuery = "SELECT * FROM mi_carga where _id="+cod;

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				Mi_carga = new JMi_carga(cursor.getInt(0),cursor.getInt(1),cursor.getInt(2),cursor.getInt(3),cursor.getInt(4),cursor.getInt(5),cursor.getString(6),cursor.getString(7),cursor.getString(8));
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","getMi_carga: "+e.getMessage());
		}
		return Mi_carga;
	}

	public void addMi_carga(JMi_carga Mi_carga){
		try {
			String selectQuery = String.format("insert into mi_carga(id_posicion,mi_carga_unidades,stock_inicial,unidades_retiradas,id_prod_retirado,motivo,incidencia,descripcion) values ('%s','%s','%s','%s','%s','%s','%s','%s')",
					Mi_carga.getPosicion_id(),Mi_carga.getCarga_unidades(),Mi_carga.getStock_inicial(), Mi_carga.getUnidades_retiradas(), Mi_carga.getProducto_retirado_id(), Mi_carga.getMotivo(), Mi_carga.getIncidencia(), Mi_carga.getDescripcon());
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","addMi_carga: "+e.getMessage());
		}
	}
	public void updateMi_carga(JMi_carga Mi_carga){
		try {
			String selectQuery = String.format("update mi_carga set id_posicion='%s',mi_carga_unidades='%s',stock_inicial='%s',unidades_retiradas='%s',id_prod_retirado='%s',motivo='%s',incidencia='%s' ,descripcion='%s' where _id='%s'",
					Mi_carga.getPosicion_id(),Mi_carga.getCarga_unidades(),Mi_carga.getStock_inicial(), Mi_carga.getUnidades_retiradas(), Mi_carga.getProducto_retirado_id(), Mi_carga.getMotivo(), Mi_carga.getIncidencia(), Mi_carga.getDescripcon(),Mi_carga.getID());
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","updateMi_carga: "+e.getMessage());
		}
	}
	public void deleteMi_carga(int id){
		try {
			String selectQuery = String.format("delete from mi_carga where _id='%s'",id);
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","deleteMi_carga: "+e.getMessage());
		}
	}
	public void vaciarMi_carga(){
		try {
			String selectQuery = "delete from mi_carga";
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","vaciarMi_carga: "+e.getMessage());
		}
	}

	//hasta aqui

	// desde aqui JPlaza
	public ArrayList<JPlaza> getPlazas(String condicion){
		ArrayList<JPlaza> recordList = new ArrayList<JPlaza>();
		try {
			String selectQuery = String.format("SELECT * FROM plazas where _id>=0 %s",condicion);

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					JPlaza Plaza = new JPlaza(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getDouble(5),cursor.getDouble(6),cursor.getInt(7),cursor.getString(8));
					recordList.add(Plaza);
				} while (cursor.moveToNext());
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","getPlazas: "+e.getMessage());
		}
		return recordList;
	}

	public JPlaza getPlaza(int cod) {
		JPlaza Plaza=null;
		try {
			String selectQuery = "SELECT * FROM plazas where _id="+cod;

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				Plaza = new JPlaza(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getDouble(5),cursor.getDouble(6),cursor.getInt(7),cursor.getString(8));
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","getPlaza: "+e.getMessage());
		}
		return Plaza;
	}

	public void addPlaza(JPlaza Plaza){
		try {
			String selectQuery = String.format("insert into plazas(plaza_empresa,plaza_direccion,plaza_poblacion, plaza_provincia, plaza_latitud,plaza_longitud,id_ruta, fecha) values ('%s','%s','%s','%s','%s','%s','%s','%s')",
					Plaza.getPlaza_empresa(),Plaza.getPlaza_direccion(),Plaza.getPlaza_poblacion(), Plaza.getPlaza_provincia(), Plaza.getPlaza_latitud(), Plaza.getPlaza_longitud(), Plaza.getPlaza_ruta_id(), Plaza.getFecha());
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","addPlaza: "+e.getMessage());
		}
	}
	public void updatePlaza(JPlaza Plaza){
		try {
			String selectQuery = String.format("update plazas set plaza_empresa='%s',plaza_direccion='%s',plaza_poblacion='%s',plaza_provincia='%s' ,plaza_latitud='%s',plaza_longitud='%s',id_ruta='%s',fecha='%s' where _id='%s'",
					Plaza.getPlaza_empresa(),Plaza.getPlaza_direccion(),Plaza.getPlaza_poblacion(), Plaza.getPlaza_provincia(), Plaza.getPlaza_latitud(), Plaza.getPlaza_longitud(), Plaza.getPlaza_ruta_id(),Plaza.getFecha(),Plaza.getID());
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","updatePlaza: "+e.getMessage());
		}
	}
	public void updatePlaza_fecha(String Fecha, int id_plaza){
		try {
			String selectQuery = String.format("update plazas set fecha='%s' where _id='%s'",Fecha ,id_plaza);
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","updatePlaza_fecha: "+e.getMessage());
		}
	}
	public void updatePlaza_GPS(double Lat, double Long, int id_plaza){
		try {
			String selectQuery = String.format("update plazas set plaza_latitud='%s', plaza_longitud='%s' where _id='%s'",Lat ,Long, id_plaza);
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","updatePlaza_GPS: "+e.getMessage());
		}
	}
	public void deletePlaza(int id){
		try {
			String selectQuery = String.format("delete from plazas where _id='%s'",id);
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","deletePlaza: "+e.getMessage());
		}
	}
	public void vaciarPlazas(){
		try {
			String selectQuery = "delete from plazas";
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","vaciarPlazas: "+e.getMessage());
		}
	}
	//hasta aqui
// 
	// desde aqui JPosicion
	public ArrayList<JPosicion> getPosicions(String condicion){
		ArrayList<JPosicion> recordList = new ArrayList<JPosicion>();
		try {
			String selectQuery = String.format("SELECT * FROM posiciones where _id>=0 %s",condicion);

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					JPosicion Posicion = new JPosicion(cursor.getInt(0),cursor.getInt(1),cursor.getInt(2),cursor.getString(3),cursor.getInt(4),cursor.getInt(5),cursor.getInt(6),cursor.getString(7), cursor.getInt(8));
					recordList.add(Posicion);
				} while (cursor.moveToNext());
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","getPosicions: "+e.getMessage());
		}
		return recordList;
	}
	
	

	public JPosicion getPosicion(int cod) {
		JPosicion Posicion=null;
		try {
			String selectQuery = "SELECT * FROM posiciones where _id="+cod;

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				Posicion = new JPosicion(cursor.getInt(0),cursor.getInt(1),cursor.getInt(2),cursor.getString(3), cursor.getInt(4), cursor.getInt(5), cursor.getInt(6),cursor.getString(7), cursor.getInt(8));
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","getPosicion: "+e.getMessage());
		}
		return Posicion;
	}

	public void addPosicion(JPosicion Posicion){
		try {
			String selectQuery = String.format("insert into posiciones(id_maquina,id_producto,pos_fila_columna, pos_prventa, pos_capacidad, ultimas, fecha, estado) values ('%s','%s','%s','%s','%s','%s','%s','%s')",
					Posicion.getMaquina_id(),Posicion.getProducto_id(),Posicion.getPos_fila_columna(), Posicion.getPr_venta(), Posicion.getCapacidad(), Posicion.getUltimas(), Posicion.getFecha(),Posicion.getEstado());
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","addPosicion: "+e.getMessage());
		}
	}
	public void updatePosicion(JPosicion Posicion){
		try {
			String selectQuery = String.format("update posiciones set id_maquina='%s',id_producto='%s',pos_fila_columna='%s',pos_prventa='%s' ,pos_capacidad='%s',ultimas='%s',fecha='%s',estado='%s' where _id='%s'",
					Posicion.getMaquina_id(),Posicion.getProducto_id(),Posicion.getPos_fila_columna(),Posicion.getPr_venta(), Posicion.getCapacidad(),Posicion.getFecha(),Posicion.getUltimas(),Posicion.getEstado(),Posicion.getID());
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","updatePosicion: "+e.getMessage());
		}
	}
	public void updatePosicionProducto(int posicion , int producto){
		try {
			String selectQuery = String.format("update posiciones set id_producto='%s' where _id='%s'",producto,posicion);
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","updatePosicionProducto: "+e.getMessage());
		}
	}
	public void updatePosicionPrecio(int posicion , int pr_venta){
		try {
			String selectQuery = String.format("update posiciones set pos_prventa='%s' where _id='%s'",pr_venta,posicion);
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","updatePosicionPrecio: "+e.getMessage());
		}
	}
	public void updatePosicionUltimas(int posicion , int ultimas){
		try {
			String selectQuery = String.format("update posiciones set ultimas='%s' where _id='%s'",ultimas,posicion);
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","updatePosicionUltimas: "+e.getMessage());
		}
	}
	public void updatePosicionFecha(int posicion , String fecha){
		try {
			String selectQuery = String.format("update posiciones set fecha='%s' where _id='%s'",fecha,posicion);
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","updatePosicionFecha: "+e.getMessage());
		}
	}
	public void deletePosicion(int id){
		try {
			String selectQuery = String.format("delete from posiciones where _id='%s'",id);
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","deletePosicion: "+e.getMessage());
		}
	}
	public void vaciarPosiciones(){
		try {
			String selectQuery = "delete from posiciones";
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","vaciarPosiciones: "+e.getMessage());
		}
	}
	//hasta aqui

	// desde aqui JProducto
	public ArrayList<JProducto> getProductos(String condicion){
		ArrayList<JProducto> recordList = new ArrayList<JProducto>();
		try {
			String selectQuery = String.format("SELECT * FROM productos where _id>=0 %s",condicion);

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					JProducto Producto = new JProducto(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getInt(3),cursor.getString(4));
					recordList.add(Producto);
				} while (cursor.moveToNext());
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","getProductos: "+e.getMessage());
		}
		return recordList;
	}
	public ArrayList<JProducto> getProductos2(String condicion){
		ArrayList<JProducto> recordList = new ArrayList<JProducto>();
		try {
			String selectQuery = String.format("SELECT * FROM productos where _id>=2 %s",condicion);

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					JProducto Producto = new JProducto(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getInt(3),cursor.getString(4));
					recordList.add(Producto);
				} while (cursor.moveToNext());
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","getProductos: "+e.getMessage());
		}
		return recordList;
	}
	public int getUnidadesFiltro(String condicion){
		int total=0;
		try {
			String selectQuery = condicion;

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
					total=cursor.getInt(0);
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","getProductos: "+e.getMessage());
		}
		return total;
	}


	public JProducto getProducto(int cod) {
		JProducto Producto=null;
		try {
			String selectQuery = "SELECT * FROM productos where _id="+cod;

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				Producto = new JProducto(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getInt(3),cursor.getString(4));
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","getProducto: "+e.getMessage());
		}
		return Producto;
	}

	public void addProducto(JProducto Producto){
		try {
			String selectQuery = String.format("insert into productos(prod_nombre,prod_tipo,prod_pack,fecha) values ('%s','%s','%s','%s')",
					Producto.getProd_nombre(),Producto.getProd_tipo(),Producto.getProd_pack(), Producto.getFecha());
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","addProducto: "+e.getMessage());
		}
	}
	public void updateProducto(JProducto Producto){
		try {
			String selectQuery = String.format("update productos set prod_nombre='%s',prod_tipo='%s',prod_pack='%s',fecha='%s' where _id='%s'",
					Producto.getProd_nombre(),Producto.getProd_tipo(),Producto.getProd_pack(),Producto.getFecha(),Producto.getID());
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","updateProducto: "+e.getMessage());
		}
	}
	//aqui aqui
	public void updateProductoFecha(String fecha, int id_producto){
		try {
			String selectQuery = String.format("update productos set fecha='%s' where _id='%s'",fecha ,id_producto);
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","updateMaquina: "+e.getMessage());
		}
	}
	public void deleteProducto(int id){
		try {
			String selectQuery = String.format("delete from productos where _id='%s'",id);
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","deleteProducto: "+e.getMessage());
		}
	}
	public void vaciarProductos(){
		try {
			String selectQuery = "delete from productos";
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","vaciarProductos: "+e.getMessage());
		}
	}
	
	public void vaciarProductos_inicial(String fecha){
		try {
			String selectQuery = String.format("delete from productos where fecha='%s'",fecha);
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","vaciarProductos: "+e.getMessage());
		}
	}

	//hasta aqui

	// desde aqui JRuta
	public ArrayList<JRuta> getRutas(String condicion){
		ArrayList<JRuta> recordList = new ArrayList<JRuta>();
		try {
			String selectQuery = String.format("SELECT * FROM rutas where _id>=0 %s order by fecha DESC",condicion);

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					JRuta Ruta = new JRuta(cursor.getInt(0),cursor.getInt(1),cursor.getString(2), cursor.getInt(3), cursor.getString(4), cursor.getFloat(5));
					recordList.add(Ruta);
				} while (cursor.moveToNext());
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","getRutas: "+e.getMessage());
		}
		return recordList;
	}

	public JRuta getRuta(int cod) {
		JRuta Ruta=null;
		try {
			String selectQuery = "SELECT * FROM rutas where _id="+cod;

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				Ruta = new JRuta(cursor.getInt(0),cursor.getInt(1),cursor.getString(2), cursor.getInt(3), cursor.getString(4),cursor.getFloat(5));
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","getRuta: "+e.getMessage());
		}
		return Ruta;
	}
	
	public int cuentaRutaPlazas(String fecha){
		
	int total=0;
	try {
		String selectQuery = String.format("SELECT COUNT(*)from ruta_dia where fecha='%s'",fecha);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			total = cursor.getInt(0);
		}
		db.close();
	} catch (Exception e) {
		Log.e("JBD","cuentaRutaPlazas: "+e.getMessage());
	}
	return total;
	
	
	
	}
	public int cuentaRutaLineas(String fecha){
		
		int total=0;
	try {
		String selectQuery = String.format("SELECT COUNT(*)from lineas where fecha='%s'",fecha);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			total = cursor.getInt(0);
		}
		db.close();
	} catch (Exception e) {
		Log.e("JBD","cuentaRutaPlazas: "+e.getMessage());
	}
	return total;
	
	
	
	}
	public int cuentaRutaProductos(String fecha){
		int total=0;
	
	try {
		
		String selectQuery = String.format("SELECT SUM(unidades) as total from lineas where fecha='%s'",fecha);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			total = cursor.getInt(0);
		}
		db.close();
	} catch (Exception e) {
		Log.e("JBD","cuentaRutaPlazas: "+e.getMessage());
	}
	return total;
	
		
	}
	public int cuentaProductos(String filtro){
		int total=0;
	
	try {
		
		String selectQuery = String.format(filtro);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			total = cursor.getInt(0);
		}
		db.close();
	} catch (Exception e) {
		Log.e("JBD","cuentaProductos: "+e.getMessage());
	}
	return total;
	
		
	}
	public int cuentaProductos_old(int id_producto){
		int total=0;
	
	try {
		
		String selectQuery = String.format("SELECT SUM(unidades) as total from lineas where id_producto='%s'",id_producto);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			total = cursor.getInt(0);
		}
		db.close();
	} catch (Exception e) {
		Log.e("JBD","cuentaProductos: "+e.getMessage());
	}
	return total;
	
		
	}
	public int cuentaProductos_old2(int id_producto, String condicion){
		int total=0;
	
	try {
		
		String selectQuery = String.format("SELECT SUM(unidades) as total from lineas where id_producto='%s' %s",id_producto, condicion);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			total = cursor.getInt(0);
		}
		db.close();
	} catch (Exception e) {
		Log.e("JBD","cuentaProductos: "+e.getMessage());
	}
	return total;
	
		
	}
	public int cuentaProductos_fecha(int id_producto, String condicion){
		int total=0;
	
	try {
		
		String selectQuery = String.format("SELECT SUM(unidades) as total from lineas where id_producto='%s' %s",id_producto, condicion);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			total = cursor.getInt(0);
		}
		db.close();
	} catch (Exception e) {
		Log.e("JBD","cuentaProductos: "+e.getMessage());
	}
	return total;
	
		
	}
	
	//Tiene que comprobar si ya existe una ruta creada con esta fecha, sino, la crea y devuelve el id:
	public int addRuta(JRuta Ruta){
		int id_ruta=0;
		try {
			SQLiteDatabase db = this.getReadableDatabase();
			//Comprobamos si ya existe en la bbdd una ruta creada con la fecha indicada
			String selectQuery=String.format("Select _id from rutas where fecha='%s'",Ruta.getRuta_fecha());
			Cursor cursor = db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				id_ruta = cursor.getInt(0);
			}
			cursor.close();
			if (id_ruta==0){
				//Insertamos la ruta:
				selectQuery = String.format("insert into rutas(id_cargador, fecha, estado) values ('%s','%s','%s')",
						Ruta.getID_cargador(),Ruta.getRuta_fecha(),0);
				db.execSQL(selectQuery);
				//Ahora obtengo el id de ruta, creado por la bbdd:
				selectQuery = String.format("Select max(_id) from rutas where fecha='%s'",Ruta.getRuta_fecha());
				cursor = db.rawQuery(selectQuery, null);
				if (cursor.moveToFirst()) {
					id_ruta = cursor.getInt(0);
				}
				cursor.close();
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","addRuta: "+e.getMessage());
		}
		return id_ruta;
	}
	public void updateRuta(JRuta Ruta){
		try {
			String selectQuery = String.format("update rutas set id_cargador='%s',fecha='%s' where _id='%s'",
					Ruta.getID_cargador(),Ruta.getRuta_fecha(),Ruta.getID());
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","updateRuta: "+e.getMessage());
		}
	}
		public void updateRuta_Estado(int id_ruta){
			try {
				String selectQuery = String.format("update rutas set estado='%s' where _id='%s'",
						id_ruta);
				SQLiteDatabase db = this.getReadableDatabase();
				db.execSQL(selectQuery);
				db.close();
			} catch (Exception e) {
				Log.e("JBD","updateRuta_Estado: "+e.getMessage());
			}
	}
	public void deleteRuta(int id){
		try {
			String selectQuery = String.format("delete from rutas where _id='%s'",id);
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","deleteRuta: "+e.getMessage());
		}
	}
	public void vaciarRutas(){
		try {
			String selectQuery = "delete from rutas";
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","vaciarRutas: "+e.getMessage());
		}
	}
	//hasta aqui
	// desde aqui JRuta_dia
	public ArrayList<JRuta_dia> getRutas_dia(String condicion){
		ArrayList<JRuta_dia> recordList = new ArrayList<JRuta_dia>();
		try {
			String selectQuery = String.format("SELECT * FROM ruta_dia where _id>=0 %s",condicion);

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					JRuta_dia Ruta_dia = new JRuta_dia(cursor.getInt(0),cursor.getInt(1),cursor.getInt(2),cursor.getString(3),cursor.getString(4));
					recordList.add(Ruta_dia);
				} while (cursor.moveToNext());
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","getRutas_dia: "+e.getMessage());
		}
		return recordList;
	}

	public JRuta_dia getRuta_dia(int cod) {
		JRuta_dia Ruta_dia=null;
		try {
			String selectQuery = "SELECT * FROM ruta_dia where _id="+cod;

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				Ruta_dia = new JRuta_dia(cursor.getInt(0),cursor.getInt(1),cursor.getInt(2),cursor.getString(3),cursor.getString(4));
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","getRuta_dia: "+e.getMessage());
		}
		return Ruta_dia;
	}
	
	
	//Tiene que comprobar si ya existe una ruta_dia creada con esta plaza,
	//sino, la crea y devuelve el id: 
		public int addRuta_dia(JRuta_dia Ruta_dia){
			int id_ruta_dia =0;
			try {
				SQLiteDatabase db = this.getReadableDatabase();
				//Comprobamos si ya existe en la bbdd una ruta_dia creada con la plaza indicada
				String selectQuery=String.format("Select _id from ruta_dia where id_plaza='%s' and fecha ='%s'",Ruta_dia.getID_plaza(), Ruta_dia.getFecha());
				Cursor cursor = db.rawQuery(selectQuery, null);
				if (cursor.moveToFirst()) {
					id_ruta_dia = cursor.getInt(0);
				}
				cursor.close();
				if (id_ruta_dia==0){
				//Insertamos la ruta_dia:
				selectQuery = String.format("insert into ruta_dia(id_ruta, id_plaza, hora, fecha) values ('%s','%s','%s','%s')",
						Ruta_dia.getID_ruta(),Ruta_dia.getID_plaza(),Ruta_dia.getHora(),Ruta_dia.getFecha());
				db.execSQL(selectQuery);
				//Ahora obtengo el id de ruta_dia, creado por la bbdd:
				selectQuery = String.format("Select max(_id) from ruta_dia where id_plaza ='%s' and fecha ='%s'" ,Ruta_dia.getID_plaza(), Ruta_dia.getFecha());
				cursor = db.rawQuery(selectQuery, null);
					if (cursor.moveToFirst()) {
						id_ruta_dia = cursor.getInt(0);
					}
					cursor.close();	
				}
			db.close();
			} catch (Exception e) {
				Log.e("JBD","addRuta_dia: "+e.getMessage());
			}
			return id_ruta_dia;
		}
	public void addRuta_diaOLD(JRuta_dia Ruta_dia){
		try {
			String selectQuery = String.format("insert into ruta_dia(id_ruta, id_plaza, hora) values ('%s','%s','%s')",
					Ruta_dia.getID_ruta(),Ruta_dia.getID_plaza(),Ruta_dia.getHora());
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","addRuta_dia: "+e.getMessage());
		}
	}
	public void updateRuta_dia(JRuta_dia Ruta_dia){
		try {
			String selectQuery = String.format("update ruta_dia set id_ruta='%s',id_plaza='%s',hora='%s', fecha='%s', where _id='%s'",
					Ruta_dia.getID_ruta(),Ruta_dia.getID_plaza(),Ruta_dia.getHora(),Ruta_dia.getFecha(),Ruta_dia.getID());
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","updateRuta_dia: "+e.getMessage());
		}
	}
	public void deleteRuta_dia(int id){
		try {
			String selectQuery = String.format("delete from ruta_dia where _id='%s'",id);
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","deleteRuta: "+e.getMessage());
		}
	}
	public void vaciarRutas_dia(){
		try {
			String selectQuery = "delete from ruta_dia";
			SQLiteDatabase db = this.getReadableDatabase();
			db.execSQL(selectQuery);
			db.close();
		} catch (Exception e) {
			Log.e("JBD","vaciarRutas_dia: "+e.getMessage());
		}
	}
	//hasta aqui
	
	public ArrayList<JCarga> exportarCargas(String fecha, int cargador_id) {
		ArrayList<JCarga> lista = new ArrayList<JCarga>();
		try {
			String selectQuery = String.format("SELECT * FROM cargas where fecha='%s'",fecha);

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				do {
					JCarga item = new JCarga(cursor.getInt(0),cursor.getInt(1),cursor.getInt(2),cursor.getInt(3),cursor.getString(4),cursor.getInt(5),cursor.getString(6),cursor.getString(7), cargador_id);
					lista.add(item);
				} while (cursor.moveToNext());
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","getRuta_dia: "+e.getMessage());
		}
		return lista;
	}
	public ArrayList<JLinea> exportarLineas(String fecha, int cargador_id) {
		ArrayList<JLinea> lista = new ArrayList<JLinea>();
		try {
			String selectQuery = String.format("SELECT * FROM lineas where fecha='%s'",fecha);

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				do {
					JLinea item = new JLinea(cursor.getInt(0),cursor.getInt(1),cursor.getInt(2),cursor.getInt(3),cursor.getInt(4),cursor.getInt(5),cursor.getInt(6),cursor.getString(7),cursor.getString(8),cursor.getInt(9),cursor.getInt(10),cursor.getInt(11),cargador_id);
					lista.add(item);
				} while (cursor.moveToNext());
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","exportarLineas: "+e.getMessage());
		}
		return lista;
	}
	public ArrayList<JPosicion> exportarPosicion(String fecha, int cargador_id) {
		ArrayList<JPosicion> lista = new ArrayList<JPosicion>();
		try {
			String selectQuery = String.format("SELECT * FROM posiciones where fecha='%s'",fecha);

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				do {
					JPosicion item = new JPosicion(cursor.getInt(0),cursor.getInt(1),cursor.getInt(2),cursor.getString(3),cursor.getInt(4),cursor.getInt(5),cursor.getInt(6),cursor.getString(7),cursor.getInt(8),cargador_id);
					lista.add(item);
				} while (cursor.moveToNext());
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","exportarPosicion: "+e.getMessage());
		}
		return lista;
	}
	
	public ArrayList<JPlaza> exportarPlazas(String fecha, int cargador_id) {
		ArrayList<JPlaza> lista = new ArrayList<JPlaza>();
		try {
			String selectQuery = String.format("SELECT * FROM plazas where fecha='%s'",fecha);

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				do {
					JPlaza item = new JPlaza(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getFloat(5),cursor.getFloat(6),cursor.getInt(7),cursor.getString(8),cargador_id);
					lista.add(item);
				} while (cursor.moveToNext());
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","exportarPosicion: "+e.getMessage());
		}
		return lista;
	}
	
	public ArrayList<JMaquina> exportarMaquinas(String fecha, int cargador_id) {
		ArrayList<JMaquina> lista = new ArrayList<JMaquina>();
		try {
			String selectQuery = String.format("SELECT * FROM maquinas where fecha='%s'",fecha);

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				do {
					JMaquina item = new JMaquina(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getInt(4),cursor.getInt(5),cursor.getInt(6),cursor.getString(7),cargador_id);
					lista.add(item);
				} while (cursor.moveToNext());
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","exportarPosicion: "+e.getMessage());
		}
		return lista;
	}
	
	public ArrayList<JRuta_dia> exportarRutas_dia(String fecha, int cargador_id) {
		ArrayList<JRuta_dia> lista = new ArrayList<JRuta_dia>();
		try {
			String selectQuery = String.format("SELECT * FROM ruta_dia where fecha='%s'",fecha);

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				do {
					JRuta_dia item = new JRuta_dia(cursor.getInt(0),cursor.getInt(1),cursor.getInt(2),cursor.getString(3),cursor.getString(4),cargador_id);
					lista.add(item);
				} while (cursor.moveToNext());
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","exportarPosicion: "+e.getMessage());
		}
		return lista;
	}
	
	public ArrayList<JRuta> exportarRutas(String fecha, int cargador_id) {
		ArrayList<JRuta> lista = new ArrayList<JRuta>();
		try {
			String selectQuery = String.format("SELECT * FROM rutas where fecha='%s'",fecha);

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				do {
					JRuta item = new JRuta(cursor.getInt(0),cursor.getInt(1),cursor.getString(2),cargador_id,cursor.getString(4),cursor.getFloat(5));
					lista.add(item);
				} while (cursor.moveToNext());
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","exportarRutas: "+e.getMessage());
		}
		return lista;
	}
	
	//Funciones para trackear la pos gps
	
	public int addGPS_Posicion(JGPS_Posicion gps_posicion){
		int _id = 0;
		try {
			SQLiteDatabase db = this.getReadableDatabase();
			String sql = String.format("insert into posiciones_gps (p_lat,p_lon,fecha,hora) values ('%s','%s','%s','%s')",
					gps_posicion.getLatitud(),gps_posicion.getLongitud(), gps_posicion.getFecha(), gps_posicion.getHora());
			db.execSQL(sql);
			db.close();
		}catch (Exception e){
			Log.e("JBD","addGPS_Posicion: "+e.getMessage());
		}
		return _id;
	}
	public ArrayList<JGPS_Posicion> getPosiciones(String condicion){
		ArrayList<JGPS_Posicion> recordList = new ArrayList<JGPS_Posicion>();
		try {
			String selectQuery = String.format("SELECT * FROM posiciones_gps where _id>0 %s",condicion);

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					JGPS_Posicion gps_posicion = new JGPS_Posicion(cursor.getInt(0), cursor.getDouble(1), cursor.getDouble(2), cursor.getString(3), cursor.getString(4), cursor.getInt(5),cursor.getInt(6));
					recordList.add(gps_posicion);
				} while (cursor.moveToNext());
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","getPosiciones: "+e.getMessage());
		}
		return recordList;
	}
	public ArrayList<JGPS_Posicion> exportarPosicionesGPS(String condicion, int cargador_id){
		ArrayList<JGPS_Posicion> recordList = new ArrayList<JGPS_Posicion>();
		try {
			String selectQuery = String.format("SELECT * FROM posiciones_gps where _id>0 %s",condicion);

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				do {
					JGPS_Posicion gps_posicion = new JGPS_Posicion(cursor.getInt(0), cursor.getDouble(1), cursor.getDouble(2),cursor.getString(3),cursor.getString(4), cursor.getInt(5),cursor.getInt(6), cargador_id);
					recordList.add(gps_posicion);
				} while (cursor.moveToNext());
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","getPosiciones: "+e.getMessage());
		}
		return recordList;
	}

	public JGPS_Posicion getUltimaPosicion(){
		JGPS_Posicion p = null;
		try {
			String selectQuery = String.format("SELECT * FROM posiciones_gps order by _id Desc limit 1");

			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(selectQuery, null);

			if (cursor.moveToFirst()) {
				p = new JGPS_Posicion(cursor.getInt(0), cursor.getDouble(1), cursor.getDouble(2), cursor.getString(3), cursor.getString(4), cursor.getInt(5),cursor.getInt(6));
			}
			db.close();
		} catch (Exception e) {
			Log.e("JBD","getUltimaPosicion: "+e.getMessage());
		}
		return p;
	}

	public void borrarTodo(){
		try {
			SQLiteDatabase db = this.getReadableDatabase();
			String sql = "delete from posiciones_gps";
			db.execSQL(sql);
			sql = "vacuum";
			db.execSQL(sql);
			db.close();
		}catch (Exception e){
			Log.e("JBD","removeAll: "+e.getMessage());
		}
	}
	
	
}
