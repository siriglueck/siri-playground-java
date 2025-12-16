import java.awt.*;
import javax.swing.*;

public class BorderLayoutExample extends JPanel {

    public BorderLayoutExample() {
        // instead of myFrame.setLayout(..)
        this.setLayout(new BorderLayout(10,10));

        // -- main layout
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();
        JPanel panel4 = new JPanel();
        JPanel panel5 = new JPanel();

        panel1.setBackground(Color.red);
        panel2.setBackground(Color.yellow);
        panel3.setBackground(Color.blue);
        panel4.setBackground(Color.green);
        panel5.setBackground(Color.white);

        // here instead of myFrame.add(...) -> add(...)
        add(panel1, BorderLayout.NORTH);
        add(panel2, BorderLayout.SOUTH);
        add(panel3, BorderLayout.EAST);
        add(panel4, BorderLayout.WEST);
        add(panel5, BorderLayout.CENTER);

        panel1.setPreferredSize(new Dimension(100,100));
        panel2.setPreferredSize(new Dimension(100,100));
        panel3.setPreferredSize(new Dimension(100,100));
        panel4.setPreferredSize(new Dimension(100,100));
        panel5.setPreferredSize(new Dimension(100,100));

        panel5.setLayout(new BorderLayout(10,10));

        // -- sub layout
        JPanel panel6 = new JPanel();
        JPanel panel7 = new JPanel();
        JPanel panel8 = new JPanel();
        JPanel panel9 = new JPanel();

        panel6.setBackground(Color.black);
        panel7.setBackground(Color.gray);
        panel8.setBackground(Color.lightGray);
        panel9.setBackground(Color.darkGray);

        panel6.setPreferredSize(new Dimension(50,50));
        panel7.setPreferredSize(new Dimension(50,50));
        panel8.setPreferredSize(new Dimension(50,50));
        panel9.setPreferredSize(new Dimension(50,50));

        panel5.add(panel6, BorderLayout.NORTH);
        panel5.add(panel7, BorderLayout.SOUTH);
        panel5.add(panel8, BorderLayout.EAST);
        panel5.add(panel9, BorderLayout.WEST);
    }
}
