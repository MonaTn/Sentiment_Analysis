package utilities;

/**
 * Last modification 25 February 2013
 */

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

public class Serialization {

	public static void serialize(Object objectToWrite, String outputAbsoluteFileName)
			throws IOException {

		try {
			OutputStream outputFile = new FileOutputStream(outputAbsoluteFileName);
			OutputStream outputBuffer = new BufferedOutputStream(outputFile);
			ObjectOutput object = new ObjectOutputStream(outputBuffer);
			try {
				object.writeObject(objectToWrite);
				System.out.println("done!");
			} finally {
				object.flush();
				object.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static Object deSerialize(String serializedAbsoluteFile) throws IOException {
		Object objectToRead = new Object();
		try {
			InputStream inputFile = new FileInputStream(serializedAbsoluteFile);
			InputStream inputBuffer = new BufferedInputStream(inputFile);
			ObjectInput object = new ObjectInputStream(inputBuffer);
			objectToRead = object.readObject();
			object.close();

		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return objectToRead;
	}

}
