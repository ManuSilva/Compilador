import java.io.IOException;
import java.util.Stack;

public class Parser {

	private Scanner scanner;
	private Token Lockahead;
	private Token LastToken;
	private int linha_lida;
	private int coluna_lida;
	private int bloco = 0;
	private Stack<Simbolo> tabela_simbolo;
	private String LastTipo;

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
	static String erroVariavelNaoDeclarada = "Vari�vel n�o foi declarada";

	// *-----------------------------------------------------*
	// *-----> Construtor
	public Parser(Arquivo arq) throws IOException {
		Token token = new Token("", "");
		Stack<Simbolo> tabela = new Stack<Simbolo>();
		Scanner scanner = new Scanner(arq);
		this.scanner = scanner;
		this.LastToken = token;
		this.Lockahead = scanner.verificLexico();
		this.tabela_simbolo = tabela;
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
				desempilhaBloco(this.bloco);
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
				MontaTabelaSimbolo();
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
				MontaTabelaSimbolo();
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
		String tipo1 = "";
		String tipo2 = "";
		String op = "";
		String tipo_result = "";

		if (this.Lockahead.getClasifica().equals(Clasifc.ID.get())) {
			tipo1 = JaDeclarado(this.Lockahead.getLexama(), 0);
			if (!tipo1.equals("")) {
				readToken();
				if (this.Lockahead.getClasifica().equals(Clasifc.Igual.get())) {
					op = Clasifc.Igual.get();
					readToken();
					tipo2 = isExprAritmetica();
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
				disparaErro(erroVariavelNaoDeclarada);
			}
		}

		tipo_result = comparaTipo(tipo1, op, tipo2);

		if (tipo_result.equals("")) {
			valid = false;
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
		String tipo1 = "";
		String tipo2 = "";
		String op = "";
		String tipo_result = "";

		tipo1 = isExprAritmetica();
		if (!tipo1.equals("")) {
			op = isOPRelacional();
			if (!op.equals("")) {
				readToken();
				tipo2 = isExprAritmetica();
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
		}

		return valid;

	}

	// *-----> Express�o Aritmetica
	public String isExprAritmetica() throws IOException {
		String tipo1 = "";
		String tipo2 = "";
		String op = "";
		String tipo_result = "";
		String[] result;
		boolean soma_dimi = false;

		tipo1 = isTermo();
		if (!tipo1.equals("")) {
			// valid = true;
			do {
				result = SomaDimin();
				tipo2 = result[0];
				op = result[1];

				if (!tipo2.equals("")) {
					tipo1 = comparaTipo(tipo1, op, tipo2);
					soma_dimi = true;
				} else {
					soma_dimi = false;
				}
			} while (soma_dimi);
		}

		tipo_result = comparaTipo(tipo1, op, tipo2);

		return tipo_result;

	}

	// *-----> Termo
	public String isTermo() throws IOException {
		String tipo1 = "";
		String tipo2 = "";
		String op = "";
		String tipo_result = "";
		String[] result;
		boolean mult_div = false;

		tipo1 = isFator();
		if (!tipo1.equals("")) {
			do {
				result = multDiv();
				tipo2 = result[0];
				op = result[1];

				if (!tipo2.equals("")) {
					tipo1 = comparaTipo(tipo1, op, tipo2);
					mult_div = true;
				} else {
					mult_div = false;
				}

			} while (mult_div);
		}

		tipo_result = comparaTipo(tipo1, op, tipo2);

		return tipo_result;

	}

	// *-----> Fator
	public String isFator() throws IOException {
		String tipo = "";

		if (this.Lockahead.getClasifica().equals(Clasifc.ID.get())) {
			tipo = JaDeclarado(this.Lockahead.getLexama(), 0);
			if (tipo.equals("")) {
				disparaErro(erroVariavelNaoDeclarada);
			} else {
				readToken();
			}
		}

		else if (this.Lockahead.getClasifica().equals(Clasifc.Inteiro.get())
				|| this.Lockahead.getClasifica().equals(Clasifc.Float.get())
				|| this.Lockahead.getClasifica().equals(Clasifc.Char.get())) {

			tipo = this.Lockahead.getClasifica();
			readToken();
		}

		else if (this.Lockahead.getClasifica().equals(Clasifc.Abre_Paren.get())) {
			readToken();
			tipo = isExprAritmetica();
			if (!tipo.equals("")) {
				if (this.Lockahead.getClasifica().equals(Clasifc.Fecha_Paren.get())) {
					readToken();
				} else {
					disparaErro(erroFechaParent);
				}
			} else {
				disparaErro(erroFatorExpress�o);
			}
		}

		return tipo;

	}

	// *-----> Multiplica��o e Divis�o
	public String[] multDiv() throws IOException {
		String tipo = "";
		String op = "";

		if (this.Lockahead.getClasifica().equals(Clasifc.Mult.get())
				|| this.Lockahead.getClasifica().equals(Clasifc.Div.get())) {
			op = this.Lockahead.getClasifica();
			readToken();
			tipo = isFator();
			if (tipo.equals("")) {
				disparaErro(erroAritFator);
			}
		}

		return new String[] { tipo, op };

	}

	// *-----> Soma e Subtra��o
	public String[] SomaDimin() throws IOException {
		String tipo = "";
		String op = "";

		if (this.Lockahead.getClasifica().equals(Clasifc.Soma.get())
				|| this.Lockahead.getClasifica().equals(Clasifc.Menos.get())) {
			op = this.Lockahead.getClasifica();
			readToken();
			tipo = isTermo();
			if (tipo.equals("")) {
				disparaErro(erroAritTermo);
			}
		}

		return new String[] { tipo, op };

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

	public void MontaTabelaSimbolo() {
		String tipo = "";
		Simbolo sb = new Simbolo("", this.LastTipo, this.bloco);

		// Validar se a vari�vel j� foi declarada
		tipo = JaDeclarado(this.Lockahead.getLexama(), this.bloco);
		if (tipo.equals("")) {
			sb.setLexama(this.Lockahead.getLexama()); // Guardar Valor na tabela de s�mbolos para o semantico
			this.tabela_simbolo.push(sb);
		} else {
			// Erro Vari�vel j� declarada
			disparaErro(erroVariav�lJaDeclarada);
		}

	}

	// *-----> desempilha variaveis da tabela de simbolos quando sai do bloco
	public void desempilhaBloco(int Bloco) {
		boolean notBloco = false;
		Simbolo sb = new Simbolo("", "", 0);
		do {
			if (!this.tabela_simbolo.isEmpty()) {
				sb = this.tabela_simbolo.pop();
				if (sb.getBloco() != bloco) {
					this.tabela_simbolo.push(sb);
					notBloco = true;
				}
			} else {
				notBloco = true;
			}

		} while (!notBloco);
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

	// Valida se a vari�vel foi declarada
	public String JaDeclarado(String lexama, int bloco) {
		String tipo = "";
		boolean exit = false;
		Simbolo sb = new Simbolo("", "", 0);
		Stack<Simbolo> tabela_aux = new Stack<Simbolo>();

		// Fazer busca considerado o bloco para pesquisar se a variavel j� existe no
		// mesmo bloco
		if (bloco != 0) {
			// Desempilhar a pilha ate encontrar a variavel declarada
			do {
				if (!this.tabela_simbolo.isEmpty()) {
					sb = this.tabela_simbolo.pop();
					tabela_aux.push(sb);
					if (sb.getLexama().equals(lexama) && sb.getBloco() == bloco) {
						exit = true;
						tipo = sb.getTipo();
					}
				} else {
					exit = true;
				}

			} while (!exit);
		}

		// Fazer a busca em toda tabela de simbolo
		else {
			// Desempilhar a pilha ate encontrar a variavel declarada
			do {
				if (!this.tabela_simbolo.isEmpty()) {
					sb = this.tabela_simbolo.pop();
					tabela_aux.push(sb);
					if (sb.getLexama().equals(lexama)) {
						exit = true;
						tipo = sb.getTipo();
					}
				} else {
					exit = true;
				}

			} while (!exit);
		}

		// Empilhar a pilha novamente
		exit = false;

		do {
			if (!tabela_aux.isEmpty()) {

				sb = tabela_aux.pop();
				this.tabela_simbolo.push(sb);

			} else {
				exit = true;
			}

		} while (!exit);

		return tipo;

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
}
