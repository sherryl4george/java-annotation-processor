import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.testng.annotations.Test;
import processor.AnnotationProcessor;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;


public class IteratorTest {
    private static Config config = ConfigFactory.load("config.conf");
    @Test
    public void testIteratorHappyCase(){
        Compilation compilation =
                javac()
                        .withProcessors(new AnnotationProcessor())
                        .compile(JavaFileObjects.forSourceString("Iterator", FileHelper.getQueryFromFile(config.getString("files.iterator"),false)),
                                JavaFileObjects.forSourceString("Aggregate", FileHelper.getQueryFromFile(config.getString("files.aggregate"), false)),
                                JavaFileObjects.forSourceString("ConcreteAggregate", FileHelper.getQueryFromFile(config.getString("files.ConcreteAggregate"), false)));
        assertThat(compilation).succeeded();
    }

    @Test
    public void testIteratorIteratorMissingParameterType(){
        Compilation compilation =
                javac()
                        .withProcessors(new AnnotationProcessor())
                        .compile(JavaFileObjects.forSourceString("Iterator", FileHelper.getQueryFromFile(config.getString("files.iterator"), true, "@IteratorPattern.Iterator(typeVariableReturnIndex = 0)", "@IteratorPattern.Iterator")),
                                JavaFileObjects.forSourceString("Aggregate", FileHelper.getQueryFromFile(config.getString("files.aggregate"), false)),
                                JavaFileObjects.forSourceString("ConcreteAggregate", FileHelper.getQueryFromFile(config.getString("files.ConcreteAggregate"), false)));
        assertThat(compilation).failed();
        assertThat(compilation).hadErrorContaining("Either returnType or typeVariableReturnIndex");
    }

    @Test
    public void testIteratorIteratorMissingHasNext(){
        Compilation compilation =
                javac()
                        .withProcessors(new AnnotationProcessor())
                        .compile(JavaFileObjects.forSourceString("Iterator", FileHelper.getQueryFromFile(config.getString("files.iterator"), true, "@IteratorPattern.Iterator.hasNext", "") ),
                                JavaFileObjects.forSourceString("Aggregate", FileHelper.getQueryFromFile(config.getString("files.aggregate"), false)),
                                JavaFileObjects.forSourceString("ConcreteAggregate", FileHelper.getQueryFromFile(config.getString("files.ConcreteAggregate"), false)));
        assertThat(compilation).failed();
        assertThat(compilation).hadErrorContaining("should have atleast one method annotated");
    }

    @Test
    public void testIteratorIteratorMissingNext(){
        Compilation compilation =
                javac()
                        .withProcessors(new AnnotationProcessor())
                        .compile(JavaFileObjects.forSourceString("Iterator", FileHelper.getQueryFromFile(config.getString("files.iterator"), true, "@IteratorPattern.Iterator.next", "") ),
                                JavaFileObjects.forSourceString("Aggregate", FileHelper.getQueryFromFile(config.getString("files.aggregate"), false)),
                                JavaFileObjects.forSourceString("ConcreteAggregate", FileHelper.getQueryFromFile(config.getString("files.ConcreteAggregate"), false)));
        assertThat(compilation).failed();
        assertThat(compilation).hadErrorContaining("should have atleast one method annotated");
    }


}
