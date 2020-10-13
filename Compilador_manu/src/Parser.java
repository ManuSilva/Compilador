import java.io.IOException;

public class Parser {

	private Scanner scanner;
	private Token Lockahead;
	private Token LastToken;
	private int linha_lida;
	private int coluna_lida;

	// *------------------Erros Parser----------------------*
	static String erroIntMain = "O programa deve inciar com o comando 'INT MAIN()' corretamente";
	static String erroBlocoAbre = "O Bloco deve ser inciado somente com '{' ";
	static String erroBlocoFecha = "O Bloco deve ser Finalizado com '}' ";
	static String erroDeclaraVariavel = "Declara��o de Vari�vel inv�lida";
	static String erroPontoVirgula = "Falta ';' para complentar o argumento";
	static String erroFechaParent = "Falta ')' para complentar o argumento";
	static String erroWhileExpress�o = "Necess�rio Express�o relacional correta para o comando WHILE / DO-WHILE";
	static String erroIFExpress�o = "Necess�rio Express�o relacional correta para o comando IF / ELSE";
	static String erroAtribExpress�o = "Necess�rio Express�o Aritmetica correta para a Atribui��o";
	static String erroFatorExpress�o = "Necess�rio Express�o Aritmetica correta para o Fator";
	static String erroRelacionalExpress�o = "Necess�rio Express�o Aritmetica correta para opera��o relacional";
	static String erroMAINAbreParen = "Falta '(' para o INT MAIN";
	static String erroWhileAbreParen = "Falta '(' para o WHILE / DO-WHILE";
	static String erroIFAbreParen = "Falta '(' para o comando IF / ELSE";
	static String erroWhileComando = "Comando do WHILE / DO-WHILE incorreto";
	static String erroIFComando = "Comando do IF / ELSE incorreto";
	static String erroDoWhile = "Falta Comando WHILE no DO-WHILE";
	static String erroAtribIgual = "Falta '=' para a Atribui��o";
	static String erroOperadoRelacional = "Operador Relacional Inv�lido";
	static String erroAritTermo = "Termo incorreto para opera��o aritim�tica";
	static String erroAritFator = "Fator incorreto para opera��o aritim�tica";
	static String erroCodigoFim = "N�o � permitido c�digo ap�s o 'INT MAIN()'";
	static String erroVirgulaDeclara = "Necess�rio de v�rgula entre as vari�veis na declara��o";
	static String erroVariavelInvalida = "Vari�vel posicionada de forma inv�lida";
	
	// *-----------------------------------------------------*

	// *-----> Construtor
	public Parser(Arquivo arq) throws IOException {
		Token token = new Token("", "");
		Scanner scanner = new Scanner(arq);
		this.scanner = scanner;
		this.LastToken = token;
		this.Lockahead = scanner.verificLexico();
	}

	// *-----> Ler proximo Token do Scanner
	public void readToken() throws IOException {
		this.LastToken = this.Lockahead;
		this.linha_lida = this.scanner.getArquivo().getlinha();
		this.coluna_lida = this.scanner.getArquivo().getColuna();
	 this.Lockahead = this.scanner.verificLexico();

	}

	// *-----> Verifica��o Sint�tica
	public void verificSintatico() throws IOException {

		// Programa
		if (this.Lockahead.getClasifica().equals(Clasifc.INT.get())) { // INT

			readToken();

			if (this.Lockahead.getClasifica().equals(Clasifc.MAIN.get())) { // MAIN

				readToken();

				if (this.Lockahead.getClasifica().equals(Clasifc.Abre_Paren.get())) { // "("

					readToken();

					if (this.Lockahead.getClasifica().equals(Clasifc.Fecha_Paren.get())) { // ")"
						readToken();
						isBloco();
						if (!this.Lockahead.getLexama().equals("EOF")) {
							disparaErro(erroCodigoFim);
						} else {
							System.out.println("Programa compilado com sucesso!");
						}
					} else {
						disparaErro(erroFechaParent);
					}
				} else {
					disparaErro(erroMAINAbreParen);
				}
			} else {
				disparaErro(erroIntMain);
			}
		} else {
			disparaErro(erroIntMain);
		}

	}

	// *-----> Bloco
	public boolean isBloco() throws IOException {
		boolean validDeclaracao = false;
		boolean validComando = false;
		boolean valid = false;

		if (this.Lockahead.getClasifica().equals(Clasifc.Abre_Chaves.get())) { // '{'
			// Um ou mais Declara��o ou Comando
			readToken();
			do {
				validComando = false;
				valid = false;
				if (this.Lockahead.getClasifica().equals(Clasifc.ID.get())
						|| this.Lockahead.getClasifica().equals(Clasifc.Abre_Chaves.get())
						|| this.Lockahead.getClasifica().equals(Clasifc.WHILE.get())
						|| this.Lockahead.getClasifica().equals(Clasifc.DO.get())
						|| this.Lockahead.getClasifica().equals(Clasifc.IF.get())){
					validComando = isComando();
				}
				
				else if (this.Lockahead.getClasifica().equals(Clasifc.INT.get())
						|| this.Lockahead.getClasifica().equals(Clasifc.FLOAT.get())
						|| this.Lockahead.getClasifica().equals(Clasifc.CHAR.get())) {
					validDeclaracao = isDeclararVariavel();
				}
				
				else if(!this.Lockahead.getClasifica().equals(Clasifc.Fecha_Chaves.get())) {
					disparaErro(erroVariavelInvalida);
				}

			} while (!this.Lockahead.getClasifica().equals(Clasifc.Fecha_Chaves.get()));


			if (this.Lockahead.getClasifica().equals(Clasifc.Fecha_Chaves.get())) { // '}'
				valid = true;
				readToken();
			} else {
				disparaErro(erroBlocoFecha);
			}
		} else {
			disparaErro(erroBlocoAbre);
		}

		return valid;

	}

	// *-----> Declara��o de Vari�veis
	public boolean isDeclararVariavel() throws IOException {
		boolean valid = false;
		boolean validDeclarMult = false;

		if (isTipo()) {
			readToken();
			if (this.Lockahead.getClasifica().equals(Clasifc.ID.get())) {
				readToken();
				// Declarar M�ltiplas vari�veis
				do {
					validDeclarMult = isDeclararVariavelMult();
				} while (validDeclarMult);

				if (this.Lockahead.getClasifica().equals(Clasifc.Ponto_Virgula.get())) {
					readToken();
					valid = true;
				} else {
					disparaErro(erroPontoVirgula);
				}

			} else {
				disparaErro(erroDeclaraVariavel);
			}
		}

		return valid;

	}

	// *-----> Declara��o de Vari�veis multilpos
	public boolean isDeclararVariavelMult() throws IOException {
		boolean valid = false;

		if (this.Lockahead.getClasifica().equals(Clasifc.Virgula.get())) {
			readToken();
			if (this.Lockahead.getClasifica().equals(Clasifc.ID.get())) {
				readToken();
				valid = true;
			} else {
				disparaErro(erroDeclaraVariavel);
			}
		}else if (this.Lockahead.getClasifica().equals(Clasifc.ID.get())) {
			disparaErro(erroVirgulaDeclara);
		}

		return valid;

	}

	// *-----> Tipo
	public boolean isTipo() {
		boolean valid = false;

		if (this.Lockahead.getClasifica().equals(Clasifc.INT.get())
				|| this.Lockahead.getClasifica().equals(Clasifc.FLOAT.get())
				|| this.Lockahead.getClasifica().equals(Clasifc.CHAR.get())) {
			valid = true;
		}

		return valid;

	}

	// *-----> Comando
	public boolean isComando() throws IOException {
		boolean valid = false;

		// Valida Comando b�sico
		if (this.Lockahead.getClasifica().equals(Clasifc.ID.get())
				|| this.Lockahead.getClasifica().equals(Clasifc.Abre_Chaves.get())) {
			if (isComandoBasico()) {
				valid = true;
			}
		}

		// Valida Intera��o
		else if (this.Lockahead.getClasifica().equals(Clasifc.WHILE.get())
				|| this.Lockahead.getClasifica().equals(Clasifc.DO.get())) {
			if (isInteracao()) {
				valid = true;
			}
		}

		// Valida Condi��o
		else if (this.Lockahead.getClasifica().equals(Clasifc.IF.get())) {
			if (isCondicao()) {
				valid = true;
			}
		}

		return valid;

	}

	// *-----> Intera��o
	public boolean isInteracao() throws IOException {
		boolean valid = false;

		// Valida Intera��o While
		if (this.Lockahead.getClasifica().equals(Clasifc.WHILE.get())) {
			if (isWhile()) {
				valid = true;
			}
		}

		// Valida Intera��o DO-While
		else if (this.Lockahead.getClasifica().equals(Clasifc.DO.get())) {
			if (isDoWhile()) {
				valid = true;
			}
			valid = true;
		}

		return valid;

	}

	// *-----> Condi��o
	public boolean isCondicao() throws IOException {
		boolean valid = false;

		if (this.Lockahead.getClasifica().equals(Clasifc.IF.get())) {
			readToken();
			if (this.Lockahead.getClasifica().equals(Clasifc.Abre_Paren.get())) {
				readToken();
				if (isExprRelacional()) {
					if (this.Lockahead.getClasifica().equals(Clasifc.Fecha_Paren.get())) {
						readToken();
						if (isComando()) {
							valid = true;
							temElse();
						} else {
							disparaErro(erroIFComando);
						}
					} else {
						disparaErro(erroFechaParent);
					}
				} else {
					disparaErro(erroIFExpress�o);
				}
			} else {
				disparaErro(erroIFAbreParen);
			}
		}

		return valid;

	}

	// *-----> Validar o ELSE
	public void temElse() throws IOException {
		if (this.Lockahead.getClasifica().equals(Clasifc.ELSE.get())) {
			readToken();
			if (!isComando()) {
				disparaErro(erroIFComando);
			}
		}

	}

	// *-----> Comando B�sico
	public boolean isComandoBasico() throws IOException {
		boolean valid = false;

		// Valida Atribui��o
		if (this.Lockahead.getClasifica().equals(Clasifc.ID.get())) {
			if (isAtribuicao()) {
				valid = true;
			}
		}

		// Valida Bloco
		else if (this.Lockahead.getClasifica().equals(Clasifc.Abre_Chaves.get())) {
			if (isBloco()) {
				valid = true;
			}
		}

		return valid;

	}

	// *-----> Atribui��o
	public boolean isAtribuicao() throws IOException {
		boolean valid = false;

		if (this.Lockahead.getClasifica().equals(Clasifc.ID.get())) {
			readToken();
			if (this.Lockahead.getClasifica().equals(Clasifc.Igual.get())) {
				readToken();
				if (isExprAritmetica()) {
					if (this.Lockahead.getClasifica().equals(Clasifc.Ponto_Virgula.get())) {
						readToken();
						valid = true;
					} else {
						disparaErro(erroPontoVirgula);
					}
				} else {
					disparaErro(erroAtribExpress�o);
				}
			} else {
				disparaErro(erroAtribIgual);
			}
		}

		return valid;

	}

	// *-----> Intera��o While
	public boolean isWhile() throws IOException {
		boolean valid = false;

		if (this.Lockahead.getClasifica().equals(Clasifc.WHILE.get())) {
			readToken();
			if (this.Lockahead.getClasifica().equals(Clasifc.Abre_Paren.get())) {
				readToken();
				if (isExprRelacional()) {
					if (this.Lockahead.getClasifica().equals(Clasifc.Fecha_Paren.get())) {
						readToken();
						if (isComando()) {
							valid = true;
						} else {
							disparaErro(erroWhileComando);
						}
					} else {
						disparaErro(erroFechaParent);
					}
				} else {
					disparaErro(erroWhileExpress�o);
				}
			} else {
				disparaErro(erroWhileAbreParen);
			}
		}

		return valid;

	}

	// *-----> Intera��o Do-While
	public boolean isDoWhile() throws IOException {
		boolean valid = false;

		if (this.Lockahead.getClasifica().equals(Clasifc.DO.get())) {
			readToken();
			if (isComando()) {
				if (this.Lockahead.getClasifica().equals(Clasifc.WHILE.get())) {
					readToken();
					if (this.Lockahead.getClasifica().equals(Clasifc.Abre_Paren.get())) {
						readToken();
						if (isExprRelacional()) {
							if (this.Lockahead.getClasifica().equals(Clasifc.Fecha_Paren.get())) {
								readToken();
								if (this.Lockahead.getClasifica().equals(Clasifc.Ponto_Virgula.get())) {
									readToken();
									valid = true;
								} else {
									disparaErro(erroPontoVirgula);
								}

							} else {
								disparaErro(erroFechaParent);
							}
						} else {
							disparaErro(erroWhileExpress�o);
						}
					} else {
						disparaErro(erroWhileAbreParen);
					}
				} else {
					disparaErro(erroDoWhile);
				}
			} else {
				disparaErro(erroWhileComando);
			}

		}

		return valid;

	}

	// *-----> Express�o Relacional
	public boolean isExprRelacional() throws IOException {
		boolean valid = false;

		if (isExprAritmetica()) {
			if (isOPRelacional()) {
				readToken();
				if (isExprAritmetica()) {
					valid = true;
				} else {
					disparaErro(erroRelacionalExpress�o);
				}
			} else {
				disparaErro(erroOperadoRelacional);
			}
		}

		return valid;

	}

	// *-----> Express�o Aritmetica
	public boolean isExprAritmetica() throws IOException {
		boolean valid = false;
		boolean soma_dimi = false;
		if (isTermo()) {
			valid = true;
			do {
				soma_dimi = SomaDimin();
			} while (soma_dimi);
		}

		return valid;

	}

	// *-----> Termo
	public boolean isTermo() throws IOException {
		boolean valid = false;
		boolean mult_div = false;
		if (isFator()) {
			valid = true;
			do {
				mult_div = multDiv();
			} while (mult_div);
		}

		return valid;

	}

	// *-----> Fator
	public boolean isFator() throws IOException {
		boolean valid = false;

		if (this.Lockahead.getClasifica().equals(Clasifc.ID.get())
				|| this.Lockahead.getClasifica().equals(Clasifc.Inteiro.get())
				|| this.Lockahead.getClasifica().equals(Clasifc.Float.get())
				|| this.Lockahead.getClasifica().equals(Clasifc.Char.get())) {
			readToken();
			valid = true;
		}

		else if (this.Lockahead.getClasifica().equals(Clasifc.Abre_Paren.get())) {
			readToken();
			if (isExprAritmetica()) {
				if (this.Lockahead.getClasifica().equals(Clasifc.Fecha_Paren.get())) {
					readToken();
					valid = true;
				} else {
					disparaErro(erroFechaParent);
				}
			} else {
				disparaErro(erroFatorExpress�o);
			}
		}

		return valid;

	}

	// *-----> Multiplica��o e Divis�o
	public boolean multDiv() throws IOException {
		boolean valid = false;

		if (this.Lockahead.getClasifica().equals(Clasifc.Mult.get())
				|| this.Lockahead.getClasifica().equals(Clasifc.Div.get())) {
			readToken();
			if (isFator()) {
				valid = true;
			} else {
				disparaErro(erroAritFator);
			}
		}

		return valid;

	}

	// *-----> Soma e Subtra��o
	public boolean SomaDimin() throws IOException {
		boolean valid = false;

		if (this.Lockahead.getClasifica().equals(Clasifc.Soma.get())
				|| this.Lockahead.getClasifica().equals(Clasifc.Menos.get())) {
			readToken();
			if (isTermo()) {
				valid = true;
			} else {
				disparaErro(erroAritTermo);
			}
		}

		return valid;

	}

	// *-----> Opera��o Relacional
	public boolean isOPRelacional() throws IOException {
		boolean valid = false;

		if (this.Lockahead.getClasifica().equals(Clasifc.Menor_Que.get())
				|| this.Lockahead.getClasifica().equals(Clasifc.Maior_Que.get())
				|| this.Lockahead.getClasifica().equals(Clasifc.Igual_Menor_Que.get())
				|| this.Lockahead.getClasifica().equals(Clasifc.Igual_Maior_Que.get())
				|| this.Lockahead.getClasifica().equals(Clasifc.Igual_Igual.get())
				|| this.Lockahead.getClasifica().equals(Clasifc.Igual_Diferente.get())) {
			valid = true;
		}

		return valid;

	}

	// *-----> Dispara Error
	public void disparaErro(String erro) {

		int Linha = this.linha_lida;
		int Coluna = this.coluna_lida;
		String lexama = this.LastToken.getLexama();
		System.out.println("ERRO na Linha: " + Linha + ", " + "Coluna: " + Coluna + ", " + "Ultimo token lido: "
				+ lexama + " Mensagem: " + erro);
		System.exit(-1);
	}
}
