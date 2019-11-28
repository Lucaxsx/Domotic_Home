package com.lucaxsx.domotichome;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private static final UUID BTUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothSocket btSocket = null;
    private BluetoothAdapter MiBT = BluetoothAdapter.getDefaultAdapter();
    private ConnectedThread MiConexionBT;
    boolean Vinculado = false;
    String address = null;
    String stPuerta = "Abrir Puerta";
    String stPersiana = "Abrir Cortinas";
    String stVentilador = "Encender Ventilador";
    Button Puerta, Persiana, Ventilador;
    ToggleButton luz1, luz2, luz_B, luz_K;
    ImageButton subirVel, bajarVel;

    TextView author;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Puerta = findViewById(R.id.Puerta);
        Persiana = findViewById(R.id.Persiana);
        Ventilador = findViewById(R.id.Ventilador);
        author = findViewById(R.id.Author);

        subirVel = findViewById(R.id.Subir_vel);
        bajarVel = findViewById(R.id.Bajar_vel);

        luz1 = findViewById(R.id.Luz_Hab1);
        luz2 = findViewById(R.id.Luz_Hab2);
        luz_B = findViewById(R.id.Luz_Bath);
        luz_K = findViewById(R.id.Luz_Kitch);

        luz1.setOnClickListener(new Listener());
        luz2.setOnClickListener(new Listener());
        luz_B.setOnClickListener(new Listener());
        luz_K.setOnClickListener(new Listener());

        subirVel.setOnLongClickListener(new LongListener());
        bajarVel.setOnLongClickListener(new LongListener());

    }

    //          Botones

    public void Puerta(View vista){
        if(estaConectado()) {
            String buff = (String) Puerta.getText();
            if (buff.equalsIgnoreCase(stPuerta)) {
                Puerta.setText(R.string.CPuerta);
                MiConexionBT.write("P");
            } else {
                Puerta.setText(R.string.APuerta);
                MiConexionBT.write("P");
            }
        }
    }

    public void Persiana(View vista){
        if(estaConectado()) {
            String buff = (String) Persiana.getText();
            if (buff.equalsIgnoreCase(stPersiana)) {
                Persiana.setText(R.string.CCortinas);
                MiConexionBT.write("C");
            } else {
                Persiana.setText(R.string.ACortina);
                MiConexionBT.write("C");
            }
        }
    }


    public void subirVelocidad(View vista){
        if(estaConectado()){
            MiConexionBT.write("+");
        }
    }

    public void bajarVelocidad(View vista){
        if(estaConectado()){
            MiConexionBT.write("-");
        }
    }

    public void Ventilador(View vista){
        if(estaConectado()) {
            String buff = (String) Ventilador.getText();
            if (buff.equalsIgnoreCase(stVentilador)) {
                Ventilador.setText(R.string.AVentilador);
                MiConexionBT.write("V");
            } else {
                Ventilador.setText(R.string.EVentilador);
                MiConexionBT.write("V");
            }
        }
    }


    //          Luces

    class Listener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.Luz_Hab1:
                        if(estaConectado()){
                            MiConexionBT.write("1");
                        }else{
                            luz1.setChecked(false);
                        }
                        break;
                    case R.id.Luz_Hab2:
                        if (estaConectado()) {
                            if (luz2.isChecked()) {
                                MiConexionBT.write("2");
                            }
                        }else{
                            luz2.setChecked(false);
                        }
                        break;
                    case R.id.Luz_Bath:
                        if (estaConectado()) {
                            MiConexionBT.write("B");
                        }else{
                            luz_B.setChecked(false);
                        }
                        break;
                    case R.id.Luz_Kitch:
                        if (estaConectado()) {
                            if (luz_K.isChecked()) {
                                MiConexionBT.write("K");
                            }
                        }else{
                            luz_K.setChecked(false);
                        }
                        break;
                    default:
                        break;
                }

        }
    }

    class LongListener implements View.OnLongClickListener{

        @Override
        public boolean onLongClick(View v){
            if(v.getId() == R.id.Bajar_vel){
                Toast.makeText(getBaseContext(), R.string.BVelocidad, Toast.LENGTH_SHORT).show();
                return true;
            }
            if (v.getId() == R.id.Subir_vel){
                Toast.makeText(getBaseContext(), R.string.SVelocidad, Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        }
    }

    public void info(View vista){
        Intent info = new Intent (this, info.class);
        startActivity(info);

    }


    public void ajustes (View vista){
        if(Vinculado){
            Intent ajuste = new Intent(this, ajuste.class);
            ajuste.putExtra("Vinculado", Vinculado);
            startActivityForResult(ajuste, 2);
        }else{
            Intent ajuste = new Intent(this, ajuste.class);
            startActivityForResult(ajuste, 2);
        }
    }
    public void cerrar(View vista){
        if(estaConectado()){
            MiConexionBT.write("0");
        }
        if(btSocket != null) {
            try {
                btSocket.close();
            } catch (IOException e) {
            }
        }
        finish();
    }

    //          Chequea si está conectado
    public boolean estaConectado(){
        if(btSocket!=null){
            return true;
        }else{
            Toast.makeText(getBaseContext(), "No está vinculado con Arduino.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }



    //              Fin de botones

    //      Método que recibe la MAC del Arduino para intentar conectar.
    @Override
    protected void onActivityResult(int requestCode, int ResultCode, Intent dato){
        if(requestCode == 2){
            if(ResultCode == Activity.RESULT_OK){
                if(dato != null)
                {
                    address = dato.getStringExtra("address");
                    Conectar(address);
                }
            }else if(ResultCode == 2){
                Desconectar();
            }
        }
    }

    //      Crea el Socket
    private BluetoothSocket createBtSocket(BluetoothDevice device) throws IOException{
        return device.createRfcommSocketToServiceRecord(BTUUID);
    }

    //      Desconectar BT y cerrar socket.
    public void Desconectar(){
        try
        {
            Toast.makeText(getBaseContext(), "Desvinculando...", Toast.LENGTH_SHORT).show();
            btSocket.close();
        }catch (IOException e){}
        Vinculado = false;
        try {
            btSocket = null;
        }catch (Exception e){}
        if(btSocket == null){
            Toast.makeText(getBaseContext(), "Desvinculado", Toast.LENGTH_SHORT).show();
        }
    }

    //          Método encargado de intentar vincular con el arduino.

    public void Conectar(String address){
            Toast.makeText(getBaseContext(), "Vinculando...", Toast.LENGTH_SHORT).show();
            BluetoothDevice conect = MiBT.getRemoteDevice(address);
            try {
                btSocket = createBtSocket(conect);
            } catch (IOException e) {
                Toast.makeText(getBaseContext(), "Falló la creación del Socket", Toast.LENGTH_LONG).show();
            }
            MiBT.cancelDiscovery();
            try {
                Toast.makeText(getBaseContext(), "Intentando conectar", Toast.LENGTH_SHORT).show();
                btSocket.connect();
                Vinculado = true;
            } catch (IOException e) {
                try {
                    Toast.makeText(getBaseContext(), "Falló la conexión.", Toast.LENGTH_SHORT).show();
                    btSocket.close();
                } catch (IOException e2) {
                }
            }
            MiConexionBT = new ConnectedThread(btSocket);
            MiConexionBT.start();
            if(btSocket!=null){
                Toast.makeText(getBaseContext(), "Vinculado", Toast.LENGTH_SHORT).show();
            }
    }

    //      Se crea la clase que heredará de Thread para manejar el envío de datos.

    private class ConnectedThread extends Thread{
        private final OutputStream Salida;

        public ConnectedThread(BluetoothSocket socket){
            OutputStream TmpOut = null;
            try{
                TmpOut = socket.getOutputStream();
            }catch (IOException e){}
            Salida = TmpOut;
        }

        public void write(String enviar){
            try{
                Salida.write(enviar.getBytes());
            }catch (IOException e){
                Toast.makeText(getBaseContext(), "No se puede enviar los datos", Toast.LENGTH_SHORT).show();
            }
        }
    }

}