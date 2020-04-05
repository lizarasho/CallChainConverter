import exceptions.InvalidSyntaxException;
import exceptions.InvalidTypeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Positive tests of <сall-сhain> converter")
class CallChainConverterTest {

    @DisplayName("Mixed tests")
    @ParameterizedTest(name = "{index} test: source={0}")
    @MethodSource("mixedTestsArguments")
    void testMixedExpressions(String source, String expected) throws InvalidSyntaxException, InvalidTypeException {
        assertEquals(CallChainConverter.convert(source), expected);
    }

    private static Stream<Arguments> mixedTestsArguments() {
        return Stream.of(
                Arguments.of(
                        "map{(element+5)}%>%filter{(5=5)}",
                        "filter{(0=0)}%>%map{(5+element)}"
                ),
                Arguments.of(
                        "filter{(5>((7*3)+(-1--10)))}%>%map{(element+100)}",
                        "filter{(1=0)}%>%map{element}"
                ),
                Arguments.of(
                        "map{(element+5)}%>%filter{(element>10)}",
                        "filter{(element>5)}%>%map{(5+element)}"
                ),
                Arguments.of(
                        "map{(element+1)}%>%filter{(element=7)}",
                        "filter{(element=6)}%>%map{(1+element)}"
                ),
                Arguments.of(
                        "map{(element+5)}%>%filter{(element>10)}%>%map{(element+10)}",
                        "filter{(element>5)}%>%map{(15+element)}"
                ),
                Arguments.of(
                        "map{(2+element)}%>%filter{(element>2)}%>%map{(element*element)}",
                        "filter{(element>0)}%>%map{((4+(4*element))+(element*element))}"
                ),
                Arguments.of(
                        "filter{((element>0)|(6=6))}%>%map{(1+((element*element)+10))}",
                        "filter{(0=0)}%>%map{(11+(element*element))}"
                ),
                Arguments.of(
                        "map{(0-element)}%>%filter{(element>0)}%>%map{(0-element)}",
                        "filter{((-1*element)>0)}%>%map{element}"
                ),
                Arguments.of(
                        "map{(0-element)}%>%map{(-1*element)}%>%filter{(element>0)}",
                        "filter{(element>0)}%>%map{element}"
                ),
                Arguments.of(
                        "map{(2*element)}%>%map{(2*element)}%>%filter{(element>0)}%>%map{(2*element)}",
                        "filter{((4*element)>0)}%>%map{(8*element)}"
                ),
                Arguments.of(
                        "filter{(((element*element)+1)>1)}%>%filter{((element*element)>-1)}%>%map{(element+(element-element))}",
                        "filter{((element*element)>0)}%>%map{element}"
                ),
                Arguments.of(
                        "filter{(((element+(element*element))=element)|(element=0))}%>%map{(element+1)}%>%filter{(element>1)}",
                        "filter{((element>0)&((element*element)=0))}%>%map{(1+element)}"
                ),
                Arguments.of(
                        "map{(element*element)}%>%filter{(element>0)}%>%map{(element*element)}%>%filter{(element>0)}",
                        "filter{(((element*element)>0)&((((element*element)*element)*element)>0))}%>%map{(((element*element)*element)*element)}"
                ),
                Arguments.of(
                        "map{(2*element)}%>%map{(2*(element*element))}%>%filter{(element>0)}%>%map{(2*(element*element))}%>%filter{((2*element)>0)}",
                        "filter{(((8*(element*element))>0)&((256*(((element*element)*element)*element))>0))}%>%map{(128*(((element*element)*element)*element))}"
                )
        );
    }


    @DisplayName("Simplification of logical expressions")
    @ParameterizedTest(name = "{index} test: source={0}")
    @MethodSource("logicalExpressionsSimplificationArguments")
    void testLogicalExpressionsSimplification(String source, String expected) throws InvalidSyntaxException, InvalidTypeException {
        assertEquals(CallChainConverter.convert(source), expected);
    }

    @Test
    private static Stream<Arguments> logicalExpressionsSimplificationArguments() {
        return Stream.of(
                Arguments.of(
                        "filter{((element>1)&(1=1))}",
                        "filter{(element>1)}%>%map{element}"
                ),
                Arguments.of(
                        "filter{((1=1)&(element<1))}",
                        "filter{(element<1)}%>%map{element}"
                ),
                Arguments.of(
                        "filter{((7=0)&(element=1))}",
                        "filter{(1=0)}%>%map{element}"
                ),
                Arguments.of(
                        "filter{((element=1)&(-5>-2))}",
                        "filter{(1=0)}%>%map{element}"
                ),
                Arguments.of(
                        "filter{((7=0)|(element>-3))}",
                        "filter{(element>-3)}%>%map{element}"
                ),
                Arguments.of(
                        "filter{((element>10)|(2>-5))}",
                        "filter{(0=0)}%>%map{element}"
                ),
                Arguments.of(
                        "filter{((element>5)|(element<-5))}%>%filter{(element>-10)}",
                        "filter{((element>5)|((element>-10)&(element<-5)))}%>%map{element}"
                ),
                Arguments.of(
                        "filter{((element>5)|(element<-5))}%>%filter{(element>0)}",
                        "filter{(element>5)}%>%map{element}"
                ),
                Arguments.of(
                        "filter{(element>1)}%>%filter{(element>2)}%>%filter{(element>0)}",
                        "filter{(element>2)}%>%map{element}"
                ),
                Arguments.of(
                        "filter{(element>0)}%>%filter{(element=0)}%>%map{(element*element)}",
                        "filter{(1=0)}%>%map{element}"
                ),
                Arguments.of(
                        "filter{(element>-2)}%>%filter{(element=1)}%>%filter{(element<-77)}%>%filter{(element=11)}%>%filter{(element=-1)}",
                        "filter{(1=0)}%>%map{element}"
                ),
                Arguments.of(
                        "filter{(element=5)}%>%filter{(element>0)}%>%filter{(element=-1000)}",
                        "filter{(1=0)}%>%map{element}"
                ),
                Arguments.of(
                        "filter{(element>0)}%>%filter{((element<0)|(element>0))}%>%filter{(element=2)}%>%filter{(element<5)}",
                        "filter{(element=2)}%>%map{element}"
                )
        );

    }

    @DisplayName("Simplification of arithmetic expressions")
    @ParameterizedTest(name = "{index} test: source={0}")
    @MethodSource("arithmeticExpressionsSimplificationArguments")
    void testArithmeticExpressionsSimplification(String source, String expected) throws InvalidSyntaxException, InvalidTypeException {
        assertEquals(CallChainConverter.convert(source), expected);
    }

    @Test
    private static Stream<Arguments> arithmeticExpressionsSimplificationArguments() {
        return Stream.of(
                Arguments.of(
                        "map{(element+1)}",
                        "filter{(0=0)}%>%map{(1+element)}"
                ),
                Arguments.of(
                        "map{(element+-1)}",
                        "filter{(0=0)}%>%map{(-1+element)}"
                ),
                Arguments.of(
                        "map{(element-1)}",
                        "filter{(0=0)}%>%map{(-1+element)}"
                ),
                Arguments.of(
                        "map{(element--1)}",
                        "filter{(0=0)}%>%map{(1+element)}"
                ),
                Arguments.of(
                        "map{(0*element)}",
                        "filter{(0=0)}%>%map{0}"
                ),
                Arguments.of(
                        "map{(element*0)}",
                        "filter{(0=0)}%>%map{0}"
                ),
                Arguments.of(
                        "map{(1*element)}",
                        "filter{(0=0)}%>%map{element}"
                ),
                Arguments.of(
                        "map{(element*1)}",
                        "filter{(0=0)}%>%map{element}"
                ),
                Arguments.of(
                        "map{((1+element)+4)}",
                        "filter{(0=0)}%>%map{(5+element)}"
                ),
                Arguments.of(
                        "map{(5*(1+element))}",
                        "filter{(0=0)}%>%map{(5+(5*element))}"
                ),
                Arguments.of(
                        "map{((1+element)+element)}",
                        "filter{(0=0)}%>%map{(1+(2*element))}"
                ),
                Arguments.of(
                        "map{((1+element)-element)}",
                        "filter{(0=0)}%>%map{1}"
                ),
                Arguments.of(
                        "map{((2+element)*element)}",
                        "filter{(0=0)}%>%map{((2*element)+(element*element))}"
                ),
                Arguments.of(
                        "map{((2+element)*(5+element))}",
                        "filter{(0=0)}%>%map{((10+(7*element))+(element*element))}"
                ),
                Arguments.of(
                        "map{((2+element)*(5-element))}",
                        "filter{(0=0)}%>%map{((10+(3*element))-(element*element))}"
                ),
                Arguments.of(
                        "map{((element*element)*((1+element)-element))}",
                        "filter{(0=0)}%>%map{(element*element)}"
                ),
                Arguments.of(
                        "map{(2+element)}%>%map{(element*element)}",
                        "filter{(0=0)}%>%map{((4+(4*element))+(element*element))}"
                ),
                Arguments.of(
                        "map{(element+1)}%>%map{(element+2)}%>%map{(element*(element*element))}",
                        "filter{(0=0)}%>%map{(((27+(27*element))+(9*(element*element)))+((element*element)*element))}"
                ),
                Arguments.of(
                        "map{(element--1)}%>%map{(5*element)}%>%map{(element-10)}",
                        "filter{(0=0)}%>%map{(-5+(5*element))}"
                ),
                Arguments.of(
                        "map{(element*element)}%>%map{(element*element)}%>%map{(element*element)}",
                        "filter{(0=0)}%>%map{(((((((element*element)*element)*element)*element)*element)*element)*element)}"
                ),
                Arguments.of(
                        "map{(element*element)}%>%map{(element*element)}%>%map{(element*element)}%>%map{(element-1)}%>%map{(5*element)}",
                        "filter{(0=0)}%>%map{(-5+(5*(((((((element*element)*element)*element)*element)*element)*element)*element)))}"
                )
        );

    }
}



