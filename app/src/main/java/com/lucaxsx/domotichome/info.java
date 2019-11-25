package com.lucaxsx.domotichome;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class info extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);

        TextView descripcion = findViewById(R.id.descrip);
        TextView fecha = findViewById(R.id.date);
        TextView author = findViewById(R.id.author);


        fecha.setText(getText(R.string.Fecha));
        author.setText(getText(R.string.Autores));
        descripcion.setText(getText(R.string.Descripcion));
    }
}
