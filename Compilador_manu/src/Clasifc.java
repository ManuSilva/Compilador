
public enum Clasifc {
	
	//*------------Tipos de Clasificação-------------------*
	ID("ID"),
		
	Menor_Que("Operador Relacional Menor que"),
	Maior_Que("Operador Relacional Maior que"),
	Igual_Menor_Que("Operador Relacional Igual Menor que"),
	Igual_Maior_Que("Operador Relacional Igual Maior que"),
	Igual_Igual("Operador Relacional Igual"),
	Igual_Diferente("Operador Relacional Diferente"),
	
	Soma("Operador Arítimético Soma"),
	Menos("Operador Arítimético Subtração"),
	Mult("Operador Arítimético Multiplicação"),
	Div("Operador Arítimético Divisão"),
	Igual("Operador Arítimético Igual"),
	
	Fecha_Paren("Caracter Especial Fecha Parênteses"),
	Abre_Paren("Caracter Especial Abre Parênteses"),
	Fecha_Chaves("Caracter Especial Fecha Chaves"),
	Abre_Chaves("Caracter Especial Abre Chaves"),
	Virgula("Caracter Especial Vírgula"),
	Ponto_Virgula("Caracter Especial Ponto e Vírgula"),
	
	MAIN("Palavra Reservada MAIN"),
	IF("Palavra Reservada IF"),
	ELSE("Palavra Reservada ELSE"),
	WHILE("Palavra Reservada WHILE"),
	DO("Palavra Reservada DO"),
	FOR("Palavra Reservada FOR"),
	INT("Palavra Reservada INT"),
	FLOAT("Palavra Reservada FLOAT"),
	CHAR("Palavra Reservada CHAR"),
	
	Inteiro("Inteiro"),
	Float("Float"),
	Char("Char");
	
	//*-----------------------------------------------------*
	  private String descricao;


	  Clasifc(String descricao) {
		  this.descricao = descricao;
	 }


	public String get() {
	     return descricao;
	  }
		



}
