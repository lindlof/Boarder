package fi.mikuz.boarder.gui.internet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.thoughtworks.xstream.XStream;

import fi.mikuz.boarder.R;
import fi.mikuz.boarder.app.BoarderListActivity;
import fi.mikuz.boarder.component.internet.Comment;
import fi.mikuz.boarder.component.internet.InternetFullBoard;
import fi.mikuz.boarder.connection.ConnectionErrorResponse;
import fi.mikuz.boarder.connection.ConnectionListener;
import fi.mikuz.boarder.connection.ConnectionManager;
import fi.mikuz.boarder.connection.ConnectionSuccessfulResponse;
import fi.mikuz.boarder.connection.ConnectionUtils;
import fi.mikuz.boarder.util.TimeoutProgressDialog;

/**
 * 
 * @author Jan Mikael Lindlöf
 */
public class DownloadBoardComments extends BoarderListActivity implements ConnectionListener, OnScrollListener {
	private static final String TAG = "InternetDownloadBoardComments";
	
	private ListView mListView;
	private List<Comment> mList;
	
	final Handler mHandler = new Handler();
	TimeoutProgressDialog mWaitDialog;
	
	private InternetFullBoard mBoard;
	
	private boolean mLoggedIn;
	private String mUserId;
	private String mSessionToken;
	
	private int mMaxResults;
	private int mListRequestCount;
	private int mListServerRequestCount;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.internet_download_comments_list);
		this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		registerForContextMenu(getListView());
		
		Bundle extras = getIntent().getExtras();
		XStream xstream = new XStream();
		mBoard = (InternetFullBoard) xstream.fromXML(extras.getString(DownloadBoard.BOARD_KEY));
		mLoggedIn = extras.getBoolean(DownloadBoardList.LOGGED_IN_KEY);
		
		if (mLoggedIn) {
			mUserId = extras.getString(InternetMenu.USER_ID_KEY);
			mSessionToken = extras.getString(InternetMenu.SESSION_TOKEN_KEY);
		}
		
		this.setTitle(mBoard.getUploaderUsername() + " - " + mBoard.getBoardName());
		
		mListView = (ListView)findViewById(android.R.id.list);
		mList = new ArrayList<Comment>();
		mMaxResults = 40;
		refreshList();
		
		Button sendComment = (Button) findViewById(R.id.sendComment);
		final EditText commentInput = (EditText) findViewById(R.id.commentInput);
		
		if (mLoggedIn) {
    		sendComment.setOnClickListener(new OnClickListener() {
    			public void onClick(View v) {
    				if (commentInput.getText().toString().length() > 0) {
	    				mWaitDialog = new TimeoutProgressDialog(DownloadBoardComments.this, "Waiting for response", TAG, false);
	    				HashMap<String, String> sendList = new HashMap<String, String>();
	    				sendList.put(InternetMenu.BOARD_ID_KEY, Integer.toString(mBoard.getBoardId()));
	    				sendList.put(InternetMenu.SESSION_TOKEN_KEY, mSessionToken);
	    				sendList.put(InternetMenu.USER_ID_KEY, mUserId);
	    				sendList.put(InternetMenu.COMMENT_KEY, commentInput.getText().toString());
	    				commentInput.setText("");
	    				new ConnectionManager(DownloadBoardComments.this, InternetMenu.mCommentURL, sendList);
    				} else {
    					Toast.makeText(DownloadBoardComments.this, "Type your comment...", Toast.LENGTH_LONG).show();
    				}
    			}
    		});
    	} else {
    		commentInput.setHint("Login to comment");
    		commentInput.setFocusable(false);
    	}
		
		getListView().setOnScrollListener(this);
	}
	
	private void refreshList() {
		mList.clear();
		mListRequestCount = 0;
		mListServerRequestCount = 0;
		getComments();
		mListRequestCount++;
	}
    
    private void populateList() {
    	if (mListView.getAdapter() == null) {
    		Log.d(TAG, "Creating adapter");
    		CommentListAdapter adapter = new CommentListAdapter(mList, this);
    		mListView.setAdapter(adapter);
    		Log.d(TAG, "count is " + adapter.getCount());
    	} else {
    		Log.d(TAG, "Refreshing list, sendin " + mList.size() + " elements");
    		((CommentListAdapter)mListView.getAdapter()).refill(mList);
    		Log.d(TAG, "count is " + ((CommentListAdapter)mListView.getAdapter()).getCount());
    	}
    }
	
	private synchronized void getComments() { // TODO autorefresh first messages?
		HashMap<String, String> sendList = new HashMap<String, String>();
		sendList.put(InternetMenu.BOARD_ID_KEY, Integer.toString(mBoard.getBoardId()));
		sendList.put(InternetMenu.REQUEST_COUNT_KEY, Integer.toString(mListServerRequestCount));
		sendList.put(InternetMenu.MAX_RESULTS_KEY, Integer.toString(mMaxResults));
		new ConnectionManager(DownloadBoardComments.this, InternetMenu.mGetCommentsURL, sendList);
	}
	
	private void setListEmpty() {
		Comment comment = new Comment();
		comment.setUsername("");
		comment.setComment("Nothing here!");
		
		mList.add(comment);
	}
	
	private void addListLoading() {
		Comment comment = new Comment();
		comment.setUsername("");
		comment.setComment("   - Loading -");
		
		mList.add(comment);
	}
	
	private void removeSpecialObjects() {
		for (Comment comment : mList) {
			if (comment.getComment().equals("   - Loading -") && comment.getUsername().equals("")) mList.remove(comment);
		}
	}
    
	@Override
	public synchronized void onScroll(AbsListView view, int firstVisible, int visibleCount, int totalCount) {
		if(firstVisible + visibleCount >= totalCount-11 && totalCount >= (mListRequestCount)*mMaxResults) {
			getComments();
			mListRequestCount++;
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView v, int s) {}
	
    public class CommentListAdapter extends BaseAdapter {

		private List<Comment> commentList;
     
        private Context context;
     
        public CommentListAdapter(List<Comment> comments, Context context) {
        	commentList = new ArrayList<Comment>();
            commentList.addAll(comments);
            this.context = context;
        }
        
        public void add(Comment comment) {
        	this.commentList.add(comment);
        }
     
        public int getCount() {
            return commentList.size();
        }
     
        public Comment getItem(int position) {
            return commentList.get(position);
        }
     
        public long getItemId(int position) {
            return commentList.get(position).getId();
        }
     
        public View getView(int position, View convertView, ViewGroup parent) {
        	LinearLayout itemLayout;
            Comment comment = getItem(position);
     
            itemLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.internet_download_comments_row, parent, false);
     
            TextView tvUsername = (TextView) itemLayout.findViewById(R.id.username);
            tvUsername.setText(comment.getUsername());
     
            TextView tvComment = (TextView) itemLayout.findViewById(R.id.comment);
            tvComment.setText(comment.getComment());
     
            return itemLayout;
        }
        
        public void refill(final List<Comment> comment) {
        	commentList.clear();
        	commentList.addAll(comment);
            notifyDataSetChanged();
        }
     
    }

	@Override
	public void onConnectionSuccessful(ConnectionSuccessfulResponse connectionSuccessfulResponse) throws JSONException {
		ConnectionUtils.connectionSuccessful(DownloadBoardComments.this, connectionSuccessfulResponse);
		
		if (ConnectionUtils.checkConnectionId(connectionSuccessfulResponse, InternetMenu.mGetCommentsURL)) {
			if (connectionSuccessfulResponse.getJSONObject().isNull("data")) {
				Log.d(TAG, "Got null");
				setListEmpty();
			} else {
				JSONArray jComments = connectionSuccessfulResponse.getJSONObject().getJSONArray(ConnectionUtils.returnData);
				removeSpecialObjects();
				
				int i;
				for(i=0;i < jComments.length();i++) {
					Comment comment = new Comment(jComments.getJSONObject(i));
					mList.add(comment);
				}
				mListServerRequestCount++;
				if (i >= 40) addListLoading();
			}
			populateList();
		} else if (ConnectionUtils.checkConnectionId(connectionSuccessfulResponse, InternetMenu.mCommentURL)) {
			mWaitDialog.dismiss();
			refreshList();
		} else {
			Log.e(TAG, "No id matched with " + connectionSuccessfulResponse.getConnectionId());
		}
	}

	@Override
	public void onConnectionError(ConnectionErrorResponse connectionErrorResponse) {
		ConnectionUtils.connectionError(this, connectionErrorResponse, TAG);
		if (ConnectionUtils.checkConnectionId(connectionErrorResponse, InternetMenu.mCommentURL)) {
			mWaitDialog.dismiss();
		}
	}
	
}
