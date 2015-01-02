import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTTPRequest {

	private static final String HTTP_11 = "HTTP/1.1";
	private static final String HTTP_10 = "HTTP/1.0";
	private static final String CONTENT_LENGTH = "content-length";
	
	private static Pattern requestLine = Pattern.compile("(\\S+)\\s+([^\\s?]+)(\\?(\\S+))?\\s+(HTTP/[0-9.]+)");
	private static Pattern headerLine = Pattern.compile("([^:]+):\\s*(.*)");
	private static Pattern queryLine = Pattern.compile("([^&=]+)=([^&=]+)");
	
	private Method method;
	private String path, query, version, body;
	private HashMap<String, String> headersMap, parametersMap;
	
	
	public HTTPRequest () {
		headersMap = new HashMap<String, String>();
		parametersMap = new HashMap<String, String>();
	}


	/**
	 * Check if version is HTTP/1.1 and does NOT have host header
	 * 
	 * @return True if version is HTTP/1.1 and does NOT have host header
	 */
	public boolean checkVersion() {
		return version.equalsIgnoreCase(HTTP_11) && !headersMap.containsKey("host");
	}

	/**
	 * Read a request body only if it is a POST request and has Content-Length Header
	 * 
	 * @param input
	 * @return 0 if OK, or the response code that should be generated.
	 * @throws IOException 
	 */
	public int readBody(InputStream input) throws IOException {
		if(method != Method.POST)
			return 0;
		
		if(!headersMap.containsKey(CONTENT_LENGTH))
			return 411; // Length Required!
		
		int bufferSize = -1;
		try {
			bufferSize = Integer.parseInt(headersMap.get(CONTENT_LENGTH));
		} catch (NumberFormatException e) {
			System.out.println("ERROR: Failed to parse Content-Length header.");
			return 500;
		}
		if(bufferSize < 0) {
			return 500;
		}
		
		byte buffer[] = new byte[bufferSize];
		input.read(buffer, 0, bufferSize);
		body = new String(buffer);
		
		return 0;
	}

	/**
	 * Parse the query.
	 */
	public void parseQuery() {
		if(query == null)
			return;
		
		Matcher matcher = queryLine.matcher(query);
		
		while(matcher.find()) {
			String key = matcher.group(1);
			String value = matcher.group(2);
			
			try {
				// Use URLDecoder to handle %20 etc
				parametersMap.put(key, URLDecoder.decode(value, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// Not supposer to happen
				System.out.println("WARN: Failed to parse one the the parameters in the query.");
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * Read HTTP request headers
	 * @param input
	 * @throws IOException
	 */
	public void readHeaders(BufferedReader input) throws IOException {
		String line;
		while ((line = input.readLine()) != null && !line.isEmpty()) {
			System.out.println(line);
			Matcher matcher = headerLine.matcher(line);
			if(!matcher.matches())
				continue; // Not a valid header line, skip it
			headersMap.put(matcher.group(1).toLowerCase(), matcher.group(2).toLowerCase());
		}
		
		
	}

	/**
	 * Parse the first line of a HTTP request
	 * 
	 * @param input
	 * @return 0 if OK or the error number to send to the user.
	 * @throws IOException
	 */
	public int parseFirstLine(BufferedReader input) throws IOException {
		String line;
		while (true) {
			line = input.readLine();
			if(line == null)
				return 500;
			
			if(!line.isEmpty())
				break;
		}
		System.out.println(line);
		
		Matcher matcher = requestLine.matcher(line);
		
		if(!matcher.matches()) {
			// Failed to parse understand header. Generate 400
			return 400;
		}
		
		String methodString = matcher.group(1).toUpperCase();
		try {
			method = Method.valueOf(methodString);
		} catch(IllegalArgumentException e) {
			// Not a supported method. Generate 501
			return 501;
		}
		
		path = matcher.group(2);
		query = matcher.group(4);
		version = matcher.group(5).toUpperCase();
		
		
		return 0;		
	}
	
	public Method getMethod() {
		return method;
	}

}
