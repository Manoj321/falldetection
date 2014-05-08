package ics.uci.falldetection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements SensorEventListener {
  private SensorManager sensorManager;
  private boolean color = false;
  private View text;
  private long lastUpdate;
  SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-HH-mm-ss-SSS");
  StringBuffer value = new StringBuffer();
  private int count =0;
  String FILENAME=sdf.format(new Date());
  File rootPath=null;
  File dataFile=null;
  /** Called when the activity is first created. */

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
   
    setContentView(R.layout.activity_main);
    text = findViewById(R.id.textView2);
    sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
    sensorManager.unregisterListener(this);
	String DNAME="Fall_detection";
	 
	rootPath = new File(Environment.getExternalStorageDirectory(), DNAME);
     if(!rootPath.exists()) {
         rootPath.mkdirs();
     }
     dataFile = new File(rootPath, FILENAME+".txt");
    
        
  }

  @Override
  public void onSensorChanged(SensorEvent event) {
    if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
      getAccelerometer(event);
    }

  }

  private void getAccelerometer(SensorEvent event){
    float[] values = event.values;
    // Movement
    float x = values[0];
    float y = values[1];
    float z = values[2];
    String currentDateandTime = sdf.format(new Date());
    value.append("||"+currentDateandTime);
    value.append( "|"+x+","+y+","+z);
    count++;
    if(count>=1000){
    	WriteData(value.toString());
    		value.setLength(0);
    		count=0;
    }
  }
  
 public void WriteData(String a){
	 
     
     if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
         Toast.makeText(this, "Cannot use storage.", Toast.LENGTH_SHORT).show();
         finish();
         return;
     }
     try {           
         FileOutputStream mOutput = new FileOutputStream(dataFile, true);
         mOutput.write(a.getBytes());
         mOutput.close();
     } catch (FileNotFoundException e) {
         e.printStackTrace();
     } catch (IOException e) {
         e.printStackTrace();
     }
 	}
 
 public void onToggle(View view) {
	 	

	 ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton1);
	    // Is the toggle on?
	    boolean on = (toggle).isChecked();
	    if (on) {
	    	value.append("started");
	    	sensorManager.registerListener(this,
	    	        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
	    	        SensorManager.SENSOR_DELAY_FASTEST);
	    	((TextView) text).setText("Collecting data..."); 
	    } else {
	    	sensorManager.unregisterListener(this);
	    	value.append("end");
	    	WriteData(value.toString());
	    	((TextView) text).setText("Done...");
	    }
	}
  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {

  }

  @Override
  protected void onResume() {
    super.onResume();
    // register this class as a listener for the orientation and
    // accelerometer sensors
    sensorManager.registerListener(this,
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
        SensorManager.SENSOR_DELAY_NORMAL);
  }

  @Override
  protected void onPause() {
    // unregister listener
    super.onPause();
    sensorManager.unregisterListener(this);
  }
} 