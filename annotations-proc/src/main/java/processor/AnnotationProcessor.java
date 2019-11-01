package processor;

import annotations.IteratorPattern;
import com.google.auto.service.AutoService;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.lang.annotation.Annotation;
import java.util.*;

/***
 * User define AnnotationProcessor extedning AbstractProcessor
 * Google AutoService is used to insert the class into the javac class
 * path.
 * No need of manual setting insdie the META-INF folder since it is auto
 * injected
 */
@AutoService(Processor.class)
public class AnnotationProcessor extends AbstractProcessor {
    // String constants for the processor
    public static final String ANNOTATION_ENDED_PROCESS = "Annotation Processing ended no more rounds to process";
    public static final String PROCESSING_CONCRETE_AGGREGATE_ANNOTATION_STARTING = "Processing ConcreteAggregateAnnotation - Starting";
    public static final String ONLY_CLASS_CAN_BE_ANNOTATED_WITH_S = "Only class can be annotated with @%s";
    public static final String S_CANNOT_HAVE_ABSTRACT_MODIFIER = "%s cannot have Abstract modifier";
    public static final String METHOD_SHOULD_BE_ANNOTATED_WITH_OVERRIDE = "Method should be annotated with @Override";
    public static final String RETURN_TYPE_PARAMETER__METHOD_ANNOTATED = "returnType parameter of annotation @%s should match with return type of method annotated";
    public static final String S_SHOULD_HAVE_PRIVATE_ACCESS_MODIFIER = "%s should have Private access modifier";
    public static final String METHOD_SHOULD_BE_ANNOTATED_WITH_OVERRIDE1 = "Method should be annotated with @Override";
    public static final String SHOULD_HAVE_ATLEAST_ONE_MEMBER_ANNOTATED_WITH = "%s should have atleast one member annotated with %s";
    public static final String PROCESSING_AGGREGATE_ANNOTATION_STARTING = "Processing AggregateAnnotation - Starting";
    public static final String ONLY_INTERFACE_OR_ABSTRACT_CLASS_CAN_BE_ANNOTATED_WITH_S = "Only interface or abstract class can be annotated with @%s";
    public static final String METHODS_WITH_S_CANNOT_BE_STATIC = "Methods with @%s cannot be static";
    public static final String METHODS_WITH_S_CANNOT_BE_PRIVATE = "Methods with @%s cannot be private";
    public static final String RETURN_TYPE_OF_METHODSITERATOR_INTERFACE = "Return type of methods with @%s should be assignable to the base Iterator interface";
    public static final String RETURN_TYPE_OF_METHODS_WITH_S_SHOULD_BE_ASSIGNABLE_TO_THE_BASE_ITERATOR_INTERFACE = "Return type of methods with @%s should be assignable to the base Iterator interface";
    public static final String S_SHOULD_HAVE_ATLEAST_ONCE_FIELD_ANNOTATED_WITH_S = "%s should have atleast once field annotated with %s";
    public static final String PROCESSING_ITERATOR_ANNOTATION_STARTING = "Processing IteratorAnnotation - Starting";
    public static final String THERE_CAN_BE_ONLY_ONE_INTERFACE_ANNOTATED_WITH_S = "There can be only one interface annotated with %s";
    public static final String ONLY_INTERFACE_CAN_BE_ANNOTATED_WITH_S = "Only interface can be annotated with @%s";
    public static final String RETURN_TYPE = "returnType";
    public static final String TYPE_VARIABLE_RETURN_INDEX = "typeVariableReturnIndex";
    public static final String S_EITHER_RETURN_TYPE_OR_TYPE_VARIABLE_RETURN_INDEX_NEEDS_TO_BE_SPECIFIED = "%s - Either returnType or typeVariableReturnIndex needs to be specified";
    public static final String S_TYPE_VARIABLE_RETURN_INDEX_SHOULD_HAVE_A_VALUE_LESSER_THAN_S = "%s typeVariableReturnIndex should have a value lesser than %s";
    public static final String S_CAN_BE_ANNOTATED_TO_ONLY_ON_METHOD_UNDER_S = "%s can be annotated to only on method under %s";
    public static final String METHODS_WITH_S_CANNOT_BE_STATIC1 = "Methods with @%s cannot be static";
    public static final String S_CAN_BE_ANNOTATED_TO_ONLY_ON_METHOD_UNDER_S1 = "%s can be annotated to only on method under %s";
    public static final String METHODS_WITH_S_CANNOT_BE_STATIC2 = "Methods with @%s cannot be static";
    public static final String S_SHOULD_HAVE_ATLEAST_ONE_METHOD_ANNOTATED_WITH_S = "%s should have atleast one method annotated with %s";
    public static final String S_NEEDS_AT_LEAST_ONE_METHOD_WITH_RETURN_TYPE_BOOLEAN = "@%s needs at least one method with return type boolean";
    public static final String S_SHOULD_HAVE_ATLEAST_ONE_METHOD_ANNOTATED_WITH_S1 = "%s should have atleast one method annotated with %s";
    public static final String S_NEEDS_AT_LEAST_ONE_METHOD_WITH_RETURN_TYPE_BASED_ON_THE_TYPE_PARAMETER = "@%s needs at least one method with return type based on the type parameter";
    public static final String PROCESSING_CONCRETE_ITERATOR_STARTING = "Processing ConcreteIterator - Starting";
    public static final String S_NEEDS_TO_BE_ENCLOSED_UNDER_S = "%s needs to be enclosed under %s";

    private Types typeUtils;
    private Messager messager;
    private boolean genericIterator = false;
    private Set<Name> iteratorInterfaceMethods = new HashSet<>();
    private Logger logger;

    /***
     * Overridden init() - Get necessary utilities from the processing
     * environment
     * @param processingEnv
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        messager = processingEnv.getMessager();
        logger = LogManager.getLogger(AnnotationProcessor.class);
    }

    /**
     * Driver method for the annotation process
     * This will be called during all rounds of annotation
     * Returning false here will stop the annotation processor
     * For current pricess there needs to be only one round that it has
     * to run
     * @param annotations
     * @param roundEnv
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // This round is over no more rounds are pending for this processor
        // as there is no extra file generated as a result of this processing
        if(roundEnv.processingOver()) {
            logger.log(Level.INFO, ANNOTATION_ENDED_PROCESS);
            return true;
        }
        // Checking all Iterator related annotations
        checkIteratorAnnotation(roundEnv);
        // Checking all Aggreagate annotations
        checkAggregateAnnotation(roundEnv);
        // Check ConcreteAggregate annotaations
        checkConcreteAggregateAnnotation(roundEnv);
        // Checking ConcreteIterator Annotations
        checkConcreteIteratorAnnotation(roundEnv);
        return true;
    }

    /**
     * Check ConcreteAggregate annotation.
     * Some rules are already enforced by the interface checks
     * Check for other rules in here
     * WE also enforce that all overridden methods have to be enforced
     * with @Override in here
     * @param roundEnv
     */
    private void checkConcreteAggregateAnnotation(RoundEnvironment roundEnv) {
        // Check if the ConcreteAggregate has IteratorFactory and ConcreteIterator inside them
        // This is the first necessarty check
        logger.log(Level.INFO, PROCESSING_CONCRETE_AGGREGATE_ANNOTATION_STARTING);
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(IteratorPattern.ConcreteAggregate.class)) {

            logger.log(Level.INFO, "Processing Annotation - " + IteratorPattern.ConcreteAggregate.class.getSimpleName());
            boolean foundIteratorFactory = false;
            boolean foundConcreteIterator = false;
            // Check if COncreteAggregate is a class and is a concrete one
            if (annotatedElement.getKind() != ElementKind.CLASS)
                error(annotatedElement, ONLY_CLASS_CAN_BE_ANNOTATED_WITH_S,
                        IteratorPattern.ConcreteAggregate.class.getSimpleName());
            if(annotatedElement.getModifiers().contains(Modifier.ABSTRACT))
                error(annotatedElement, S_CANNOT_HAVE_ABSTRACT_MODIFIER,
                        IteratorPattern.ConcreteAggregate.class.getSimpleName());
            // Enforce @Override for the IteratorFactory
            // Also check if the return type of AnnotatorFactory is assignable to the Iterator interface
            // To ensure this a mandatory returnType parameter is being passed as part of Iterator Factory.
            for (Element enclosedElement : annotatedElement.getEnclosedElements()) {
                Annotation aggregateIteratorFactoryAnnotatedElement = enclosedElement.getAnnotation(IteratorPattern.ConcreteAggregate.IteratorFactory.class);
                if (aggregateIteratorFactoryAnnotatedElement != null) {
                    logger.log(Level.INFO, "Processing Annotation - "
                            + IteratorPattern.ConcreteAggregate.IteratorFactory.class.getSimpleName());
                    foundIteratorFactory = true;
                    if(enclosedElement.getAnnotation(java.lang.Override.class)==null)
                        error(enclosedElement, METHOD_SHOULD_BE_ANNOTATED_WITH_OVERRIDE);
                    TypeMirror returnDataType = null;
                    for (AnnotationMirror annotationParameter : enclosedElement.getAnnotationMirrors()) {
                        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationParameter.getElementValues().entrySet()) {
                            if (entry.getKey().getSimpleName().contentEquals("returnType")) {
                                returnDataType = ((TypeMirror) entry.getValue().getValue());
                            }

                        }
                    }
                    // CHeck here for assignability;
                    if(!typeUtils.isAssignable(returnDataType, ((ExecutableElement)enclosedElement).getReturnType()))
                        error(enclosedElement, RETURN_TYPE_PARAMETER__METHOD_ANNOTATED,
                                IteratorPattern.ConcreteAggregate.IteratorFactory.class.getSimpleName());
                }
                // Checks for COnceteIterator inside COncreteAggregate
                // Making sure
                Annotation concreteIteratorAnnotatedElement = enclosedElement.getAnnotation(IteratorPattern.ConcreteIterator.class);
                if(concreteIteratorAnnotatedElement != null){
                    logger.log(Level.INFO, "Processing Annotation - " + IteratorPattern.ConcreteIterator.class.getSimpleName());

                    // Check if annotated type is class
                    // Class is not abstract and class is a private member
                    // Make sure all overridden methods are marked with Override
                    foundConcreteIterator = true;
                    if (enclosedElement.getKind() != ElementKind.CLASS)
                        error(enclosedElement, ONLY_CLASS_CAN_BE_ANNOTATED_WITH_S,
                                IteratorPattern.ConcreteIterator.class.getSimpleName());
                    if(enclosedElement.getModifiers().contains(Modifier.ABSTRACT))
                        error(enclosedElement, S_CANNOT_HAVE_ABSTRACT_MODIFIER,
                                IteratorPattern.ConcreteIterator.class.getSimpleName());
                    if(!enclosedElement.getModifiers().contains(Modifier.PRIVATE))
                        error(enclosedElement, S_SHOULD_HAVE_PRIVATE_ACCESS_MODIFIER,
                                IteratorPattern.ConcreteIterator.class.getSimpleName());
                    for(Element e: enclosedElement.getEnclosedElements()){
                        if(e instanceof ExecutableElement){
                            ExecutableElement method = (ExecutableElement) e;
                            if(iteratorInterfaceMethods.contains(method.getSimpleName())){
                                if(method.getAnnotation(java.lang.Override.class) == null){
                                    error(e, METHOD_SHOULD_BE_ANNOTATED_WITH_OVERRIDE1);
                                }
                            }
                        }
                    }
                }
            }
            // Check to make sure that we found ConcreteIterator and IteratorFactory inside
            // COncreteAggregate
            if(!foundConcreteIterator)
                error(annotatedElement, SHOULD_HAVE_ATLEAST_ONE_MEMBER_ANNOTATED_WITH,
                        IteratorPattern.ConcreteAggregate.class, IteratorPattern.ConcreteIterator.class);
            if(!foundIteratorFactory)
                error(annotatedElement, SHOULD_HAVE_ATLEAST_ONE_MEMBER_ANNOTATED_WITH,
                        IteratorPattern.ConcreteAggregate.class, IteratorPattern.ConcreteAggregate.IteratorFactory.class);
        }
        logger.log(Level.INFO, "Processing ConcreteAggregateAnnotation - Ending");
    }

    /**
     * Chekc all annotations that are needed for the Aggregate annotation.
     * Aggregeate annotation can be either an abstaract class or interface
     *
     * @param roundEnv
     */
    private void checkAggregateAnnotation(RoundEnvironment roundEnv) {
        logger.log(Level.INFO, PROCESSING_AGGREGATE_ANNOTATION_STARTING);
        boolean iteratorFactoryFound = false;
        // IteratorPatter.Aggregate - Needs to be applied to only Interface.
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(IteratorPattern.Aggregate.class)) {
            logger.log(Level.INFO, "Processing Annotation - " + IteratorPattern.Aggregate.class.getSimpleName());
            // Check if annotated type is interface
            if (!(annotatedElement.getKind() == ElementKind.INTERFACE ||
                    (annotatedElement.getKind() == ElementKind.CLASS && annotatedElement.getModifiers().contains(Modifier.ABSTRACT))))
                error(annotatedElement, ONLY_INTERFACE_OR_ABSTRACT_CLASS_CAN_BE_ANNOTATED_WITH_S,
                        IteratorPattern.Aggregate.class.getSimpleName());

            // IteratorPatter.Aggregate.IteratorFactory - Should be a public non-static method
            // With return type of returnType
            for (Element enclosedElement : annotatedElement.getEnclosedElements()) {
                Annotation iteratorFactoryAnnotatedElement = enclosedElement.getAnnotation(IteratorPattern.Aggregate.IteratorFactory.class);
                if (iteratorFactoryAnnotatedElement != null) {
                    logger.log(Level.INFO, "Processing Annotation - " + IteratorPattern.Aggregate.IteratorFactory.class.getSimpleName());
                    // No need to check for Type as Method cannot be applied to anyother types
                    // Check the method is not static so that it can be accessed from an instance
                    if(annotatedElement.getModifiers().contains(Modifier.STATIC))
                        error(annotatedElement, METHODS_WITH_S_CANNOT_BE_STATIC,
                                IteratorPattern.Aggregate.IteratorFactory.class.getSimpleName());
                    // Check the method is not static so that it can be accessed from outside world
                    if(annotatedElement.getModifiers().contains(Modifier.PRIVATE))
                        error(annotatedElement, METHODS_WITH_S_CANNOT_BE_PRIVATE,
                                IteratorPattern.Aggregate.IteratorFactory.class.getSimpleName());
                    // Check return type of method with the returnType parameter which is declared.
                    TypeMirror returnType = ((ExecutableElement)enclosedElement).getReturnType();
                    TypeMirror iteratorType = null;
                    for (Element element : roundEnv.getElementsAnnotatedWith(IteratorPattern.Iterator.class)){
                        iteratorType = element.asType();
                        break;
                    }
                    // The boolean is obtained as a part of the Iterator annotation run
                    // Check if there is a generic parameter assigned to the Aggregate
                    // If it is a generic Iterator then we need to have a type varibale passed
                    // into the FactoryMethod also. As of now the limitation is that there should only be one type variable
                    // passed to Aggregate.
                    // Check here if the IteratoryFactor method return type is in any case assignable to the Iterator interface
                    if(!genericIterator) {
                        if (!typeUtils.isAssignable(returnType, iteratorType))
                            error(annotatedElement, RETURN_TYPE_OF_METHODSITERATOR_INTERFACE,
                                    IteratorPattern.Aggregate.IteratorFactory.class.getSimpleName());
                    }
                    else{
                        // Check here to ensure that the type of FactoryMethod and Iterarot are assignable
                        // This will be triggered only in case of generics being used
                        TypeMirror returnTypeEnclosingType = null;
                        if(returnType instanceof DeclaredType)
                            returnTypeEnclosingType = ((DeclaredType) returnType).asElement().asType();
                        TypeMirror iteratorTypeEnclosingType = null;
                        if(iteratorType instanceof DeclaredType)
                            iteratorTypeEnclosingType = ((DeclaredType) iteratorType).asElement().asType();
                        if(returnTypeEnclosingType != null && iteratorTypeEnclosingType != null
                                && !typeUtils.isAssignable(returnTypeEnclosingType, iteratorTypeEnclosingType))
                            error(annotatedElement, RETURN_TYPE_OF_METHODS_WITH_S_SHOULD_BE_ASSIGNABLE_TO_THE_BASE_ITERATOR_INTERFACE,
                                    IteratorPattern.Aggregate.IteratorFactory.class.getSimpleName());
                    }
                    iteratorFactoryFound = true;
                }
            }
        }
        if(!iteratorFactoryFound)
            error(null, S_SHOULD_HAVE_ATLEAST_ONCE_FIELD_ANNOTATED_WITH_S,
                    IteratorPattern.Aggregate.class, IteratorPattern.Aggregate.IteratorFactory.class);
        for(Element annotatedElements: roundEnv.getElementsAnnotatedWith(IteratorPattern.Aggregate.IteratorFactory.class)){
            if (annotatedElements.getEnclosingElement().getAnnotation(IteratorPattern.Aggregate.class) == null)
                error(annotatedElements, S_NEEDS_TO_BE_ENCLOSED_UNDER_S,
                        IteratorPattern.Aggregate.IteratorFactory.class, IteratorPattern.Aggregate.class);
        }
        logger.log(Level.INFO, "Processing AggregateAnnotation - Ending");
    }

    /***
     * Check all rules regarding IteratorPattern.Iterator
     * Includes rule check for everything enclosed inside the Iterator annotatior
     * @param roundEnv
     * @return
     */
    private void checkIteratorAnnotation(final RoundEnvironment roundEnv) {
        logger.log(Level.INFO, PROCESSING_ITERATOR_ANNOTATION_STARTING);
        // IteratorPattern.Iterator
        // We need to see what is the argument that is set to the Iterator<> or if it is just empty
        // Whatever the case is keep it stored in a variable and then could be checked later on
        // to match what needs to be done later on.
        TypeMirror typeParameterName = null;
        TypeMirror returnDataType = null;
        // Flip when hasNext and next() is found
        boolean booleanMethod = false;
        boolean matchedReturnType = false;
        // Store parameter index in case of Generic Iterator, initialize and lowest value
        int parameterIndex = Integer.MIN_VALUE;
        int mainIteratorCount = 0;
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(IteratorPattern.Iterator.class)) {
            logger.log(Level.INFO, "Processing Annotation - " + IteratorPattern.Iterator.class.getSimpleName());
            // TypeCheck needs to be skipped in case of an empty parameter list
            boolean skipTypeCheck = false;
            // Allow only one Iterator interface. More interfaces can be created by extending and overriding
            // this interface
            if(mainIteratorCount == 1)
                error(annotatedElement, THERE_CAN_BE_ONLY_ONE_INTERFACE_ANNOTATED_WITH_S,
                        IteratorPattern.Iterator.class);
            // Check if annotated type is interface
            if (annotatedElement.getKind() != ElementKind.INTERFACE)
                error(annotatedElement, ONLY_INTERFACE_CAN_BE_ANNOTATED_WITH_S,
                        IteratorPattern.Iterator.class.getSimpleName());
            // Retrieve parameter values inside the annotation
            // We have the option of giving either returnType or typeVariableReturnIndex
            // Query for both of the variables
            for (AnnotationMirror annotationParameter : annotatedElement.getAnnotationMirrors()) {
                for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationParameter.getElementValues().entrySet()) {
                    if (entry.getKey().getSimpleName().contentEquals(RETURN_TYPE)) {
                        returnDataType = ((TypeMirror) entry.getValue().getValue());
                    }
                    if (entry.getKey().getSimpleName().contentEquals(TYPE_VARIABLE_RETURN_INDEX)) {
                        parameterIndex = (int) entry.getValue().getValue();
                    }

                }
            }
            // This case means that both are not provided. Manual check is needed as we don't
            // enforce it from annotation level. Also skip type check since we don't have information yet
            if(returnDataType == null && parameterIndex == Integer.MIN_VALUE) {
                error(annotatedElement, S_EITHER_RETURN_TYPE_OR_TYPE_VARIABLE_RETURN_INDEX_NEEDS_TO_BE_SPECIFIED,
                        IteratorPattern.Iterator.class);
                skipTypeCheck = true;
            }
            // If there is a parameter given then in that case check if it is between the given length
            // If not throw an error here
            if (annotatedElement instanceof TypeElement && returnDataType == null && parameterIndex != Integer.MIN_VALUE) {
                TypeElement te = (TypeElement) annotatedElement;
                try {
                    TypeParameterElement typeParameterElement = te.getTypeParameters().get(parameterIndex);
                    typeParameterName = typeParameterElement.asType();
                }
                catch (IndexOutOfBoundsException e){
                    error(annotatedElement, S_TYPE_VARIABLE_RETURN_INDEX_SHOULD_HAVE_A_VALUE_LESSER_THAN_S,
                            IteratorPattern.Iterator.class, te.getTypeParameters().size());
                    return;
                }
            }
            int hasNextElements = 0;
            int nextElements = 0;
            // Every iterator needs to have a hasNext() and next() methods annotated accordingly
            // Check it here
            // Also make sure that they aren't declared static and also they are enclosed inside annotation
            // Iterator. Only one of each method should exist with the annotation in each interface
            for (Element enclosedElement : annotatedElement.getEnclosedElements()) {
                // **** Check if the methods has any parameters in them
                Annotation hasNextAnnotatedElement = enclosedElement.getAnnotation(IteratorPattern.Iterator.hasNext.class);
                Annotation nextAnnotatedElement = enclosedElement.getAnnotation(IteratorPattern.Iterator.next.class);
                // Check for hasNext here
                if (hasNextAnnotatedElement != null){
                    logger.log(Level.INFO, "Processing Annotation - " + IteratorPattern.Iterator.hasNext.class.getSimpleName());
                    if (hasNextElements == 1)
                        error(enclosedElement, S_CAN_BE_ANNOTATED_TO_ONLY_ON_METHOD_UNDER_S,
                                IteratorPattern.Iterator.hasNext.class, IteratorPattern.Iterator.class);
                    if (enclosedElement instanceof ExecutableElement) {
                        if (enclosedElement.getEnclosingElement().getAnnotation(IteratorPattern.Iterator.class) == null)
                            error(enclosedElement, S_NEEDS_TO_BE_ENCLOSED_UNDER_S,
                                    IteratorPattern.Iterator.hasNext.class, IteratorPattern.Iterator.class);
                        if (annotatedElement.getModifiers().contains(Modifier.STATIC))
                            error(enclosedElement, METHODS_WITH_S_CANNOT_BE_STATIC1,
                                    IteratorPattern.Iterator.hasNext.class.getSimpleName());
                        // Check return type
                        if (((ExecutableElement) enclosedElement).getReturnType().getKind().equals(TypeKind.BOOLEAN))
                            booleanMethod = true;
                        iteratorInterfaceMethods.add(enclosedElement.getSimpleName());
                        hasNextElements++;
                    }
                }
                if (nextAnnotatedElement != null){
                    logger.log(Level.INFO, "Processing Annotation - " + IteratorPattern.Iterator.next.class.getSimpleName());
                    // CHeck for next here
                    // We allow only one method annotated per interface
                    if (nextElements == 1)
                        error(enclosedElement, S_CAN_BE_ANNOTATED_TO_ONLY_ON_METHOD_UNDER_S1,
                                IteratorPattern.Iterator.next.class, IteratorPattern.Iterator.class);
                    if (enclosedElement instanceof ExecutableElement) {
                        if (annotatedElement.getModifiers().contains(Modifier.STATIC))
                            error(enclosedElement, METHODS_WITH_S_CANNOT_BE_STATIC2,
                                    IteratorPattern.Iterator.next.class.getSimpleName());
                        // Check the return type here
                        if(!skipTypeCheck) {
                            if (returnDataType != null) {
                                // Usual type check in case a return type is specified
                                if (typeUtils.isSameType(returnDataType, ((ExecutableElement) enclosedElement).getReturnType()))
                                    matchedReturnType = true;
                            } else {
                                // Tweaked to check the type parameter incase a generic type variable is used
                                matchedReturnType = typeParameterName.toString().equals(((ExecutableElement) enclosedElement).getReturnType().toString());
                                genericIterator = true;
                            }
                        }
                        iteratorInterfaceMethods.add(enclosedElement.getSimpleName());
                        nextElements++;
                    }
                }
                mainIteratorCount++;
            }


            // Final check if there is no hasNext or next() found then error
            // else if a method with suitable return types are not found error out
            if(hasNextElements == 0)
                error(annotatedElement, S_SHOULD_HAVE_ATLEAST_ONE_METHOD_ANNOTATED_WITH_S,
                        IteratorPattern.Iterator.class, IteratorPattern.Iterator.hasNext.class);
            else if (!booleanMethod)
                error(annotatedElement, S_NEEDS_AT_LEAST_ONE_METHOD_WITH_RETURN_TYPE_BOOLEAN, IteratorPattern.Iterator.class.getSimpleName());
            if(nextElements == 0)
                error(annotatedElement, S_SHOULD_HAVE_ATLEAST_ONE_METHOD_ANNOTATED_WITH_S1,
                        IteratorPattern.Iterator.class, IteratorPattern.Iterator.next.class);
            else if(!matchedReturnType && !skipTypeCheck)
                error(annotatedElement, S_NEEDS_AT_LEAST_ONE_METHOD_WITH_RETURN_TYPE_BASED_ON_THE_TYPE_PARAMETER,
                        IteratorPattern.Iterator.class);

        }
        // Check the enclosing element of both items
        // Make sure they are enclosed inside Iterator and not anywhere as a standalone
        for(Element annotatedElements: roundEnv.getElementsAnnotatedWith(IteratorPattern.Iterator.hasNext.class)){
            if (annotatedElements.getEnclosingElement().getAnnotation(IteratorPattern.Iterator.class) == null)
                error(annotatedElements, S_NEEDS_TO_BE_ENCLOSED_UNDER_S,
                        IteratorPattern.Iterator.hasNext.class, IteratorPattern.Iterator.class);
        }
        for(Element annotatedElements: roundEnv.getElementsAnnotatedWith(IteratorPattern.Iterator.next.class)){
            if (annotatedElements.getEnclosingElement().getAnnotation(IteratorPattern.Iterator.class) == null)
                error(annotatedElements, S_NEEDS_TO_BE_ENCLOSED_UNDER_S,
                        IteratorPattern.Iterator.next.class, IteratorPattern.Iterator.class);
        }
        logger.log(Level.INFO, "Processing AggregateAnnotation - Ending");
    }

    /**
     * Checks requried for ConcreteIterator annotation
     * We check starting from the interfaces first. Hence all the rules that are enforced
     * by the interface annotation are already enforced in here by the compiler itself
     * Having to check them again is not necessary and hece only absolute necessary checks are mad
     * @param roundEnv
     */
    private void checkConcreteIteratorAnnotation(RoundEnvironment roundEnv) {
        logger.log(Level.INFO, PROCESSING_CONCRETE_ITERATOR_STARTING);
        // CHeck for the enclosing class of ConcreteIterator.
        // They should be enclosed inside ConcreteAggregate annotations
        for(Element annotatedElements: roundEnv.getElementsAnnotatedWith(IteratorPattern.ConcreteIterator.class)){
            if (annotatedElements.getEnclosingElement().getAnnotation(IteratorPattern.ConcreteAggregate.class) == null)
                error(annotatedElements, S_NEEDS_TO_BE_ENCLOSED_UNDER_S,
                        IteratorPattern.ConcreteIterator.class, IteratorPattern.ConcreteAggregate.class);
        }
        // CHeck for the enclosing class of IteratorFactory methods annotation.
        // They should be enclosed inside ConcreteAggregate annotations
        for(Element annotatedElements: roundEnv.getElementsAnnotatedWith(IteratorPattern.ConcreteAggregate.IteratorFactory.class)){
            if (annotatedElements.getEnclosingElement().getAnnotation(IteratorPattern.ConcreteAggregate.class) == null)
                error(annotatedElements, S_NEEDS_TO_BE_ENCLOSED_UNDER_S,
                        IteratorPattern.ConcreteAggregate.IteratorFactory.class, IteratorPattern.ConcreteAggregate.class);
        }
        logger.log(Level.INFO, "Processing ConcreteIterator - Ending");
    }

    /**
     * Generic method to display error messages during the annotation processing
     * Logging has also been implemented inside the same inorder to avoid multiple log statements
     * in the code
     * @param e Element where the error has occured
     * @param msg Message to be displayed with string placeholder
     * @param args Varargs are used inorder to pass argumets to fill in placeholder
     * Adapted from: http://hannesdorfmann.com/annotation-processing/annotationprocessing101
     */
    private void error(Element e, String msg, Object... args) {
        // Log the annotation error
        logger.log(Level.ERROR, "Annotation violation - " + String.format(msg, args));
        messager.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(msg, args),
                e);
    }

    /**
     * Generic method to display error messages during the annotation processing
     * Logging has also been implemented inside the same inorder to avoid multiple log statements
     * in the code
     * @param e Element where the error has occured
     * @param msg Message to be displayed with string placeholder
     * @param args Varargs are used inorder to pass argumets to fill in placeholder
     * Adapted from: http://hannesdorfmann.com/annotation-processing/annotationprocessing101
     */
    private void info(Element e, String msg, Object... args) {

        messager.printMessage(
                Diagnostic.Kind.NOTE,
                String.format(msg, args),
                e);
    }

    /**
     * Pass the version that would be handled by the current processor
     * @return
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * All annotations that can be processed by this processor
     * Add all the class details into a set and can be returned
     * Since we are using only one processor all annotations wer are interested are added in here
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedAnnotations = new HashSet<>();
        supportedAnnotations.add(IteratorPattern.Iterator.class.getCanonicalName());
        supportedAnnotations.add(IteratorPattern.Aggregate.class.getCanonicalName());
        supportedAnnotations.add(IteratorPattern.Aggregate.IteratorFactory.class.getCanonicalName());
        return supportedAnnotations;
    }
}