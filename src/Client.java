import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Random;


public class Client extends Thread{

	int portServeur;
	int portClient;
	String addrIpServeur;
	byte [] id;


	public Client(int portServeur, int portClient, String addrIpServeur) {

		this.portServeur = portServeur;
		this.portClient = portClient;
		this.addrIpServeur = addrIpServeur;
	}


	/**
	 * @param args
	 */


	public void run() {
		// TODO Auto-generated method stub
		boolean recu=false;
		try {
			DatagramSocket sRecoie = new DatagramSocket(portClient);
			DatagramSocket sEnvoie= new DatagramSocket();
			byte[] data = new byte [100];
			DatagramPacket paquet = new DatagramPacket(data, data.length);



			sRecoie.receive(paquet);
			byte[] id = paquet.getData();
			
			int taillePaquet = paquet.getLength();
			String st = new String (id,0,taillePaquet);
			this.id = st.getBytes();
			//System.out.println("J'ai recu "+ st + "  "+this.id + "   " + taillePaquet);
			String str = new String (this.id,0,this.id.length);
			//System.out.println(str);
			
			recu = true;
			// On ferme le socket afin de renvoyer l'id sur le port du serveur
			sRecoie.close();

			// On vide le buffer
			data = new byte[100];
			// On dit ou va aller le nouveau paquet
			InetSocketAddress sa = new InetSocketAddress(addrIpServeur,portServeur);
			DatagramPacket paquetId = new DatagramPacket(id, id.length,sa);
			//On envoie le paquet
			sEnvoie.send(paquetId);

			System.out.println();

			sEnvoie.close();
			sRecoie = new DatagramSocket(portClient);
			sRecoie.receive(paquet);
			id = paquet.getData();
			taillePaquet = paquet.getLength();
			st = new String (id,0,taillePaquet);

			//System.out.println("J'ai recu"+ st);
			sRecoie.close();
			if (st.contains("go"))
			{
				
				long nbEnvoie;
				//tableau qui contient le big endian a envoyer
				byte [] tableauOctets = new byte [10]; 
				//tableau avec le big endian + id client
				byte [] tableauAEnvoyer = new byte [10];
				Random r = new Random();
				
				for (int i = 0 ; i<10;i++)
				{
					nbEnvoie = r.nextLong();
					tableauOctets = Main.longToBytes(nbEnvoie);
					for (int j =0 ; j<8;j++)
					{
						tableauAEnvoyer[j]=tableauOctets[j];
					}
					tableauAEnvoyer[8]=this.id[0];
					tableauAEnvoyer[9]=this.id[1];
					sEnvoie = new DatagramSocket();
					paquetId = new DatagramPacket(tableauAEnvoyer	, tableauAEnvoyer.length,sa);
					sEnvoie.send(paquetId);
					sEnvoie.close();

				}
				sEnvoie = new DatagramSocket();
				for (int i = 0 ; i < 8 ; i++)
				{
					tableauOctets[i]=0;
				}
				paquetId = new DatagramPacket(tableauOctets, tableauOctets.length);
				
			}


		} catch (SocketException e) {

			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		System.out.println("fin du client "+ this.getName());
	}

}
