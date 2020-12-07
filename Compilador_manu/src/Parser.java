import java.io.IOException;
import java.util.Stack;

public class Parser {

	private Scanner scanner;
	private Token Lockahead;
	private Token LastToken;
	private int linha_lida;
	private int coluna_lida;
	private int bloco = 0;
	private TabSimbolo tabela_simbolo;
	private String LastTipo;
	private int contTemp = -1;
	private int contLabel = -1;
	private String LastLabel;

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

	// *------------------Erros Sem�ntico----------------------*

	static String erroTipoChar = "Valor do tipo CHAR apenas permitidos opera��o com outro tipo CHAR";
	static String erroIntIgualFloat = "Vari�vel INT n�o pode receber valor do tipo FLOAT";
	static String erroVariav�lJaDeclarada = "Vari�vel j� delcarada nesse escopo";
	//static String erroVariavelNaoDeclarada = "Vari�vel n�o foi declarada";

	// *-----------------------------------------------------*
	// *-----> Construtor
	public Parser(Arquivo arq) throws IOException {
		Token token = new Token("", "");
		TabSimbolo tab = new TabSimbolo();
		Scanner scanner = new Scanner(arq);
		this.scanner = scanner;
		this.LastToken = token;
		this.Lockahead = scanner.verificLexico();
		this.tabela_simbolo = tab;
	}

	// *-----> Ler proximo Token do Scanner
	public void readToken() throws IOException {
		this.LastToken = this.Lockahead;
		this.linha_lida = this.scanner.getArquivo().getlinha();
		this.coluna_lida = this.scanner.getArquivo().getColuna();
		this.Lockahead = this.scanner.verificLexico();

	}

	// *------>Criar vari�vel tempor�ria (GCI)
	public String createTemp() {
		this.contTemp++;
		return "t" + this.contTemp;
	}

	// *------>Criar Label (GCI)
	public String createLabel() {
		this.contLabel++;
		return "L" + this.contLabel;
		
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
			this.bloco++; // Contagem de bloco
			readToken();
			do {
				validComando = false;
				valid = false;
				if (this.Lockahead.getClasifica().equals(Clasifc.ID.get())
						|| this.Lockahead.getClasifica().equals(Clasifc.Abre_Chaves.get())
						|| this.Lockahead.getClasifica().equals(Clasifc.WHILE.get())
						|| this.Lockahead.getClasifica().equals(Clasifc.DO.get())
						|| this.Lockahead.getClasifica().equals(Clasifc.IF.get())) {
					validComando = isComando();
				}

				else if (this.Lockahead.getClasifica().equals(Clasifc.INT.get())
						|| this.Lockahead.getClasifica().equals(Clasifc.FLOAT.get())
						|| this.Lockahead.getClasifica().equals(Clasifc.CHAR.get())) {
					this.LastTipo = setarTipo(this.Lockahead.getClasifica());
					validDeclaracao = isDeclararVariavel();
				}

				else if (!this.Lockahead.getClasifica().equals(Clasifc.Fecha_Chaves.get())) {
					disparaErro(erroVariavelInvalida);
				}

			} while (!this.Lockahead.getClasifica().equals(Clasifc.Fecha_Chaves.get()));

			if (this.Lockahead.getClasifica().equals(Clasifc.Fecha_Chaves.get())) { // '}'
				valid = true;
				readToken();
				this.tabela_simbolo.desempilhaBloco(this.bloco);
				this.bloco--;
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
				this.tabela_simbolo.MontaTabelaSimbolo(this.LastTipo, this.bloco, this.Lockahead);
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
		this.LastTipo = "";
		return valid;

	}

	// *-----> Declara��o de Vari�veis multilpos
	public boolean isDeclararVariavelMult() throws IOException {
		boolean valid = false;

		if (this.Lockahead.getClasifica().equals(Clasifc.Virgula.get())) {
			readToken();
			if (this.Lockahead.getClasifica().equals(Clasifc.ID.get())) {
				this.tabela_simbolo.MontaTabelaSimbolo(this.LastTipo, this.bloco, this.Lockahead);
				readToken();
				valid = true;
			} else {
				disparaErro(erroDeclaraVariavel);
			}
		} else if (this.Lockahead.getClasifica().equals(Clasifc.ID.get())) {
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
		Token token1 = new Token("", "");
		String label = "";

		if (this.Lockahead.getClasifica().equals(Clasifc.IF.get())) {
			readToken();
			if (this.Lockahead.getClasifica().equals(Clasifc.Abre_Paren.get())) {
				readToken();
				token1 = isExprRelacional();
				if (!token1.getLexama().equals("")) {
					if (this.Lockahead.getClasifica().equals(Clasifc.Fecha_Paren.get())) {
						readToken();
						label = createLabel();
						System.out.println("IF " + token1.getLexama() + " == 0 GOTO " + label);
						this.LastLabel = label;
						if (isComando()) {
							valid = true;
							if(!temElse()) {
								System.out.println(label + ":");
							}
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
	public boolean temElse() throws IOException {
		String label = "";
		boolean tem = false;
		if (this.Lockahead.getClasifica().equals(Clasifc.ELSE.get())) {
			tem = true;
			label = createLabel();
			System.out.println("GOTO " + label);
			System.out.println(this.LastLabel + ":");
			this.LastLabel = label;
			readToken();
			if (!isComando()) {
				disparaErro(erroIFComando);
			}else {				
				System.out.println(label + ":");
			}
		}
		return tem;

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
		String tipo1 = "";
		String tipo2 = "";
		String op = "";
		String tipo_result = "";
		Token token1 = new Token("", "");
		Token token2 = new Token("", "");
		String aux1 = "";
		String aux2 = "";
		String aux3 = "";

		if (this.Lockahead.getClasifica().equals(Clasifc.ID.get())) {
			token1 = this.tabela_simbolo.JaDeclarado(this.Lockahead.getLexama(), 0);
			tipo1 = token1.getTipo();
			aux1 = token1.getLexama();
			if (!tipo1.equals("")) {
				readToken();
				if (this.Lockahead.getClasifica().equals(Clasifc.Igual.get())) {
					op = Clasifc.Igual.get();
					readToken();
					token2 = isExprAritmetica();
					tipo2 = token2.getTipo();
					aux2 = token2.getLexama();
					if (!tipo2.equals("")) {
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
			} else {
				disparaErroVariavel(this.Lockahead.getLexama());
			}
		}

		tipo_result = comparaTipo(tipo1, op, tipo2);

		if (tipo_result.equals("")) {
			valid = false;
		} else {
			System.out.println(aux1 + op + aux2);
		}

		return valid;

	}

	// *-----> Intera��o While
	public boolean isWhile() throws IOException {
		boolean valid = false;
		Token token1 = new Token("", "");
		String label = "";
		String label_old = "";

		if (this.Lockahead.getClasifica().equals(Clasifc.WHILE.get())) {
			readToken();
			if (this.Lockahead.getClasifica().equals(Clasifc.Abre_Paren.get())) {
				readToken();
				label = createLabel();
				System.out.println(label + ":");
				label_old = label;
				token1 = isExprRelacional();
				if (!token1.getLexama().equals("")) {
					if (this.Lockahead.getClasifica().equals(Clasifc.Fecha_Paren.get())) {
						label = createLabel();
						System.out.println("IF " + token1.getLexama() + " == 0 GOTO " + label);
						readToken();
						if (isComando()) {
							System.out.println("GOTO " + label_old);
							System.out.println(label + ":");
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
		String label = "";
		boolean valid = false;
		Token token1 = new Token("", "");

		if (this.Lockahead.getClasifica().equals(Clasifc.DO.get())) {
			label = createLabel();
			System.out.println(label + ":");
			readToken();
			if (isComando()) {
				if (this.Lockahead.getClasifica().equals(Clasifc.WHILE.get())) {
					readToken();
					if (this.Lockahead.getClasifica().equals(Clasifc.Abre_Paren.get())) {
						readToken();
						token1 = isExprRelacional();
						if (!token1.getLexama().equals("")) {
							if (this.Lockahead.getClasifica().equals(Clasifc.Fecha_Paren.get())) {
								System.out.println("IF " + token1.getLexama() + " == 0 GOTO " + label);
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
	public Token isExprRelacional() throws IOException {
		boolean valid = false;
		Token token1 = new Token("", "");
		Token token2 = new Token("", "");
		Token token_Result = new Token("", "");
		String tipo1 = "";
		String tipo2 = "";
		String op = "";
		String tipo_result = "";
		String aux1 = "";
		String aux2 = "";
		String aux3 = "";

		token1 = isExprAritmetica();
		tipo1 = token1.getTipo();
		aux1 = token1.getLexama();
		if (!tipo1.equals("")) {
			op = isOPRelacional();
			if (!op.equals("")) {
				readToken();
				token2 = isExprAritmetica();
				tipo2 = token2.getTipo();
				aux2 = token2.getLexama();
				if (!tipo2.equals("")) {
					valid = true;
				} else {
					disparaErro(erroRelacionalExpress�o);
				}
			} else {
				disparaErro(erroOperadoRelacional);
			}
		}

		tipo_result = comparaTipo(tipo1, op, tipo2);

		if (tipo_result.equals("")) {
			valid = false;
		} else {
			aux3 = createTemp();
			token_Result.setLexama(aux3);
			token_Result.setTipo(tipo1);
			System.out.println(aux3 + " = " + aux1 + op + aux2);
		}

		return token_Result;

	}

	// *-----> Express�o Aritmetica
	public Token isExprAritmetica() throws IOException {
		Token token1 = new Token("", "");
		Token token2 = new Token("", "");
		Token token_result = new Token("", "");
		String tipo1 = "";
		String tipo2 = "";
		String aux1 = "";
		String aux2 = "";
		String aux3 = "";
		String tipo_aux = "";
		String op = "";
		String tipo_result = "";
		boolean soma_dimi = false;

		token1 = isTermo();
		tipo1 = token1.getTipo();
		aux1 = token1.getLexama();
		if (!tipo1.equals("")) {
			// valid = true;
			do {

				tipo_aux = "";

				if (this.Lockahead.getClasifica().equals(Clasifc.Soma.get())
						|| this.Lockahead.getClasifica().equals(Clasifc.Menos.get())) {
					op = this.Lockahead.getClasifica();
					readToken();
					token2 = isTermo();
					tipo_aux = token2.getTipo();
					if (tipo_aux.equals("")) {
						disparaErro(erroAritTermo);
					}
				}

				if (!tipo_aux.equals("")) {
					tipo2 = tipo_aux;
					aux2 = token2.getLexama();
					tipo_result = comparaTipo(tipo1, op, tipo2);

					if (tipo_result.equals(Clasifc.Float.get())) {
						if (!tipo1.equals(tipo_result) && !tipo1.equals(tipo2)) {
							aux3 = createTemp();
							tipo1 = tipo_result;
							System.out.println(aux3 + " = " + "FLOAT" + " (" + aux1 + ")");
							aux1 = aux3;
						}

						if (!tipo2.equals(tipo_result) && !tipo2.equals(tipo2)) {
							aux3 = createTemp();
							tipo2 = tipo_result;
							System.out.println(aux3 + " = " + "FLOAT" + " (" + aux2 + ")");
							aux2 = aux3;
						}
						tipo1 = tipo_result;
					}
					aux3 = createTemp();
					System.out.println(aux3 + " = " + aux1 + op + aux2);
					aux1 = aux3;
					soma_dimi = true;
				} else {
					soma_dimi = false;
				}
			} while (soma_dimi);
		}

		if (tipo_result.equals("")) {
			// tipo_result = comparaTipo(tipo1, op, tipo2);

		}

		token_result.setLexama(aux1);
		token_result.setTipo(tipo1);
		return token_result;

	}

	// *-----> Termo
	public Token isTermo() throws IOException {
		Token token1 = new Token("", "");
		Token token2 = new Token("", "");
		Token token_result = new Token("", "");
		String tipo1 = "";
		String tipo2 = "";
		String aux1 = "";
		String aux2 = "";
		String aux3 = "";
		String tipo_aux = "";
		String op = "";
		String tipo_result = "";
		boolean mult_div = false;

		token1 = isFator();
		tipo1 = token1.getTipo();
		aux1 = token1.getLexama();
		if (!tipo1.equals("")) {
			do {
				tipo_aux = "";
				if (this.Lockahead.getClasifica().equals(Clasifc.Mult.get())
						|| this.Lockahead.getClasifica().equals(Clasifc.Div.get())) {
					op = this.Lockahead.getClasifica();
					readToken();
					token2 = isFator();
					tipo_aux = token2.getTipo();
					if (tipo_aux.equals("")) {
						disparaErro(erroAritFator);
					}
				}

				if (!tipo_aux.equals("")) {
					tipo2 = tipo_aux;
					aux2 = token2.getLexama();
					tipo_result = comparaTipo(tipo1, op, tipo2);

					if (tipo_result.equals(Clasifc.Float.get())) {
						if (!tipo1.equals(tipo_result) && !tipo1.equals(tipo2)) {
							aux3 = createTemp();
							tipo1 = tipo_result;
							System.out.println(aux3 + " = " + "FLOAT" + " (" + aux1 + ")");
							aux1 = aux3;
						} else if (!tipo2.equals(tipo_result) && !tipo2.equals(tipo1)) {
							aux3 = createTemp();
							tipo2 = tipo_result;
							System.out.println(aux3 + " = " + "FLOAT" + " (" + aux2 + ")");
							aux2 = aux3;
						}
						tipo1 = tipo_result;
					}
					aux3 = createTemp();
					System.out.println(aux3 + " = " + aux1 + op + aux2);
					aux1 = aux3;

					mult_div = true;
				} else {
					mult_div = false;
				}

			} while (mult_div);
		}

		if (tipo_result.equals("")) {
			// tipo_result = comparaTipo(tipo1, op, tipo2);

		}

		token_result.setLexama(aux1);
		token_result.setTipo(tipo1);
		return token_result;

	}

	// *-----> Fator
	public Token isFator() throws IOException {
		Token token = new Token("", "");

		if (this.Lockahead.getClasifica().equals(Clasifc.ID.get())) {
			token = this.tabela_simbolo.JaDeclarado(this.Lockahead.getLexama(), 0);
			if (token.getTipo().equals("")) {
				disparaErroVariavel(this.Lockahead.getLexama());
			} else {
				readToken();
			}
		}

		else if (this.Lockahead.getClasifica().equals(Clasifc.Inteiro.get())
				|| this.Lockahead.getClasifica().equals(Clasifc.Float.get())
				|| this.Lockahead.getClasifica().equals(Clasifc.Char.get())) {

			token = this.Lockahead;
			token.setTipo(this.Lockahead.getClasifica());
			readToken();
		}

		else if (this.Lockahead.getClasifica().equals(Clasifc.Abre_Paren.get())) {
			readToken();
			token = isExprAritmetica();
			if (!token.getTipo().equals("")) {
				if (this.Lockahead.getClasifica().equals(Clasifc.Fecha_Paren.get())) {
					readToken();
				} else {
					disparaErro(erroFechaParent);
				}
			} else {
				disparaErro(erroFatorExpress�o);
			}
		}

		return token;

	}

	// *-----> Opera��o Relacional
	public String isOPRelacional() throws IOException {
		String op = "";

		if (this.Lockahead.getClasifica().equals(Clasifc.Menor_Que.get())
				|| this.Lockahead.getClasifica().equals(Clasifc.Maior_Que.get())
				|| this.Lockahead.getClasifica().equals(Clasifc.Igual_Menor_Que.get())
				|| this.Lockahead.getClasifica().equals(Clasifc.Igual_Maior_Que.get())
				|| this.Lockahead.getClasifica().equals(Clasifc.Igual_Igual.get())
				|| this.Lockahead.getClasifica().equals(Clasifc.Igual_Diferente.get())) {
			op = this.Lockahead.getClasifica();
		}

		return op;

	}

	// Validar se variav�is s�o compativeis e faz convers�o de tipo
	public String comparaTipo(String tipo1, String op, String tipo2) {
		String tipo_result = "";

		if (!op.equals("")) {
			if ((tipo1.equals(Clasifc.Char.get()) && !tipo2.equals(Clasifc.Char.get()))
					|| (!tipo1.equals(Clasifc.Char.get()) && tipo2.equals(Clasifc.Char.get()))) {

				// ERRO SEMANTICO CHAR
				disparaErro(erroTipoChar);
			}

		}

		if (tipo1.equals(Clasifc.Inteiro.get()) && op.equals(Clasifc.Igual.get())
				&& tipo2.equals(Clasifc.Float.get())) {
			// ERRO SEMANTICO INT N�O PODE RECEBER FLOAT
			disparaErro(erroIntIgualFloat);
		}
		// A partir daqui a compatibilidade est� correta
		// Basta fazer a Convers�o de tipo

		if (!(tipo1.equals(Clasifc.Char.get()) || tipo2.equals(Clasifc.Char.get()))) {

			if (op.equals(Clasifc.Div.get())) {
				tipo_result = Clasifc.Float.get();
			}

			else if (op.equals(Clasifc.Mult.get()) || op.equals(Clasifc.Soma.get()) || op.equals(Clasifc.Menos.get())) {
				if (tipo1.equals(Clasifc.Float.get()) || tipo2.equals(Clasifc.Float.get())) {
					tipo_result = Clasifc.Float.get();
				} else {
					tipo_result = Clasifc.Inteiro.get();
				}

			} else {
				tipo_result = tipo1;
			}
		} else {
			tipo_result = Clasifc.Char.get();

		}

		return tipo_result;

	}

	// *Tipo
	public String setarTipo(String palavra_reservada) {
		String tipo = "";

		if (this.Lockahead.getClasifica().equals(Clasifc.INT.get())) {
			tipo = Clasifc.Inteiro.get();
		} else if (this.Lockahead.getClasifica().equals(Clasifc.FLOAT.get())) {
			tipo = Clasifc.Float.get();
		} else if (this.Lockahead.getClasifica().equals(Clasifc.CHAR.get())) {
			tipo = Clasifc.Char.get();
		}

		return tipo;
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
	
	// *-----> Dispara Error Vari�vel n�o declarada
	public void disparaErroVariavel(String Lexama) {

		int Linha = this.linha_lida;
		int Coluna = this.coluna_lida;
		String lexama = this.LastToken.getLexama();
		System.out.println("ERRO na Linha: " + Linha + ", " + "Coluna: " + Coluna + ", " + "Ultimo token lido: "
				+ lexama + " Mensagem: " + "Vari�vel '" + Lexama + "' n�o foi declarada");
		System.exit(-1);
	}

}
