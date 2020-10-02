
import java.io.File;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

public class Arquivo {
	private String path;
	private BufferedReader br;
	private FileReader fr;
	private int linha;
	private int coluna;
	private int linha_marc;
	private int coluna_marc;

	// *-----> Construtor
	public Arquivo(String path) {
		this.path = path;
		this.linha = 1;
		this.coluna = 0;
		File arquivo = new File(this.path);
		if (arquivo.exists() == true) { // Se arquivo existe
			try {
				FileReader fr = new FileReader(arquivo);
				BufferedReader br = new BufferedReader(fr);
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

	// *-----> Get
	public int getlinha() {
		return this.linha;
	}

	public int getColuna() {
		return this.coluna;
	}
	

	public BufferedReader getBR() {
		return this.br;
	}

	// *-----> Ler arquivo
	public String readCaracter() throws IOException {

		String Carc_String = "";
		int carac_int = this.br.read();
		char caracter = (char) carac_int;

		if (carac_int == -1) { // Fim de arquivo
			Carc_String = "EOF";
		} else if (caracter == '\r') {                   //Fim de linha
			this.linha++;
			this.coluna = 0;
			Carc_String = Character.toString(caracter); 
		} else if (caracter == '\n'){                   //Pula linha
			this.coluna = 0;
			Carc_String = Character.toString(caracter);  
		} else if (caracter == '\t'){                   // Tab
			this.coluna = coluna + 4;
			Carc_String = Character.toString(caracter);
		}else {
			this.coluna++;
			Carc_String = Character.toString(caracter);
		}

		return Carc_String;
	}

	// *-----> Fechar Arquivo
	public void fecharArquivo() throws IOException {
		br.close();
		fr.close();
		System.out.println("Arquivo Fechado!");
	}

	// *----> Marcar o local no arquivo para voltar
	public void marcar() throws IOException {
		this.linha_marc = this.linha;
		this.coluna_marc = this.coluna;
		this.br.mark(0);
	}

	// *----> Voltar para onde foi marcado
	public void reset() throws IOException {
		this.linha = this.linha_marc;
		this.coluna = this.coluna_marc;
		this.br.reset();
	}
}
