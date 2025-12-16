import javax.swing.*;
import java.awt.*;

public class MyFrame extends JFrame {
    // ### Constructor ###
    // JFrame = a GUI window to add components to
    MyFrame(){
        // JFrame frame = new JFrame(); // create a frame
        this.setTitle("JFrame title goes here"); // sets title of frame
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // exit after close button
        this.setSize(420,420); // set x and y size of frame
        // this.setResizable(false); // prevent resizing frame
        //this.setVisible(true); // make frame visible

        ImageIcon image = new ImageIcon("siri-logo.png"); // create an ImageIcon
        this.setIconImage(image.getImage()); // change the icon of the frame

        //this.getContentPane().setBackground(new Color(123,50,250)); // change color of background

        // ## JButton 3/3
        /*
        JButton button1 = new JButton("btn1");
        button1.setBounds(0,50,100,20);
        button1.addActionListener(e -> System.out.println("btn1 clicked!"));
        this.add(button1);

        JButton button2 = new JButton("btn2");
        button2.setBounds(120,50,100,20);
        button2.addActionListener(e -> System.out.println("btn2 clicked!"));
        this.add(button2);
        */

    }
}
