
public class InfoClients {
String id;
String host;
String port;
long somme;
public long getSomme() {
	return somme;
}
public void setSomme(long somme) {
	this.somme = somme;
}
public InfoClients(String id, String host, String port) {
	super();
	this.id = id;
	this.host = host;
	this.port = port;
	somme =0;
}
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public String getHost() {
	return host;
}
public void setHost(String host) {
	this.host = host;
}
public String getPort() {
	return port;
}
public void setPort(String port) {
	this.port = port;
}

}
