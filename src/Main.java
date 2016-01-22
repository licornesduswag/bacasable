import java.nio.ByteBuffer;
import java.security.Timestamp;

public class Main {

	public static void main(String[] args) {
		String addrServeur ="localhost";
		// TODO Auto-generated method stub
		Client c1 = new Client(54002, 8081, addrServeur);
		Client c2 = new Client(54002, 7813, addrServeur);
		Client c3 = new Client(54002, 5000, addrServeur);
		c1.start();
		c2.start();
		c3.start();
		
		
	}
	
	public static byte[] longToBytes(long x)
	{
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		buffer.putLong(x);
		return buffer.array();
	}

}
