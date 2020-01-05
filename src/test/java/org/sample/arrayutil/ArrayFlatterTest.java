package org.sample.arrayutil;

import static java.util.Arrays.asList;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Unit test for class: {@link ArrayFlatter}.
 */
@RunWith(Parameterized.class)
public class ArrayFlatterTest {
    private final int[][] input;
    private final int[] expected;

    public ArrayFlatterTest(final int[][] input, final int[] expected) {
        this.input = input;
        this.expected = expected;
    }

    @Test
    public void testFlatArrayWorksAsExpected() {
        assertArrayEquals(expected, ArrayFlatter.flatArray(input));
    }

    @Parameterized.Parameters
    public static Iterable<Object[]> data() {
        return asList(
            new Object[][]{
                    {new int[0][0], new int[0]},
                    {new int[][]{{1, 2, 6}, {3}, {44, 511}}, new int[] {1, 2, 6, 3, 44, 511}}
            });
    }
}