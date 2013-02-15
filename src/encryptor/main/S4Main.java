package encryptor.main;

import encryptor.ui.S4Frame;

public class S4Main{


	public static void main(String[] argStrings) {
		S4Frame frame = new S4Frame();
		EncryptorListener listener= new EncryptorListener();
		frame.setListener(listener);
		
		frame.setVisible(true);	
	}
	
}
