import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Token {
	
	private String lexama;
	private String Clasific;
	private String Tipo;
	private int Bloco;
	
	
	// *-----> Construtor
	public Token(String lexama, String Clasific) {
		this.lexama = lexama;
		this.Clasific = Clasific;
		this.Tipo = "";
		this.Bloco = 0;
	}
	
	// *-----> Set
	public void setLexama(String lexama) {
		this.lexama = lexama;
	}
	
	public void setClasifica(String Clasific) {
		this.Clasific = Clasific;
	}
	
	public void setTipo(String Tipo) {
		this.Tipo = Tipo;
	}
	
	public void setBloco(int Bloco) {
		this.Bloco = Bloco;
	}
	// *-----> Get
	public String getLexama() {
		return this.lexama;
	}
	
	public String getClasifica() {
		return this.Clasific;
	}
	
	public String getTipo() {
		return this.Tipo;
	}
	
	public int getBloco() {
		return this.Bloco;
	}
}
