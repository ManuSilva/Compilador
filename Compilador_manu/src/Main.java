import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Args Vazio");
            System.exit(-1);
            
        }else {
        	System.out.println("Abrindo Arquivo...");
        	Arquivo o_arq = new Arquivo(args[0]);
        	System.out.println("Linha:" + o_arq.getlinha() + " Coluna:" + o_arq.getColuna() + " Caracter:" + o_arq.readCaracter());
        	System.out.println("Linha:" + o_arq.getlinha() + " Coluna:" + o_arq.getColuna() + " Caracter:" + o_arq.readCaracter());
        	System.out.println("Linha:" + o_arq.getlinha() + " Coluna:" + o_arq.getColuna() + " Caracter:" + o_arq.readCaracter());
        	System.out.println("Linha:" + o_arq.getlinha() + " Coluna:" + o_arq.getColuna() + " Caracter:" + o_arq.readCaracter());
        	System.out.println("Linha:" + o_arq.getlinha() + " Coluna:" + o_arq.getColuna() + " Caracter:" + o_arq.readCaracter());
        	System.out.println("Linha:" + o_arq.getlinha() + " Coluna:" + o_arq.getColuna() + " Caracter:" + o_arq.readCaracter());
        	System.out.println("Linha:" + o_arq.getlinha() + " Coluna:" + o_arq.getColuna() + " Caracter:" + o_arq.readCaracter());
        	System.out.println("Linha:" + o_arq.getlinha() + " Coluna:" + o_arq.getColuna() + " Caracter:" + o_arq.readCaracter());
        	
        	System.out.println("Testando GitHub");
        	
        	o_arq.fecharArquivo();
        }
        
	}
}
