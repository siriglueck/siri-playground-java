import javax.swing.*;
import java.awt.*;

public class FlowLayoutExample extends JPanel {
    FlowLayoutExample(){
        setLayout(new FlowLayout(FlowLayout.RIGHT,10,10));

        JPanel secondPanel = new JPanel();
        secondPanel.setLayout(new FlowLayout());
        secondPanel.setBackground(Color.lightGray);
        secondPanel.setPreferredSize(new Dimension(100 ,200));
        secondPanel.add(new JButton("1"));
        secondPanel.add(new JButton("2"));
        secondPanel.add(new JButton("3"));
        secondPanel.add(new JButton("4"));
        secondPanel.add(new JButton("5"));
        secondPanel.add(new JButton("6"));
        secondPanel.add(new JButton("7"));
        secondPanel.add(new JButton("8"));
        secondPanel.add(new JButton("9"));

        add(secondPanel);
    }
}
