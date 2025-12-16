import java.awt.*;
import javax.swing.*;

public class GridLayoutExample extends JPanel {
    GridLayoutExample() {
        //setLayout(new GridLayout(9,1));
        this.setLayout(new GridLayout(3,3, 0,0));
        this.add(new JButton("1"));
        this.add(new JButton("2"));
        this.add(new JButton("3"));
        this.add(new JButton("4"));
        add(new JButton("5"));
        add(new JButton("6"));
        add(new JButton("7"));
        add(new JButton("8"));
        add(new JButton("9"));
        //can have this or without this, both work

    }
}
