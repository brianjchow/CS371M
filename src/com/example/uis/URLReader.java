package com.example.uis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class URLReader {

	/* Wrapper around an InputStream */

	private BufferedReader reader;
	private URL url;
	
	/* Create a new InputReader that reads from the file specified by the argument. */
	public URLReader(String url_name) {
		if (url_name == null || url_name.length() < 0) {
			throw new IllegalArgumentException("Error: URL is either null or empty, URLReader constructor");
		}
		
		try {
			url_name = new URI(url_name).toString();	// check url now?
		}
		catch (URISyntaxException e) {
			System.out.printf("Error encoding URL \"%s\" to URI, URLReader constructor\n", url_name);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		URL url = null;
		try {
			url = new URL(url_name);
		}
		catch (MalformedURLException e) {
			System.out.printf("Error: malformed URL \"%s\", URLReader constructor\n", url_name);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		this.url = url;

		this.reader = null;
		try {
			this.reader = new BufferedReader(new InputStreamReader(url.openStream()));
		}
		catch (IOException e) {
			System.out.printf("Error opening BufferedReader for URL \"%s\", URLReader constructor\n", url_name);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/* "Reset the file pointer to the beginning" */
	public void reset() {
		try {
			this.reader.close();
			this.reader = new BufferedReader(new InputStreamReader(url.openStream()));
		}
		catch (IOException e) {
			System.out.printf("Error resetting BufferedReader for URL \"%s\", URLReader.reset()\n", this.url.toString());
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/* Close this InputReader */
	public void close() {
		try {
			this.reader.close();
		}
		catch (IOException e) {
			System.out.printf("Error closing BufferedReader for URL \"%s\", URLReader.close()\n", this.url.toString());
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
			System.out.printf("Error reading byte from URL \"%s\", URLReader.read()\n", this.url.toString());
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
			System.out.printf("Error reading line from URL \"%s\", URLReader.readLine()\n", this.url.toString());
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
