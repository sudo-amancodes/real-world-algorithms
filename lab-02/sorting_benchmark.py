from bridges import *
from bridges.sorting_benchmark import *
import random, time, os

linear_search_ops = []
binary_search_ops = []

def linear_search(arr, target):
    ops = 0
    for i in range(len(arr)):
        ops += 1
        if arr[i] == target:
            return i, ops
    return -1, ops

def binary_search(arr, target):
    ops = 0
    low, high = 0, len(arr) - 1
    while low <= high:
        ops += 1
        mid = (low + high) // 2
        if arr[mid] < target:
            low = mid + 1
        elif arr[mid] > target:
            high = mid - 1
        else:
            return mid, ops
    return -1, ops

def do_linear_search(my_list):
    target = [random.choice(my_list) for _ in range(100)]
    average = 0
    for i in range(100):
        _, ops = linear_search(my_list, target[i])
        average += ops
    average /= 100
    linear_search_ops.append(round(average))

    
def do_binary_search(my_list):
    target = [random.choice(my_list) for _ in range(100)]
    average = 0
    for i in range(100):
        _, ops = binary_search(my_list, target[i])
        average += ops

    average /= 100
    binary_search_ops.append(round(average))

    
    
def main():
    # create the Bridges object, set credentials
    # command line args provide credentials and server to test on
    args = sys.argv[1:]
    bridges = Bridges(5, "amanw", os.getenv("API_KEY"))
    if len(args) > 3:
        bridges.connector.set_server(args[3])

    bridges.set_title("Sorting Benchmark")
    bridges.set_description("Plot the performance of sorting algorithms using Bridges Line Chart.")

    plot = LineChart()
    plot.title = "Run Times: Linear vs Binary Search" 

    bench = SortingBenchmark(plot)

    # Set the data series for the plot
    bench.generator = "inorder"
    bench.linear_range(100, 10_000_000, 10)

    bench.time_cap = 1000*30  

    bench.run("Linear Search", do_linear_search)
    bench.run("Binary Search", do_binary_search)

    bridges.set_data_structure(plot)
    bridges.visualize()

    plot.title = "Operation Counts: Linear vs Binary Search" 

    plot.set_y_data("Linear Search", linear_search_ops)
    plot.set_y_data("Binary Search", binary_search_ops)

    bridges.set_data_structure(plot)
    bridges.visualize()

    plot_log_binary = LineChart()

    plot_log_binary.title = "Operation Counts: Binary Search (Log Profile)" 

    plot_log_binary.set_data_series("Binary Search Op Count", plot.get_x_data("Binary Search"), binary_search_ops)

    bridges.set_data_structure(plot_log_binary)
    bridges.visualize()
    
if __name__ == "__main__":
    main()
