import bridges.base.LineChart;
import bridges.benchmark.SortingBenchmark;
import bridges.connect.Bridges;
import bridges.validation.RateLimitException;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;

public class App {
    static Consumer<int[]> mergesort = A -> {
        mergesort(A);
    };

    static void mergesort(int[] A) {
        if (A.length < 100) {
            insertionSort(A);
        } else {
            if (A.length > 1) {
                int[] B = Arrays.copyOfRange(A, 0, A.length / 2);
                int[] C = Arrays.copyOfRange(A, A.length / 2, A.length);
                mergesort(B);
                mergesort(C);
                merge(B, C, A);
            }
        }
    };

    public static void merge(int B[], int C[], int A[]) {
        int i = 0;
        int j = 0;
        int k = 0;
        while (i < B.length && j < C.length) {
            if (B[i] <= C[j]) {
                A[k] = B[i];
                i++;
            } else {
                A[k] = C[j];
                j++;
            }
            k++;
        }
        if (i == B.length) {
            for (int l = j; l < C.length; l++) {
                A[k] = C[l];
                k++;
            }
        } else {
            for (int l = i; l < B.length; l++) {
                A[k] = B[l];
                k++;
            }
        }

    }

    public static void insertionSort(int[] A) {
        for (int i = 1; i < A.length; i++) {
            int v = A[i];
            int j = i - 1;
            while (j >= 0 && A[j] > v) {
                A[j + 1] = A[j];
                j--;
            }
            A[j + 1] = v;
        }
    }

    public static void main(String[] args) throws IOException, RateLimitException, InterruptedException {

        Bridges bridges = new Bridges(7, "amanw", System.getenv("API_KEY"));

        bridges.setTitle("Merge Sort: Run Time, N Log N curve");

        int startSize = 100;
        int endSize = 100_000_000;
        int numSteps = 20;

        LineChart plot = new LineChart();
        plot.setTitle("Sort Runtime");
        SortingBenchmark bench = new SortingBenchmark(plot);
        bench.linearRange(startSize, endSize, numSteps);
        bench.run("Mergesort", mergesort);

        double[] ptsMs = new double[numSteps + 1];
        for (int i = 0; i < numSteps + 1; i++) {
            ptsMs[i] = startSize + i * (endSize - startSize) / (double) (numSteps - 1);
        }
        System.out.println(Arrays.toString(ptsMs));

        double[] nLogNValues = new double[ptsMs.length];

        for (int i = 0; i < ptsMs.length; i++) {
            nLogNValues[i] = ptsMs[i] * Math.log(ptsMs[i]);
        }

        // Calculate maxRuntime
        double maxRuntime = 13000;
        System.out.println(maxRuntime);

        double[] scaledNLogN = new double[nLogNValues.length];

        double maxNLogN = Arrays.stream(nLogNValues, 0, nLogNValues.length).max().getAsDouble();

        for (int i = 0; i < nLogNValues.length; i++) {
            scaledNLogN[i] = (nLogNValues[i] * (maxRuntime / maxNLogN));
        }

        plot.setDataSeries("Merge Sort: Run times, N Log N fn", plot.getXData("Mergesort"), scaledNLogN);
        bridges.setDataStructure(plot);
        bridges.visualize();

    }

}
