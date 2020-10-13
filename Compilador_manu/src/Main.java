import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Args Vazio");
            System.exit(-1);
            
        }else {
        	System.out.println("Abrindo Arquivo...");
        	Arquivo o_arq = new Arquivo(args[0]);
        	
        	//Scanner scanner = new Scanner(o_arq);
        	//scanner.verificLexico();
        	Parser parser = new Parser(o_arq);
        	parser.verificSintatico();

        }
        
	}
}
