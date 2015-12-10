import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
Code for server application

*/

public class Server {

	public static void main(String args[])
	{
		try{
			int serverPort = 7897;
			
			ServerSocket listenSocket = new ServerSocket(serverPort);
			while(true) {
			Socket clientSocket = listenSocket.accept();
			Connection c = new Connection(clientSocket);
			}
		} catch(IOException e) {System.out.println("Listen :"+e.getMessage());}
	}
			
	}

class Connection extends Thread {
	DataInputStream in;
	DataOutputStream out;
	Socket clientSocket;
	public Connection (Socket aClientSocket) {
		try {
		clientSocket = aClientSocket;
		in = new DataInputStream( clientSocket.getInputStream());
		out =new DataOutputStream( clientSocket.getOutputStream());
		System.out.println("inside connection");
		this.start();
		} catch(IOException e){System.out.println("Connection:"+e.getMessage());}
	}
	public void run(){
		try { // an echo server
			String data = in.readUTF();
			System.out.println("inside run");
			String[] input_data=data.split(":");
			if(input_data[1]!=null)
			{
			if(input_data[0].equals("o"))
			{
				System.out.println("inside open");
				File file=new File(input_data[1]);
				if(file.exists())
				{
					System.out.println("if file exists");
					String output="1" + new SimpleDateFormat("dd-MM-yyyy HH-mm-ss").format(
						    new Date(new File(input_data[1]).lastModified()) 
						);
					System.out.println(output);
					out.writeUTF(output);
				}
				else
				{
					out.writeUTF("0");
				}
				
			}
			if(input_data[0].equals("r"))
			{
				System.out.println("inside read");
				File temp=new File(input_data[1]);
				FileInputStream fis=new FileInputStream(temp);
				fis.skip(Integer.parseInt(input_data[2]));
				
				byte[] fileBytes = new byte[Integer.parseInt(input_data[3])];
				int num_bytes_read=fis.read(fileBytes);
				String out_str = new String(fileBytes, "UTF-8");
				
				String output=Integer.toString(num_bytes_read) + "+" + out_str ;
				System.out.println(output);
				out.writeUTF(output);
		     }
			if(input_data[0].equals("eof"))
			{
			   System.out.println("inside eof");
				File temp=new File(input_data[1]);
				FileInputStream fis=new FileInputStream(temp);
				fis.skip(Integer.parseInt(input_data[2]));
				String output=null;
				if(fis.available() == 0)
				{
					output="0";
				}
				else
				{
					int num_available=fis.available();
					System.out.println(num_available);
					output=Integer.toString(num_available);
				}
				out.writeUTF(output);
		       }
			if(input_data[0].equals("w"))
			{
				File temp=new File(input_data[1]);
			//	FileInputStream fis=new FileInputStream(temp);
				FileOutputStream fos=new FileOutputStream(temp,true);
			//	fis.skip(Integer.parseInt(input_data[2]));
				FileChannel ch = fos.getChannel();
				ch.position(Integer.parseInt(input_data[3]));
				byte[] write_data=input_data[2].getBytes();
				ch.write(ByteBuffer.wrap(write_data));
				String output="1";
				out.writeUTF(output);
				
			}
			}
			
			out.writeUTF(data);
		} catch(EOFException e) {System.out.println("EOF:"+e.getMessage());
		} catch(IOException e) {System.out.println("IO:"+e.getMessage());
		} 
		finally
		{ 
			try 
			{
				clientSocket.close();
			}
			catch (IOException e)
		{
				System.out.println("close:"+e.getMessage());
				}
			}
	}
}
