package fi.mikuz.boarder.util.editor;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.thoughtworks.xstream.XStream;

import fi.mikuz.boarder.component.soundboard.GraphicalSound;
import fi.mikuz.boarder.component.soundboard.GraphicalSoundboard;
import fi.mikuz.boarder.gui.BoardEditor;
import fi.mikuz.boarder.util.XStreamUtil;

public class EditorLastState {
	private static final String TAG = EditorLastState.class.getSimpleName();
	
	private BoardEditor editor;
	
	private GraphicalSoundboard lastPage;
	private GraphicalSound lastPressedSound;
	
	public EditorLastState(BoardEditor editor, Bundle extras) {
		this.editor = editor;
		
		XStream xstream = XStreamUtil.graphicalBoardXStream();
        String lastPressedSound = null;
        String lastPage = null;
        try {
        	lastPressedSound = extras.getString(XStreamUtil.SOUND_KEY);
        	lastPage = extras.getString(XStreamUtil.SOUNDBOARD_KEY);
        } catch (NullPointerException e) {}
        if (lastPressedSound != null) {
        	Log.v(TAG, "Recovering last pressed sound as pressed.");
        	// revert pressed sound being added to board when saving
        	GraphicalSound sound = (GraphicalSound) xstream.fromXML(lastPressedSound);
        	GraphicalSoundboard page = (GraphicalSoundboard) xstream.fromXML(lastPage);
        	
        	if (editor.mGsb.getId() == page.getId()) {
        		editor.mPressedSound = sound;
        		editor.mGsb.getSoundList().remove(sound);
        		this.lastPage = editor.mGsb;
        	} else {
        		this.lastPage = page;
        	}
        	this.lastPressedSound = sound;
        }
	}
	
	public GraphicalSoundboard getLastPage() {
		return lastPage;
	}
	
	public GraphicalSound getLastPressedSound() {
		return lastPressedSound;
	}
	
	public void saveEditorState(Context context, Bundle outState) {
		Bundle pressedSoundBundle = XStreamUtil.getSoundBundle(context, editor.mPressedSound, editor.mGsb);
    	outState.putAll(pressedSoundBundle);
	}
}
