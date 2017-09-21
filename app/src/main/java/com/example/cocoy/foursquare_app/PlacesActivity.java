package com.example.cocoy.foursquare_app;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

public class PlacesActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener  {

    private TextView tvLat, tvLon;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private static final String CLIENT_ID = "0R1KYFSLIKTDHB0ED4JJD4MHZ3WF10S4EQFGICDTHSQLKUMG"; // clave ID
    private static final String CLIENT_SECRET = "TO05OOMSDXIVYMWYLEU0LJ0G5PXX15YIV2LWROHIBMJYAPEM"; // clave secreta


    private RecyclerViewClickListener recyclerViewClickListener;

    String url;
    String token;

    double latitud, longitud;

    NetworkServices client;

    // TODO: 2.- Definir variables

    private GoogleApiClient googleApiClient; // Cliente de Google API
    private Location location;           // Objeto para obtener ubicación
    private final int REQUEST_LOCATION = 1;   // Código de petición para ubicación

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);

        //tvLat = (TextView) findViewById(R.id.textView);
        //tvLon = (TextView) findViewById(R.id.textView2);

        Intent intent = getIntent();
        token = intent.getStringExtra("accessToken");

        // TODO: 3.- Inicializar cliente de Google API
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();



    }

    // TODO: 9.- Se obtienen los valores de la ubicación
    private void updateLocationUI() {
       // tvLat.setText(String.valueOf(location.getLatitude()));
        //tvLon.setText(String.valueOf(location.getLongitude()));

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        url = "https://api.foursquare.com/v2/venues/search?client_id="+CLIENT_ID+"&client_secret="+CLIENT_SECRET+"&v=20130815&ll="+String.valueOf(location.getLatitude())+","+String.valueOf(location.getLongitude())+"&limit=30";

        client = new NetworkServices();

        new AsyncTask<Void,Void,Void>(){
            String result;

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Log.d("Url: ", url);
                    result = client.makeRequest(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;

            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                /*ArrayList<Foto> fotos = new ArrayList<Foto>();

                fotos.add(new Foto("Foto 01", R.mipmap.like_icon));
                fotos.add(new Foto("Foto 02", R.mipmap.like_icon));
                fotos.add(new Foto("Foto 03", R.mipmap.like_icon));*/

                final ArrayList<Venue> venues = new ArrayList<Venue>();
                JSONParser jsonParser = new JSONParser(result);

                try {
                    for(int i=0; i<jsonParser.getSizeArray();i++) {
                        Log.d("lo hizo: ",i+"");
                        venues.add(new Venue(jsonParser.getVenueID(i),
                                            jsonParser.getVenueName(i),
                                            jsonParser.getVenueAddress(i),
                                            jsonParser.getCategoryName(i),
                                           // jsonParser.getPhone(i),
                                            jsonParser.getCheckIns(i),
                                            jsonParser.getPrefix(i) + "bg_88" + jsonParser.getSufix(i)));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    // TODO: 13.- Ingresamos a nuestro adaptador un nuevo listener para poder saber el elemento al que se le dio click
                    RecyclerViewCustomAdapter adapter = new RecyclerViewCustomAdapter(getBaseContext(),venues, new RecyclerViewClickListener() {
                        @Override
                        public void onClick(View view, int position) {
                            //Toast.makeText(PlacesActivity.this, "Elemento " + position, Toast.LENGTH_SHORT).show();
                            ArrayList<String> content = new ArrayList<String>();
                            content.add(venues.get(position).getId());
                            content.add(venues.get(position).getName());
                            content.add(venues.get(position).getAddress());
                            content.add(venues.get(position).getCategory());
                            content.add(venues.get(position).getCheckins());
                            Intent intent1 = new Intent(PlacesActivity.this, DescriptionActivity.class);
                            intent1.putExtra("position",position);
                            intent1.putExtra("token",token);
                            intent1.putStringArrayListExtra("content",content);
                            startActivity(intent1);
                        }
                    });
                    recyclerView.setAdapter(adapter);
                }


            }
        }.execute();
    }


    // TODO: 4.- Mandar a llamar la función processLocation()

    /**
     *
     *
     * Mandamos a llamar el método processLocation donde vamos a validar
     * permisos, ubicación y errores
     * @param bundle
     *
     *
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        processLocation();
    }


    // TODO: 5.- obtener ubicación y validar que no esté vacío

    /**
     *
     * Obtiene la ubicación y el permiso, y adicional valida si el objeto Location es vacío o no
     * para poder actualizar la interfaz de usuario
     *
     *
     */
    private void processLocation() {
        // Se trata de obtener la última ubicación registrada
        getLocation();

        // Si ubicación es diferente de nulo se actualizan los campos para escribir la ubicación
        if (location != null) {

            latitud = location.getLatitude();
            longitud = location.getLongitude();
            updateLocationUI();
            Log.d("Permision Result", "---------");
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(PlacesActivity.this);
            builder.setTitle("NO LOCATION FOUND")
                    .setMessage("Can't find location. :(")
                    .setPositiveButton("OK, got it", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                        }
                    })
                    .setCancelable(false)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }


    // TODO 6.- Definimos el método getLocation()

    /**
     *
     *
     * Valida si la app tiene los permisos para poder obtener ubicación. Por defecto no los tiene
     * aunque se tenga declarado en el manifiesto los permisos necesarios.
     * La llamada de permisos se tiene que hacer manual, ya que te lo pide la clase para continuar
     *
     * <p>
     *     En caso de que si se den permisos para acceder a la ubicación se va a ejecutar el
     *     método sobre escrito onRequestPermissionsResult() que es donde ya podemos obtener
     *     las coordenadas para usar posteriormente
     * </p>
     *
     * <p>
     * En este caso creamos una clase isLocationPermissionGranted para manejar la parte de los permisos
     * ya que se requieren más adelante otra vez
     *</p>
     */
    private void getLocation() {
        // Se valida que haya permisos garantizados
        if (isLocationPermissionGranted()) {
            // Si los hay se regresa un objeto con información de ubicacion
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        } else {
            // Sino se administra la petición de permisos
            requestPermission();
        }
    }

    // TODO 7.- Definimos el método para saber si tenemos acceso a usar la ubicación

    /**
     *
     *
     * Maneja la condicional para saber si el usuario ha dado permiso
     * de usar la ubicación en la app. En este ejemplo se usa el condicional
     * solo con ACCESS_FINE_LOCATION
     * @return
     */
    private boolean isLocationPermissionGranted() {
        /* Valida si ya se dio permiso */
        int permission = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        /* Se regresa un valor booleano para saber si la app tiene permisos o no */
        return permission == PackageManager.PERMISSION_GRANTED;

    }

    // TODO: 8.- Pedimos permiso para poder acceder a la ubicación

    /**
     *
     * Aquí se maneja el cuadro de diálogo para que el usuario de permisos a la app
     * <p>
     * Típicamente el método regresa una respuesta booleana para saber si es necesario que el
     * desarrollador maneje la interfaz para avisarle al usuario que se negó a dar acceso
     * o no es necesario. Si es verdadero quiere decir que el usuario rechazó dar permisos. Si
     * es negativo se lanza la solicitud con requestPermissions incluyendo los permisos que
     * queremos que el usuario acepte.
     * </p>
     */
    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            /*
                Aquí muestras confirmación explicativa al usuario
                por si rechazó los permisos
              */
            Toast.makeText(this, "No quisiste dar acceso a tu ubicación", Toast.LENGTH_SHORT).show();
        } else {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        }
    }



    /**
     *
     * Cada vez que se garantice el acceso para un permiso
     * el método se activará y es donde podemos validar que nuestra solicitud
     * puede continuar. Identificamos nuestra solicitud de acceso a ubicación con
     * la constante REQUEST_LOCATION y a partir de ahí manejamos los resultados
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                /* se pide la última ubicación */
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

                /* Si la ubicación es diferente de nulo, es decir, se regresó la ubicación
                *   Entonces se actualiza la interfaz con los valores
                *   */
                if (location != null) {

                    latitud = location.getLatitude();
                    longitud = location.getLongitude();
                    updateLocationUI();
                    Log.d("Permission result", "---------");
                } else
                    Toast.makeText(this, "Ubicación no encontrada", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Permisos no otorgados", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}


    // TODO: 10.- Define los métodos de onStart y onStop, ya que son los que permiten que se active el servicio
    /*
    *
    *   LOS SIGUIENTES MÉTODOS SON IMPORTANTES PORQUE
    *   SIN ELLOS NO ES POSIBLE QUE EL CLIENTE DE GOOGLE SE INICIE
    *   PARA EMPEZAR A TRABAJAR
     */
    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        Log.d("onLocationChanged", "cambió ubicación");

        latitud = location.getLatitude();
        longitud = location.getLongitude();
        updateLocationUI();
        //Log.d("on Location changed", "---------");
    }


}
