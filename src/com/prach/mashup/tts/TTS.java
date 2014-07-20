package com.prach.mashup.tts;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class TTS extends Activity implements OnClickListener, OnInitListener{
    /** Called when the activity is first created. */
	public static final int MY_DATA_CHECK_CODE = 0x0ba7c0de;
	private TextView entry;
    private TextToSpeech mTts;
    private Spinner s;
    private Locale locale;
    //private AlertDialog.Builder alert;
    private Button button_ok;
    private Button button_clear;
	@SuppressWarnings("unchecked")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub);
        
        s = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.locales, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        
        mTts = new TextToSpeech(this,this);
        //mTts.setLanguage(Locale.US);
        //Context context = ttsdemoActivity.this;
        //alert = new AlertDialog.Builder(context);
        entry = (TextView)findViewById(R.id.entry);
        entry.setText("Please input your text to speech.");
        
        button_ok = (Button) findViewById(R.id.speech);
        button_ok.setOnClickListener(this);
        
        button_clear = (Button) findViewById(R.id.clear);
        button_clear.setOnClickListener(this);
    }

	@Override
	public void onResume(){
		Log.i("com.prach.mashup.TTS", "onResume()");
		super.onResume();
		Intent intent = getIntent();
		String readtext = intent.getStringExtra("READ_TEXT");
		String[] filters = intent.getStringArrayExtra("FILTERS");
		String lang = intent.getStringExtra("SPEECH_LANG");
		
		if(intent!=null){
			if(readtext!=null&&filters!=null&&lang!=null){ //check if not intent
				for (int i = 0; i < filters.length; i++) {
					if(filters[i].equals("CUT_TAGS")){
						readtext = this.removeTags(readtext);
					}else if(filters[i].equals("CUT_MULTIBYTES")){
						readtext = this.removeMultibytes(readtext);
					}
				}
				setSelection(lang);
				entry.setText(readtext);
				locale = new Locale(s.getSelectedItem().toString(), "","");
	    		mTts.setLanguage(locale);
	    		Log.i("com.prach.mashup.TTS", "speak()");
	    		Log.i("com.prach.mashup.TTS",readtext);
	    		mTts.speak(readtext, TextToSpeech.QUEUE_FLUSH, null);
			}
		}		
	}
	
	@Override
	public void onClick(View v){
    	switch(v.getId()){
    	case (R.id.speech):
    		locale = new Locale(s.getSelectedItem().toString(), "","");
    		mTts.setLanguage(locale);
    		mTts.speak(entry.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
    		//alert.setMessage(entry.getText().toString());
    		//asetter.show(AlertSetter.AS_OK,location.getText().toString());
    		break;
    	case (R.id.clear):  
    		entry.setText("");
    		break;
    	}
    }
	
	//Implement
	public void onInit(int status) {
		Log.i("com.prach.mashup.TTS", "onInit()");
		locale = new Locale(s.getSelectedItem().toString(), "","");
		if(mTts.isLanguageAvailable(locale) >= TextToSpeech.LANG_AVAILABLE){
			mTts.setLanguage(locale);
		}
		mTts.speak(entry.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
		//mTts.speak("starting text to speech application", TextToSpeech.QUEUE_FLUSH, null);
	}
	
	//Local
	public String removeTags(String str){
		String originalString = str.replaceAll("\\<.*?\\>", "<>");
		originalString = originalString.replaceAll("<><>", ",\n");
		originalString = originalString.replaceAll("<>", "");
		return originalString;
	}
	
	//Local
	public String removeMultibytes(String str){
		StringBuilder builder = new StringBuilder();
		for (char currentChar : str.toCharArray()) {
			if (Character.UnicodeBlock.of(currentChar) == Character.UnicodeBlock.BASIC_LATIN) {
				builder.append(currentChar);
			}
		}
		return builder.toString();
	}
	
	//Local
	public void setSelection(String lang){
		String[] langarray = getResources().getStringArray(R.array.locales);
		for (int i = 0; i < langarray.length; i++) {
			if(lang.equals(langarray[i])){
				s.setSelection(i);
				
			}
		}
	}
}