import java.io.IOException;

public class Scanner {
	
	private Arquivo arq;
	private String Lockahead;
	private Token token;
	
	//*------------------Erros Scanner----------------------*
	static String erroFloatMalFormado = "Float mal formado";
	//*-----------------------------------------------------*

	// *-----> Construtor
	public Scanner(Arquivo arq) throws IOException {
		this.arq = arq;
		this.Lockahead = arq.readCaracter();
	}
	
	// *-----> Get
	public Token getToken() {
		return this.token;
	}
	
	// *-----> Verificação Léxica
	public void verificLexico() throws IOException {
		
		while(this.Lockahead != "EOF") {
			if(isID()) {                 //ID
				exibirToken();
			}else if (isInt_Float()){   //Inteiro ou Float
				exibirToken();
			}
		}
		
		this.arq.fecharArquivo();
		
	}
	
	// *-----> Letra
	public boolean isLetra(String s) {
		boolean valid = false;

		if(s.matches("[a-zA-Z]+")) {
			valid = true;
		}
		return valid;
	}
	
	// *----->Digito
	public boolean isDigito(String s) {
		boolean valid = false;

		if(s.matches("[0-9]+")) {
			valid = true;
		}
		return valid;
	}
	
	// *-----> ID
	public boolean isID() throws IOException {
		boolean valid = false;
		String la = this.Lockahead;
		String lexama = la;
		
		this.arq.marcar(); //Marcar o local no arquivo para voltar caso não foi classificado o token
				
		if(isLetra(la) ||  la.equals("_")) {
			valid = true;
			
			la = arq.readCaracter();
			lexama = lexama.concat(la);
			
			while(isLetra(la) || la.equals("_")|| isDigito(la)) {
				lexama = lexama.concat(la);
				la = arq.readCaracter();
			}
		}
		
		
		if(!valid) {
			this.arq.reset(); //Voltar ponto no arquivo caso não foi classificado o token
		}else {
			Token token = new Token(lexama, Clasifc.ID.get());
			this.token = token;
			this.Lockahead = la;
		}
		
		
		return valid;
	}
	

	// *-----> Inteiro ou Float
	public boolean isInt_Float() throws IOException {
		boolean valid = false;
		String la = this.Lockahead;
		String lexama = la;
		String clasifc;
		
		this.arq.marcar(); //Marcar o local no arquivo para voltar caso não foi classificado o token
		
		while(isDigito(la)) {
			valid = true;	 // Inteiro
			clasifc = Clasifc.Inteiro.get();
			lexama = lexama.concat(la);
			la = arq.readCaracter();
		}
		
		//Validar se pode ser float
		if(la.equals(".")) {
			valid = false;
			lexama = lexama.concat(la);
			la = arq.readCaracter();
			
			while(isDigito(la)) {
				valid = true;	 // Float
				clasifc = Clasifc.Inteiro.get();
				lexama = lexama.concat(la);
				la = arq.readCaracter();
			}
			
			if(la.equals(".") || isLetra(la)) {
				valid = false;
				disparaErro(erroFloatMalFormado);
			}
			
		}
		
		
		if(!valid) {
			this.arq.reset(); //Voltar ponto no arquivo caso não foi classificado o token
		}else {
			Token token = new Token(lexama, Clasifc.ID.get());
			this.token = token;
			this.Lockahead = la;
		}
		
		return valid;
	}
	
	// *-----> Exibir Token
	public void exibirToken() {
		
    	String Lexama = this.token.getLexama();
    	String Clasif = this.token.getClasifica();
    	System.out.println("Lexama: " + Lexama + "\n" + "Clasificação: " + Clasif);
		
	}
	
	// *-----> Dispara Error
	public void disparaErro(String erro) {
		
    	int Linha = this.arq.getlinha();
    	int Coluna = this.arq.getColuna();
    	String lexama = this.token.getLexama();
    	System.out.println("ERRO na Linha: " + Linha + ", " + "Coluna: " + Coluna +  ", " + "Ultimo token lido: " + lexama + " Mensagem: " + erro);
    	System.exit(-1);
	}
}
