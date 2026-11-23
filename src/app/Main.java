package app;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import ui.MainFrame;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // if the system look is not available, Swing keeps its default look
        }
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
