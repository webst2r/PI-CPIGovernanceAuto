package org.project.backend;

import org.project.backend.jenkins.CodenarcReportReaderDeserializer;
import org.project.backend.jenkins.DependencyCheckReportReaderDeserializer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication implements CommandLineRunner {
	private final CodenarcReportReaderDeserializer codenarcReportReaderDeserializer;
	private final DependencyCheckReportReaderDeserializer dependencyCheckReportReaderDeserializer;

	public BackendApplication(CodenarcReportReaderDeserializer codenarcReportReaderDeserializer, DependencyCheckReportReaderDeserializer dependencyCheckReportReaderDeserializer) {
		this.codenarcReportReaderDeserializer = codenarcReportReaderDeserializer;
		this.dependencyCheckReportReaderDeserializer = dependencyCheckReportReaderDeserializer;
	}

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//codeNarcReportReaderDeserializer.deserialize();
		//dependencyCheckReportReaderDeserializer.deserialize();
	}

}
