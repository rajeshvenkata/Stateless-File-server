import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.StringTokenizer;

/*
Code for client API.

It receives requests from application and fetches data from the server

*/
public class Client implements fileSystemAPI{

	 /* It needs a table relating filehandles and real files. */
    Hashtable fh_tbl = new Hashtable();
    String server_ip;
    int server_port;
    int file_offset=0;
    int number_of_bytes_toread;
    int number_of_bytes_available;
    int number_of_bytes_read;
   
    
	@Override
	public filehandle open(String url) throws IOException {
		file_offset=0;
		// TODO Auto-generated method stub
		filehandle fh=new filehandle();
		StringTokenizer st=new StringTokenizer(url,":/");
		String file_name=null;
		while(st.hasMoreTokens())
		{
			server_ip=st.nextToken();
			server_port=Integer.parseInt(st.nextToken());
			file_name=st.nextToken();
			System.out.println(server_ip);
			System.out.println(server_port);
			System.out.println(file_name);
			
			break;
		}
		Socket s = null;
		try{
	//	int serverPort = 7896;
		s = new Socket(server_ip, server_port);
		DataInputStream in = new DataInputStream(s.getInputStream());
		DataOutputStream out =
		new DataOutputStream(s.getOutputStream());
		System.out.println("before open");
		String output="o"+":"+file_name;
		out.writeUTF(output); // UTF is a string encoding
		String data = in.readUTF();
		System.out.println("Received: "+ data) ;
		if(data.charAt(0)=='0')
		{
			return null;
		}
		fh_tbl.put(fh, file_name);
		}
		
		catch (UnknownHostException e)
		{
		System.out.println("Sock:"+e.getMessage());
		}catch (EOFException e)
		{
			System.out.println("EOF:"+e.getMessage());
		}catch (IOException e)
		{
			System.out.println("IO:"+e.getMessage());
		}
		finally
		{
			if(s!=null) try 
			{
			s.close();
			}
		catch (IOException e)
		{
			System.out.println("close:"+e.getMessage());
		}
			}
		
		return fh;
	}

	@Override
	public boolean write(filehandle fh, byte[] data) throws IOException {
		// TODO Auto-generated method stub
		Socket s = null;
		boolean write_success=false;
		try{
	//	int serverPort = 7896;
		s = new Socket(server_ip, server_port);
		DataInputStream in = new DataInputStream(s.getInputStream());
		DataOutputStream out =
		new DataOutputStream(s.getOutputStream());
		String temp = new String(data, "UTF-8");
		String output="w"+":"+fh_tbl.get(fh) + ":" +temp + ":" + file_offset;
		out.writeUTF(output); // UTF is a string encoding
		
		String input = in.readUTF();
		//System.out.println("Received: "+ data) ;
		if(input.charAt(0)=='1')
		{
			file_offset=data.length+file_offset;
			write_success= true;
		}
		else
		{
			write_success= false;
		}
		
		}
		
		catch (UnknownHostException e)
		{
		System.out.println("Sock:"+e.getMessage());
		}catch (EOFException e)
		{
			System.out.println("EOF:"+e.getMessage());
		}catch (IOException e)
		{
			System.out.println("IO:"+e.getMessage());
		}
		finally
		{
			if(s!=null) try 
			{
			s.close();
			}
		catch (IOException e)
		{
			System.out.println("close:"+e.getMessage());
		}
			}
		
		return write_success;
	}

	@Override
	public int read(filehandle fh, byte[] data) throws IOException {
		// TODO Auto-generated method stub
		Socket s = null;
		try{
	//	int serverPort = 7896;
		s = new Socket(server_ip, server_port);
		DataInputStream in = new DataInputStream(s.getInputStream());
		DataOutputStream out =
		new DataOutputStream(s.getOutputStream());
		if((number_of_bytes_available-data.length)>=0)
		{
			number_of_bytes_toread=data.length;
		}
		else
		{
			number_of_bytes_toread=number_of_bytes_available;
		}
		String output="r"+":"+fh_tbl.get(fh)+":"+file_offset+":"+number_of_bytes_toread;
		//System.out.println(output);
		out.writeUTF(output); // UTF is a string encoding
		String received = in.readUTF();
		//System.out.println(received);
		String[] received_buffer=received.split("[+]");
		//System.out.println("received buffer"+received_buffer[1]);
		number_of_bytes_read = Integer.parseInt(received_buffer[0]);
		file_offset=file_offset+number_of_bytes_read;
		byte[] data_read=new byte[number_of_bytes_read];
		byte[] data_recv = new byte[number_of_bytes_read];
		data_recv = received_buffer[1].getBytes();
		data[0] = data_recv[0];
		
		}
		
		catch (UnknownHostException e)
		{
		System.out.println("Sock:"+e.getMessage());
		}catch (EOFException e)
		{
			System.out.println("EOF:"+e.getMessage());
		}catch (IOException e)
		{
			System.out.println("IO:"+e.getMessage());
		}
		finally
		{
			if(s!=null) try 
			{
			s.close();
			}
		catch (IOException e)
		{
			System.out.println("close:"+e.getMessage());
		}
			}
		
		return number_of_bytes_read;
	}

	@Override
	public boolean close(filehandle fh) throws IOException {
		// TODO Auto-generated method stub
		fh_tbl.remove(fh);
		fh.discard();
		return true;
		//return false;
	}

	@Override
	public boolean isEOF(filehandle fh) throws IOException {
		// TODO Auto-generated method stub
		Socket s = null;
		boolean neof=false;
		try{
	//	int serverPort = 7896;
		s = new Socket(server_ip, server_port);
		DataInputStream in = new DataInputStream(s.getInputStream());
		DataOutputStream out =
		new DataOutputStream(s.getOutputStream());
		String output="eof"+":"+fh_tbl.get(fh)+":"+file_offset;
		
		//System.out.println(output);

		out.writeUTF(output); // UTF is a string encoding
		String input_received = in.readUTF();
		int temp = Integer.parseInt(input_received);
		
		if(temp == 0)
		{
			neof= true;
		}
		else
		{
		number_of_bytes_available=temp;
		neof=false;
		}
			
		}
		catch (UnknownHostException e)
		{
		System.out.println("Sock:"+e.getMessage());
		}catch (EOFException e)
		{
			System.out.println("EOF:"+e.getMessage());
		}catch (IOException e)
		{
			System.out.println("IO:"+e.getMessage());
		}
		finally
		{
			if(s!=null) try 
			{
			s.close();
			}
		catch (IOException e)
		{
			System.out.println("close:"+e.getMessage());
		}
		}
		return neof;
	}

	
}
