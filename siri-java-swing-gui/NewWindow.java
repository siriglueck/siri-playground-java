import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NewWindow extends JFrame implements ActionListener {
    JLabel label = new JLabel("More!");
    JButton nextPageBtn = new JButton("Next Same Page");

    NewWindow(){
        label.setBounds(0,0,100,50);
        label.setFont(new Font(null, Font.PLAIN,25));
        nextPageBtn.setBounds(0,50,200,40);
        nextPageBtn.addActionListener(this);
        this.add(label);
        this.add(nextPageBtn);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(200,200);
        this.setLayout(null);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==nextPageBtn) {
            //this.dispose();
            NewWindow newWindow = new NewWindow();
        }
    }
}
