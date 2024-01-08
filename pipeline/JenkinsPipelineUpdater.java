import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;

public class JenkinsPipelineUpdater {

    public static void updateAndWriteToFile(String patternString,String filePath, String placeString) {
        try {
            // Lê o conteúdo do arquivo
            Path path = Paths.get(filePath);
            String originalPipeline = new String(Files.readAllBytes(path));

            // Define um padrão regex para encontrar a seção específica
            Pattern pattern = Pattern.compile(patternString);
            Matcher matcher = pattern.matcher(originalPipeline);

            // Substitui os caminhos originais pelos novos caminhos
            String updatedPipeline = matcher.replaceFirst(placeString);

            // Escreve o conteúdo atualizado de volta no arquivo
            Files.write(path, updatedPipeline.getBytes(), StandardOpenOption.WRITE);

            System.out.println("Pipeline atualizado e gravado com sucesso no arquivo: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erro ao processar o arquivo do pipeline.");
        }
    }

    public static void main(String[] args) {
        // Exemplo de uso da função
        String filePath = "file.xml";
        String patternString_cpi = "sh \"cpilint -rules (.*?) -files (.*?)\"";
        String placeString_cpi = "sh \"cpilint -rules novo/caminho -files caminho\"";
	//CPI
        updateAndWriteToFile(patternString_cpi,filePath, placeString_cpi);
        //codenarc
        
        String patternString_codenarc1 = "sh 'unzip /files/firstflow.zip -d /files/(.*?)'";
        String placeString_codenarc1= "sh 'unzip /files/firstflow.zip -d /files/novo'";
        
        updateAndWriteToFile(patternString_codenarc1,filePath, placeString_codenarc1);
        
        String patternString_codenarc2 = "sh 'java -cp /cp/codenarc.jar org.codenarc.CodeNarc -rulesetfiles=file:/files/(.*?) -basedir=/files/unzip_flow/(.*?)'";
        String placeString_codenarc2 = "sh 'java -cp /cp/codenarc.jar org.codenarc.CodeNarc -rulesetfiles=file:/files/novo -basedir=/files/unzip_flow/novo'";
        
        updateAndWriteToFile(patternString_codenarc2,filePath, placeString_codenarc2);
        
        
    }
}
