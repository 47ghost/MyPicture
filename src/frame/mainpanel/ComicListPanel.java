package frame.mainpanel;

import javax.swing.*;
import java.awt.*;


public class ComicListPanel extends JPanel {
    public ComicListPanel() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("未开发", SwingConstants.CENTER);
        Font largerFont = new Font("Serif", Font.BOLD, 36);
        label.setFont(largerFont);
        add(label, BorderLayout.CENTER);
    }
}
