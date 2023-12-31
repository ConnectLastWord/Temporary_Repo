package lect.chat.client.components;

import javax.swing.*;

@SuppressWarnings("serial")
public class CommandButton extends JButton {
    public static final String CMD_DISCONNECT = "Log out";
    public static final String CMD_CONNECT = "Log in";

    public CommandButton() {
        this(CMD_CONNECT);
    }

    public CommandButton(String labelCmd) {
        this(labelCmd, labelCmd);
    }

    public CommandButton(String label, String cmd) {
        super(label);
        setActionCommand(cmd);
    }

    public void toButton(String cmd) {
        setActionCommand(cmd);
        setText(getActionCommand());
    }
}
