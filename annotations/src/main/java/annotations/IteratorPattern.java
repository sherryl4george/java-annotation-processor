package annotations;

import java.lang.annotation.*;

/**
 * Annotations for the Iterator Pattern
 * All annotations are enforced during compile time only
 * @IteratorPattern is the parent for all annotations
 * pertaining to the iterator pattern
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
// Apply to interfaces and classes
public @interface IteratorPattern {
    /**
     * Annotation for Aggregate interface or abstract class
     * Rules are enforce in AnnotationProcessor.checkAggregateAnnotation
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Documented
    @interface Aggregate {

        // Annotation for the IteratorFactory method
        // Applicatble to only methods
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.METHOD)
        @interface IteratorFactory{
        }
    }

    /**
     * Annotation for Iterator interface
     * Rules are enforce in AnnotationProcessor.checkAIteratorAnnotation
     * Has two parameters to be specified during declaration
     * Due to the nature of checks no default is provided here
     * But they will be enforced during the runtime of processor
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Documented
    @interface Iterator {
        // Return type of next() method
        Class returnType() default void.class;
        // Index of type in the TypeParameter declaration
        // starting from 0
        int typeVariableReturnIndex() default -1;

        /**
         * Annotation for hasNext() method inside the Iterator
         * Enforced in the same method as for Iterator
         * Applciatble to only methods
         */
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.METHOD)
        @interface hasNext{};
        /**
         * Annotation for next() method inside the Iterator
         * Enforced in the same method as for Iterator
         * Applciatble to only methods
         */
        @Target(ElementType.METHOD)
        @Retention(RetentionPolicy.RUNTIME)
        @interface next{};
    }
    /**
     * Annotation for Iterator interface
     * Rules are enforce in AnnotationProcessor.checkConcreteAggregateAnnotation
     * to be enclose inside ConcreteAggregate
     * Can be applied to only classes. Check made in annotation processor
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Documented
    @interface ConcreteAggregate {

        /**
         * For the factory method inside ConcreteAggregate
         * Has a mandatory parameter which should specify the class
         * type of the return of the FactoryMethod which will be checked
          */

        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.METHOD)
        @interface IteratorFactory{
            Class returnType();
        }
//        Check for datafield. Not enforced currently
//        @Retention(RetentionPolicy.RUNTIME)
//        @Target(ElementType.FIELD)
//        @interface DataField{
//            Class type() default void.class;
//        }
    }

    /**
     * Annotation for Iterator interface
     * Rules are enforce in AnnotationProcessor.checkConcreteIteratorAnnotation
     * to be enclose inside ConcreteAggregate
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Documented
    @interface ConcreteIterator {}
}
