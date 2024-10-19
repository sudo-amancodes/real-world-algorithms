from bridges import *
from bridges.sorting_benchmark import *

insertion_ops = []
bubble_ops = []

def insertionsort(arr):
    global insertion_ops
    ops = 0
    n = len(arr)  

    for i in range(1, n):  
        key = arr[i]  
        j = i-1
        while j >= 0: 
            ops += 1
            if key < arr[j]: 
                arr[j+1] = arr[j]  
                j -= 1
            else:
                break
            
        arr[j+1] = key  

    insertion_ops.append(ops)


def bubblesort(arr):
    n = len(arr)
    global bubble_ops 
    ops = 0

    for i in range(n - 1):
        for j in range(n - 1 - i):
            ops += 1
            if arr[j + 1] < arr[j]:
                arr[j], arr[j + 1] = arr[j + 1], arr[j]
                

    bubble_ops.append(ops)



def main():

    # create the Bridges object, set credentials
    # command line args provide credentials and server to test on
    args = sys.argv[1:]
    bridges = Bridges(6, "amanw", os.getenv("API_KEY"))
    if len(args) > 3:
        bridges.connector.set_server(args[3])

    bridges.set_title("Sorting Benchmark")
    bridges.set_description("Plot the performance of sorting algorithms using Bridges Line Chart.")

    plot = LineChart()
    plot.title = "Sort Runtime Comparison"
    bench = SortingBenchmark(plot)

    bench.generator = "random"

    bench.linear_range(10_000, 50_000, 12)
    bench.run("Bubble Sort (random)", bubblesort)
    bench.run("Insertion Sort (random)", insertionsort)

    bridges.set_data_structure(plot)
    bridges.visualize()

    plot2 = LineChart()
    plot2.title = "Operations Comparison"

    plot2.set_data_series("Bubble Sort Operations (random)", plot.get_x_data("Bubble Sort (random)")  , bubble_ops)
    plot2.set_data_series("Insertion Sort Operations (random)", plot.get_x_data("Insertion Sort (random)")  , insertion_ops)

    bridges.set_data_structure(plot2)
    bridges.visualize()


if __name__ == "__main__":
    main()
