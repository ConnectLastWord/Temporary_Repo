package lect.chat.client;
import java.io.*;
import java.text.*;
import javax.swing.text.*;
import javax.swing.*;
public class ChatTextArea extends JTextArea {
	private static final long serialVersionUID = 1L;
	private int linesToHold = 50;
	private int maxLines = 100;
	private boolean recordRemovedMsg;
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
	public ChatTextArea () {
		setEditable(false);
	}
	public void setLinesToHold(int num) {
		linesToHold = num;
	}
	public void setMaxLines(int num) {
		maxLines = num;
	}
	public void setRecordRemovedMsg(boolean rrm) {
		recordRemovedMsg = rrm;
	}
	public void append(String str) {
		super.append(str);
		int count = getLineCount();
		if(count >= maxLines) {
			try {
				int endOffset = getLineEndOffset(count - 1 - linesToHold);
				if(recordRemovedMsg) saveBeforeRemove(endOffset);
			this.replaceRange("", 0, endOffset);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		setCaretPosition(getDocument().getLength());
	}
	private void saveBeforeRemove(int len) throws BadLocationException {
		String contentToSave = getText(0, len);
		FileWriter fw = null;
		try {
			File f = new File("saved_messages");
			if(!f.exists()) {
				f.mkdir();
			}
			f = new File(f, formatter.format(new java.util.Date()) + ".txt");
			fw = new FileWriter(f);
			fw.write(contentToSave);
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if(fw != null) try { fw.close(); } catch(IOException e) {}
		}
	}
}