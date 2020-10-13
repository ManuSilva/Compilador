import java.io.IOException;

public class Parser {

	private Scanner scanner;
	private Token Lockahead;
	private Token LastToken;

	// *------------------Erros Parser----------------------*
	static String erroIntMain = "O programa deve inciar com o comando 'INT MAIN()' corretamente";
	static String erroBlocoAbre = "O Bloco deve ser inciado com '{' ";
	static String erroBlocoFecha = "O Bloco deve ser Finalizado com '}' ";
	static String erroDeclaraVariavel = "Declaração de Variável inválida";
	static String erroPontoVirgula = "Falta ';' para complentar o argumento";
	static String erroFechaParent = "Falta ')' para complentar o argumento";
	static String erroWhileExpressão = "Necessário Expressão relacional correta para o comando WHILE / DO-WHILE";
	static String erroIFExpressão = "Necessário Expressão relacional correta para o comando IF / ELSE";
	static String erroAtribExpressão = "Necessário Expressão Aritmetica correta para a Atribuição";
	static String erroFatorExpressão = "Necessário Expressão Aritmetica correta para o Fator";
	static String erroRelacionalExpressão = "Necessário Expressão Aritmetica correta para operação relacional";
	static String erroFatorAbreParen = "Falta '(' para o Fator";
	static String erroWhileAbreParen = "Falta '(' para o WHILE / DO-WHILE";
	static String erroIFAbreParen = "Falta '(' para o comando IF / ELSE";
	static String erroWhileComando = "Comando do WHILE / DO-WHILE incorreto";
	static String erroIFComando = "Comando do IF / ELSE incorreto";
	static String erroDoWhile = "Falta Comando WHILE no DO-WHILE";
	static String erroAtribIgual = "Falta '=' para a Atribuição";
	static String erroOperadoRelacional = "Operador Relacional Inválido";
	static String erroAritTermo = "Termo incorreto para operação aritimética";
	static String erroAritFator = "Fator incorreto para operação aritimética";
	static String erroCodigoFim = "Não é permitido código após o 'INT MAIN()'";
	// *-----------------------------------------------------*

	// *-----> Construtor
	public Parser(Arquivo arq) throws IOException {
		Scanner scanner = new Scanner(arq);
		this.scanner = scanner;
		this.Lockahead = scanner.verificLexico();
	}

	// *-----> Ler proximo Token do Scanner
	public void readToken() throws IOException {
		this.LastToken = this.Lockahead;
		this.Lockahead = this.scanner.verificLexico();
	}

	// *-----> Verificação Sintática
	public void verificSintatico() throws IOException {

		// Programa
		if (this.Lockahead.getClasifica().equals(Clasifc.INT.get())) { // INT

			readToken();

			if (this.Lockahead.getClasifica().equals(Clasifc.MAIN.get())) { // MAIN

				readToken();

				if (this.Lockahead.getClasifica().equals("(")) { // "("

					readToken();

					if (this.Lockahead.getClasifica().equals(")")) { // ")"
						readToken();
						isBloco();
						if(!this.Lockahead.getLexama().equals("EOF")) {
							disparaErro(erroCodigoFim);
						}
					} else {
						disparaErro(erroIntMain);
					}
				} else {
					disparaErro(erroIntMain);
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
			// Um ou mais Declaração ou Comando

			do {
				readToken();
				validDeclaracao = isDeclararVariavel();
				validComando = isComando();
			} while (validDeclaracao || validComando);

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

	// *-----> Declaração de Variáveis
	public boolean isDeclararVariavel() throws IOException {
		boolean valid = false;
		boolean validDeclarMult = false;

		if (isTipo()) {
			readToken();
			if (this.Lockahead.getClasifica().equals(Clasifc.ID.get())) {
				readToken();
				if (this.Lockahead.getClasifica().equals(Clasifc.Virgula.get())) {
					readToken();
					do {
						validDeclarMult = isDeclararVariavelMult();
					} while (validDeclarMult);

				}

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

	// *-----> Declaração de Variáveis multilpos
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

		// Valida Comando básico
		if (this.Lockahead.getClasifica().equals(Clasifc.ID.get())
				|| this.Lockahead.getClasifica().equals(Clasifc.Abre_Chaves.get())) {
			if (isComandoBasico()) {
				valid = true;
			}
		}

		// Valida Interação
		else if (this.Lockahead.getClasifica().equals(Clasifc.WHILE.get())
				|| this.Lockahead.getClasifica().equals(Clasifc.DO.get())) {
			if (isInteracao()) {
				valid = true;
			}
		}

		// Valida Condição
		else if (this.Lockahead.getClasifica().equals(Clasifc.IF.get())) {
			if (isCondicao()) {
				valid = true;
			}
		}

		return valid;

	}

	// *-----> Interação
	public boolean isInteracao() throws IOException {
		boolean valid = false;

		// Valida Interação While
		if (this.Lockahead.getClasifica().equals(Clasifc.WHILE.get())) {
			if (isWhile()) {
				valid = true;
			}
		}

		// Valida Interação DO-While
		else if (this.Lockahead.getClasifica().equals(Clasifc.DO.get())) {
			if (isDoWhile()) {
				valid = true;
			}
			valid = true;
		}

		return valid;

	}

	// *-----> Condição
	public boolean isCondicao() throws IOException {
		boolean valid = false;

		if (this.Lockahead.getClasifica().equals(Clasifc.IF.get())) {
			if (this.Lockahead.getClasifica().equals(Clasifc.Abre_Paren.get())) {
				readToken();
				if (isExprRelacional()) {
					readToken();
					if (this.Lockahead.getClasifica().equals(Clasifc.Fecha_Paren.get())) {
						readToken();
						if (isComando()) {
							readToken();
							valid = true;
							temElse();
						} else {
							disparaErro(erroIFComando);
						}
					} else {
						disparaErro(erroFechaParent);
					}
				} else {
					disparaErro(erroIFExpressão);
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
			if (isComando()) {
				readToken();
			} else {
				disparaErro(erroIFComando);
			}
		}

	}

	// *-----> Comando Básico
	public boolean isComandoBasico() throws IOException {
		boolean valid = false;

		// Valida Atribuição
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

	// *-----> Atribuição
	public boolean isAtribuicao() throws IOException {
		boolean valid = false;

		if (this.Lockahead.getClasifica().equals(Clasifc.ID.get())) {
			readToken();
			if (this.Lockahead.getClasifica().equals(Clasifc.Igual.get())) {
				readToken();
				if (isExprAritmetica()) {
					readToken();
					if (this.Lockahead.getClasifica().equals(Clasifc.Ponto_Virgula.get())) {
						readToken();
						valid = true;
					} else {
						disparaErro(erroPontoVirgula);
					}
				} else {
					disparaErro(erroAtribExpressão);
				}
			} else {
				disparaErro(erroAtribIgual);
			}
		}

		return valid;

	}

	// *-----> Interação While
	public boolean isWhile() throws IOException {
		boolean valid = false;

		if (this.Lockahead.getClasifica().equals(Clasifc.WHILE.get())) {
			readToken();
			if (this.Lockahead.getClasifica().equals(Clasifc.Abre_Paren.get())) {
				readToken();
				if (isExprRelacional()) {
					readToken();
					if (this.Lockahead.getClasifica().equals(Clasifc.Fecha_Paren.get())) {
						readToken();
						if (isComando()) {
							readToken();
							valid = true;
						} else {
							disparaErro(erroWhileComando);
						}
					} else {
						disparaErro(erroFechaParent);
					}
				} else {
					disparaErro(erroWhileExpressão);
				}
			} else {
				disparaErro(erroWhileAbreParen);
			}
		}

		return valid;

	}

	// *-----> Interação Do-While
	public boolean isDoWhile() throws IOException {
		boolean valid = false;

		if (this.Lockahead.getClasifica().equals(Clasifc.DO.get())) {
			readToken();
			if (isComando()) {
				readToken();
				if (this.Lockahead.getClasifica().equals(Clasifc.WHILE.get())) {
					readToken();
					if (this.Lockahead.getClasifica().equals(Clasifc.Abre_Paren.get())) {
						readToken();
						if (isExprRelacional()) {
							readToken();
							if (this.Lockahead.getClasifica().equals(Clasifc.Fecha_Paren.get())) {
								readToken();
								valid = true;
							} else {
								disparaErro(erroFechaParent);
							}
						} else {
							disparaErro(erroWhileExpressão);
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

	// *-----> Expressão Relacional
	public boolean isExprRelacional() throws IOException {
		boolean valid = false;

		if (isExprAritmetica()) {
			readToken();
			if (isOPRelacional()) {
				readToken();
				if (isExprAritmetica()) {
					readToken();
					valid = true;
				} else {
					disparaErro(erroRelacionalExpressão);
				}
			} else {
				disparaErro(erroOperadoRelacional);
			}
		} else {
			disparaErro(erroRelacionalExpressão);
		}

		return valid;

	}

	// *-----> Expressão Aritmetica
	public boolean isExprAritmetica() throws IOException {
		boolean valid = false;
		boolean soma_dimi = false;
		if (isTermo()) {
			readToken();
			valid = true;
			do {
				soma_dimi = SomaDimin();
			} while (soma_dimi);
		} else {
			disparaErro(erroAritTermo);
		}

		return valid;

	}

	// *-----> Termo
	public boolean isTermo() throws IOException {
		boolean valid = false;
		boolean mult_div = false;
		if (isFator()) {
			readToken();
			valid = true;
			do {
				mult_div = multDiv();
			} while (mult_div);
		} else {
			disparaErro(erroAritFator);
		}

		return valid;

	}

	// *-----> Fator
	public boolean isFator() throws IOException {
		boolean valid = false;

		if (this.Lockahead.getClasifica().equals(Clasifc.ID.get())
				|| this.Lockahead.getClasifica().equals(Clasifc.INT.get())
				|| this.Lockahead.getClasifica().equals(Clasifc.FLOAT.get())
				|| this.Lockahead.getClasifica().equals(Clasifc.CHAR.get())) {
			valid = true;
		}

		else if (this.Lockahead.getClasifica().equals(Clasifc.Abre_Paren.get())) {
			readToken();
			if (isExprAritmetica()) {
				readToken();
				if (this.Lockahead.getClasifica().equals(Clasifc.Fecha_Paren.get())) {
					readToken();
					valid = true;
				} else {
					disparaErro(erroFechaParent);
				}
			} else {
				disparaErro(erroFatorExpressão);
			}
		} else {
			disparaErro(erroFatorAbreParen );
		}

		return valid;

	}

	// *-----> Soma e Subtração
	public boolean SomaDimin() throws IOException {
		boolean valid = false;

		if (this.Lockahead.getClasifica().equals(Clasifc.Mult.get())
				|| this.Lockahead.getClasifica().equals(Clasifc.Div.get())) {
			readToken();
			if (isTermo()) {
				readToken();
				valid = true;
			} else {
				disparaErro(erroAritFator);
			}
		}

		return valid;

	}

	// *-----> Multiplicação e Divisão
	public boolean multDiv() throws IOException {
		boolean valid = false;

		if (this.Lockahead.getClasifica().equals(Clasifc.Soma.get())
				|| this.Lockahead.getClasifica().equals(Clasifc.Menos.get())) {
			readToken();
			if (isFator()) {
				readToken();
				valid = true;
			} else {
				disparaErro(erroAritTermo);
			}
		}

		return valid;

	}

	// *-----> Operação Relacional
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

		int Linha = this.scanner.getArquivo().getlinha();
		int Coluna = this.scanner.getArquivo().getColuna();
		String lexama = this.LastToken.getLexama();
		System.out.println("ERRO na Linha: " + Linha + ", " + "Coluna: " + Coluna + ", " + "Ultimo token lido: "
				+ lexama + " Mensagem: " + erro);
		System.exit(-1);
	}
}
