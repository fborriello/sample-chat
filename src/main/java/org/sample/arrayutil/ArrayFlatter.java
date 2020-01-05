package org.sample.arrayutil;

import java.util.Arrays;

public class ArrayFlatter {
    public static int[] flatArray(final int[][] array) {
        return Arrays.stream(array)
                .flatMapToInt(Arrays::stream)
                .toArray();
    }
}
