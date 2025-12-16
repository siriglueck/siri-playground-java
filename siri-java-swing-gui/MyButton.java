import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MyButton extends JButton implements ActionListener {
    // we can also have buttons in frame and handle it there with different actions

    MyButton() {
        this.setBounds(0,100,300,200);
        this.setText("My Button");
        this.addActionListener(this); // important to tell that this is the action listener
        // inside this listener we can use a lambda function and then we dont need the implements above and no need the function down there
        // see the example in MyFrame
        this.setFocusable(false); // to hide the line about button label

        ImageIcon icon = new ImageIcon("siri-logo.png");
        this.setIcon(icon);
        this.setHorizontalTextPosition(JButton.CENTER);
        this.setVerticalTextPosition(JButton.BOTTOM);
        this.setFont(new Font("Comic Sans", Font.BOLD,25));
        this.setBackground(Color.LIGHT_GRAY);
        this.setForeground(Color.GREEN);
        this.setIconTextGap(-15);
        this.setBorder(BorderFactory.createEtchedBorder());
        //this.setEnabled(false);
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==this) {
            System.out.println("My button is clicked");
        }
    }
}
