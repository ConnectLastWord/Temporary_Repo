package lect.chat.client;

import lect.chat.client.p2p.FileReceiver;
import lect.chat.client.p2p.event.FileProgressListener;
import lect.chat.protocol.ChatCommandUtil;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class ChatTextPane extends JTextPane implements FileProgressListener {
    private static final long serialVersionUID = 1L;
    private SimpleDateFormat formatter = new SimpleDateFormat("MMdd_HH_mm_ss");
    AttributeSet normalAttrSet;
    AttributeSet whisperAttrSet;
    AttributeSet enterExitAttrSet;
    private int linesToHold = 20;
    private int maxLines = 40;
    private boolean recordRemovedMsg = true;
    private JProgressBar curActiveProgressBar;

    public ChatTextPane() {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        normalAttrSet = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.DARK_GRAY);

        //attSet = sc.addAttribute(attSet, StyleConstants.FontFamily, "Lucida Console");
        normalAttrSet = sc.addAttribute(normalAttrSet, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        sc = new StyleContext();
        enterExitAttrSet = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLUE.darker());
        enterExitAttrSet = sc.addAttribute(enterExitAttrSet, StyleConstants.FontFamily, "Lucida Console");
        enterExitAttrSet = sc.addAttribute(enterExitAttrSet, StyleConstants.Italic, true);
        enterExitAttrSet = sc.addAttribute(enterExitAttrSet, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        sc = new StyleContext();
        whisperAttrSet = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.PINK);
        //whisperAttrSet = sc.addAttribute(whisperAttrSet, StyleConstants.FontFamily, "Lucida Console");
        whisperAttrSet = sc.addAttribute(whisperAttrSet, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
        FileReceiver.addFileProgressListener(this);
    }

    // 채팅창 초기화
    public void initDisplay() {
        setText("");
    }

    public void append(String msg, char command) {
        AttributeSet attrset;
        switch (command) {
            case ChatCommandUtil.WHISPER:
                attrset = whisperAttrSet;
                break;
            case ChatCommandUtil.ENTER_ROOM:
            case ChatCommandUtil.EXIT_ROOM:
                attrset = enterExitAttrSet;
                break;
            case ChatCommandUtil.NORMAL:
            default:
                attrset = normalAttrSet;
                break;
        }
        Document doc = getDocument();
        int count = doc.getDefaultRootElement().getElementCount();
        try {
            doc.insertString(doc.getLength(), msg, attrset);
            System.out.println("line count: " + count);
            if (count >= maxLines) {
                int line = count - linesToHold - 1;
                Element map = getDocument().getDefaultRootElement();
                Element lineElem = map.getElement(line);
                int endOffset = lineElem.getEndOffset();
                // hide the implicit break at the end of the document
                endOffset = ((line == count - 1) ? (endOffset - 1) : endOffset);
                if (recordRemovedMsg) saveBeforeRemove(endOffset);
                System.out.println();
                doc.remove(0, endOffset);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setRecordRemovedMsg(boolean rrm) {
        recordRemovedMsg = rrm;
    }

    private void saveBeforeRemove(int len) throws BadLocationException {
        String contentToSave = getText(0, len);
        FileWriter fw = null;
        try {
            File f = new File("saved_messages");
            if (!f.exists()) {
                f.mkdir();
            }
            f = new File(f, formatter.format(new java.util.Date()) + ".txt");
            fw = new FileWriter(f);
            fw.write(contentToSave);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fw != null) try {
                fw.close();
            } catch (IOException e) {
            }
        }
    }

    @Override
    public void progressWillStart() {
        Document doc = getDocument();
        curActiveProgressBar = new JProgressBar();
        curActiveProgressBar.setStringPainted(true);
        setCaretPosition(getDocument().getLength());
        insertComponent(curActiveProgressBar);
        try {
            doc.insertString(doc.getLength(), "\n", null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fileProgressed(String msg, int p) {
        if (p == curActiveProgressBar.getValue()) return;
        curActiveProgressBar.setValue(p);
        curActiveProgressBar.setString(msg);
    }

    @Override
    public void progressFinished() {
        // TODO Auto-generated method stub

    }
}
