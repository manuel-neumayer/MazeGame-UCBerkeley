package byow.Core;

import java.util.Random;

public class RandomWrapper {
    private static Random RANDOM;
    private static long SEED;
    private static int CALLCOUNT;

    public static void setup() {
        long seed = (long) (Math.random() * 10000);
        setup(seed);
    }

    public static void setup(long seed) {
        SEED = seed;
        RANDOM = new Random(seed);
        CALLCOUNT = 0;
        System.out.println("Seed: " + SEED);
    }

    public static void setup(long seed, int callCount) {
        setup(seed);
        for (int i = 0; i < callCount; i++) {
            nextDouble();
        }
    }

    public static double nextDouble() {
        CALLCOUNT++;
        return RANDOM.nextDouble();
    }

    public static int callCount() {
        return CALLCOUNT;
    }

    public static Object[] shuffle(Object[] array) {
        int numberOfShuffles = (int) (array.length * (5 + 15 * nextDouble()));
        for (int i = 0; i < numberOfShuffles; i++) {
            int swap1 = (int) (array.length * nextDouble());
            int swap2 = (int) (array.length * nextDouble());
            Object swapped = array[swap2];
            array[swap2] = array[swap1];
            array[swap1] = swapped;
        }
        return array;
    }

    public static int[] shuffle(int[] array) {
        int numberOfShuffles = (int) (array.length * (5 + 15 * nextDouble()));
        for (int i = 0; i < numberOfShuffles; i++) {
            int swap1 = (int) (array.length * nextDouble());
            int swap2 = (int) (array.length * nextDouble());
            int swapped = array[swap2];
            array[swap2] = array[swap1];
            array[swap1] = swapped;
        }
        return array;
    }
}
