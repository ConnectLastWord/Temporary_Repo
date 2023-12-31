package lect.chat.client.components;

import lect.chat.client.connect.ChatStatusListener;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class StatusBar extends JPanel implements ChatStatusListener {
    // 유저 정보 출력
    private JLabel statusText;
    private static StatusBar statusBar;
    private GridBagConstraints c = new GridBagConstraints();

    private StatusBar() {
        super(new GridBagLayout());
        statusText = new JLabel();
        statusText.setHorizontalAlignment(SwingConstants.LEFT);
        c.weightx = 0.5;
        c.fill = GridBagConstraints.HORIZONTAL;
        this.add(statusText, c);
        statusText.setText("User Name: ");
        //this.add(progBar, c);
    }

    public static StatusBar getStatusBar() {
        if (statusBar == null) statusBar = new StatusBar();
        return statusBar;
    }

    public void chatStatusChanged(Object obj) {
        statusText.setText(obj.toString());
    }

    public void setUserName(String userName) {
        statusText.setText("User Name: " + userName);
    }
}
