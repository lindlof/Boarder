/* ========================================================================= *
 * Boarder                                                                   *
 * http://boarder.mikuz.org/                                                 *
 * ========================================================================= *
 * Copyright (C) 2013 Boarder                                                *
 *                                                                           *
 * Licensed under the Apache License, Version 2.0 (the "License");           *
 * you may not use this file except in compliance with the License.          *
 * You may obtain a copy of the License at                                   *
 *                                                                           *
 *     http://www.apache.org/licenses/LICENSE-2.0                            *
 *                                                                           *
 * Unless required by applicable law or agreed to in writing, software       *
 * distributed under the License is distributed on an "AS IS" BASIS,         *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
 * See the License for the specific language governing permissions and       *
 * limitations under the License.                                            *
 * ========================================================================= */

package fi.mikuz.boarder.gui.internet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import fi.mikuz.boarder.R;
import fi.mikuz.boarder.app.BoarderActivity;
import fi.mikuz.boarder.component.internet.InternetBoard;
import fi.mikuz.boarder.connection.ConnectionErrorResponse;
import fi.mikuz.boarder.connection.ConnectionListener;
import fi.mikuz.boarder.connection.ConnectionManager;
import fi.mikuz.boarder.connection.ConnectionSuccessfulResponse;
import fi.mikuz.boarder.connection.ConnectionUtils;

public class DownloadBoardList extends BoarderActivity {
	private static final String TAG = "InternetDownloadBoardList";
	
	private static final int LAST_PAGE_COUNT_MAX = 10000;
	private int LAST_ACCESSABLE_PAGE = LAST_PAGE_COUNT_MAX;
	private int LAST_PAGE_COUNT = LAST_PAGE_COUNT_MAX;
	
	ViewPager mViewPager;
	ListViewAdapter mListViewAdapter;
	private final Object mBoardListLock = new Object();
	
	private EditText mSearch;
	private String mCurrentSearch;
	int mSearchlength = 0;
	
	final Handler mHandler = new Handler();
	
	boolean mLoggedIn = false;
	public final static String LOGGED_IN_KEY = "loggedIn";
	
	private String mUserId;
	private String mSessionToken;
	
	String mOrderRule;
	public static final String ORDER_RULE_CHANGE_TIME = "edit_time";
	public static final String ORDER_RULE_BOARD_RATING = "board_rating";
	
	String mOrderDirection;
	public static final String ORDER_DIRECTION_DESCENDING = "DESC";
	public static final String ORDER_DIRECTION_ASCENDING = "ASC";
	
	private int mMaxResults;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.internet_download);
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
            	setPageTitle(position+1);
            }
        });
		
		setNewViewPager();
		mSearch = (EditText) findViewById(R.id.searchInput);
		mCurrentSearch = "";
		ImageView refresh = (ImageView) findViewById(R.id.refresh);
		
		Button orderByDate = (Button) findViewById(R.id.orderByDate);
		Button orderByRate = (Button) findViewById(R.id.orderByRate);
		mOrderRule = ORDER_RULE_CHANGE_TIME;
		mOrderDirection = ORDER_DIRECTION_DESCENDING;
		mMaxResults = 40;
		
		Bundle extras = getIntent().getExtras();
		if (extras.getSerializable(InternetMenu.LOGIN_KEY) != null) {
			@SuppressWarnings("unchecked")
			HashMap<String,String> lastSession = (HashMap<String,String>) extras.getSerializable(InternetMenu.LOGIN_KEY);
			
			mLoggedIn = true;
			mUserId = lastSession.get(InternetMenu.USER_ID_KEY);
			mSessionToken = lastSession.get(InternetMenu.SESSION_TOKEN_KEY);
		}
		
		mSearch.setOnEditorActionListener(new TextView.OnEditorActionListener(){
			@Override
			public boolean onEditorAction(TextView exampleView, int actionId, KeyEvent event) {
				String search = mSearch.getText().toString();
				if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN &&
						!search.equals(mCurrentSearch)) { 
					mCurrentSearch = search;
					setNewViewPager();
				}
				return true;
			}
		});
		
		refresh.setOnClickListener(new View.OnClickListener(){
		    public void onClick(View v) {
		    	refreshViewPager();
		    }
		});
	    
	    orderByDate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
            	if (mOrderRule.equals(ORDER_RULE_CHANGE_TIME) && mOrderDirection.equals(ORDER_DIRECTION_DESCENDING)) {
            		mOrderDirection = ORDER_DIRECTION_ASCENDING;
            	} else {
            		mOrderRule = ORDER_RULE_CHANGE_TIME;
            		mOrderDirection = ORDER_DIRECTION_DESCENDING;
            	}
            	setNewViewPager();
			}
		});
	    
	    orderByRate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (mOrderRule.equals(ORDER_RULE_BOARD_RATING) && mOrderDirection.equals(ORDER_DIRECTION_DESCENDING)) {
            		mOrderDirection = ORDER_DIRECTION_ASCENDING;
            	} else {
            		mOrderRule = ORDER_RULE_BOARD_RATING;
            		mOrderDirection = ORDER_DIRECTION_DESCENDING;
            	}
				setNewViewPager();
			}
		});
	    
	}
	
	private void setNewViewPager() {
		ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter();
        mViewPager.setAdapter(viewPagerAdapter);
        setPageTitle(1);
	}
	
	private void refreshViewPager() {
		int position = mViewPager.getCurrentItem();
		LAST_ACCESSABLE_PAGE = LAST_PAGE_COUNT_MAX;
		setNewViewPager();
		mViewPager.setCurrentItem(position);
	}
	
	private void setPageTitle(int page) {
		setTitle("Internets - Page " + page);
	}
    
    private class ListViewAdapter implements ConnectionListener {
    	
    	private ListView mListView;
    	private int pageNumber;
    	
    	private List<InternetBoard> mBoardList;
    	
    	public ListViewAdapter(int pageNumber) {
    		mBoardList = new ArrayList<InternetBoard>();
    		this.pageNumber = pageNumber;
    	}
    	
    	private synchronized void getBoards() {
    		HashMap<String, String> sendList = new HashMap<String, String>();
    		sendList.put(InternetMenu.ORDER_RULE_KEY, mOrderRule);
    		sendList.put(InternetMenu.ORDER_DIRECTION_KEY, mOrderDirection);
    		sendList.put(InternetMenu.PAGE_NUMBER_KEY, Integer.toString(pageNumber));
    		sendList.put(InternetMenu.MAX_RESULTS_KEY, Integer.toString(mMaxResults));
    		sendList.put(InternetMenu.SEARCH_WORD_KEY, mCurrentSearch);
    		new ConnectionManager(ListViewAdapter.this, InternetMenu.mGetBoardsURL, sendList);
    	}
    	
    	
        private void populateList() {
        	synchronized(mBoardListLock) {
    	    	BoardListAdapter adapter = new BoardListAdapter(mBoardList);
    	    	mListView.setAdapter(adapter);
    	    	setListener();
        	}
        }
        
        private void setListener() {
        	mListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
					synchronized(mBoardListLock) {
    	    			InternetBoard board = ((BoardListAdapter)mListView.getAdapter()).getItem(position);
    	    			if (!board.getUploaderUsername().equals("")) {
    	    				Intent i = new Intent(DownloadBoardList.this, DownloadBoard.class);
    	    				i.putExtra(DownloadBoard.SHOW_KEY, DownloadBoard.SHOW_INTERNET_BOARD);
    	    		    	i.putExtra(InternetMenu.BOARD_ID_KEY, board.getBoardId());
    	    		    	i.putExtra(LOGGED_IN_KEY, mLoggedIn);
    	    		    	
    	    		    	if (mLoggedIn) {
    	    		    		i.putExtra(InternetMenu.USER_ID_KEY, mUserId);
    	    		    		i.putExtra(InternetMenu.SESSION_TOKEN_KEY, mSessionToken);
    	    		    	}
    	    		    	
    	    		    	startActivity(i);
    	    			}
    	    		}
				}
    		});
        }
    	
        public class BoardListAdapter extends BaseAdapter {

    		private List<InternetBoard> boardList;
         
            public BoardListAdapter(List<InternetBoard> boards) {
            	boardList = new ArrayList<InternetBoard>();
                boardList.addAll(boards);
            }
         
            public int getCount() {
                return boardList.size();
            }
         
            public InternetBoard getItem(int position) {
                return boardList.get(position);
            }
         
            public long getItemId(int position) {
                return boardList.get(position).getId();
            }
         
            public View getView(int position, View convertView, ViewGroup parent) {
            	RelativeLayout itemLayout;
                InternetBoard board = getItem(position);
         
                LayoutInflater layoutInflater = DownloadBoardList.this.getLayoutInflater();
                itemLayout = (RelativeLayout) layoutInflater.inflate(R.layout.internet_download_row, parent, false);
         
                TextView uploaderUsername = (TextView) itemLayout.findViewById(R.id.uploaderUsername);
                uploaderUsername.setText(board.getUploaderUsername());
         
                TextView boardName = (TextView) itemLayout.findViewById(R.id.boardName);
                boardName.setText(board.getBoardName());
                
                TextView uploaderRating = (TextView) itemLayout.findViewById(R.id.uploaderRating);
                uploaderRating.setText(board.getRating());
         
                return itemLayout;
            }
         
        }

        @Override
        public void onConnectionSuccessful(ConnectionSuccessfulResponse connectionSuccessfulResponse) throws JSONException {
        	ConnectionUtils.connectionSuccessful(DownloadBoardList.this, connectionSuccessfulResponse);

        	if (ConnectionUtils.checkConnectionId(connectionSuccessfulResponse, InternetMenu.mGetBoardsURL)) {

        		if (connectionSuccessfulResponse.getJSONObject().isNull("data")) {
        			setUploadListEmpty();
        			LAST_ACCESSABLE_PAGE = (pageNumber-1 < LAST_ACCESSABLE_PAGE) ? pageNumber-1 : LAST_ACCESSABLE_PAGE;
        			if (mViewPager.getCurrentItem() > LAST_ACCESSABLE_PAGE) mViewPager.setCurrentItem(LAST_ACCESSABLE_PAGE);
        		} else {
        			JSONArray jBoards = connectionSuccessfulResponse.getJSONObject().getJSONArray(ConnectionUtils.returnData);

        			int i;
        			synchronized(mBoardListLock) {
        				for(i=0;i < jBoards.length();i++){
        					InternetBoard internetBoard = new InternetBoard(jBoards.getJSONObject(i));
        					mBoardList.add(internetBoard);
        				}
        			}
        			if (i < mMaxResults) {
        				LAST_ACCESSABLE_PAGE = pageNumber;
        			}
        		}
        		populateList();

        	} else {
        		Log.e(TAG, "No id matched with " + connectionSuccessfulResponse.getConnectionId());
        	}
        }

		@Override
		public void onConnectionError(ConnectionErrorResponse connectionErrorResponse) {
			ConnectionUtils.connectionError(DownloadBoardList.this, connectionErrorResponse, TAG);
		}
		
		private void setUploadListEmpty() {
    		synchronized(mBoardListLock) {
    			InternetBoard internetBoard = new InternetBoard();
    			internetBoard.setBoardName("No boards here");
    			internetBoard.setUploaderUsername("");
    			internetBoard.setRating("");
    			internetBoard.setBoardId(0);
    			mBoardList.add(internetBoard);
    		}
    		populateList();
    	}
    }

	private class ViewPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return LAST_PAGE_COUNT;
		}

		/**
		 * Create the page for the given position.  The adapter is responsible
		 * for adding the view to the container given here, although it only
		 * must ensure this is done by the time it returns from
		 * {@link #finishUpdate()}.
		 *
		 * @param container The containing View in which the page will be shown.
		 * @param position The page position to be instantiated.
		 * @return Returns an Object representing the new page.  This does not
		 * need to be a View, but can be some other container of the page.
		 */
		@Override
		public Object instantiateItem(View collection, int position) {
			
			if (position < LAST_ACCESSABLE_PAGE-2) {
				LAST_ACCESSABLE_PAGE = LAST_PAGE_COUNT_MAX;
			}
			
			LayoutInflater layoutInflater = DownloadBoardList.this.getLayoutInflater();
			View layout = layoutInflater.inflate(R.layout.internet_download_list, (ViewGroup) findViewById(R.id.root));
			
			ListViewAdapter listViewAdapter = new ListViewAdapter(position);
			listViewAdapter.mListView = (ListView) layout.findViewById(R.id.listView);
			listViewAdapter.getBoards();
	        
	        ((ViewPager) collection).addView(layout,0);

			return layout;
		}

		/**
		 * Remove a page for the given position.  The adapter is responsible
		 * for removing the view from its container, although it only must ensure
		 * this is done by the time it returns from {@link #finishUpdate()}.
		 *
		 * @param container The containing View from which the page will be removed.
		 * @param position The page position to be removed.
		 * @param object The same object that was returned by
		 * {@link #instantiateItem(View, int)}.
		 */
		@Override
		public void destroyItem(View collection, int position, Object view) {
			((ViewPager) collection).removeView((LinearLayout) view);
		}


		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view==((LinearLayout)object);
		}

		/**
		 * Called when the a change in the shown pages has been completed.  At this
		 * point you must ensure that all of the pages have actually been added or
		 * removed from the container as appropriate.
		 * @param container The containing View which is displaying this adapter's
		 * page views.
		 */
		@Override
		public void finishUpdate(View arg0) {}


		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {}

	}

}