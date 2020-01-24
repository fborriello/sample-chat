package org.sample.arrayutil;

import java.util.Arrays;

/**
 * Array flatter class.
 */
public final class ArrayFlatter {
    private ArrayFlatter() {
    }

    /**
     * Makes a bi-dimensional int array flat.
     * @param array a bi-dimensional array
     * @return a one-dimensional array
     */
    public static int[] flatArray(final int[][] array) {
        return Arrays.stream(array)
                .flatMapToInt(Arrays::stream)
                .toArray();
    }
}
