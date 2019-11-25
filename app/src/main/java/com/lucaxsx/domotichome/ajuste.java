package com.lucaxsx.domotichome;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class ajuste extends Activity{

    public final int REQUEST_ENABLE_BT = 1;
    String address = null;
    BluetoothAdapter MiBT = BluetoothAdapter.getDefaultAdapter();
    boolean Vinculado = false;
    boolean Buscar = false;

    TextView Estado, MAC;
    ListView Dispositivos;
    Button Vincular;
    ImageButton BlueT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajustes);

        Estado = findViewById(R.id.Estado);
        MAC = findViewById(R.id.MAC);
        Dispositivos = findViewById(R.id.Dispositivos);
        Vincular = findViewById(R.id.Vincular);
        BlueT = findViewById(R.id.Bluetooth);

        if(MiBT.isEnabled()) {
            BlueT.setVisibility(View.INVISIBLE);
            String Activado = "Activado.";
            Estado.setText(Activado);
        }else{
            String Desactivado = "Desactivado.";
            Estado.setText(Desactivado);
        }

        Dispositivos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String texto = Dispositivos.getItemAtPosition(position).toString();
                String mac = texto.substring((texto.length())-17);
                address = mac;
                if (address.length()>0){
                    Vincular.setVisibility(View.VISIBLE);
                    MAC.setText(address);
                }
            }
        });

        Bundle Recibido = getIntent().getExtras();
        if(Recibido!=null) {
            Vinculado = Recibido.getBoolean("Vinculado");
            if (Vinculado){
                Vincular.setVisibility(View.VISIBLE);
                Vincular.setText("Desvincular");
            }
        }

    }// OnCreate


    @Override
    public void onStop(){
        super.onStop();
        if(MiBT.isDiscovering()){
            MiBT.cancelDiscovery();
        }
        if(Buscar){
            unregisterReceiver(Finded);
        }
    }

    @Override //    Capturar si aceptó o no la activación de Bluetooth.
    protected void onActivityResult(int requestCode, int ResultCode, Intent dato){
        if(requestCode == REQUEST_ENABLE_BT){
            if(ResultCode == RESULT_OK){
                String Activado = "Activado";
                BlueT.setVisibility(View.INVISIBLE);
                Estado.setText(Activado);
            }else if(ResultCode == RESULT_CANCELED){
                String Cancelado = "Desactivado";
                Estado.setText(Cancelado);
            }
        }
    }

    public void Emparejados(View vista){
        if(MiBT.isEnabled()) {
            Set<BluetoothDevice> Sincronizados = MiBT.getBondedDevices();
            if (Sincronizados.size() > 0) {
                ArrayAdapter<String> Lista = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1);
                for (BluetoothDevice Encontrados : Sincronizados) {
                    Lista.add(Encontrados.getName() + "\n" + Encontrados.getAddress());
                }
                Dispositivos.setAdapter(Lista);
            } else {
                Toast.makeText(getBaseContext(), "No hay dispositivos emparejados.", Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(getBaseContext(), "Bluetooth no encendido.", Toast.LENGTH_SHORT).show();
        }
    }

    public void activarBT(View vista){
        Intent Activar = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(Activar, REQUEST_ENABLE_BT);
    }

    public void Buscar(View vista) {
        if (MiBT.isEnabled()) {
            if (MiBT.isDiscovering()) {
                Toast.makeText(getBaseContext(), "Cancelando búsqueda", Toast.LENGTH_SHORT).show();
                MiBT.cancelDiscovery();
            }
            MiBT.startDiscovery();
            Buscar = true;
            Toast.makeText(getBaseContext(), "Buscando...", Toast.LENGTH_LONG).show();
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(Finded, filter);
        }else{
            Toast.makeText(getBaseContext(), "Bluetooth no encendido.", Toast.LENGTH_SHORT).show();
        }
    }


    //          Acción a realizar cuando encuentre un Dispositivo.
    private final BroadcastReceiver Finded = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent){
            String accion = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(accion)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String datos = (device.getName()+"\n"+device.getAddress());
                AgregarAList(datos);
            }
        }
    };

    public void AgregarAList(String datos){
        ArrayAdapter<String> Lista = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1);
        Lista.add(datos);
        Dispositivos.setAdapter(Lista);
    }

    public void Vincular(View vista){
        if(Vinculado){
            Intent pasar = new Intent(this, MainActivity.class);
            setResult(2, pasar);
            finish();
        }else{
            Intent pasar = new Intent(this, MainActivity.class);
            pasar.putExtra("address", address);
            setResult(Activity.RESULT_OK, pasar);
            finish();
        }
    }
}
