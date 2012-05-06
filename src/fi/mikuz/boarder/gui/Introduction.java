package fi.mikuz.boarder.gui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import fi.mikuz.boarder.R;

/**
 * 
 * @author Jan Mikael Lindlöf
 */
public class Introduction extends Activity {
	public static final String TAG = "Guide";
	
	LinearLayout mBody;
	int mPage = 0;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    	LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.introduction, (ViewGroup) findViewById(R.id.root));
        
        mBody = (LinearLayout) layout.findViewById(R.id.introduction_body);
        Button endButton = (Button) layout.findViewById(R.id.guide_end);
        Button lastButton = (Button) layout.findViewById(R.id.guide_last);
        Button nextButton = (Button) layout.findViewById(R.id.guide_next);

        endButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		exit();
        	}
        });

        lastButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		if (mPage > 0) {
        			mPage--;
        			changePage();
        		} else {
        			Toast.makeText(Introduction.this, "This is the first page", Toast.LENGTH_SHORT).show();
        		}
        	}
        });
        
        nextButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		mPage++;
        		changePage();
        	}
        });
        
        setContentView(layout);
        
        changePage();
    }
    
    private void changePage() {
    	setTitle("Boarder introduction - Page " + mPage);
    	mBody.removeAllViews();
    	switch(mPage) {
	        case 0:
	        	TextView text = new TextView(this);
	        	text.setText("Hello world");
	        	mBody.addView(text);
	        	break;
	        default:
	        	exit();
    	}
    }
    
    private void exit() {
    	try {
			Introduction.this.finish();
		} catch (Throwable e) {
			Log.e(TAG, "Unable to finalize", e);
		}
    }
    
}
