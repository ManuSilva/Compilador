import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Token {
	
	private String lexama;
	private String Clasific;
	
	
	// *-----> Construtor
	public Token(String lexama, String Clasific) {
		this.lexama = lexama;
		this.Clasific = Clasific;
	}
	
	// *-----> Set
	public void setLexama(String lexama) {
		this.lexama = lexama;
	}
	
	public void setClasifica(String Clasific) {
		this.Clasific = Clasific;
	}
	// *-----> Get
	public String getLexama() {
		return this.lexama;
	}
	
	public String getClasifica() {
		return this.Clasific;
	}
}
