package com.example.playsound;



import com.androidplot.xy.XYPlot;

import SoundBox.SoundBox;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.graphics.Color;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnItemSelectedListener {

	Spinner spinnerOsversions;
	private String[] waveForm = {"Sine","Triangular","Square","Ramp"};

	Handler handler = new Handler();

	Button button;

	public XYPlot plot;
	SeekBar sbVoltage;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sbVoltage = (SeekBar) findViewById(R.id.changeTipSeekBar);
		sbVoltage.setMax(32767);

		ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(this,  android.R.layout.simple_spinner_item, waveForm);
		addListenerOnButton();
		spinnerOsversions = (Spinner) findViewById(R.id.Osversions);
		adapter_state.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerOsversions.setAdapter(adapter_state);
	}


	public void addListenerOnButton() {

		button = (Button) findViewById(R.id.button1);

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				plot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
				EditText etFrequency = (EditText) findViewById(R.id.etfrequency);
				EditText etDuration = (EditText) findViewById(R.id.etduration);
				
			
				String waveType = (String)spinnerOsversions.getSelectedItem();
				int liFrequency = Integer.parseInt(etFrequency.getText().toString());
				int liDuration = Integer.parseInt(etDuration.getText().toString());
			    double ldVoltage = (double) (sbVoltage.getProgress()+1);
			    
				SoundBox sndBoxObject = new SoundBox();
				sndBoxObject.configSoundBox(waveType, liDuration, liFrequency, ldVoltage);
				
				sndBoxObject.playSoundWave(plot);
			}
		});
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		spinnerOsversions.setSelection(position);
	}



	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}
