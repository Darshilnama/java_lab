package main.java.libraryapp;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LibraryFrame frame = new LibraryFrame();
            frame.setVisible(true);
        });
    }
}
