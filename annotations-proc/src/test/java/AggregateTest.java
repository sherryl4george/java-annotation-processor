import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.testng.annotations.Test;
import processor.AnnotationProcessor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;


public class AggregateTest {
    private static Config config = ConfigFactory.load("config.conf");
    @Test
    public void testAggregateIteratorFactoryMissing(){
        Compilation compilation =
                javac()
                        .withProcessors(new AnnotationProcessor())
                        .compile(JavaFileObjects.forSourceString("Aggregate", FileHelper.getQueryFromFile(config.getString("files.aggregate"), true, "@IteratorPattern.Aggregate.IteratorFactory", "")),
                                JavaFileObjects.forSourceString("Iterator", FileHelper.getQueryFromFile(config.getString("files.iterator"), false)),
                                JavaFileObjects.forSourceString("ConcreteAggregate", FileHelper.getQueryFromFile(config.getString("files.ConcreteAggregate"), false)));
        assertThat(compilation).failed();
        assertThat(compilation).hadErrorContaining("annotated with interface annotations.IteratorPattern$Aggregate$IteratorFactory");
    }
}
