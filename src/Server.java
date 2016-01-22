import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class Server {
	final static int PORTSERVER = 54002;


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int nbClientsTotal=0;
		int nbClientsRepondu=0;
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		ArrayList <InfoClients> clients = new ArrayList<InfoClients>();
		ArrayList <InfoClients> clientsQuiOntRepondus = new ArrayList<InfoClients>();
		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(new File("configurationServeur.xml"));
			doc.normalize();


			NodeList nodeList = doc.getElementsByTagName("client");
			for (int i = 0; i<nodeList.getLength();i++)
			{
				Element e =(Element)nodeList.item(i);
				Element host,port;
				String id;
				host= (Element) e.getElementsByTagName("host").item(0);
				id=  e.getAttribute("id");
				port= (Element) e.getElementsByTagName("port").item(0);
				clients.add(new InfoClients(id,host.getTextContent(),port.getTextContent()));
			}

			nbClientsTotal=clients.size();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DatagramSocket 	sRecoie;
		try {
			DatagramSocket sEnvoie = new DatagramSocket();
			byte [] data;
			DatagramPacket donneeRecu;
			for (int i=0; i< clients.size();i++)
			{
				data = new byte[100];
				sRecoie = new DatagramSocket(PORTSERVER);
				donneeRecu = new DatagramPacket(data, data.length);	
				InetSocketAddress sa = new InetSocketAddress(clients.get(i).getHost(),Integer.parseInt(clients.get(i).getPort()));
				DatagramPacket paquet = new DatagramPacket(clients.get(i).getId().getBytes(), clients.get(i).getId().getBytes().length,sa );
				sEnvoie.send(paquet);
				try {
					sRecoie.setSoTimeout(5000);
					sRecoie.receive(donneeRecu);
					byte [] id = donneeRecu.getData();
					int taille = donneeRecu.getLength();
					String str = new String (id,0,2);
					nbClientsRepondu++;
					for (InfoClients c : clients)
					{
						if( c.getId().equals(str))
						{
							clientsQuiOntRepondus.add(c);
						}
					}

					
				}
				catch (SocketTimeoutException e)
				{
					System.out.println("le client avec l'id "+clients.get(i).getId()+" n'as pas r�pondu");
				}
				finally {
					sRecoie.close();
				}							
			}
			System.out.println("il y a "+ nbClientsRepondu + " clients qui ont r�pondus sur "+ nbClientsTotal);
			System.out.println("Les clients sont :");
			for (InfoClients c : clientsQuiOntRepondus)
			{
				System.out.println(c.getId());
			}

			for (int i=0; i< clientsQuiOntRepondus.size();i++)
			{
				data = "go".getBytes();
				donneeRecu = new DatagramPacket(data, data.length);

				InetSocketAddress sa = new InetSocketAddress(clientsQuiOntRepondus.get(i).getHost(),Integer.parseInt(clientsQuiOntRepondus.get(i).getPort()));
				DatagramPacket paquet = new DatagramPacket(data, data.length,sa );
				sEnvoie.send(paquet);
			}

			//RECEPTION BIG ENDIAN
			sEnvoie.close();
			sRecoie= new DatagramSocket(PORTSERVER);
			boolean recoit = true;
			while (recoit){
				try {

					byte [] tableauOctets = new byte [8];
					sRecoie.setSoTimeout(5000);
					data = new byte[10];
					donneeRecu = new DatagramPacket(data, data.length);
					sRecoie.receive(donneeRecu);
					for (int i =0 ; i< 8;i++)
					{
						tableauOctets[i]=data[i];
					}
					// On recupere l'id qui a envoyer le bigEndian
					String str = new String (data,8,2);
					long valeurCourante = Server.bytesToLong(tableauOctets);
					System.out.println(valeurCourante);
					for (InfoClients c : clientsQuiOntRepondus)
					{
						if( c.getId().equals(str))
						{
							System.out.println("valeur "+valeurCourante + " id "+c.getId());
							c.setSomme(c.getSomme()+ valeurCourante);
							
						}
					}
				}catch ( SocketTimeoutException e)
				{
					System.out.println("Plus de donn�e");
					recoit = false;
					sRecoie.close();
				}
			}
			
			for (InfoClients c : clientsQuiOntRepondus)
			{
				System.out.println("FIN somme = "+ c.getSomme()+ " pour le client id "+c.getId());
			}

		}
		catch (Exception E)
		{
			E.printStackTrace();
		}
	}
	public static long bytesToLong(byte[] bytes) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
		buffer.put(bytes);
		buffer.flip();
		return buffer.getLong();
	}

}
