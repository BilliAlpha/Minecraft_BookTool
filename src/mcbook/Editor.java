package mcbook;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.concurrent.Callable;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
//import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;

public class Editor extends JFrame {
    public static String VERSION = "v1.0.0";
    
    private JTextArea textInput;
    private JButton editorButton0;
    private JButton editorButton1;
    private JButton editorButton2;
    private JButton editorButton3;
    private JButton editorButton4;
    private JButton editorButton5;
    private JButton copy;
    private JTextPane bookContent;
    private JButton convert;
    private JButton prev;
    private JLabel page;
    private JButton next;
    private int idPage;
    private ArrayList<String> pages;
    private HashMap<String,String> replace;
    private HashMap<String,String> MC_Colors;
    private ColorPicker colorPicker;
    private String prefix = "<style>"
                +"em{text-decoration: underline;font-style: normal;}"
                +"p{margin:none;padding:none;font-size:10px;}"
                +"span{font-stretch: ultra-condensed;background-color:#753951;}"
            +"</style>\n";

    public Editor() {
        super("Editeur de livre");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().add(initGUI());
        setSize(650, 400);
        setResizable(false);
        setLocationRelativeTo(null);
        initListeners();
        setVisible(true);
        MC_Colors = new HashMap();
        MC_Colors.put("0", "000000");
        MC_Colors.put("1", "0000AA");
        MC_Colors.put("2", "00AA00");
        MC_Colors.put("3", "00AAAA");
        MC_Colors.put("4", "AA0000");
        MC_Colors.put("5", "AA00AA");
        MC_Colors.put("6", "FFAA00");
        MC_Colors.put("7", "AAAAAA");
        MC_Colors.put("8", "555555");
        MC_Colors.put("9", "5555FF");
        MC_Colors.put("a", "55FF55");
        MC_Colors.put("b", "55FFFF");
        MC_Colors.put("c", "FF5555");
        MC_Colors.put("d", "FF55FF");
        MC_Colors.put("e", "FFFF55");
        MC_Colors.put("f", "FFFFFF");
        replace = new HashMap();
        replace.put("l", "b");
        replace.put("m", "s");
        replace.put("n", "em");
        replace.put("o", "i");
        replace.put("k", "span");
        colorPicker = new ColorPicker();
    }

    private JPanel initGUI() {
        JPanel body = new JPanel();
        body.setLayout(new BorderLayout());

        body.add(new JLabel("MCBook by Billi ("+VERSION+")", SwingConstants.RIGHT), BorderLayout.SOUTH);
        
        try {
            JPanel content = new JPanel();
            content.setLayout(new BorderLayout());

            JPanel input = new JPanel();
            input.setLayout(new BorderLayout());
            JPanel inputFormatBtns = new JPanel();
            //inputFormatBtns.add(new JLabel("Format text: ",SwingConstants.RIGHT));
            BufferedImage sprites = ImageIO.read(this.getClass().getResourceAsStream("/assets/textFormats.png"));
            ArrayList<ImageIcon> icons = new ArrayList();
            for (int i=0; i<6; i++) {
                icons.add(new ImageIcon(sprites.getSubimage(i*30, 0, 30, 30)));
            }
            editorButton0 = new JButton(); // Italic
            editorButton0.setIcon(icons.get(0));
            inputFormatBtns.add(editorButton0);
            editorButton1 = new JButton(); // Bold
            editorButton1.setIcon(icons.get(1));
            inputFormatBtns.add(editorButton1);
            editorButton2 = new JButton(); // Strikethrough
            editorButton2.setIcon(icons.get(2));
            inputFormatBtns.add(editorButton2);
            editorButton3 = new JButton(); // Underline
            editorButton3.setIcon(icons.get(3));
            inputFormatBtns.add(editorButton3);
            editorButton4 = new JButton(); // Obfuscated
            editorButton4.setIcon(icons.get(4));
            inputFormatBtns.add(editorButton4);
            editorButton5 = new JButton(); // Color
            editorButton5.setIcon(icons.get(5));
            inputFormatBtns.add(editorButton5);
            input.add(inputFormatBtns,BorderLayout.NORTH);
            textInput = new JTextArea();
            textInput.setLineWrap(true);
            textInput.setWrapStyleWord(true);
            input.add(new JScrollPane(textInput), BorderLayout.CENTER);
            convert = new JButton("Convertir");
            input.add(convert, BorderLayout.SOUTH);
            content.add(input,BorderLayout.CENTER);

            JPanel output = new JPanel();
            output.setLayout(new BorderLayout());
            JLayeredPane book = new JLayeredPane();
            BufferedImage bookPic = ImageIO.read(this.getClass().getResourceAsStream("/assets/book.png"));
            JLabel pic = new JLabel(new ImageIcon(bookPic));
            pic.setBounds(0,0,197,300);
            book.add(pic,0);
            bookContent = new JTextPane();
            bookContent.setEditable(false);
            bookContent.setContentType("text/html");
            bookContent.setBounds(20,15,160,260);
            book.add(bookContent,1);
            output.add(book, BorderLayout.CENTER);
            
            JPanel bookBottomPanel = new JPanel();
            bookBottomPanel.setLayout(new BorderLayout());
            copy = new JButton("Copier");
            copy.setEnabled(false);
            bookBottomPanel.add(copy,BorderLayout.NORTH);
            JPanel outputBtns = new JPanel();
            outputBtns.setLayout(new BorderLayout());
            prev = new JButton("< Precedent");
            prev.setEnabled(false);
            outputBtns.add(prev, BorderLayout.WEST);
            page = new JLabel("  0/0  ", SwingConstants.CENTER);
            outputBtns.add(page, BorderLayout.CENTER);
            next = new JButton("Suivant >");
            next.setEnabled(false);
            outputBtns.add(next, BorderLayout.EAST);
            bookBottomPanel.add(outputBtns,BorderLayout.SOUTH);
            output.add(bookBottomPanel, BorderLayout.SOUTH);
            content.add(output,BorderLayout.EAST);
            
            body.add(content, BorderLayout.CENTER);

        } catch(IOException e) {
            e.printStackTrace();
        }
        return body;
    }

    private void initListeners() {
        convert.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!textInput.getText().isEmpty()) {
                    convert();
                }
            }
        });
        prev.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                prev();
            }
        });  
        next.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                next();
            }
        });
        copy.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
                StringSelection selection = new StringSelection(pages.get(idPage));
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
                //JOptionPane.showMessageDialog(getContentPane(), "Texte copié !");
           }
        });
        editorButton0.addActionListener(new ActionListener() { // Italic
           @Override
           public void actionPerformed(ActionEvent e) {
               setTextFormat('o');
           }
        });
        editorButton1.addActionListener(new ActionListener() { // Bold
           @Override
           public void actionPerformed(ActionEvent e) {
                setTextFormat('l');
            }
        });
        editorButton2.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
                setTextFormat('m');
            }
        });
        editorButton3.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
                setTextFormat('n');
            }
        });
        editorButton4.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
                setTextFormat('k');
            }
        });
        editorButton5.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
                colorPicker.promptColor(new Callable() {
                    @Override
                    public Object call() throws Exception {
                        setTextColor(colorPicker.getColor());
                        return null;
                    }
                });
            }
        });
    }
    
    private void setTextFormat(char f){
        int start = textInput.getSelectionStart();
        int end = textInput.getSelectionEnd();
        String before = textInput.getText().substring(0,start);
        String content = textInput.getText().substring(start,end);
        String after = textInput.getText().substring(end);
        textInput.setText(before.concat("§"+f).concat(content).concat("§r").concat(after));
        textInput.setSelectionStart(start);
        textInput.setSelectionEnd(end+4);
        convert();
    }
    private void setTextColor(char c){
        int start = textInput.getSelectionStart();
        int end = textInput.getSelectionEnd();
        String before = textInput.getText().substring(0,start);
        String content = textInput.getText().substring(start,end);
        String after = textInput.getText().substring(end);
        textInput.setText(before.concat("§"+c).concat(content).concat("§0").concat(after));
        textInput.setSelectionStart(start);
        textInput.setSelectionEnd(end+4);
        convert();
    }
    
    private void convert() {
        System.out.println("Conversion en cours ...");
        pages = readPages(textInput.getText());
        System.out.println("Conversion terminée");
        System.out.println("Parsing ...");
        bookContent.setText(parseMCFormat(pages.get(0)));
        System.out.println("Parsing over");
        idPage = 0;
        page.setText("  1/"+pages.size()+"  ");
        copy.setEnabled(true);
        if (pages.size()>1) {
            next.setEnabled(true);
        }
        prev.setEnabled(false);
    }
    
    private void prev() {
        idPage--;
        bookContent.setText(parseMCFormat(pages.get(idPage)));
        prev.setEnabled(idPage>=1);
        next.setEnabled(pages.size()>idPage+1);
        page.setText("  "+(idPage+1)+"/"+pages.size()+"  ");
        copy.setText("Copier");
    }
    
    private void next() {
        idPage++;
        bookContent.setText(parseMCFormat(pages.get(idPage)));
        prev.setEnabled(idPage>=1);
        next.setEnabled(pages.size()>idPage+1);
        page.setText("  "+(idPage+1)+"/"+pages.size()+"  ");
        copy.setText("Copier");
    }
    
    public static ArrayList<String> readPages(String text) { // TODO >>> Des fois on tombe sur une boucle infinie ?!
        text=text.replaceAll("\r\n", "¤¤");
        text=text.replaceAll("\r", "¤¤");
        text=text.replaceAll("\n", "¤¤");
        ArrayList<String> res = new ArrayList();
        int idChar=0;
        int idPage=0;
        while (idChar<text.length()) { // boucle construction livre
            int idCharPage=idChar;
            String pageTxt="";
            boolean overflowPage=false;
            int idLigne=0;
            while(idLigne<14 && idCharPage<idChar+255 && idCharPage<text.length() && !overflowPage) { // boucle construction page
                overflowPage=false;
                int idCharLigne=idChar;
                boolean overflowLigne=false;
                String ligneTxt="";
                while (idChar<text.length() && !overflowLigne) { // boucle construction ligne (max 14 ligne/page)
                    overflowLigne=false;
                    int idCharMot=idChar;
                    int motLength=0;
                    String motTxt="";
                    boolean finMot = false;
                    while(idCharMot<text.length() && !finMot) { // boucle construction mot (max 19 char/ligne)
                        char curChar = text.charAt(idCharMot);
                        motTxt += curChar;
                        idCharMot++;
                        motLength++;
                        if (curChar==' ' || curChar=='¤') {
                            finMot=true;
                        }
                        if (motLength==18) {
                            finMot=true;
                        }
                    } // fin du mot
                    if (idCharMot<idCharLigne+19) { // si le mot ne dépasse pas la fin de page alors on l'ajoute a la page
                        ligneTxt += motTxt;
                        idChar=idCharMot;
                    } else {
                        overflowLigne=true;
                    }
                } // fin de la ligne
                pageTxt += ligneTxt+(ligneTxt.contains("¤¤")?"":"\n");
                //System.out.println("Fin ligne ("+idLigne+"-"+idPage+"): "+(ligneTxt.contains("¤¤")?"Rien":"\\n"));
                idLigne++;
            } // fin de la page
            res.add(pageTxt.replaceAll("¤¤", "\n")); // On ajoute la page au livre / .replaceAll("¤¤", "\n")
            idPage++;
        } // fin du livre
        return res;
    }
    
    public String parseMCFormat(String in) { // test string : §n§2Alpha§r §9Beta§0 Gamma
        in = in.replaceAll("<", "&#60;"); // When having §r close color and then re-open to have well-formed html.
        in = in.replaceAll(">", "&#62;");
        in = in.replaceAll("\n", "<br/>\n");
        String res = prefix+"<p>"+in+"</p>";
        boolean colorInUse = false;
        int index = res.indexOf('§');
        Stack symboles = new Stack();
        while (index!=-1) {
            if (index<res.length()-2) { // Pour eviter le bug du § en dernier caractère
                String key = res.substring(index+1,index+2);
                String tags = "";
                switch(key) {
                    case "r":
                        while(!symboles.isEmpty()) {
                            tags += "</"+replace.get((String)symboles.pop())+">";
                        }
                        break;
                    default:
                        if (MC_Colors.containsKey(key)) {
                            if (colorInUse && key.equals("0")) {
                                tags+="</font>";
                                colorInUse=false;
                            } else {
                                if (colorInUse){
                                    tags+="</font>";
                                }
                                tags+="<font color=#"+MC_Colors.get(key)+">";
                                colorInUse=true;
                            }
                        } else {
                            tags+="<"+replace.get(key)+">";
                            symboles.add(key);
                        }
                }
                res = res.substring(0,index).concat(tags).concat(res.substring(index+2));
                index = res.indexOf('§');
            }
        }
        if (colorInUse){
            res +="</font>";
        }
        while(!symboles.isEmpty()) {
            res += "</"+replace.get((String)symboles.pop())+">";
        }
        return res;
    }
}