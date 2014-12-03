package com.example.app;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;

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
	
	// http://stackoverflow.com/questions/9544737/read-file-from-assets
	// http://ponystyle.com/blog/2010/03/26/dealing-with-asset-compression-in-android-apps/

	// TODO
	// http://stackoverflow.com/questions/4153246/how-to-check-android-asset-resource
	public InputReader(Context context, String file_name) {
		if (context == null) {
			throw new IllegalArgumentException("Error: Context argument is null");
		}
		else if (file_name == null || file_name.length() <= 0) {
			throw new IllegalArgumentException("Error: file name is either null or empty, InputReader constructor");
		}
		
		try {
//			AssetFileDescriptor fd = context.getAssets().openFd(file_name);
//			FileReader file_reader = new FileReader(fd.getFileDescriptor());
//			this.reader = new BufferedReader(file_reader);
			
			this.reader = new BufferedReader(new InputStreamReader(context.getAssets().open(file_name), "UTF-8"));
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
	
	/* Create a new InputReader that reads from the file specified by the argument. */
	public InputReader(Context context, int res_id) {
		
		InputStream input_stream = context.getResources().openRawResource(res_id);
		InputStreamReader input_stream_reader = new InputStreamReader(input_stream);
		this.reader = new BufferedReader(input_stream_reader, BUF_SIZE);
		
//		Log.d("CSVReader", "Error opening BufferedReader for file " + this.file_name);
	}

	/* Close this InputReader */
	public void close() {
		try {
			this.reader.close();
		}
		catch (IOException e) {
//			System.out.printf("Error closing BufferedReader for file \"%s\", InputReader.close()\n", this.file_name.toString());
//			e.printStackTrace();
//			throw new RuntimeException(e);
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
