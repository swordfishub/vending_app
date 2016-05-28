package com.example.etxeberria_vending;

import java.util.ArrayList;

import com.example.etxeberria_vending.clases.JBD;
import com.example.etxeberria_vending.clases.JPlaza;
import com.example.etxeberria_vending.clases.JRuta_dia;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.ProgressDialog;
import android.content.pm.PackageInfo;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Mapa_Ruta_Activity extends FragmentActivity implements LocationProvider.LocationCallback {

	public static final String TAG = MapsActivity.class.getSimpleName();


	private LocationProvider mLocationProvider;
	private TextView mTVPlaceLat;
	private TextView mTVPlaceLon;
	private Marker mMyPosMarker;
	private GoogleMap mMap; // Might be null if Google Play services APK is not available.
	//Atributos donde dejamos la ubicación actual del dispositivo:
	private double mMyLat;
	private double mMyLon;
	private int mTipoVista = 0;
	private int[] TIPO_VISTAS = {GoogleMap.MAP_TYPE_NORMAL,GoogleMap.MAP_TYPE_HYBRID};
	private boolean mPosicionConseguida;
	private ProgressDialog mPD;


	private int id_ruta;
	private String ruta_fecha;
	private LatLng mPosicionPlaza;
	private JBD mBD;
	private ArrayList<JRuta_dia> mRuta_dia;
	private JPlaza mPlaza;
	private ArrayList<JPlaza> mPlazas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapa_ruta_layout);
		mPosicionPlaza=null;
		
		mTVPlaceLat = (TextView)findViewById(R.id.tvPlaceLat);
		mTVPlaceLon = (TextView)findViewById(R.id.tvPlaceLon);
		//Indicamos que aun no tenemos la ubicación del usuario:
		mPosicionConseguida=false;
		//((ImageView)findViewById(R.id.ivMyPos)).setImageResource(R.drawable.icon_mypos_desc);
		setUpMapIfNeeded();

		mLocationProvider = new LocationProvider(this, this);
		mMap.setMyLocationEnabled(true);
		//Al iniciar la app, damos 5 segundos de margen para ver si conseguimos la ubicación actual
		/*mPD = new ProgressDialog(this);
		mPD.setCancelable(true);
		mPD.setTitle("Calculando tu posición actual...");
		mPD.show();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (mPD.isShowing()) mPD.dismiss();
				if (!mPosicionConseguida)
					Toast.makeText(Mapa_Ruta_Activity.this, "Aun no conocemos tu posición. El botón MY POS se volverá verde cuando la obtengamos.", Toast.LENGTH_LONG).show();
			}
		}, 5000);*/

		//Si nos han pasado la localización de una plaza, la mostramos en el mapa:
		/*if (mPosicionPlaza!=null){
			MarcarPosicionPlaza(mPosicionPlaza.latitude,mPosicionPlaza.longitude);
		}*/
		try {
			//Comprobamos si recibimos id ruta:
			Bundle datos = getIntent().getExtras();
			id_ruta = datos.getInt("id_ruta");
			if(id_ruta>0){
			ruta_fecha = datos.getString("ruta_fecha");
			getActionBar().setTitle("ruta "+ruta_fecha);
			getActionBar().setIcon(R.drawable.icon_ruta);
			PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
			mBD = new JBD(Mapa_Ruta_Activity.this,getString(R.string.app_preferences),info.versionCode);
			String condicion = String.format(" and id_ruta='%s'", id_ruta);
			mRuta_dia = mBD.getRutas_dia(condicion);

			int i=0;
			while (i<mRuta_dia.size()){
				mPlaza =mBD.getPlaza(mRuta_dia.get(i).getID_plaza());
				String title =mPlaza.getPlaza_empresa(); 
				Double lat =mPlaza.getPlaza_latitud();
				Double lon =mPlaza.getPlaza_longitud();
				mPosicionPlaza = new LatLng(lat,lon);
				//mMyPosMarker = mMap.addMarker(new MarkerOptions().position(mPosicionPlaza).title(title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
				String patron = String.format("icon_mypos_%s", i+1);
				int resId = getResources().getIdentifier(patron, "drawable", getPackageName());
				mMyPosMarker = mMap.addMarker(new MarkerOptions().position(mPosicionPlaza).title(title).icon(BitmapDescriptorFactory.fromResource(resId)));
				i++;
			}
			}else{
				getActionBar().setTitle("Mis plazas");
				getActionBar().setIcon(R.drawable.icon_ruta);
				PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
				mBD = new JBD(Mapa_Ruta_Activity.this,getString(R.string.app_preferences),info.versionCode);
				String condicion = "";
				mPlazas = mBD.getPlazas(condicion);
				int i=0;
				while (i<mPlazas.size()){
					mPlaza =mBD.getPlaza(mPlazas.get(i).getID());
					String title =mPlaza.getPlaza_empresa(); 
					Double lat =mPlaza.getPlaza_latitud();
					Double lon =mPlaza.getPlaza_longitud();
					mPosicionPlaza = new LatLng(lat,lon);
					mMyPosMarker = mMap.addMarker(new MarkerOptions().position(mPosicionPlaza).title(title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
					i++;
				}
			}
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mPosicionPlaza, 12));
		} catch (Exception e) {
			Log.e(TAG, "onCreate (bundle)"+e.getMessage());
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
		mLocationProvider.connect();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mLocationProvider.disconnect();
	}

	/**
	 * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
	 * installed) and the map has not already been instantiated.. This will ensure that we only ever
	 * call {@link #setUpMap()} once when {@link #mMap} is not null.
	 * <p/>
	 * If it isn't installed {@link SupportMapFragment} (and
	 * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
	 * install/update the Google Play services APK on their device.
	 * <p/>
	 * A user can return to this FragmentActivity after following the prompt and correctly
	 * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
	 * have been completely destroyed during this process (it is likely that it would only be
	 * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
	 * method in {@link #onResume()} to guarantee that it will be called.
	 */
	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
					.getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				setUpMap();
			}
		}
	}

	//Aqui solo llegamos si mMap no es null. Añadimos el evento click sobre el mapa:
	private void setUpMap() {
		/*
			mMap.setOnMapClickListener(new OnMapClickListener() {
				@Override
				public void onMapClick(LatLng point) {
					NumberFormat f = NumberFormat.getInstance(Locale.ENGLISH);
					f.setMaximumFractionDigits(7);
					f.setMinimumFractionDigits(7);
					//Obtenemos las coordenadas y dirección:
					double lat = Double.valueOf(f.format(point.latitude));
					double lon = Double.valueOf(f.format(point.longitude));
					MarcarPosicion(lat, lon,false);
				}
			});
		 */
	}
	public void btnTipoMapa_Click(View view){
		mTipoVista++;
		if (mTipoVista>=TIPO_VISTAS.length) mTipoVista=0;
		mMap.setMapType(TIPO_VISTAS[mTipoVista]);
	}
	/*public void MarcarMiPosicion(){
		try {
			LatLng posicion = new LatLng(mMyLat,mMyLon);
			//Eliminamos el marker anterior:
			//if (mMyPosMarker!=null) mMyPosMarker.remove();
			//Añadimos el nuevo marker:
			mMyPosMarker = mMap.addMarker(new MarkerOptions()
					.position(posicion)
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
			//Indicamos los datos de la posición actual:
			mTVPlaceLat.setText(String.valueOf(mMyLat));
			mTVPlaceLon.setText(String.valueOf(mMyLon));
			mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicion, 13));
		}catch(Exception e){
			Log.e(TAG,"MarcarMiPosicion: "+e.getMessage());
		}

	}*/

	/*public void MarcarPosicionPlaza(double lat, double lon){
		try {
			LatLng posicion = new LatLng(lat,lon);
			Marker plazaMarker = mMap.addMarker(new MarkerOptions()
					.position(posicion)
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
			//Indicamos los datos de la posición actual:
			//mTVPlaceLat.setText(String.valueOf(lat));
			//mTVPlaceLon.setText(String.valueOf(lon));
			//mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posicion, 13));
		}catch(Exception e){
			Log.e(TAG,"MarcarPosicion: "+e.getMessage());
		}

	}*/
	/*public void btnMiPosicion(View view){
		if (mPosicionConseguida)
			MarcarMiPosicion();
		else
			Toast.makeText(this, "Aun no conocemos tu posición", Toast.LENGTH_SHORT).show();
	}*/

	public void handleNewLocation(Location location) {
		mMyLat = location.getLatitude();
		mMyLon = location.getLongitude();
		//Cambiamos el color del boton de mi posición a verde para indicar que ya tenemos la ubicación del usuario
		//((ImageView)findViewById(R.id.ivMyPos)).setImageResource(R.drawable.icon_mypos_ok);
		if (!mPosicionConseguida) {
			mPosicionConseguida=true;
		}
		//MarcarMiPosicion();
	}
}
