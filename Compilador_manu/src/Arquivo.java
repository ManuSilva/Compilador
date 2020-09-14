
import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;
import java.io.BufferedReader;
import java.io.FileReader;

public class Arquivo {
	private String path;
	private BufferedReader br;
	private FileReader fr;
	private int linha;
	private int coluna;
	private String linha_atual;

	// *-----> Construtor
	public Arquivo(String path) {
		this.path = path;
		this.linha = 1;
		this.coluna = 1;
		File arquivo = new File(this.path);
		if (arquivo.exists() == true) { // Se arquivo existe
			try {
				FileReader fr = new FileReader(arquivo);
				BufferedReader br = new BufferedReader(fr);
				//br.mark(0);
				this.linha_atual = br.readLine();
				this.fr = fr;
				this.br = br;
			} catch (IOException ioExeception) {

			}
		}
	}

	// *-----> Set Path
	public void setPath(String path) {
		this.path = path;
	}
	
	// *-----> Get Linha e coluna
	public int getlinha() {
		return this.linha;
	}
	
	public int getColuna() {
		return this.coluna;
	}

	// *-----> Ler arquivo
	public char readCaracter() throws IOException {
		long lineNo = this.br.lines().count();
		attLinhaColuna(this.br.readLine());
		char caracter = (char) this.br.read();
		
		return caracter;
	}

	// *-----> Fechar Arquivo
	public void fecharArquivo() throws IOException {
		br.close();
		fr.close();
		System.out.println("Arquivo Fechado!");
	}
	
	// *-----> Calcular linha e coluna
	public void attLinhaColuna(String TextoLinha) {
		if(TextoLinha.equals(this.linha_atual)) {
			this.coluna ++;
		}else {
			this.linha_atual = TextoLinha;
			this.linha ++;
			this.coluna = 1;
		}
	}
}
