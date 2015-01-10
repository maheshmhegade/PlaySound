package SoundBox;

import java.util.Arrays;

import com.androidplot.Plot;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.example.playsound.R;

import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;


public class SoundBox 
{

	public SoundBox() 
	{
		selecteWaveType =  new String("Sine");
		duration = 3;
		freqOfTone = 440;
		voltage = 16000;
		//configSoundBox();
		// TODO Auto-generated constructor stub
	}

	String selecteWaveType;
	private int duration;            //default value is 3 seconds
	private final int sampleRate = 44100;//default value
	private int numSamples = duration * sampleRate;
	private double freqOfTone; //default value is 440 Hz
	private double voltage;  //default is mid position
	private byte actualSndSamples[];
	private double waveSamples[];

	Handler plotHandler = new Handler();

	public void configSoundBox(String m_waveType, int m_duration,int m_frequency, double m_voltage )
	{
		setWaveType(m_waveType);
		if (m_duration > 0) setDuration(m_duration); 
		if (m_frequency > 0) setFrequency(m_frequency);
		if (m_voltage > 0) setVoltage(m_voltage);
		numSamples = duration * sampleRate;
	}

	public void playSoundWave(final XYPlot plotObj) 
	{
		setWaveSamples();
		plotData(plotObj);
		formatSamples();
		playSound();

	}

	private void plotData(XYPlot plot) {
		// Create a couple arrays of y-values to plot:
		plot.clear();
		Number[] series1Numbers = dataToPlot();

		// Turn the above arrays into XYSeries':
		XYSeries series1 = new SimpleXYSeries(
				Arrays.asList(series1Numbers),          // SimpleXYSeries takes a List so turn our array into a List
				SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
				selecteWaveType);                             // Set the display title of the series

		LineAndPointFormatter series1Format = new LineAndPointFormatter(
				Color.rgb(0, 200, 0),                   // line color
				Color.rgb(0, 100, 0),                   // point color
				null,                                   // fill color (none)
				null);                           // text color

		// add a new series' to the xyplot:
		plot.addSeries(series1, series1Format);

		// reduce the number of range labels
		plot.setTicksPerRangeLabel(3);
		plot.redraw();
	}

	private void setFrequency(double m_frequency)
	{
		freqOfTone = m_frequency;
	}

	private void setVoltage(double m_voltage) 
	{
		voltage = m_voltage;
	}

	private void  setDuration(int m_duration)
	{
		duration = m_duration;
	}

	private void  setWaveType(String m_waveType)
	{
		selecteWaveType = m_waveType;
	}

	private void setWaveSamples()
	{
		waveSamples = new double[numSamples];
		actualSndSamples = new byte[2 * numSamples];

		if( selecteWaveType.equals("Sine") )
		{
			setSineWave();
		}
		else if( selecteWaveType.equals("Triangular"))
		{
			setTriangularWave();
		}
		else if( selecteWaveType.equals("Square"))
		{
			setSquareWave();
		}
		else if( selecteWaveType.equals("Ramp"))
		{
			setRampWave();
		}
		else 
		{

		}
	}

	private void setSineWave()
	{
		// fill out the array
		for (int i = 0; i < numSamples; ++i) 
		{
			waveSamples[i] = Math.sin(2 * Math.PI * i * freqOfTone / sampleRate);
		}
	}

	private void setTriangularWave() 
	{
		int c=0,x=0;
		double step = (double)(2*freqOfTone)/sampleRate;
		while(c < (duration * freqOfTone))
		{

			waveSamples[x] = (double)(-1);
			x++;
			for (int i = 1; i < (int)(sampleRate/(freqOfTone*2)) ; i++)
			{
				waveSamples[x] = waveSamples[x-1] + step;
				x++;
			}
			for (int i = 1; i < (int)(sampleRate/(freqOfTone*2)); i++)
			{
				waveSamples[x] = waveSamples[x-1] - step;
				x++;
			}
			c++;
		}
	}

	private void setSquareWave() 
	{
		int c=0,x=0;
		int limit = (int)(sampleRate/(freqOfTone*2));
		while(c < (duration * freqOfTone))
		{
			for(int i = 0;i < limit; i++)
			{
				waveSamples[x]=(double)(1);
				x++;
			}
			for(int i = 0 ; i < limit; i++)
			{
				waveSamples[x]=(double)(-1);
				x++;
			}
			c++;
		}
	}

	private void setRampWave() 
	{
		int c=0,x=0;
		double step = (double)(freqOfTone)/sampleRate;
		while(c < (duration * freqOfTone))
		{
			waveSamples[x] = (double)(-1);
			x++;
			for (int i = 1; i < (int)(sampleRate/freqOfTone) ; i++)
			{
				waveSamples[x] = waveSamples[x-1] + step;
				x++;
			}
			c++;
		}

	}

	private void formatSamples()
	{
		// convert to 16 bit pcm sound array
		// assumes the sample buffer is normalised.
		int idx = 0;
		for (final double dVal : waveSamples) 
		{
			// scale to maximum amplitude
			final short val = (short) ((dVal * voltage)); //Maximum value = 32767
			// in 16 bit wav PCM, first byte is the low order byte
			actualSndSamples[idx++] = (byte) (val & 0x00ff);
			actualSndSamples[idx++] = (byte) ((val & 0xff00) >>> 8);
		}
	}

	private void playSound()
	{
		final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
				sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, actualSndSamples.length,
				AudioTrack.MODE_STATIC);
		audioTrack.write(actualSndSamples, 0, actualSndSamples.length);

		audioTrack.play();
	}
	private Number[] dataToPlot()
	{
		Number[] dataPoints = new Number[500];
		for(int i=0; i<500; i++)
		{
			dataPoints[i] = waveSamples[i];
		}
		return dataPoints;
	}
}
