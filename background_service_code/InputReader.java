
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/* Wrapper around a BufferedReader */
public class InputReader {

	private static final int BUF_SIZE = 8192;		// bytes
	
	private BufferedReader reader;
	
	public InputReader(String file_name) {
		if (file_name == null || file_name.length() < 0) {
			throw new IllegalArgumentException("Error: file name is either null or empty, InputReader constructor");
		}
		
		try {
			this.reader = new BufferedReader(new FileReader(file_name));
//			this.file_name = file_name;
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public InputReader(InputStream stream) {
		if (stream == null) {
			throw new IllegalArgumentException("InputStream cannot be null, InputReader constructor");
		}
		this.reader = new BufferedReader(new InputStreamReader(stream), BUF_SIZE);
	}
	
	public InputReader(InputStreamReader input_stream_reader) {
		if (input_stream_reader == null) {
			throw new IllegalArgumentException("InputStreamReader cannot be null, InputReader constructor");
		}
		this.reader = new BufferedReader(input_stream_reader, BUF_SIZE);
	}

	/* Close this InputReader */
	public void close() {
		try {
			this.reader.close();
		}
		catch (IOException e) {
//			System.out.printf("Error closing BufferedReader for file \"%s\", InputReader.close()\n", this.file_name.toString());
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/* Read the next character in the file */
	public int read() {
		try {
			return (this.reader.read());
		}
		catch (IOException e) {
//			System.out.printf("Error reading byte from file \"%s\", InputReader.read()\n", this.file_name.toString());
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/* Read the next line in the file */
	public String readLine() {
		try {
			return (this.reader.readLine());
		}
		catch (IOException e) {
//			System.out.printf("Error reading line from file \"%s\", InputReader.readLine()\n", this.file_name.toString());
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/* "Reset the file pointer to the beginning" */
//	public void reset() {
//		try {
//			this.reader.close();
//			System.out.printf("Error resetting BufferedReader for file \"%s\", InputReader.reset()\n", this.file_name.toString());
//			this.reader = new BufferedReader(new FileReader(this.file_name));
//		}
//		catch (IOException e) {
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		}
//	}
	
}
