import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.print.PrinterException;
import javax.swing.*;
import javax.swing.undo.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.text.rtf.RTFEditorKit;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class TextEditor {

    // ### Instance variable ### //
    private JFrame frame;
    // private JTextArea textArea; , change to JTextPane (better for Rich Text Format)
    private JTextPane textArea;
    private StyledDocument doc; // no need to initial like an object, can use it right away ex. doc = textPane.getStyleDocument();
    private JPanel southPanel;
    private JLabel lbInfo; // be added in southPanel
    private JLabel lbCursorPos;
    private File currentFile; // to store the file path and be displayed in lbInfo
    private JPanel northPanel;
    private JToolBar toolBar;
    private UndoManager undoManager = new UndoManager(); // can create an object already, since the object remains itself (no further customization)
    private JComboBox<String> fontCombo;
    private JComboBox<Integer> sizeCombo;
    private JButton colorBtn;
    private boolean isUpdatingStyle = false;


    /*
     * These instances above are private, can be accessed within this class only
     * When we create a new instance within this class
     * We write only -> frame = new JFrame("Title");
     * Instead of -> JFrame frame = new JFrame("Title");
     */

    // ### Constructor - Constructor has the same name as Class, no return 
    public TextEditor() {
        // Frame
        frame = new JFrame("Text Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,600);
        frame.setMinimumSize(new Dimension(400, 300));

        // Menu Bar
        JMenuBar mnHauptmenue = new JMenuBar();
        frame.setJMenuBar(mnHauptmenue);

        // Tool Bar
        toolBar = new JToolBar();
        toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
        //toolBar.setLayout(new FlowLayout(FlowLayout.LEFT));
        toolBar.setFloatable(false);

        northPanel = new JPanel();
        northPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        //northPanel.setPreferredSize(new Dimension(800,70));

        placeToolBar(toolBar);
        northPanel.add(toolBar);
        //frame.getContentPane().add(toolBar, BorderLayout.PAGE_START);
        frame.add(northPanel, BorderLayout.NORTH);
        //frame.add(northPanel, BorderLayout.NORTH);


        // This listener helps toolbar to be dynamically resized its height and rerendered according to the frame size
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int frameWidth = frame.getWidth();
                int newHeight = 0;

                if (frameWidth >= 700 ) {
                    newHeight = 50;
                } else if (frameWidth <= 700) {
                    newHeight = 90;
                } else {
                    // Linear scaling is a method of proportional adjustment or a system where the output changes proportionally to the input.
                    // to be research
                }

                toolBar.setPreferredSize(new Dimension(frameWidth, newHeight));
                toolBar.revalidate(); // update layout
            }
        });



        // South Panel
        southPanel = new JPanel();
        southPanel.setPreferredSize(new Dimension(800,20));
        frame.getContentPane().add(southPanel, "South");

        // Text Area
        textArea = new JTextPane();
        // Replace Text Area (.txt) with Text Pane (.rtf) because we want to style texts
        /*
        // textArea.setLineWrap(true);
        // textArea.setWrapStyleWord(true);
        textArea.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                int caretPos = textArea.getCaretPosition();
                try {
                    int row = textArea.getLineOfOffset(caretPos); //current position start from 0
                    int col = caretPos - textArea.getLineStartOffset(row); //คำนวณจากระยะห่างระหว่าง caret กับต้นบรรทัด
                    lbCursorPos.setText("Cursorposition Zeile " + (row + 1) + ", Spalte " + (col + 1));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
        //textArea.getDocument().addUndoableEditListener(undoMng); // add the undo manager
        textArea.getDocument().addUndoableEditListener(new UndoableEditListener() {
            public void undoableEditHappened(UndoableEditEvent e) {
                undoMng.addEdit(e.getEdit());
            }
        });
         */

        updateStyleControls();
        doc = textArea.getStyledDocument();
        doc.addUndoableEditListener(new UndoableEditListener() {
            public void undoableEditHappened(UndoableEditEvent e) {
                undoManager.addEdit(e.getEdit());
            }
        });

        textArea.addCaretListener(e -> updateStyleControls());

        // Ctrl+Z for Undo
        textArea.getInputMap().put(KeyStroke.getKeyStroke("control Z"),"Undo");
        textArea.getActionMap().put("Undo", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canUndo()){
                    undoManager.undo();
                }
            }
        });
        // Ctrl+Y for Redo
        textArea.getInputMap().put(KeyStroke.getKeyStroke("control Y"),"Redo");
        textArea.getActionMap().put("Redo", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (undoManager.canRedo()){
                    undoManager.redo();
                }
            }
        });


        // Scroll bar of textAre
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        frame.getContentPane().add(scrollPane, "Center");

        // Right panel
        String[] listItems = {"Eintrag1", "Eintrag2", "Eintrag3", "Eintrag4", "Eintrag5"};
        JList<String> list = new JList<>(listItems);
        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e){
                if(e.getClickCount() == 1){
                    String selectedItem = list.getSelectedValue();
                    if(selectedItem != null){
                        int pos = textArea.getCaretPosition();
                        try {
                            textArea.getDocument().insertString(pos, selectedItem + "\n", null);
                            textArea.requestFocus();
                        } catch (Exception ex) {
                            // TODO: handle exception
                            ex.printStackTrace();
                        }
                    }
                }
            }
        });
        JScrollPane listScroll = new JScrollPane(list);
        listScroll.setPreferredSize(new Dimension(150,0));
        frame.add(listScroll, BorderLayout.EAST);

        // build a tree        
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Projekt");
        DefaultMutableTreeNode util1 = new DefaultMutableTreeNode("Hilfsmittel1");
        DefaultMutableTreeNode util2 = new DefaultMutableTreeNode("Hilfsmittel2");
        DefaultMutableTreeNode util3 = new DefaultMutableTreeNode("Hilfsmittel3");
        DefaultMutableTreeNode src1 = new DefaultMutableTreeNode("Quellen1");
        DefaultMutableTreeNode src2 = new DefaultMutableTreeNode("Quellen2");
        DefaultMutableTreeNode src3 = new DefaultMutableTreeNode("Quellen3");
        util1.add(util2);
        util1.add(util3);
        src1.add(src2);
        src1.add(src3);
        root.add(src1);
        root.add(util1);

        JTree tree = new JTree(root);
        // when select a node
        tree.addTreeSelectionListener(e -> {
            TreePath path = e.getPath();
            Object[] nodes = path.getPath();
            StringBuilder treePath = new StringBuilder();

            for (Object node : nodes) {
                treePath.append(node.toString());
                if (node != nodes[nodes.length - 1]) {
                    treePath.append(" > ");
                }
            }
            lbInfo.setText("Path: " + treePath);
        });

        JScrollPane treeScroll = new JScrollPane(tree);
        treeScroll.setPreferredSize(new Dimension(150,0));
        frame.add(treeScroll, BorderLayout.WEST);


        // Function calls
        placeMenuElements(mnHauptmenue); //textArea, frame
        placeSouthPanelElements(southPanel); // textArea, frame
        frame.setVisible(true);
    }

    // ### Methods ### //

    private void updateStyleControls() {
        isUpdatingStyle = true;

        // # Early Exit - if the document is empty (New File), then set styles (font name, color, size) to default, and not proceed further
        if (textArea.getDocument().getLength() == 0) {
            if (fontCombo != null) fontCombo.setSelectedItem("Arial");
            if (sizeCombo != null) sizeCombo.setSelectedItem(12);
            if (colorBtn != null) colorBtn.setBackground(Color.BLACK);
            isUpdatingStyle = false; // close the flag
            return; // exit
        }

        // if the document is not empty (Open File), then ..
        int pos = textArea.getCaretPosition();
        StyledDocument doc = textArea.getStyledDocument();
        Element element;
        if (pos > 0) {
            element = doc.getCharacterElement(pos - 1);
        } else {
            element = doc.getCharacterElement(pos);
        }

        AttributeSet as = element.getAttributes();

        // to update Font Style
        String font = StyleConstants.getFontFamily(as);
        if (fontCombo != null && !font.equals(fontCombo.getSelectedItem())) {
            fontCombo.setSelectedItem(font);
        }

        // to update Font Size
        int size = StyleConstants.getFontSize(as);
        if (sizeCombo != null && size != (Integer) sizeCombo.getSelectedItem()) {
            sizeCombo.setSelectedItem(size);
        }

        // to update Color Button
        Color color = StyleConstants.getForeground(as);
        colorBtn.setBackground(color);

        isUpdatingStyle = false;


    }

    private void placeMenuElements(JMenuBar menu){

        // *** Menu Datei *** 
        JMenu mnDatei = new JMenu("Datei");
        mnDatei.setMnemonic('D');
        menu.add(mnDatei); // refer to mnHauptMenu

        // New file
        JMenuItem mnNew = new JMenuItem("Neu");
        mnNew.setMnemonic('N');
        mnNew.addActionListener(e -> newFile());
        mnDatei.add(mnNew);

        // Open
        JMenuItem mnOpen = new JMenuItem("Öffen");
        mnOpen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });
        mnOpen.setMnemonic('Ö');
        mnDatei.add(mnOpen);

        // Save
        JMenuItem mnSave = new JMenuItem("Speichen");
        mnSave.setMnemonic('S');
        mnSave.addActionListener(e -> saveFile());
        mnDatei.add(mnSave);

        // Save as 
        JMenuItem mnSaveAs = new JMenuItem("Speichen unter");
        mnSaveAs.addActionListener(e -> saveFileAs());
        mnDatei.add(mnSaveAs);

        mnDatei.addSeparator(); // add a seperator

        // End programme
        JMenuItem mnEnd = new JMenuItem("Beenden");
        mnEnd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        mnEnd.setMnemonic('B');
        mnDatei.add(mnEnd);

        // *** Menu Bearbeiten ***
        JMenu mnEdit = new JMenu("Bearbeiten");
        mnEdit.setMnemonic('B');
        menu.add(mnEdit);

        // Select All
        JMenuItem mnSelectAll = new JMenuItem("Alles auswählen");
        mnSelectAll.addActionListener(e -> textArea.selectAll());
        mnEdit.add(mnSelectAll);

        // Copy
        JMenuItem mnCopy = new JMenuItem("Kopieren");
        mnCopy.addActionListener(e -> textArea.copy());
        mnEdit.add(mnCopy);

        // Cut
        JMenuItem mnCut = new JMenuItem("Ausschneiden");
        mnCut.addActionListener(e -> textArea.cut());
        mnEdit.add(mnCut);

        // Paste
        JMenuItem mnPaste = new JMenuItem("Einfügen");
        mnPaste.addActionListener(e -> textArea.paste());
        mnEdit.add(mnPaste);

        mnEdit.addSeparator(); // add a seperator

        // Undo - need the Undomanager
        JMenuItem mnUndo = new JMenuItem("Rückgängig");
        mnUndo.addActionListener(e -> {
            if (undoManager.canUndo()){
                undoManager.undo();
            }
        });
        mnEdit.add(mnUndo);

        // Redo - need the Undomanager
        JMenuItem mnRedo = new JMenuItem("Wiederherstellen");
        mnRedo.addActionListener(e -> {
            if (undoManager.canRedo()){
                undoManager.redo();
            }
        });
        mnEdit.add(mnRedo);



    }

    private void placeToolBar(JToolBar toolBar){

        // Icons creations
        ImageIcon newIcon = new ImageIcon("images/new.png"); // create an ImageIcon
        // Image newIcon2 = newIcon.getImage().getScaledInstance(20,20, Image.SCALE_SMOOTH);
        // ImageIcon newIconResized = new ImageIcon(newIcon2);
        ImageIcon openIcon = new ImageIcon("images/open.png"); // create an ImageIcon
        ImageIcon saveIcon = new ImageIcon("images/save.png"); // create an ImageIcon
        ImageIcon saveAsIcon = new ImageIcon("images/save_as.png"); // create an ImageIcon
        ImageIcon undoIcon = new ImageIcon("images/undo.png"); // create an ImageIcon
        ImageIcon redoIcon = new ImageIcon("images/redo.png"); // create an ImageIcon
        ImageIcon printIcon = new ImageIcon("images/print.png"); // create an ImageIcon

        // New
        JButton btnNew = new JButton("");
        btnNew.addActionListener(e -> newFile());
        btnNew.setIcon(newIcon);
        toolBar.add(btnNew);

        // Open
        // JButton btnOpen = new JButton(new JButton(new ImageIcon("icons/open.png")));
        JButton btnOpen = new JButton("");
        btnOpen.addActionListener(e -> openFile());
        btnOpen.setIcon(openIcon);
        toolBar.add(btnOpen);

        // Save
        JButton btnSave = new JButton("");
        btnSave.addActionListener(e -> saveFile());
        btnSave.setIcon(saveIcon);
        toolBar.add(btnSave);

        // SaveAs
        JButton btnSaveAs = new JButton("");
        btnSaveAs.addActionListener(e -> saveFileAs());
        btnSaveAs.setIcon(saveAsIcon);
        toolBar.add(btnSaveAs);

        // Undo
        JButton btnUndo = new JButton("");
        btnUndo.addActionListener(e -> {
            if(undoManager.canUndo()){
                undoManager.undo();
            }
        });
        btnUndo.setIcon(undoIcon);
        toolBar.add(btnUndo);

        // Redo
        JButton btnRedo = new JButton("");
        btnRedo.addActionListener(e -> {
            if(undoManager.canRedo()){
                undoManager.redo();
            }
        });
        btnRedo.setIcon(redoIcon);
        toolBar.add(btnRedo);

        // Bold
        JButton btnBold = new JButton("B");
        btnBold.setFont(new Font("Arial", Font.BOLD, 20));
        btnBold.addActionListener(e -> toggleStyle(StyleConstants.Bold));
        toolBar.add(btnBold);

        // Italic
        JButton btnItalic = new JButton("I");
        btnItalic.setFont(new Font("Arial", Font.BOLD, 20));
        btnItalic.addActionListener(e -> toggleStyle(StyleConstants.Italic));
        toolBar.add(btnItalic);

        // Undeline
        JButton btnUnderline = new JButton("U");
        btnUnderline.setFont(new Font("Arial", Font.BOLD, 20));
        btnUnderline.addActionListener(e -> toggleStyle(StyleConstants.Underline));
        toolBar.add(btnUnderline);

        // Font Family
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        fontCombo = new JComboBox<>(fonts);

        fontCombo.addActionListener(e -> {
            // Early Exit
            if (isUpdatingStyle) return;

            String fontName = (String) fontCombo.getSelectedItem();
            int start = textArea.getSelectionStart();
            int end = textArea.getSelectionEnd();
            if (start == end) return;

            StyledDocument doc = textArea.getStyledDocument();
            SimpleAttributeSet style = new SimpleAttributeSet();
            StyleConstants.setFontFamily(style, fontName);
            doc.setCharacterAttributes(start, end - start, style, false);

        });
        toolBar.add(fontCombo);

        // Font Sizes
        Integer[] sizes = {8, 10, 12, 14, 16, 18, 20, 24, 28, 32, 36};
        sizeCombo = new JComboBox<>(sizes);

        sizeCombo.addActionListener(e -> {
            // Early Exit
            if (isUpdatingStyle) return;

            int fontSize = (Integer) sizeCombo.getSelectedItem();
            int start = textArea.getSelectionStart();
            int end = textArea.getSelectionEnd();
            if (start == end) return; // Early Exit, if there is no selection

            StyledDocument doc = textArea.getStyledDocument();
            SimpleAttributeSet style = new SimpleAttributeSet();
            StyleConstants.setFontSize(style, fontSize);
            doc.setCharacterAttributes(start, end - start, style, false);

        });
        toolBar.add(sizeCombo);

        // Color Chooser
        colorBtn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // วาดวงกลมพื้นหลัง
                if (getModel().isPressed()) {
                    g2.setColor(Color.LIGHT_GRAY);
                } else {
                    g2.setColor(getBackground());
                }
                g2.fillOval(0, 0, getWidth(), getHeight());

                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.GRAY);
                g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(35, 35);
            }

            @Override
            public boolean contains(int x, int y) {
                Ellipse2D circle = new Ellipse2D.Float(0, 0, getWidth(), getHeight());
                return circle.contains(x, y);
            }
        };

        colorBtn.setOpaque(false);
        colorBtn.setContentAreaFilled(false);
        colorBtn.setBorderPainted(false); // วาดขอบเอง
        colorBtn.setForeground(Color.BLACK);

        colorBtn.addActionListener(e -> {
            Color color = JColorChooser.showDialog(frame, "Choose Text Color", Color.BLACK);
            if (color != null) {
                StyledDocument doc = textArea.getStyledDocument();
                SimpleAttributeSet style = new SimpleAttributeSet();
                StyleConstants.setForeground(style, color);
                doc.setCharacterAttributes(
                        textArea.getSelectionStart(),
                        textArea.getSelectionEnd() - textArea.getSelectionStart(),
                        style, false
                );
                colorBtn.setBackground(color);
            }
        });

        toolBar.add(colorBtn);



        // Print 
        JButton btnPrint = new JButton("");
        btnPrint.addActionListener(e -> {
            try {
                boolean complete = textArea.print();
                if (complete) {
                    JOptionPane.showMessageDialog(null, "Druckvorgang abgeschlossen", "Fertig", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Druckvorgang abgebrochen","Abgebrochen", JOptionPane.ERROR_MESSAGE);
                }
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(null, "Druckfehler" + ex.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnPrint.setIcon(printIcon);
        toolBar.add(btnPrint);

    }

    private void resetTextStyle() {
        // if (textArea == null) return;

        // 5. Reset undo manager history, to prevent error after reset
        if (undoManager != null) {
            undoManager.discardAllEdits();
        }

        // 1. create a new StyledDocument and reassign it to the doc
        // This will NOT carry styles and undo history to the new document
        StyledDocument newDoc = new DefaultStyledDocument();
        textArea.setDocument(newDoc);
        doc = newDoc;

        // 3. ต่อ listener ให้กับ UndoManager ใหม่
        doc.addUndoableEditListener(e -> undoManager.addEdit(e.getEdit()));

        // 2. create default style
        SimpleAttributeSet defaultStyle = new SimpleAttributeSet();
        StyleConstants.setFontFamily(defaultStyle, "Arial");
        StyleConstants.setFontSize(defaultStyle, 12);
        StyleConstants.setForeground(defaultStyle, Color.BLACK); //text
        StyleConstants.setBold(defaultStyle, false);
        StyleConstants.setItalic(defaultStyle, false);
        StyleConstants.setUnderline(defaultStyle, false);

        // 3. apply default styles to the new document
        doc = textArea.getStyledDocument();
        doc.setCharacterAttributes(0, doc.getLength(), defaultStyle, true);

        // ✅ reset caret input attributes
        textArea.setCharacterAttributes(defaultStyle, true);

        // 4. Reset GUI Element
        // reset JComboBox
        if (fontCombo != null) fontCombo.setSelectedItem("Arial");
        if (sizeCombo != null) sizeCombo.setSelectedItem(12);
        if (colorBtn != null) colorBtn.setBackground(Color.BLACK);



    }

    private void toggleStyle(Object styleType) {
        StyledDocument doc = textArea.getStyledDocument();
        int start = textArea.getSelectionStart();
        int end = textArea.getSelectionEnd();
        if (start == end) return; // Early exit when nothing is selected

        Element element = doc.getCharacterElement(start);
        AttributeSet as = element.getAttributes();

        boolean isSet = false;
        if (styleType.equals(StyleConstants.Bold)) {
            isSet = StyleConstants.isBold(as);
        } else if (styleType.equals(StyleConstants.Italic)) {
            isSet = StyleConstants.isItalic(as);
        } else if (styleType.equals(StyleConstants.Underline)) {
            isSet = StyleConstants.isUnderline(as);
        }

        SimpleAttributeSet sas = new SimpleAttributeSet();
        if (styleType.equals(StyleConstants.Bold)) {
            StyleConstants.setBold(sas, !isSet);
        } else if (styleType.equals(StyleConstants.Italic)) {
            StyleConstants.setItalic(sas, !isSet);
        } else if (styleType.equals(StyleConstants.Underline)) {
            StyleConstants.setUnderline(sas, !isSet);
        }

        doc.setCharacterAttributes(start, end - start, sas, false);
    }

    private void placeSouthPanelElements(JPanel panel) {
        panel.setLayout(new BorderLayout());

        lbInfo = new JLabel("Info");
        lbInfo.setBounds(0,0,300,20);

        lbCursorPos = new JLabel("Curserposition:");
        lbCursorPos.setBounds(700,0,100,20);

        // 🖱️ Mouse position listener for entire frame
        frame.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                lbCursorPos.setText("Cursorposition: X=" + e.getX() + ", Y=" + e.getY());
            }
        });

        // 🖱️ Also track inside textArea (important)
        textArea.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                // ต้องบวก offset เพราะตำแหน่งของ textArea ไม่เริ่มที่ (0,0)
                Point p = SwingUtilities.convertPoint(textArea, e.getPoint(), frame.getContentPane());
                lbCursorPos.setText("Cursorposition: X=" + p.x + ", Y=" + p.y);
            }
        });

        panel.add(lbInfo, BorderLayout.WEST);
        panel.add(lbCursorPos, BorderLayout.EAST);

    }

    private void saveFile() {
        if (currentFile != null) {
            saveRTFFile(currentFile);
        } else {
            saveFileAs();
        }
    }

    private void saveFileAs() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showSaveDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            currentFile = fileToSave;
            saveRTFFile(fileToSave);
        }
    }

    private void saveRTFFile(File file) {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            RTFEditorKit rtfKit = new RTFEditorKit();
            rtfKit.write(fos, textArea.getStyledDocument(), 0, textArea.getStyledDocument().getLength());
            JOptionPane.showMessageDialog(frame, "Datei erfolgreich gespeichert!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Fehler beim Speichern: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Datei öffnen");
        int returnValue = fileChooser.showOpenDialog(frame);
        if(returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            lbInfo.setText(selectedFile.getAbsolutePath());
            currentFile = selectedFile;

            try (FileInputStream fis = new FileInputStream(selectedFile)) {
                RTFEditorKit rtfKit = new RTFEditorKit();
                StyledDocument doc = new DefaultStyledDocument();
                rtfKit.read(fis, doc, 0);
                textArea.setDocument(doc);

                // to add a new UndoManager to the new doc
                doc.addUndoableEditListener(new UndoableEditListener() {
                    public void undoableEditHappened(UndoableEditEvent e) {
                        undoManager.addEdit(e.getEdit());
                    }
                });

                updateStyleControls();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Fehler beim Lesen der Datei: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /* codes here works with JTextArea but not with JTextPane 
    private void openFile() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Datei öffnen");
    int returnValue = fileChooser.showOpenDialog(frame);
    if(returnValue == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();
        lbInfo.setText(selectedFile.getAbsolutePath());
        currentFile = selectedFile;

        try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

            // ลบเนื้อหาเก่า
            textArea.setText("");


            // ใส่ข้อความใหม่พร้อม style
            doc = textArea.getStyledDocument();
            try {
                doc.insertString(0, content.toString(), null);

                // // **สำคัญ**: รีเซ็ต style ของข้อความทั้งหมดให้ตรง default
                // doc.setCharacterAttributes(0, doc.getLength(), defaultStyle, true);

            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }

            // // **เรียกฟังก์ชัน resetTextStyle()**
            // resetTextStyle();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Fehler beim Lesen der Datei: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

    private void saveFileAs() {
    JFileChooser fileChooser = new JFileChooser();
    int result = fileChooser.showSaveDialog(frame); // เปิดหน้าต่างเลือกที่บันทึก
    if (result == JFileChooser.APPROVE_OPTION) {
        File fileToSave = fileChooser.getSelectedFile();
        try (FileWriter writer = new FileWriter(fileToSave)) {
            textArea.write(writer); // บันทึกเนื้อหา
            JOptionPane.showMessageDialog(frame, "Datei erfolgreich gespeichert!"); // แจ้งสำเร็จ
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Fehler beim Speichern: " + e.getMessage());
        }
    }
}

    private void saveFile() {
        if (currentFile != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(currentFile))) {
                textArea.write(writer);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(frame, "Fehler beim Speichern: " + e.getMessage());
            }
        } else {
            saveFileAs(); // ถ้ายังไม่มีไฟล์ ให้ใช้ Save As
        }
    }
    */

    private void newFile() {
        int choice = JOptionPane.showConfirmDialog(
                frame,
                "Möchten Sie die aktuelle Datei schließen?",
                "Schließen bestätigen",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            // textArea.setText(""); // empty the text area
            currentFile = null;     // empty the current file path
            lbInfo.setText("Neue Datei"); // reset the file path label

            resetTextStyle();

        }
    }

    public static void main(String[] args) {
        new TextEditor(); // create the instance (here to display GUI)
    }

}