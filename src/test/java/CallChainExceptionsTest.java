import exceptions.InvalidSyntaxException;
import exceptions.InvalidTypeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Negative tests of <сall-сhain> converter")
class CallChainExceptionsTest {

    @DisplayName("Syntax exception tests")
    @ParameterizedTest(name = "{index} test: source={0}")
    @MethodSource("testSyntaxExceptionArguments")
    void testSyntaxException(String source) {
        assertThrows(InvalidSyntaxException.class, () -> CallChainConverter.convert(source));
    }

    @Test
    private static Stream<Arguments> testSyntaxExceptionArguments() {
        return Stream.of(
                Arguments.of(
                        "m"
                ),
                Arguments.of(
                        "map"
                ),
                Arguments.of(
                        "map{"
                ),
                Arguments.of(
                        "map}"
                ),
                Arguments.of(
                        "map{}"
                ),
                Arguments.of(
                        "map{()}"
                ),
                Arguments.of(
                        "map{(1)}"
                ),
                Arguments.of(
                        "map{(-1)}"
                ),
                Arguments.of(
                        "map{(element)}"
                ),
                Arguments.of(
                        "map{-element}"
                ),
                Arguments.of(
                        "map{(1+ele$ent)}"
                ),
                Arguments.of(
                        "map{(1+elem)}"
                ),
                Arguments.of(
                        "map{+1}"
                ),
                Arguments.of(
                        "map{(1+)}"
                ),
                Arguments.of(
                        "map{(+1)}"
                ),
                Arguments.of(
                        "map{1+1}"
                ),
                Arguments.of(
                        "map{(1+1+1)}"
                ),
                Arguments.of(
                        "map{(1+(1+1)+1)}"
                ),
                Arguments.of(
                        "map{(1+(1+1+1))}"
                ),
                Arguments.of(
                        "map{(1+1))}"
                ),
                Arguments.of(
                        "map{((1+1)}"
                ),
                Arguments.of(
                        "map{((1+1))}"
                ),
                Arguments.of(
                        "map{(1++1)}"
                ),
                Arguments.of(
                        "map{(1+1)(1+1)}"
                ),
                Arguments.of(
                        "map{(1+1)}%>%filter"
                ),
                Arguments.of(
                        "map{(1+1)}filter{(1+1)}"
                ),
                Arguments.of(
                        "map{(1+1)}%filter{(1+1)}"
                ),
                Arguments.of(
                        "map{(1+1)}%%filter{(1+1)}"
                ),
                Arguments.of(
                        "map{(1+1)}%<%filter{(1+1)}"
                ),
                Arguments.of(
                        "map{(1+1)}%"
                ),
                Arguments.of(
                        "map{(1+1)}%>%"
                )
        );
    }


    @DisplayName("Type exception tests")
    @ParameterizedTest(name = "{index} test: source={0}")
    @MethodSource("testTypeExceptionArguments")
    void testTypeException(String source) {
        assertThrows(InvalidTypeException.class, () -> CallChainConverter.convert(source));
    }

    private static Stream<Arguments> testTypeExceptionArguments() {
        return Stream.of(
                Arguments.of(
                        "map{(1=1)}"
                ),
                Arguments.of(
                        "map{(element=1)}"
                ),
                Arguments.of(
                        "map{(1>0)}"
                ),
                Arguments.of(
                        "map{(1<0)}"
                ),
                Arguments.of(
                        "map{((element>0)&(element<1))}"
                ),
                Arguments.of(
                        "map{((element>0)|(element<1))}"
                ),
                Arguments.of(
                        "map{(element+1)}%>%map{(element=1)}"
                ),
                Arguments.of(
                        "map{(element=1)}%>%map{(element+1)}"
                ),

                Arguments.of(
                        "filter{1}"
                ),
                Arguments.of(
                        "filter{-1}"
                ),
                Arguments.of(
                        "filter{element}"
                ),
                Arguments.of(
                        "filter{(element+1)}"
                ),
                Arguments.of(
                        "filter{(1+1)}"
                ),
                Arguments.of(
                        "filter{(2-1)}"
                ),
                Arguments.of(
                        "filter{(2*2)}"
                ),
                Arguments.of(
                        "filter{(element>0)}%>%filter{(element+1)}"
                ),
                Arguments.of(
                        "filter{(element+1)}%>%filter{(element>0)}"
                ),

                Arguments.of(
                        "map{((element+1)&(element>1))}"
                ),
                Arguments.of(
                        "map{((element+1)|(element>1))}"
                ),
                Arguments.of(
                        "map{(element+(1>1))}"
                ),
                Arguments.of(
                        "map{((1>1)-(element+1))}"
                ),
                Arguments.of(
                        "map{((1>1)*5)}"
                ),
                Arguments.of(
                        "map{(1+(1+(element=5)))}"
                ),

                Arguments.of(
                        "filter{element}"
                ),
                Arguments.of(
                        "filter{((element>1)+1)}"
                ),
                Arguments.of(
                        "filter{(1+(element>1))}"
                ),
                Arguments.of(
                        "filter{((element=2)-(1=1))}"
                ),
                Arguments.of(
                        "filter{(element|(1=1))}"
                ),
                Arguments.of(
                        "filter{(element&(1=1))}"
                ),
                Arguments.of(
                        "filter{((1=1)|(element+1))}"
                ),
                Arguments.of(
                        "filter{((1=1)&(element+1))}"
                )
        );
    }

}
