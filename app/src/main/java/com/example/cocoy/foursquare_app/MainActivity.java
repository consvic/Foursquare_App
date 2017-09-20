package com.example.cocoy.foursquare_app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.foursquare.android.nativeoauth.FoursquareCancelException;
import com.foursquare.android.nativeoauth.FoursquareDenyException;
import com.foursquare.android.nativeoauth.FoursquareInvalidRequestException;
import com.foursquare.android.nativeoauth.FoursquareOAuth;
import com.foursquare.android.nativeoauth.FoursquareOAuthException;
import com.foursquare.android.nativeoauth.FoursquareUnsupportedVersionException;
import com.foursquare.android.nativeoauth.model.AccessTokenResponse;
import com.foursquare.android.nativeoauth.model.AuthCodeResponse;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity {

    //TODO 1: Creacion de variables para validar resultado de solicitud  de conexion y de intercambio de tokens
    private static final int REQUEST_CODE_FSQ_CONNECT = 200; // Código de conexión exitosa
    private static final int REQUEST_CODE_FSQ_TOKEN_EXCHANGE = 201; // Código de intercambio de token exitosa
    private static final String CLIENT_ID = "0R1KYFSLIKTDHB0ED4JJD4MHZ3WF10S4EQFGICDTHSQLKUMG"; // clave ID
    private static final String CLIENT_SECRET = "TO05OOMSDXIVYMWYLEU0LJ0G5PXX15YIV2LWROHIBMJYAPEM"; // clave secreta

    //TODO 2: Boton de login
    private Button bLogin;
    private Button bLocation;


   private TextView tvLat, tvLon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //TODO 3: Link button
        bLogin = (Button) findViewById(R.id.bLogin);

        bLocation = (Button) findViewById(R.id.bLocation);

        manageLoginUI();

    }

    private void manageLoginUI(){
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean connected = false;
                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;
                }
                else {
                    connected = false;
                }

                if(!connected){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("NO INTERNET CONNECTION")
                            .setMessage("Can't open Foursquare. No internet connection. :(")
                            .setPositiveButton("OK, got it", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // continue with delete
                                }
                            })
                            .setCancelable(false)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    // TODO: 4.- Inicio de un nuevo flujo a través de un intent
                    Intent intent = FoursquareOAuth.getConnectIntent(MainActivity.this, CLIENT_ID);

                    // TODO: 5.-
                    // Si el dispositivo no tiene instalada la app de Fousquare se le va a pedir que
                    // la instale, usando un intent hacia la Play Store
                    if (FoursquareOAuth.isPlayStoreIntent(intent)) {
                        Toast.makeText(MainActivity.this, "La app no está instalada", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                    } else {
                        // Si está la app instalada empieza el proceso de autenticación
                        Log.i("Entro", "Entro");
                        startActivityForResult(intent, REQUEST_CODE_FSQ_CONNECT);
                    }


                }

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_FSQ_CONNECT: // Se pudo hacer la conexión
                onCompleteConnect(resultCode, data);
                break;
            case REQUEST_CODE_FSQ_TOKEN_EXCHANGE: // Ya se dio el permiso, hace falta obtener el token
                onCompleteTokenExchange(resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void onCompleteConnect(int resultCode, Intent data) {
        // TODO: 3.- Se valida que no haya excepción de conexión
        AuthCodeResponse codeResponse = FoursquareOAuth.getAuthCodeFromResult(resultCode, data);
        Exception exception = codeResponse.getException();

        if (exception == null) {
            // Si no hay excepción se obtiene el código para cambiarlo por un token
            // y se llama la función para hacer el intercambio
            String code = codeResponse.getCode();
            performTokenExchange(code);
        } else {
            if (exception instanceof FoursquareCancelException) {/* Cancel */
            } else if (exception instanceof FoursquareDenyException) {/* Deny.*/
            } else if (exception instanceof FoursquareOAuthException) {
                // OAuth error.
                String errorMessage = exception.getMessage();
                String errorCode = ((FoursquareOAuthException) exception).getErrorCode();
                Log.i("Method: ", "onCompleteConnect");
                toastMessage(this, errorMessage + " [" + errorCode + "]");
            } else if (exception instanceof FoursquareUnsupportedVersionException) {/* Unsupported Fourquare app version on the device. */
            } else if (exception instanceof FoursquareInvalidRequestException) {/* Invalid request. */
            } else {/*Error.*/}
        }
    }


    private void onCompleteTokenExchange(int resultCode, Intent data) {
        // Se lleva a cabo la solicitud de token
        AccessTokenResponse tokenResponse = FoursquareOAuth.getTokenFromResult(resultCode, data);
        Exception exception = tokenResponse.getException();

        // Si no hay ninguna excepción o problema quiere decir que ya se puede obtener el token
        // de acceso
        if (exception == null) {
            String accessToken = tokenResponse.getAccessToken();

            // Guardamos el token para usarlo posteriormentdx {ñ.
            Toast.makeText(this, "Access token " + accessToken, Toast.LENGTH_SHORT).show();
            // Actualizamos la interfaz
            manageLoginUI();
            Intent intent = new Intent(MainActivity.this, PlacesActivity.class);
            intent.putExtra("accessToken",accessToken);
            startActivity(intent);
        } else {
            if (exception instanceof FoursquareOAuthException) {
                // Error en el proceso de autenticación OAuth
                String errorMessage = exception.getMessage();
                String errorCode = ((FoursquareOAuthException) exception).getErrorCode();
                Log.i("Method: ", "onCompleteTokenExchange");
                toastMessage(this, errorMessage + " [" + errorCode + "]");
            } else {
                // Excepciones
                toastError(this, exception);
            }
        }
    }

    private void performTokenExchange(String code) {
        Intent intent = FoursquareOAuth.getTokenExchangeIntent(this, CLIENT_ID, CLIENT_SECRET, code);
        startActivityForResult(intent, REQUEST_CODE_FSQ_TOKEN_EXCHANGE);
    }

    public static void toastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void toastError(Context context, Throwable t) {
        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
