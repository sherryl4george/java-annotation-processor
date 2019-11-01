import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.testng.annotations.Test;
import processor.AnnotationProcessor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;


public class ConcreteAggregateTest {
    private static Config config = ConfigFactory.load("config.conf");
    @Test
    public void testAggregateConcreteIteratorFactoryMissing(){
        Compilation compilation =
                javac()
                        .withProcessors(new AnnotationProcessor())
                        .compile(JavaFileObjects.forSourceString("Aggregate", FileHelper.getQueryFromFile(config.getString("files.aggregate"), false)),
                                JavaFileObjects.forSourceString("Iterator", FileHelper.getQueryFromFile(config.getString("files.iterator"), false)),
                                JavaFileObjects.forSourceString("ConcreteAggregate", FileHelper.getQueryFromFile(config.getString("files.ConcreteAggregate"), true, "@IteratorPattern.ConcreteAggregate.IteratorFactory(returnType = Iterator1.class)","")));
        assertThat(compilation).failed();
        assertThat(compilation).hadErrorContaining("atleast one member annotated with interface annotations.IteratorPattern$ConcreteAggregate$IteratorFactory");
    }

    @Test
    public void testAggregateOverrideMissing(){
        Compilation compilation =
                javac()
                        .withProcessors(new AnnotationProcessor())
                        .compile(JavaFileObjects.forSourceString("Aggregate", FileHelper.getQueryFromFile(config.getString("files.aggregate"), false)),
                                JavaFileObjects.forSourceString("Iterator", FileHelper.getQueryFromFile(config.getString("files.iterator"), false)),
                                JavaFileObjects.forSourceString("ConcreteAggregate", FileHelper.getQueryFromFile(config.getString("files.ConcreteAggregate"), true, "@Override","")));
        assertThat(compilation).failed();
        assertThat(compilation).hadErrorContaining("Method should be annotated with @Override");
    }
}
