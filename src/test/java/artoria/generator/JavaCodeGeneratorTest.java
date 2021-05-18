package artoria.generator;

import artoria.jdbc.DatabaseClient;
import artoria.jdbc.SimpleDataSource;
import artoria.engine.template.VelocityTemplateEngine;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class JavaCodeGeneratorTest {
    private static DatabaseClient databaseClient = new DatabaseClient(new SimpleDataSource());

    @Test
    public void test1() {
        JavaCodeGenerator generator = new JavaCodeGenerator().newCreator()
                .setDatabaseClient(databaseClient)
//                .setBaseTemplatePath("classpath:templates/generator/java/custom")
                .setBaseOutputPath("src\\test\\java")
                .setBasePackageName("artoria.generator.out")
                .setTemplateEngine(new VelocityTemplateEngine())
                .addRemovedTableNamePrefixes("t_")
//                .addExcludedTables("t_15_user")
//                .addReservedTables("t_user")
                ;
        generator.addAttribute("author", "Kahle");
        generator.generate();
    }

}
