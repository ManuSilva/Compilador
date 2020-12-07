import java.util.Stack;

public class TabSimbolo {

	private Stack<Token> tabela_simbolo;

	// *-----> Construtor
	public TabSimbolo() {
		Stack<Token> tabela = new Stack<Token>();
		this.tabela_simbolo = tabela;
	}

	public boolean MontaTabelaSimbolo(String LastTipo, int bloco, Token token) {
		String tipo = "";
		boolean erro = false;
		Token t = new Token("", "");
		//Simbolo sb = new Simbolo("", this.LastTipo, this.bloco);

		// Validar se a variável já foi declarada
		t = JaDeclarado(token.getLexama(), bloco);
		tipo = t.getTipo();
		if (tipo.equals("")) {
			token.setTipo(LastTipo);
			token.setBloco(bloco);
			// Guardar Valor na tabela de símbolos para o semantico
			this.tabela_simbolo.push(token);
		} else {
			// Erro Variável já declarada
			erro = true;
		}
		return erro;
	}

	// *-----> desempilha variaveis da tabela de simbolos quando sai do bloco
	public void desempilhaBloco(int Bloco) {
		boolean notBloco = false;
		Token token = new Token("", "");
		do {
			if (!this.tabela_simbolo.isEmpty()) {
				token = this.tabela_simbolo.pop();
				if (token.getBloco() != Bloco) {
					this.tabela_simbolo.push(token);
					notBloco = true;
				}
			} else {
				notBloco = true;
			}

		} while (!notBloco);
	}

	// Valida se a variável foi declarada
	public Token JaDeclarado(String lexama, int bloco) {
		boolean exit = false;
		Token token = new Token("", "");
		Token token_result = new Token("", "");
		Stack<Token> tabela_aux = new Stack<Token>();

		// Fazer busca considerado o bloco para pesquisar se a variavel já existe no
		// mesmo bloco
		if (bloco != 0) {
			// Desempilhar a pilha ate encontrar a variavel declarada
			do {
				if (!this.tabela_simbolo.isEmpty()) {
					token = this.tabela_simbolo.pop();
					tabela_aux.push(token);
					if (token.getLexama().equals(lexama) && token.getBloco() == bloco) {
						exit = true;
						token_result = token;
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
					token = this.tabela_simbolo.pop();
					tabela_aux.push(token);
					if (token.getLexama().equals(lexama)) {
						exit = true;
						token_result = token;
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

				token = tabela_aux.pop();
				this.tabela_simbolo.push(token);

			} else {
				exit = true;
			}

		} while (!exit);

		return token_result;

	}

}
