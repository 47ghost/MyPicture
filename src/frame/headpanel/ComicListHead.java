package frame.headpanel;

import javax.swing.*;
import java.awt.*;

public class ComicListHead extends JPanel {

public ComicListHead(){
    setBackground(new Color(0xB5C3C3));
    setLayout(new BorderLayout());
    JLabel label = new JLabel("空", SwingConstants.CENTER);
    Font largerFont = new Font("Serif", Font.BOLD, 28);
    label.setFont(largerFont);
    add(label, BorderLayout.CENTER);
}
}
