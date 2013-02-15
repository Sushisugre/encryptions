package encryptor.main;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class EncryptorListener implements IencryptorListener {

	public void encryptRequest(EncryptEvent event) throws IOException {

		FileReader fileRead = new FileReader(event.getFilePath());

		int _index = event.getFilePath().lastIndexOf('.');
		String outPutPath = event.getFilePath().substring(0, _index) + "_e"
				+ event.getFilePath().substring(_index);
		FileWriter fileWrite = new FileWriter(outPutPath);

		if (event.getMethod() == Define.SUBSTITUTION) {
			substitution(fileRead, fileWrite, event.getKey());
		} else if (event.getMethod() == Define.TRANSPOSITION) {
			transposition(fileRead, fileWrite, event.getKey());
		}

		fileRead.close();
		fileWrite.close();
	}

	@Override
	public void decryptRequest(EncryptEvent event) throws IOException {
		FileReader fileRead = new FileReader(event.getFilePath());

		int _index = event.getFilePath().lastIndexOf('.');
		String outPutPath = event.getFilePath().substring(0, _index) + "_d"
				+ event.getFilePath().substring(_index);
		FileWriter fileWrite = new FileWriter(outPutPath);

		int ch = 0;
		byte key = event.getKey();

		// read first character to determine the encrypt method
		ch = fileRead.read();
		ch = (ch + key % 26) ^ key;
		// encrypted with substitution
		if (ch == 's') {
			while ((ch = fileRead.read()) != -1) {
				ch = (ch + key % 26) ^ key;

				fileWrite.write(ch);
				fileWrite.flush();
			}
		} else {
			tDecrypt(fileRead, fileWrite, key);
		}
		fileRead.close();
		fileWrite.close();
	}

	public void substitution(FileReader plainText, FileWriter cipherText,
			Byte key) throws IOException {
		int ch = 0;
		cipherText.write(('s' ^ key) - key % 26);
		cipherText.flush();

		while ((ch = plainText.read()) != -1) {
			ch = (ch ^ key) - key % 26;

			cipherText.write(ch);
			cipherText.flush();
		}
	}

	public void transposition(FileReader plainText, FileWriter cipherText,
			Byte key) throws IOException {
    	Vector<char[]> arrays = readFileForTrans(plainText);
        //mark encrypt method
    	cipherText.write('t');
    	
		prePermute(arrays);
		byte[] subkeys = getSubKeys(key);
		shiftLeft(arrays, subkeys[0], subkeys[1]);
		shiftLeft(arrays, subkeys[1], subkeys[0]);
		shiftLeft(arrays, subkeys[2], subkeys[3]);
		shiftLeft(arrays, subkeys[3], subkeys[2]);
		shiftUp(arrays, subkeys[0], subkeys[1]);
		shiftUp(arrays, subkeys[1], subkeys[0]);
		shiftUp(arrays, subkeys[2], subkeys[3]);
		shiftUp(arrays, subkeys[3], subkeys[2]);

		for (int i = 0; i < arrays.size(); i++) {
			cipherText.write(arrays.get(i));
			cipherText.flush();
		}

	}
	
	public Vector<char[]> readFileForTrans(FileReader fileRead) throws IOException
	{
		char[] array = new char[16];
		Vector<char[]> arrays = new Vector<char[]>();
		while (fileRead.read(array, 0, 16) != -1) {
			char[] block = array.clone();
			arrays.add(block);
		}
		return arrays;
	}

	public byte[] getSubKeys(byte key) {
		byte[] subkeys = new byte[4];
		subkeys[0] = (byte) ((key & 255) >>> 6);
		subkeys[1] = (byte) ((key & 63) >>> 4);
		subkeys[2] = (byte) ((key & 15) >>> 2);
		subkeys[3] = (byte) (key & 3);

		return subkeys;
	}

	public void prePermute(Vector<char[]> arrays) {
		// 4 char as a block, first chars in first block...
		char[] array = new char[16];
		for (int j = 0; j < arrays.size(); j++) {
			for (int i = 0; i < 4; i++) {
				array[4 * i] = arrays.get(j)[i];
				array[4 * i + 1] = arrays.get(j)[i + 4];
				array[4 * i + 2] = arrays.get(j)[i + 8];
				array[4 * i + 3] = arrays.get(j)[i + 12];
			}
			for (int i = 0; i < array.length; i++) {
				arrays.get(j)[i] = array[i];
			}
		}
	}
	
	public void rePermute(Vector<char[]> arrays) {

		char[] array = new char[16];
		for (int j = 0; j < arrays.size(); j++) {
			for (int i = 0; i < 4; i++) {
				array[i] = arrays.get(j)[4 * i];
				array[i+4] = arrays.get(j)[4 * i+1];
				array[i+8] = arrays.get(j)[4 * i+2];
				array[i+12] = arrays.get(j)[4 * i+3];
			}
			
			for (int i = 0; i < array.length; i++) {
				arrays.get(j)[i] = array[i];
			}
		}
	}

	public void shiftLeft(Vector<char[]> arrays, byte rowIndex, byte offset) {
		// four rows as a group
		for (int rowGroup = 0; rowGroup < arrays.size() / 4; rowGroup++) {
			char[] row = arrays.get(4 * rowGroup + rowIndex);
			char[] ch = row.clone();
			int iNew = 0;
			for (int j = 0; j < row.length; j++) {
				iNew = (j + offset) % 16;
				row[j] = ch[iNew];
			}
		}
	}

	public void shiftUp(Vector<char[]> arrays, byte columnGroup, byte offset) {
		char[] ch = new char[4];

		for (int rowGroup = 0; rowGroup < arrays.size() / 4; rowGroup++) {

			for (int column = 0; column < 4; column++) {
				int iCol = 4 * columnGroup + column;
				for (int i = 0; i < ch.length; i++) {
					ch[i]=arrays.get(4 * rowGroup+i)[iCol];
				}
				for (int row = 0; row < 4; row++) {
					int iRow = 4 * rowGroup + row;
					arrays.get(iRow)[iCol] = ch[(row+offset)%4];
				}
			}

		}
	}
	
	public void tDecrypt(FileReader fileRead, FileWriter fileWrite,
			Byte key) throws IOException 
	{
		Vector<char[]> arrays = readFileForTrans(fileRead);
		byte[] subkeys = getSubKeys(key);
		
		//loop
		shiftUp(arrays, subkeys[3], (byte)(4-subkeys[2]));
		shiftUp(arrays, subkeys[2], (byte)(4-subkeys[3]));
		shiftUp(arrays, subkeys[1], (byte)(4-subkeys[0]));
		shiftUp(arrays, subkeys[0], (byte)(4-subkeys[1]));
		shiftLeft(arrays, subkeys[3], (byte)(16-subkeys[2]));
		shiftLeft(arrays, subkeys[2], (byte)(16-subkeys[3]));
		shiftLeft(arrays, subkeys[1],(byte)(16-subkeys[0]));
		shiftLeft(arrays, subkeys[0],(byte)(16-subkeys[1]));
		
		rePermute(arrays);
		
		for (int i = 0; i < arrays.size(); i++) {
			fileWrite.write(arrays.get(i));
			fileWrite.flush();
		}	
		
	}
}
