import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewWindowLaunchPage extends JPanel implements ActionListener {
    JFrame launchFrame = new JFrame();
    JButton myButton = new JButton("New Window");

    NewWindowLaunchPage(){
        myButton.setBounds(100,160,200,40);
        myButton.setFocusable(false);
        myButton.addActionListener(this);
        launchFrame.add(myButton);
        launchFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        launchFrame.setSize(420,420);
        launchFrame.setLayout(null);
        launchFrame.setVisible(true);
        this.add(launchFrame);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==myButton) {
            launchFrame.dispose();
            NewWindow newWindow = new NewWindow();
        }
    }
}
