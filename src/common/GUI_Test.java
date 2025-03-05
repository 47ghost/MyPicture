package common;

import com.formdev.flatlaf.FlatLightLaf;
import frame.MainFrame;

import javax.swing.*;

public class GUI_Test {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel( new FlatLightLaf() );
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF" );
        }

        DataProcessing dp = DataProcessing.getInstance();
        MainFrame mainFrame = new MainFrame(dp);
        mainFrame.initFrame();




    }
}
