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

public class CopyOfcaseBaseSetSerialization {

	public static void serialize(List<CaseBase> caseBaseSet,
			String outputFileName) throws IOException {

		try {
			OutputStream outputFileStream = new FileOutputStream("Result/"
					+ outputFileName);
			OutputStream outputBufferStream = new BufferedOutputStream(
					outputFileStream);
			ObjectOutput object = new ObjectOutputStream(outputBufferStream);
			
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
			InputStream inputFileStream = new FileInputStream("Result/"
					+ serializedFile);
			InputStream inputBufferStream = new BufferedInputStream(
					inputFileStream);
			ObjectInput object = new ObjectInputStream(inputBufferStream);
			try {
				caseBaseSet = (List<CaseBase>) object.readObject();

			} finally {
				object.close();
			}
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		return caseBaseSet;
	}

}
