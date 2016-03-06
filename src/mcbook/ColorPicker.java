/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mcbook;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.concurrent.Callable;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ColorPicker {
    private JFrame frame;
    private char selectedColor;
    private ActionListener submitListener;
    private ActionListener colorSelectorListener;
    private JButton submit;
    private HashMap<String,Color> mcColors;
    
    public ColorPicker() {
        frame = new JFrame("Selecteur de couleur");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mcColors = new HashMap();
        mcColors.put("0", new Color(0,0,0));
        mcColors.put("1", new Color(0,0,170));
        mcColors.put("2", new Color(0,170,0));
        mcColors.put("3", new Color(0,170,170));
        mcColors.put("4", new Color(170,0,0));
        mcColors.put("5", new Color(170,0,170));
        mcColors.put("6", new Color(255,170,0));
        mcColors.put("7", new Color(170,170,170));
        mcColors.put("8", new Color(85,85,85));
        mcColors.put("9", new Color(85,85,255));
        mcColors.put("a", new Color(85,255,85));
        mcColors.put("b", new Color(85,255,255));
        mcColors.put("c", new Color(255,85,85));
        mcColors.put("d", new Color(255,85,255));
        mcColors.put("e", new Color(255,255,85));
        mcColors.put("f", new Color(255,255,255));
        initListeners();
        frame.getContentPane().add(initGUI());
        frame.setSize(700, 150);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        selectedColor='0';
        submitListener=null;
    }
    
    private JPanel initGUI() {
        JPanel body = new JPanel(new BorderLayout());
        body.add(new JLabel("Séléctionnez une couleur :"),BorderLayout.NORTH);
        JPanel colors = new JPanel(new GridLayout(1,16));
        for (String s : mcColors.keySet()) {
            JButton b = new JButton();
            b.setBackground(mcColors.get(s));
            b.setName(s);
            b.setText(s);
            b.addActionListener(colorSelectorListener);
            colors.add(b);
        }
        body.add(colors,BorderLayout.CENTER);
        submit = new JButton("Valider");
        body.add(submit,BorderLayout.SOUTH);
        return body;
    }
    
    private void initListeners() {
        colorSelectorListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedColor = ((JButton)e.getSource()).getName().charAt(0);
            }
        };
    }
    
    public char getColor() {
        return selectedColor;
    }
    
    public void promptColor(Callable c) {
        if (submitListener!=null)
            submit.removeActionListener(submitListener);
        submitListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
                try {
                    c.call();
                } catch(Exception err) {}
            }
        };
        submit.addActionListener(submitListener);
        frame.setVisible(true);
    }
}
