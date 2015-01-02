import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.Semaphore;

/**
 * @author kashi
 *
 */
public class HTTPConnection implements Runnable {
	
	private Socket sockect;
	private BufferedReader input;
	private OutputStream output;
	private File root;

	public HTTPConnection(Socket socket, File root) throws IOException {
		this.sockect = socket;
		this.root = root;
		input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		output = socket.getOutputStream();
	}

	@Override
	public void run() {
		HTTPRequest request  = new HTTPRequest();
		
		// Parse first line
		try {
			switch(request.parseFirstLine(input)) {
			case 0:
				break; // Parsed OK, continue to headers
			case 500:
				// TODO: Generate 500 Response
				// TODO: Close connection
			case 400:
				// TODO: Generate 400 Response
				// TODO: Close connection
			case 501:
				// TODO: Generate 501 Response
				// TODO: Close connection
			}
			
			request.readHeaders(input);
			
			if(request.checkVersion()) {
				// TODO: Generate 400 as says in the RFC
				// TODO: Close connection
			}
			
			request.parseQuery();
			
			switch(request.readBody(sockect.getInputStream())) {
			case 0:
				break; // Parsed OK
			case 411:
				// TODO: Generate 411 and close connection
			case 500:
				// TODO: Generate 500 and close connection
			}
			
			
		} catch (IOException e) {
			// TODO: Generate 500 Response
			// TODO: Close connection
		}
		
		
		
	}
}
