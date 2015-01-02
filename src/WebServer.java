import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * @author kashi
 *
 */
public class WebServer implements Runnable {
	
	private int port, maxThreads;
	private String defaultPage;
	private File root;
	private ServerSocket server;

	public WebServer(File root, String defaultPage, int port, int maxThreads) throws IOException {
		
		this.port = port;
		this.maxThreads = maxThreads;
		this.root = root;
		this.defaultPage = defaultPage;
		server = new ServerSocket(port);
		System.out.println("Listening port: " + port);
	}

	@Override
	public void run() {
		while(true) {
			try {
				Socket httConnectiont = server.accept();
			
				
			} catch (IOException e) {
				System.out.println("WARN: Failed to create the new connection");
			}				
		}
	}
	

}
