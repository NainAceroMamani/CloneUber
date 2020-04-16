package com.nain.cloneuber.activities.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.gson.JsonObject;
import com.nain.cloneuber.R;
import com.nain.cloneuber.includes.MyToolbar;
import com.nain.cloneuber.providers.GoogleApiProvider;
import com.nain.cloneuber.utils.DecodePoints;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailRequestActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;

    private double mExtraOriginLat;
    private double mExtraOriginLng;
    private double mExtraDestinationLat;
    private double mExtraDestinationLng;
    private String mExtraOrigin;
    private String mExtraDestination;

    // para inicio y final
    private LatLng mOrigenLatLong;
    private LatLng mDestinationLatLong;

    private GoogleApiProvider mGoogleApiProvider;

    // para decodificar
    private List<LatLng> mPolylineList;
    private PolylineOptions mPolygonOptions;

    private TextView mTextViewOrigin,mTextViewDestination,mTextViewTime,mTextViewDistance;

    private Button mbtnRequestNow;

    private CircleImageView mCircleImageBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_request);
        // MyToolbar.show(this, "TUS DATOS", true);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this); //  es ese fragmento cargamos el mapa de google

        mExtraOriginLat = getIntent().getDoubleExtra("origin_lat", 0);
        mExtraOriginLng = getIntent().getDoubleExtra("origin_lng", 0);
        mExtraDestinationLat = getIntent().getDoubleExtra("destino_lat", 0);
        mExtraDestinationLng = getIntent().getDoubleExtra("destino_lng", 0);
        mExtraOrigin = getIntent().getStringExtra("origin");
        mExtraDestination = getIntent().getStringExtra("destination");

        mOrigenLatLong = new LatLng(mExtraOriginLat, mExtraOriginLng);
        mDestinationLatLong = new LatLng(mExtraDestinationLat, mExtraDestinationLng);

        mGoogleApiProvider = new GoogleApiProvider(DetailRequestActivity.this);

        mTextViewOrigin = findViewById(R.id.textViewOrigen);
        mTextViewDestination = findViewById(R.id.textViewDestination);
        mTextViewTime = findViewById(R.id.textViewTime);
        mTextViewDistance = findViewById(R.id.textViewDistance);

        mbtnRequestNow = findViewById(R.id.btnRequestNow);

        mTextViewOrigin.setText(" " + mExtraOrigin);
        mTextViewDestination.setText(" " +mExtraDestination);

        mbtnRequestNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRequestDriver();
            }
        });

        mCircleImageBack = findViewById(R.id.circleImageBack);

        mCircleImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // terminamos la actividad
            }
        });
    }

    private void goToRequestDriver() {
        Intent intent = new Intent(DetailRequestActivity.this , RequestDriverActivity.class);
        // pasamos el coordenada de origen
        intent.putExtra("origin_lat", mOrigenLatLong.latitude);
        intent.putExtra("origin_lng", mOrigenLatLong.longitude);
        intent.putExtra("origin", mExtraOrigin);
        intent.putExtra("destination", mExtraDestination);
        intent.putExtra("destination_lat", mDestinationLatLong.latitude);
        intent.putExtra("destination_lng", mDestinationLatLong.longitude);
        startActivity(intent);
        finish(); // para cerrar esta actividad
    }

    private void drawRoute() {
        mGoogleApiProvider.getDirections(mOrigenLatLong, mDestinationLatLong).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    // ver que te trae el json para comprender => googleAppiProvider
                    JSONObject jsonObject = new JSONObject(response.body());
                    JSONArray jsonArray = jsonObject.getJSONArray("routes"); // string de la propiedad del json
                    JSONObject route = jsonArray.getJSONObject(0); // para obtener todos los datos de la ruta todo el json de esa position
                    JSONObject polylines = route.getJSONObject("overview_polyline"); // position esfecifica del json
                    String poins = polylines.getString("points"); // asi se llama la propiedad del json

                    // ahora esto esta codifico lo decodificamos (esta encryptado)
                    mPolylineList = DecodePoints.decodePoly(poins);

                    // dibujamos la ruta
                    mPolygonOptions = new PolylineOptions();
                    mPolygonOptions.color(Color.WHITE);
                    mPolygonOptions.width(13f);
                    mPolygonOptions.startCap(new SquareCap());
                    mPolygonOptions.jointType(JointType.ROUND);
                    mPolygonOptions.addAll(mPolylineList); // pasar una lista
                    mMap.addPolyline(mPolygonOptions);

                    // datos del json te trae la distancia
                    JSONArray legs = route.getJSONArray("legs");
                    JSONObject leg = legs.getJSONObject(0);
                    // getJSONObject => obtener un ojeto especifico
                    JSONObject distance = leg.getJSONObject("distance");
                    JSONObject duration = leg.getJSONObject("duration");

                    // obtenemos las propiedades del json
                    String distanceText = distance.getString("text");
                    String durationText = duration.getString("text");

                    mTextViewTime.setText(" " +durationText);
                    mTextViewDistance.setText(" " +distanceText);

                } catch (Exception e) {
                    Log.d("Error", "Error encontrado " + e.getMessage() );
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // habilitar el api de google
        mMap = googleMap;
        // tipo de mapa
        boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.estilos_mapa));
        if(!success) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
        mMap.getUiSettings().setZoomControlsEnabled(true); // para mostrar el zoom

        // añadimos marcadores
        mMap.addMarker(new MarkerOptions().position(mOrigenLatLong).title("Origen").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_red)));
        mMap.addMarker(new MarkerOptions().position(mDestinationLatLong).title("Destino").icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_blue)));

        // para mover la camara
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                .target(mOrigenLatLong)
                .zoom(14f)
                .build()
        ));

        drawRoute(); // llamamos a las rutas
    }
}
