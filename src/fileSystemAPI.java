
/* This is the interface to the client-side file system. */




public interface fileSystemAPI  
{ 
    /* url has form IP:port/path. */
    public abstract filehandle open(String url) 
	throws java.io.IOException; 

    /* write data starting from the current pointer. */
    public abstract boolean write(filehandle fh, byte[] data)
	throws java.io.IOException; 

    /* read data.length bytes from the current position; if
       end-of-fike, returns -1; if not, the next byte (position) is
       returned. */
    public abstract int read(filehandle fh, byte [] data)
	throws java.io.IOException; 

    /* close file. you should flush the data (over the network). */  
    public abstract boolean close(filehandle fh)
	throws java.io.IOException; 

    /* check if it is at the end-of-file. */
    public abstract boolean isEOF(filehandle fh)
	throws java.io.IOException; 

    /* You do not have to implement the following two methods. */
    //    public abstract boolean flush(filehandle fh);
    //   public abstract byte [] read(filehandle fh, int n);


} 
    
	
