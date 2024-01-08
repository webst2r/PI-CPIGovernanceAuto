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
        String patternString = "sh \"cpilint -rules (.*?) -files (.*?)\"";
        String placeString = "sh \"cpilint -rules novo/caminho -files caminho\"";

        updateAndWriteToFile(patternString,filePath, placeString);
    }
}
