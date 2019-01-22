package ee.esport.spring2019.db;

import lombok.extern.apachecommons.CommonsLog;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.jooq.util.GenerationTool;
import org.jooq.util.jaxb.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;
import java.sql.Connection;

@CommonsLog
@SpringBootApplication
public class DatabaseSetupApplication implements CommandLineRunner {

    @Resource
    private DataSource dataSource;

	public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(DatabaseSetupApplication.class);
        app.setWebEnvironment(false);
        app.run(args);
	}

    @Override
    public void run(String... args) throws Exception {
        generateJooqClasses();
    }

    private void generateJooqClasses() throws Exception {
        GenerationTool generationTool = new GenerationTool();
        Connection connection = dataSource.getConnection();
        generationTool.setConnection(connection);
        generationTool.run(createJooqGeneratorConfig(connection.getCatalog()));
    }

    private static Configuration createJooqGeneratorConfig(String schema) {
        Database databaseConfig = new Database().withName("org.jooq.util.mariadb.MariaDBDatabase")
                                                .withExcludes("schema_version")
                                                .withInputSchema(schema);
        Target targetConfig = new Target().withPackageName("ee.esport.spring2019.jooq")
                                          .withDirectory("src/generated/java");
        Strategy strategyConfig = new Strategy().withName(EsportGenerationStrategy.class.getCanonicalName());
        return new Configuration().withGenerator(new Generator().withDatabase(databaseConfig)
                                                                .withTarget(targetConfig)
                                                                .withStrategy(strategyConfig));
    }


}
