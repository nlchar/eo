package org.eolang;

import org.eolang.core.EOObject;
import org.eolang.core.data.EODataObject;
import org.eolang.txt.EOsprintf;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/**
 * Test cases for {@link EOarray}
 */
class EOarrayTest {

    /***
     * Test for {@code EOisEmpty}
     * checks if an array is empty
     */
    @Test
    void EOisEmpty() {
        EOarray array = new EOarray();
        MatcherAssert.assertThat(
                array.EOisEmpty()._getData().toBoolean(),
                Matchers.equalTo(true)
        );

    }

    /***
     * Test for {@code EOlength}
     * checks if the length of the array is returned
     */
    @Test
    void EOlength() {
        EOarray array = new EOarray(
                new EODataObject(1),
                new EODataObject(3),
                new EODataObject(5),
                new EODataObject(7),
                new EODataObject(9)
        );
        MatcherAssert.assertThat(
                array.EOlength()._getData().toInt(),
                Matchers.equalTo(5L)
        );
    }

    /***
     * Test for {@code EOget}
     * checks if the element at a specified position of the array is returned
     */
    @Test
    void EOget() {
        EOarray array = new EOarray(
                new EODataObject(1),
                new EODataObject(3),
                new EODataObject(5),
                new EODataObject(7),
                new EODataObject(9)
        );
        MatcherAssert.assertThat(
                array.EOget(
                        new EODataObject(2))._getData().toInt(),
                Matchers.equalTo(5L)
        );
    }

    /**
     * Checks that {@code EOappend} is able to add a new element to the end of a non-empty array.
     */
    @Test
    void EOappendWorksWithNonEmptyArrays() {
        EOarray inputArray = new EOarray(
                new EOint(1),
                new EOint(3),
                new EOint(5),
                new EOint(7),
                new EOint(9)
        );
        EOObject appendedElement = new EOint(10);
        EOarray expectedResultArray = new EOarray(
                new EOint(1),
                new EOint(3),
                new EOint(5),
                new EOint(7),
                new EOint(9),
                appendedElement
        );
        EOarray resultArray = inputArray.EOappend(appendedElement);

        MatcherAssert.assertThat(resultArray, Matchers.is(expectedResultArray));
    }

    /**
     * Checks that {@code EOappend} is able to add a new element to the end of an empty array.
     */
    @Test
    void EOappendWorksWithEmptyArrays() {
        EOarray inputArray = new EOarray();
        EOObject appendedElement = new EOint(10);
        EOarray expectedResultArray = new EOarray(appendedElement);
        EOarray resultArray = inputArray.EOappend(appendedElement);

        MatcherAssert.assertThat(resultArray, Matchers.is(expectedResultArray));
    }

    /***
     * Test for {@code EOappend}
     * checks if an elements successfully appends to an array
     */
    //@Test
    void EOappendAll() {
        EOarray array = new EOarray(
                new EODataObject(1),
                new EODataObject(3),
                new EODataObject(5),
                new EODataObject(7),
                new EODataObject(9)
        );
        Long arraySize = array.EOlength()._getData().toInt();
        EOarray array2 = new EOarray(
                new EODataObject(2),
                new EODataObject(4),
                new EODataObject(6),
                new EODataObject(8),
                new EODataObject(10),
                new EODataObject(12)
        );
        Long array2Size = array2.EOlength()._getData().toInt();
        //Append not an EOarray
        EOarray appendedArray1 = array.EOappendAll(new EODataObject(10));
        MatcherAssert.assertThat(
                appendedArray1.EOlength()._getData().toInt(),
                Matchers.equalTo(arraySize+1));
        for(int i = 0; i < arraySize; ++i){
            MatcherAssert.assertThat(
                    appendedArray1.EOget(new EODataObject(i))._getData().toInt(),
                    Matchers.equalTo(array.EOget(new EODataObject(i))._getData().toInt()));
        }
        MatcherAssert.assertThat(
                appendedArray1.EOget(new EODataObject(arraySize))._getData().toInt(),
                Matchers.equalTo(10L)
        );

        //Append an empty EOarray
        EOarray appendedArray2 = array.EOappendAll(new EOarray());
        MatcherAssert.assertThat(
                appendedArray2.EOlength()._getData().toInt(),
                Matchers.equalTo(arraySize));
        for(int i = 0; i < arraySize; ++i){
            MatcherAssert.assertThat(
                    appendedArray2.EOget(new EODataObject(i))._getData().toInt(),
                    Matchers.equalTo(array.EOget(new EODataObject(i))._getData().toInt()));
        }

        //Append an not empty EOarray
        EOarray appendedArray3 = array.EOappendAll(array2);
        MatcherAssert.assertThat(
                appendedArray3.EOlength()._getData().toInt(),
                Matchers.equalTo(arraySize+array2Size));
        for(Long i = 0L; i < arraySize; ++i){
            MatcherAssert.assertThat(
                    appendedArray3.EOget(new EODataObject(i))._getData().toInt(),
                    Matchers.equalTo(array.EOget(new EODataObject(i))._getData().toInt()));
        }
        for(Long i = arraySize; i < arraySize+array2Size; ++i){
            MatcherAssert.assertThat(
                    appendedArray3.EOget(new EODataObject(i))._getData().toInt(),
                    Matchers.equalTo(array2.EOget(new EODataObject(i - arraySize))._getData().toInt()));
        }

    }

    /**
     * Checks that {@code EOreduce} is able to reduce a non-empty int array to a sum of its elements.
     */
    @Test
    void EOreduceSumsNonEmptyIntArray() {
        EOarray inputArray = new EOarray(
                new EOint(1),
                new EOint(3),
                new EOint(5),
                new EOint(7),
                new EOint(9)
        );
        Long expectedResult = 25L;
        EOObject reducerObject = new EOObject() {
            public EOObject EOreduce(EOint subtotal, EOObject element) {
                return new EOObject() {
                    @Override
                    protected EOObject _decoratee() {
                        // adds the current element of the array to the subtotal
                        return subtotal.EOadd(element);
                    }
                };
            }
        };
        EOint initialAccumulator = new EOint(0);
        EOObject reducedValue = inputArray.EOreduce(initialAccumulator, reducerObject);

        MatcherAssert.assertThat(reducedValue._getData().toInt(), Matchers.equalTo(expectedResult));
    }

    /**
     * Checks that {@code EOreduce} return the initial value when working with empty arrays.
     */
    @Test
    void EOreduceWorksWithEmptyArrays() {
        EOarray inputArray = new EOarray();
        Long expectedResult = 0L;
        EOObject reducerObject = new EOObject() {
            public EOObject EOreduce(EOint subtotal, EOObject element) {
                return new EOObject() {
                    @Override
                    protected EOObject _decoratee() {
                        // adds the current element of the array to the subtotal
                        return subtotal.EOadd(element);
                    }
                };
            }
        };
        EOint initialAccumulator = new EOint(0);
        EOObject reducedValue = inputArray.EOreduce(initialAccumulator, reducerObject);

        MatcherAssert.assertThat(reducedValue._getData().toInt(), Matchers.equalTo(expectedResult));
    }

    /**
     * Checks that {@code EOreducei} is able to evaluate a polynomial 5*x^4 - 7*x^2 + 20*x + 1, where x = 10.
     * This problem is tested by reducing the array [1, 20, -7, 0, 5]
     * representing the polynomial coefficients (from the lowest to the highest degree of x).
     * Reduction considers the indices of the coefficients array [0, 1, 2, 3, 4],
     * where each index represents a degree of the corresponding x.
     * Reduction works as follows:
     * 1. The initial value is 0.
     * 2. For each element of the coefficients array,
     *    multiply it to x raised into the power of the current index, where x = 10.
     * Hence, the expected result is 49501.
     */
    @Test
    void EOreduceiWorksWithNonEmptyIntArray() {
        EOarray inputArray = new EOarray(
                new EOint(1),
                new EOint(20),
                new EOint(-7),
                new EOint(0),
                new EOint(5)
        );
        Long expectedResult = 49501L;
        EOint xValue = new EOint(10L);
        EOObject reducerObject = new EOObject() {
            public EOObject EOreducei(EOint subtotal, EOint element, EOint index) {
                return new EOObject() {
                    @Override
                    protected EOObject _decoratee() {
                        // + x*(a_i)^(c_i)
                        return subtotal.EOadd(element.EOmul(xValue.EOpow(index)));
                    }
                };
            }
        };
        EOint initialAccumulator = new EOint(0);
        EOObject reducedValue = inputArray.EOreducei(initialAccumulator, reducerObject);

        MatcherAssert.assertThat(reducedValue._getData().toInt(), Matchers.equalTo(expectedResult));
    }

    /**
     * Checks that {@code EOreducei} is able to work with empty arrays. The result must be equal to the initial value.
     */
    @Test
    void EOreduceiWorksWithEmptyIntArray() {
        EOarray inputArray = new EOarray();
        EOObject reducerObject = new EOObject() {
            public EOObject EOreducei(EOint subtotal, EOint element, EOint index) {
                return new EOObject() {
                    @Override
                    protected EOObject _decoratee() {
                        // adds the current element of the array and the current index to the subtotal
                        return subtotal.EOadd(element).EOadd(index);
                    }
                };
            }
        };
        EOint initialAccumulator = new EOint(177);
        EOObject reducedValue = inputArray.EOreducei(initialAccumulator, reducerObject);

        MatcherAssert.assertThat(reducedValue, Matchers.equalTo(initialAccumulator));
    }

    /**
     * Checks that {@code EOmap} is able to map a non-empty int array to an array of squares of its elements.
     */
    @Test
    void EOmapTransformsNonEmptyIntArrayToItsSquares() {
        EOarray inputArray = new EOarray(
                new EOint(1),
                new EOint(3),
                new EOint(5),
                new EOint(7),
                new EOint(9)
        );
        EOarray expectedResultArray = new EOarray(
                new EOint(1),
                new EOint(9),
                new EOint(25),
                new EOint(49),
                new EOint(81)
        );
        EOObject mapperObject = new EOObject() {
            public EOObject EOmap(EOint element) {
                return new EOObject() {
                    @Override
                    protected EOObject _decoratee() {
                        return element.EOpow(new EOint(2));
                    }
                };
            }
        };
        EOarray resultArray = inputArray.EOmap(mapperObject);
        MatcherAssert.assertThat(resultArray, Matchers.is(expectedResultArray));
    }

    /**
     * Checks that {@code EOmap} is able to map an empty array to another empty array.
     */
    @Test
    void EOmapWorksWithEmptyArrays() {
        EOarray inputArray = new EOarray();
        EOObject mapperObject = new EOObject() {
            public EOObject EOmap(EOint element) {
                return new EOObject() {
                    @Override
                    protected EOObject _decoratee() {
                        return element.EOpow(new EOint(2));
                    }
                };
            }
        };
        EOarray resultArray = inputArray.EOmap(mapperObject);
        MatcherAssert.assertThat(resultArray.EOisEmpty()._getData().toBoolean(), Matchers.equalTo(true));
    }

    /**
     * Checks that {@code EOmapi} is able to map a non-empty string array
     * to an array of strings with indices concatenated to its elements.
     */
    @Test
    void EOmapiTransformsNonEmptyStringArrayUsingIndices() {
        EOarray inputArray = new EOarray(
                new EOstring("this"),
                new EOstring("is"),
                new EOstring("a"),
                new EOstring("test"),
                new EOstring("strings array")
        );
        EOarray expectedResultArray = new EOarray(
                new EOstring("this0"),
                new EOstring("is1"),
                new EOstring("a2"),
                new EOstring("test3"),
                new EOstring("strings array4")
        );
        EOObject mapperObject = new EOObject() {
            public EOObject EOmapi(EOstring element, EOint index) {
                return new EOObject() {
                    @Override
                    protected EOObject _decoratee() {
                        return new EOsprintf(
                                    new EOstring("%s%d"),
                                    element,
                                    index
                        );
                    }
                };
            }
        };
        EOarray resultArray = inputArray.EOmapi(mapperObject);
        MatcherAssert.assertThat(resultArray, Matchers.is(expectedResultArray));
    }

    /**
     * Checks that {@code EOmapi} is able to map an empty array to another empty array.
     */
    @Test
    void EOmapiWorksWithEmptyArrays() {
        EOarray inputArray = new EOarray();
        EOObject mapperObject = new EOObject() {
            public EOObject EOmapi(EOstring element, EOint index) {
                return new EOObject() {
                    @Override
                    protected EOObject _decoratee() {
                        return new EOsprintf(
                                new EOstring("%s%d"),
                                element,
                                index
                        );
                    }
                };
            }
        };
        EOarray resultArray = inputArray.EOmapi(mapperObject);
        MatcherAssert.assertThat(resultArray.EOisEmpty()._getData().toBoolean(), Matchers.equalTo(true));
    }

}