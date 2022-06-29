package com.github.vegeto079.PoESparkDPSCalculator;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.github.vegeto079.PoESparkDPSCalculator.areas.PathOfExileMap;

public class SparkOptions extends JFrame {

	private static final long serialVersionUID = 5967817778434406753L;

	public double castRate = 7.7;
	public int proj = 12;
	public int pierce = 1;
	public int fork = 0;
	public long duration = 5740; // milliseconds
	public long damagePerHit = 655652;
	
	public double sparkSpeed = 4.2; // How many pixels the Spark moves per-tick. I set this arbitrarily
	public double sparkSize = 25;
	
	public long timeToAverageBy = 6000;
	public PathOfExileMap selectedMap = PathOfExileMap.ORIATH_DOCKS;
	
	private final static Dimension FIELD_SIZE = new Dimension(150, 20);
	
    public SparkOptions() throws HeadlessException {
        initComponents();
    }

    protected void initComponents() {
		Dimension size = new Dimension(300, 500);

		setPreferredSize(size);
		setMinimumSize(size);
		
    	JPanel panel = new JPanel();
    	getContentPane().add(panel);
    	
        setTitle("Spark Options");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        addCastRateField(panel);
        addProjectileField(panel);
        addPierceField(panel);
        addForkField(panel);
        addDurationField(panel);
        addDamageField(panel);
        addSpeedField(panel);
        addSizeField(panel);
        addMapField(panel);
        
        pack();
        
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width - getWidth()) / 2 + 550,
					(screenSize.height - getHeight()) / 2);
    }
    
    public void addCastRateField(JPanel panel) {
        JLabel label = new JLabel("Cast Rate: ");
        JTextField textField = new JTextField("" + castRate);
        textField.setPreferredSize(FIELD_SIZE);
        panel.add(label);
        panel.add(textField);

        textField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                JTextField textField = (JTextField) e.getSource();
                try {
                	castRate = Double.parseDouble(textField.getText());
                } catch(NumberFormatException nfe) {
                	// ignore bad input
                }
            }
        });
    }
    
    public void addProjectileField(JPanel panel) {
        JLabel label = new JLabel("Projectiles: ");
        JTextField textField = new JTextField("" + proj);
        textField.setPreferredSize(FIELD_SIZE);
        panel.add(label);
        panel.add(textField);

        textField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                JTextField textField = (JTextField) e.getSource();
                try {
                	proj = Integer.parseInt(textField.getText());
                } catch(NumberFormatException nfe) {
                	// ignore bad input
                }
            }
        });
    }
    
    public void addPierceField(JPanel panel) {
        JLabel label = new JLabel("Pierce: ");
        JTextField textField = new JTextField("" + pierce);
        textField.setPreferredSize(FIELD_SIZE);
        panel.add(label);
        panel.add(textField);

        textField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                JTextField textField = (JTextField) e.getSource();
                try {
                	pierce = Integer.parseInt(textField.getText());
                } catch(NumberFormatException nfe) {
                	// ignore bad input
                }
            }
        });
    }
    
    public void addForkField(JPanel panel) {
        JLabel label = new JLabel("Fork: ");
        JTextField textField = new JTextField("" + fork);
        textField.setPreferredSize(FIELD_SIZE);
        panel.add(label);
        panel.add(textField);

        textField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                JTextField textField = (JTextField) e.getSource();
                try {
                	fork = Integer.parseInt(textField.getText());
                } catch(NumberFormatException nfe) {
                	// ignore bad input
                }
            }
        });
    }
    
    public void addDurationField(JPanel panel) {
        JLabel label = new JLabel("Duration: ");
        JTextField textField = new JTextField("" + duration);
        textField.setPreferredSize(FIELD_SIZE);
        panel.add(label);
        panel.add(textField);

        textField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                JTextField textField = (JTextField) e.getSource();
                try {
                	duration = Long.parseLong(textField.getText());
                } catch(NumberFormatException nfe) {
                	// ignore bad input
                }
            }
        });
    }
    
    public void addDamageField(JPanel panel) {
        JLabel label = new JLabel("Damage Per Hit: ");
        JTextField textField = new JTextField("" + damagePerHit);
        textField.setPreferredSize(FIELD_SIZE);
        panel.add(label);
        panel.add(textField);

        textField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                JTextField textField = (JTextField) e.getSource();
                try {
                	damagePerHit = Long.parseLong(textField.getText());
                } catch(NumberFormatException nfe) {
                	// ignore bad input
                }
            }
        });
    }
    
    public void addSpeedField(JPanel panel) {
        JLabel label = new JLabel("Spark Speed (arbitrary number): ");
        JTextField textField = new JTextField("" + sparkSpeed);
        textField.setPreferredSize(FIELD_SIZE);
        panel.add(label);
        panel.add(textField);

        textField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                JTextField textField = (JTextField) e.getSource();
                try {
                	sparkSpeed = Double.parseDouble(textField.getText());
                } catch(NumberFormatException nfe) {
                	// ignore bad input
                }
            }
        });
    }
    
    public void addSizeField(JPanel panel) {
        JLabel label = new JLabel("Spark Size (in pixels): ");
        JTextField textField = new JTextField("" + sparkSize);
        textField.setPreferredSize(FIELD_SIZE);
        panel.add(label);
        panel.add(textField);

        textField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                JTextField textField = (JTextField) e.getSource();
                try {
                	sparkSize = Double.parseDouble(textField.getText());
                } catch(NumberFormatException nfe) {
                	// ignore bad input
                }
            }
        });
    }
    
    public void addMapField(JPanel panel) {
        JLabel label = new JLabel("Map: ");
        JComboBox<String> comboBox = new JComboBox<String>(getEnumNames(PathOfExileMap.class));
        comboBox.setPreferredSize(FIELD_SIZE);
        panel.add(label);
        panel.add(comboBox);

        comboBox.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
                @SuppressWarnings("unchecked")
				JComboBox<String> comboBox = (JComboBox<String>) e.getSource();
                try {
                	String selectedItem = (String) comboBox.getSelectedItem();
                	if(selectedItem != null) {
                		selectedMap = PathOfExileMap.valueOf(selectedItem);
                	}
                } catch(NumberFormatException nfe) {
                	// ignore bad input
                }
            }
        });
    }
    
    private static String[] getEnumNames(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }
}
