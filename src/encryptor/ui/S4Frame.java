package encryptor.ui;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import encryptor.file.S4Filter;
import encryptor.main.Define;
import encryptor.main.EncryptEvent;
import encryptor.main.EncryptorListener;

public class S4Frame extends JFrame {
	// Components
	private JComboBox modeComboBox;
	private JButton startButton;
	private JButton btn_select;
	private JTextField filePath;
	private JTextField keyField;
	private JRadioButton substitution;
	private JRadioButton transposition;
	private EncryptorListener listener;

	// attributes
	private String s_filePath;


	public EncryptorListener getListener() {
		return listener;
	}

	public void setListener(EncryptorListener listener) {
		this.listener = listener;
	}

	public int getMode() {
		return modeComboBox.getSelectedIndex();
	}

	public int getMethod() {
		if (substitution.isSelected()) {
			return Define.SUBSTITUTION;
		} else {
			return Define.TRANSPOSITION;
		}
	}

	public String getFilePath() {
		return s_filePath;
	}

	public void setFilePath(String file_path) {
		this.s_filePath = file_path;
	}

	public byte getKey() {

		int keyInt = Integer.parseInt(keyField.getText());

		byte key = 0;

		byte k = (byte) (keyInt / 1000);
		key = (byte)(key + (k << 6));
//		System.out.println("k:"+k+" key:"+Integer.toBinaryString(key));
		k=(byte)((keyInt%1000)/100);
        key=(byte)(key+(k<<4));
//        System.out.println("k:"+k+" key:"+Integer.toBinaryString(key));
        k=(byte)((keyInt%100)/10);
        key=(byte)(key+(k<<2));
//        System.out.println("k:"+k+" key:"+Integer.toBinaryString(key));
        k=(byte)(keyInt%10);
        key=(byte)(key+k);
//        System.out.println("k:"+k+" key:"+Integer.toBinaryString(key));

		return key;
	}

	// create component and add them to the frame
	public S4Frame() {
		// openning event
		addWindowListener(new WindowAdapter() {
			public void windowActivated(WindowEvent e) {
				setTitle("Encriptor --S4");
			}
		});

		// closing event
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		filePath = new JTextField(
				"please enter the file path                            ");
		keyField = new JTextField(3);
		btn_select = new JButton("Select");
		startButton = new JButton("Start");
		substitution = new JRadioButton("Substitution");
		transposition = new JRadioButton("Transposition");
		modeComboBox = new JComboBox();
		modeComboBox.addItem("Encrypt");
		modeComboBox.addItem("Decrypt");

		// add component
		this.getContentPane().setLayout(new FlowLayout());
		this.getContentPane().add(modeComboBox);
		this.getContentPane().add(btn_select);
		this.getContentPane().add(filePath);
		this.getContentPane().add(new JLabel("Choose encrypt method:"));
		this.getContentPane().add(substitution);
		this.getContentPane().add(transposition);
		this.getContentPane().add(new JLabel("Input the key (four number from 0-3):"));
		this.getContentPane().add(keyField);
		this.getContentPane().add(startButton);

		initialComponent();

		setResizable(false);
		setSize(500,150);
	}

	// initial components and set listeners
	public void initialComponent() {
		setFilePath(null);
		setListener(null);

		ButtonGroup group = new ButtonGroup();
		group.add(substitution);
		group.add(transposition);
		group.setSelected(substitution.getModel(), true);

		// limited the input of the key field
		keyField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				if ((keyField.getText().length()) >= 4
						&& e.getKeyChar() != '\b') {
					e.setKeyChar('\0');
				}
				if (e.getKeyChar() > '3' || e.getKeyChar() < '0') {
					e.setKeyChar('\0');
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});

		// select file then change the path field
		btn_select.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setFilePath(selectFile());
				filePath.setText(getFilePath());
			}
		});

		// click start then send encrypt/decrypt request
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (getFilePath() != null) {
						EncryptEvent event = new EncryptEvent(this, s_filePath,
								getMethod(), getKey());
						if (getMode() == Define.ENCRYPT) {
							listener.encryptRequest(event);
						}
						if (getMode() == Define.DECRYPT) {
							listener.decryptRequest(event);
						}
					}

				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}

	// select file user wants to encrypt
	public String selectFile() {
		JFileChooser jf = new JFileChooser();
		jf.setDialogTitle("Chose file to encript:");
		jf.setFileFilter(new S4Filter("txt"));
		int result = jf.showOpenDialog(null);

		jf.setVisible(true);

		File selectedFile = null;
		if (result == JFileChooser.APPROVE_OPTION) {
			selectedFile = jf.getSelectedFile();
			if (selectedFile.exists()) {
				return selectedFile.getAbsolutePath();
			} else {
			}
		} else if (result == JFileChooser.CANCEL_OPTION) {
		} else if (result == JFileChooser.ERROR_OPTION) {
		}
		return null;
	}
}
