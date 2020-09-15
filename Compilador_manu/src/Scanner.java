import java.io.IOException;

public class Scanner {

	private Arquivo arq;
	private String Lockahead;
	private Token token;
	
	// *------------------Erros Scanner----------------------*
	static String erroFloatMalFormado = "Valor Float mal formado";
	static String erroComentarioMult = "Comando de comentário multilha não foi fechado, fim do arquivo alcançado";
	static String erroExclamacao = "Exclamação (‘!’) não seguida de ‘=’";
	static String erroCharMalFormado = "Valor Char mal formado";
	static String erroCaracterInvalido = "Caracter Inválido";
	// *-----------------------------------------------------*

	// *-----> Construtor
	public Scanner(Arquivo arq) throws IOException {
		this.arq = arq;
		this.Lockahead = arq.readCaracter();
		Token token = new Token("","");
		this.token = token;
	}

	// *-----> Get
	public Token getToken() {
		return this.token;
	}

	// *-----> Verificação Léxica
	public void verificLexico() throws IOException {

		while (!this.Lockahead.equals("$")) { // Enquanto não chega no Fim de arquivo
			if (!this.Lockahead.equals("\r") && !this.Lockahead.equals("\n")    //Ignorar sinal de pula linha 
		     && !this.Lockahead.equals(" ")  && !this.Lockahead.equals("\t")) { // Ignorar espaços em branco 

				if (!isComentario()) { // Ignorar Comentários
					if (isID()) { // ID
						isPalavraReservada(); // Palavra Reservada
						exibirToken();
					} else if (isInt_Float()) { // Inteiro ou Float
						exibirToken();
					}else if (isRelacional()) { // Operadores Relaicionais
						exibirToken();
					}else if (isAritimetico()) { // Operadores Aritiméticos
						exibirToken();
					}else if (isChar()) {     // Char
						exibirToken();
					}else if (isCaracterEspecial()) { // Caracter Especial
						exibirToken();
					}else {
						disparaErro(erroCaracterInvalido);
					}
				}

			} else {
				this.Lockahead = arq.readCaracter();
			}
		}

		// Fechar o arquivo
		this.arq.fecharArquivo();

	}

	// *-----> Letra
	public boolean isLetra(String s) {
		boolean valid = false;

		if (s.matches("[a-zA-Z]+")) {
			valid = true;
		}
		return valid;
	}

	// *-----> Digito
	public boolean isDigito(String s) {
		boolean valid = false;

		if (s.matches("[0-9]+")) {
			valid = true;
		}
		return valid;
	}

	// *-----> ID
	public boolean isID() throws IOException {
		boolean valid = false;
		String la = this.Lockahead;
		String lexama = "";

		this.arq.marcar(); // Marcar o local no arquivo para voltar caso não foi classificado o token

		if (isLetra(la) || la.equals("_")) {
			valid = true;

			lexama = lexama.concat(la);
			la = arq.readCaracter();

			while (isLetra(la) || la.equals("_") || isDigito(la)) {
				lexama = lexama.concat(la);
				la = arq.readCaracter();
			}
		}

		if (!valid) {
			this.arq.reset(); // Voltar ponto no arquivo caso não foi classificado o token
		} else {
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
		String lexama = "";
		String clasifc = "";

		this.arq.marcar(); // Marcar o local no arquivo para voltar caso não foi classificado o token

		while (isDigito(la)) {
			valid = true; // Inteiro
			clasifc = Clasifc.Inteiro.get();
			lexama = lexama.concat(la);
			la = arq.readCaracter();
		}

		// Validar se pode ser float
		if (la.equals(".")) {
			valid = false;
			lexama = lexama.concat(la);
			la = arq.readCaracter();
			
			if(isDigito(la)) {
				valid = true; // Float
				clasifc = Clasifc.Float.get();
				lexama = lexama.concat(la);
				la = arq.readCaracter();
				
				while (isDigito(la)) {
					lexama = lexama.concat(la);
					la = arq.readCaracter();
				}

				if (la.equals(".")) {
					valid = false;
					disparaErro(erroFloatMalFormado);
				}
			}else {
				valid = false;
				disparaErro(erroFloatMalFormado);
			}
		}

		if (!valid) {
			this.arq.reset(); // Voltar ponto no arquivo caso não foi classificado o token
		} else {
			Token token = new Token(lexama, clasifc);
			this.token = token;
			this.Lockahead = la;
		}

		return valid;
	}

	// *-----> Palavra Reservada
	public boolean isPalavraReservada() {
		boolean valid = false;
		String lexama = this.token.getLexama();
		String clasifc = "";

		switch (lexama) {
		case "MAIN":
			valid = true;
			clasifc = Clasifc.MAIN.get();
			break;
		case "IF":
			valid = true;
			clasifc = Clasifc.IF.get();
			break;
		case "ELSE":
			valid = true;
			clasifc = Clasifc.ELSE.get();
			break;
		case "WHILE":
			valid = true;
			clasifc = Clasifc.WHILE.get();
			break;
		case "DO":
			valid = true;
			clasifc = Clasifc.DO.get();
			break;
		case "FOR":
			valid = true;
			clasifc = Clasifc.FOR.get();
			break;
		case "INT":
			valid = true;
			clasifc = Clasifc.INT.get();
			break;
		case "FLOAT":
			valid = true;
			clasifc = Clasifc.FLOAT.get();
			break;
		case "CHAR":
			valid = true;
			clasifc = Clasifc.CHAR.get();
			break;
		default:
		}

		if (valid) {
			this.token.setClasifica(clasifc);
		}

		return valid;
	}

	// *-----> Comentário
	public boolean isComentario() throws IOException {
		boolean valid = false;
		String la = this.Lockahead;

		this.arq.marcar(); // Marcar o local no arquivo para voltar caso não for comentário

		if (la.equals("/")) {
			la = this.arq.readCaracter();
			if (la.equals("/")) { // Coméntario de uma linha
				valid = true;

				// Passar o lookahead para o final da linha ou até acabar o arquivo
				while (!la.equals("\r") && !la.equals("$")) {
					la = arq.readCaracter();
				}

			}
		} else if (la.equals("*")) { // Coméntario Multilinha
			la = this.arq.readCaracter();
			if (la.equals("/")) {
				la = this.arq.readCaracter();
				// Encontrar o comando para fecha comentário (/*)
				do {

					while (!la.equals("/") && !la.equals("$")) {
						la = arq.readCaracter();
					}
					if (la.equals("$")) {
						break;
					} else {
						la = arq.readCaracter();

						if (la.equals("*")) {
							break;
						}else {
							la = arq.readCaracter();
						}
					}

				} while (!la.equals("$")); // Enquanto não chega no final do arquivo

				// Se chegou no final do arquivo sem fechar o comentario multilinha
				if (la.equals("$")) {
					valid = false;
					disparaErro(erroComentarioMult);
				} else {
					la = arq.readCaracter();
					valid = true;
				}
			}
		}

		if (!valid) {
			this.arq.reset(); // Voltar ponto no arquivo caso não for comentário
		} else {
			this.Lockahead = la;
		}

		return valid;
	}

	// *-----> Operadores Relacionais
	public boolean isRelacional() throws IOException {
		boolean valid = false;
		String la = this.Lockahead;
		String lexama = "";
		String clasifc = "";

		this.arq.marcar(); // Marcar o local no arquivo para voltar caso não foi classificado o token

		if (la.equals("<")) {
			valid = true; // Menor Que
			clasifc = Clasifc.Menor_Que.get();
			lexama = lexama.concat(la);
			la = arq.readCaracter();

			if (la.equals("=")) {
				valid = true; // Igual Menor Que
				clasifc = Clasifc.Igual_Menor_Que.get();
				lexama = lexama.concat(la);
				la = arq.readCaracter();
			}

		} else if (la.equals(">")) {
			valid = true; // Maior Que
			clasifc = Clasifc.Maior_Que.get();
			lexama = lexama.concat(la);
			la = arq.readCaracter();

			if (la.equals("=")) {
				valid = true; // Igual Maior Que
				clasifc = Clasifc.Igual_Maior_Que.get();
				lexama = lexama.concat(la);
				la = arq.readCaracter();
			}
		} else if (la.equals("=")) {
			lexama = lexama.concat(la);
			la = arq.readCaracter();

			if (la.equals("=")) {
				valid = true; // Igual Igual
				clasifc = Clasifc.Igual_Igual.get();
				lexama = lexama.concat(la);
				la = arq.readCaracter();
			}
		} else if (la.equals("!")) {
			lexama = lexama.concat(la);
			la = arq.readCaracter();

			if (la.equals("=")) {
				valid = true; // Diferença (!=)
				clasifc = Clasifc.Igual_Diferente.get();
				lexama = lexama.concat(la);
				la = arq.readCaracter();
			}else{
				valid = false;
				disparaErro(erroExclamacao); //Exclamação (‘!’) não seguida de ‘=’
			}
		}

		if (!valid) {
			this.arq.reset(); // Voltar ponto no arquivo caso não foi classificado o token
		} else {
			Token token = new Token(lexama, clasifc);
			this.token = token;
			this.Lockahead = la;
		}

		return valid;
	}
	// *-----> Operadores Aritiméticos
	public boolean isAritimetico() throws IOException {
		boolean valid = false;
		String la = this.Lockahead;
		String lexama = "";
		String clasifc = "";
		
		this.arq.marcar(); // Marcar o local no arquivo para voltar caso não foi classificado o token
		
		switch (la) {
		case "+":
			valid = true;
			lexama = la;
			clasifc = Clasifc.Soma.get();
			break;
		case "-":
			valid = true;
			lexama = la;
			clasifc = Clasifc.Menos.get();
			break;
		case "*":
			valid = true;
			lexama = la;
			clasifc = Clasifc.Mult.get();
			break;
		case "/":
			valid = true;
			lexama = la;
			clasifc = Clasifc.Div.get();
			break;
		case "=":
			valid = true;
			lexama = la;
			clasifc = Clasifc.Igual.get();
			break;
		default:
		}

		if (!valid) {
			this.arq.reset(); // Voltar ponto no arquivo caso não foi classificado o token
		} else {
			Token token = new Token(lexama, clasifc);
			this.token = token;
			this.Lockahead = arq.readCaracter();
		}

		return valid;
	}
	
	// *-----> Char
	public boolean isChar() throws IOException {
		boolean valid = false;
		String la = this.Lockahead;
		String lexama = "";
		
		this.arq.marcar(); // Marcar o local no arquivo para voltar caso não foi classificado o token
		
		if(la.equals("'")) {
			lexama = lexama.concat(la);
			la = arq.readCaracter();
			
			if(isLetra(la) || isDigito(la)){
				
				lexama = lexama.concat(la);
				la = arq.readCaracter();
				
				if(la.equals("'")) {
					valid = true;
					lexama = lexama.concat(la);
					la = arq.readCaracter();
				}else {
					valid = false;
					disparaErro(erroCharMalFormado); 
				}
			}else {
				valid = false;
				disparaErro(erroCharMalFormado); 
			}
		}
		
		
		if (!valid) {
			this.arq.reset(); // Voltar ponto no arquivo caso não foi classificado o token
		} else {
			Token token = new Token(lexama,Clasifc.Char.get());
			this.token = token;
			this.Lockahead = la;
		}
		
		return valid;
	}
	
	// *-----> Caracter Especial
	public boolean isCaracterEspecial() throws IOException {
		boolean valid = false;
		String la = this.Lockahead;
		String lexama = "";
		String clasifc = "";
		
		this.arq.marcar(); // Marcar o local no arquivo para voltar caso não foi classificado o token
		
		switch (la) {
		case ")":
			valid = true;
			lexama = la;
			clasifc = Clasifc.Fecha_Paren.get();
			break;
		case "(":
			valid = true;
			lexama = la;
			clasifc = Clasifc.Abre_Paren.get();
			break;
		case "{":
			valid = true;
			lexama = la;
			clasifc = Clasifc.Abre_Chaves.get();
			break;
		case "}":
			valid = true;
			lexama = la;
			clasifc = Clasifc.Fecha_Chaves.get();
			break;
		case ",":
			valid = true;
			lexama = la;
			clasifc = Clasifc.Virgula.get();
			break;
		case ";":
			valid = true;
			lexama = la;
			clasifc = Clasifc.Ponto_Virgula.get();
			break;
		default:
		}
		
		
		if (!valid) {
			this.arq.reset(); // Voltar ponto no arquivo caso não foi classificado o token
		} else {
			Token token = new Token(lexama, clasifc);
			this.token = token;
			this.Lockahead = arq.readCaracter();
		}
		
		return valid;
		
	}
	
	// *-----> Exibir Token
	public void exibirToken() {

		String Lexama = this.token.getLexama();
		String Clasif = this.token.getClasifica();
		System.out.println("Lexama: " + Lexama + "\t" + " Clasificação: " + Clasif);

	}

	// *-----> Dispara Error
	public void disparaErro(String erro) {

		int Linha = this.arq.getlinha();
		int Coluna = this.arq.getColuna();
		String lexama = this.token.getLexama();
		System.out.println("ERRO na Linha: " + Linha + ", " + "Coluna: " + Coluna + ", " + "Ultimo token lido: "
				+ lexama + " Mensagem: " + erro);
		System.exit(-1);
	}
}
