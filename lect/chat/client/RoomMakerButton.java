package lect.chat.client;

import javax.swing.*;

public class RoomMakerButton extends JButton {

    public void toButton(String rmb) {
        setActionCommand(rmb);
        setText(getActionCommand());
    }
}
