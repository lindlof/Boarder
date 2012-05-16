/*
 * Copyright 2009 Moritz Wundke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fi.mikuz.boarder.gui.checkboxList;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import fi.mikuz.boarder.R;

public class ExtendedCheckBoxList extends ListActivity {
	
	private ExtendedCheckBoxListAdapter mListAdapter;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkbox_list);
        
        // Build the list adapter
        mListAdapter = new ExtendedCheckBoxListAdapter(this);
        
        // Add some items
        for( int i = 1; i < 20; i++ )
        {
        	String newItem = "Item " + i;
        	mListAdapter.addItem( new ExtendedCheckBox(newItem,false));
        }
        
        // Bind it to the activity!
        setListAdapter(mListAdapter);
    }
    
    /**
     * If a list item is clicked 
     * we need to toggle the checkbox too!
     */
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// Toggle the checkbox state!
		if ( v != null )
		{
			ExtendedCheckBoxListView CurrentView = (ExtendedCheckBoxListView)v;
			if ( CurrentView != null )
			{
				CurrentView.toggleCheckBoxState();
			}
		}
		
		super.onListItemClick(l, v, position, id);
	}
}