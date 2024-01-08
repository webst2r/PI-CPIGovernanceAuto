public class Main {

    public static void main(String[] args) {
        JenkinsPipeline jenkinsPipeline = new JenkinsPipeline("11fa0ad06989c56774548a0aaaac927069","admin");
        jenkinsPipeline.create("Hello", "file.xml");
        jenkinsPipeline.execute("Hello");
    }
}
