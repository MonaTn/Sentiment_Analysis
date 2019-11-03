package Utility;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import Main.CaseBase;

public class Serialization {
	private static final String path = "Result/";
	public static void serialize(List<CaseBase> caseBaseSet,
			String outputFileName) throws IOException {

		try {
			OutputStream outputFile = new FileOutputStream( path
					+ outputFileName);
			OutputStream outputBuffer = new BufferedOutputStream(outputFile); 
			ObjectOutput object = new ObjectOutputStream(outputBuffer);
			try {
				object.writeObject(caseBaseSet);
			} finally {
				object.flush();
				object.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static List<CaseBase> deSerialize(String serializedFile)
			throws IOException {
		List<CaseBase> caseBaseSet = new ArrayList<CaseBase>();
		try {
			InputStream inputFile = new FileInputStream("Result/"
					+ serializedFile);
			InputStream inputBuffer = new BufferedInputStream(inputFile);
			ObjectInput object = new ObjectInputStream(inputBuffer);
			caseBaseSet = (List<CaseBase>) object.readObject();
			object.close();
		
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		} 
		return caseBaseSet;
	}

}
