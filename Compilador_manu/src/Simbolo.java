
public class Simbolo {
	
	private String lexama;
	private String tipo;
	private int bloco;
	
	// *-----> Construtor
	public Simbolo(String lexama, String tipo, int bloco) {
		this.lexama = lexama;
		this.tipo = tipo;
		this.bloco = bloco;
	}
	
	// *-----> Set
	public void setLexama(String lexama) {
		this.lexama = lexama;
	}
	
	public void setClasifica(String tipo) {
		this.tipo = tipo;
	}
	
	public void setBloco(int bloco) {
		this.bloco = bloco;
	}
	// *-----> Get
	public String getLexama() {
		return this.lexama;
	}
	
	public String getTipo() {
		return this.tipo;
	}
	
	public int getBloco() {
		return this.bloco;
	}

}
