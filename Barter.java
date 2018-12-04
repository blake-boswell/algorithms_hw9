import java.util.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * numVertices: stores the number of vertices in the graph
 * rateMap: stores the map from exchange rate (weight2 / weight1) to weight1 weight2 for outputting results
 * exchangeRates: stores the exchange rate from vertex a to b as exchangeRate[a][b] (weightB / weightA)
 * adjacencyList: Representation of directed edges. Used for DFS in search of a cycle using back edge detection
 */
class Graph {
    public int numVertices;
    public Map<Double, String> rateMap;
    public String exchange[][];
    public double exchangeRates[][];
    public List< List<Integer> > adjacencyList;
    public Graph(int numVertices) {
        this.numVertices = numVertices;
        this.rateMap = new HashMap<Double, String>();
        this.exchange = new String[numVertices + 1][numVertices + 1];
        this.exchangeRates = new double[numVertices + 1][numVertices + 1];
        for (int i = 0; i <= numVertices; i++) {
            for (int j = 0; j <= numVertices; j++) {
                exchangeRates[i][j] = -1.0;
            }
        }
        adjacencyList = new LinkedList<>();
        for (int i = 0; i <= numVertices; i++) {
            adjacencyList.add(new LinkedList<Integer>());
        }
    }

    /**
     * Prints the adjacency list along with the exchange rates as the weight of each edge
     */
    public void print() {
        for (int i = 1; i <= this.numVertices; i++) {
            System.out.print("[" + i + "] -> ");
            for (int j = 0; j < adjacencyList.get(i).size(); j++) {
                int to = adjacencyList.get(i).get(j);
                System.out.print("[" + to + "] " + this.exchangeRates[i][to] + " -> ");
            }
            System.out.println();
        }
    }

    /**
     * Converts the sequence and profit into a formatted output to be written to the file in the path of outPath
     * 
     * @param sequence : sequence taken
     * @param profit : profit of sequence
     * @param outPath : output file path
     */
    public void writeResult(ArrayList<Integer> sequence, int cycleStart, double profit, String outPath) {
        try {
            PrintWriter writer = new PrintWriter(outPath, "UTF-8");
            if (sequence.size() > 0) {
                writer.println("yes");
                for (int i = cycleStart; i < sequence.size() - 1; i++) {
                    int from = sequence.get(i);
                    int to = sequence.get(i + 1);
                    writer.println(from + " " + to + " " + this.rateMap.get(this.exchangeRates[from][to]));
                }
                // int from = sequence.get(sequence.size() - 1);
                // int to = sequence.get(0);
                // writer.println(from + " " + to + " " + this.exchange[from][to]);
                writer.println("one kg of product " + sequence.get(cycleStart) + " gets " + profit + " kg of product " + sequence.get(cycleStart) + " from the above sequence.");
            } else {
                writer.println("no");
            }
            writer.close();
            
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    /**
     * Returns the profit from a given sequence
     * 
     * @param sequence : sequence taken
     * @return profit of sequence
     */
    public double getProfit(ArrayList<Integer> sequence, int cycleStart) {
        double profit = 1.0;
        for (int i = cycleStart; i < sequence.size() - 1; i++) {
            profit *= this.exchangeRates[sequence.get(i)][sequence.get(i + 1)];
        }
        return profit;
    }

    /**
     * Debugging function for printing the contents of the sequence
     * 
     * @param sequence : sequence taken
     */
    public void printSequence(ArrayList<Integer> sequence) {
        for (int i = 0; i < sequence.size(); i++) {
            System.out.print(sequence.get(i) + " ");
        }
        System.out.println();
    }

    /**
     * Debugging function for printing the contents of the cycle
     * 
     * @param sequence : sequence taken
     */
    public void printCycle(ArrayList<Integer> sequence, int cycleStart) {
        for (int i = cycleStart; i < sequence.size(); i++) {
            System.out.print(sequence.get(i) + " ");
        }
        System.out.println();
    }


    /**
     * Recursive function for finding an inefficiency in a trade model
     * 
     * 1. Explore a new vertex
     * 2. Does the new vertex make a cycle?
     *      yes -> does the cycle make a profit?
     *          yes -> stop searching & write result
     *          no -> return
     *      no -> explore a new adjacent vertex
     * 
     * O(V!) if no profit exist within a strongly connected graph ... :(
     * 
     * @param vertex : new vertex to explore
     * @param sequence : sequence taken
     * @param alreadyVisited : keeps track of all vertices visited (for detached graphs)
     * @param outPath : output file path
     * @return if inefficientCycle was found
     */
    public boolean findInefficientCycle(int vertex, ArrayList<Integer> sequence, boolean alreadyVisited[], String outPath) {
        // DEBUG
        System.out.println("Call with " + vertex);
        printSequence(sequence);

        // Stop if vertex is in the sequence
        alreadyVisited[vertex] = true;
        if (sequence.contains(vertex)) {
            int cycleStart = sequence.indexOf(vertex);
            sequence.add(vertex);
            System.out.print("\tCycle found: \n\t");
            printCycle(sequence, cycleStart);
            double profit = getProfit(sequence, cycleStart);
            System.out.println("\tProfit: " + profit);
            if (profit > 1.0) {
                writeResult(sequence, cycleStart, profit, outPath);
                return true;
            }
            sequence.remove(sequence.size() - 1);
            return false;
        }

        sequence.add(vertex);

        // for each adjacent vertice, findInefficientCycle
        Iterator<Integer> it = adjacencyList.get(vertex).iterator();
        boolean done = false;
        while (it.hasNext() && !done) {
            int next = it.next();
            done = findInefficientCycle(next, sequence, alreadyVisited, outPath);
        }
        sequence.remove(sequence.size() - 1);
        
        return done;
    }

    /**
     * Initializer for recursive findInefficientCycle
     * 
     * O(V) + O(findInefficientCycle) = O(V!)
     * 
     * @param outPath : output file path
     */
    public void findInefficiency(String outPath) {
        ArrayList<Integer> sequence = new ArrayList<>();
        boolean alreadyVisited[] = new boolean[this.numVertices + 1];
        for (int i = 1; i < this.numVertices + 1; i++) {
            alreadyVisited[i] = false;
        }

        boolean cycleFound = false;

        for (int i = 1; i < this.numVertices + 1; i++) {
            if (!alreadyVisited[i]) {
                cycleFound = findInefficientCycle(i, sequence, alreadyVisited, outPath);
                if (cycleFound) {
                    break;
                }
            }
                
        }
        System.out.println("profit: " + cycleFound);

        if (!cycleFound) {
            writeResult(sequence, 0, 0, outPath);
        }
    }
}

public class Barter {
    public static void main(String[] args) throws Exception {
        String inPath = "input.txt";
        String outPath = "output.txt";
        System.out.println(args.length);
        if (args.length > 0) {
            inPath = args[0];
        }
        if (args.length > 1) {
            outPath = args[1];
        }
        File file = new File(inPath);
        Scanner in = new Scanner(file);

        int numVertices = in.nextInt();
        System.out.println("Read in numVertices: " + numVertices);
        Graph graph = new Graph(numVertices);
        while (in.hasNextLine()) {

            // Set up adjacency list
            int from, to;
            from = in.nextInt();
            to = in.nextInt();
            System.out.println("Read in from & to: " + from + ", " + to);
            graph.adjacencyList.get(from).add(to);

            // Set up edge weights
            double fromWeight, toWeight;
            fromWeight = in.nextDouble();
            toWeight = in.nextDouble();
            double exchangeRate = toWeight / fromWeight;
            graph.exchangeRates[from][to] = exchangeRate;

            // Set up edges for result
            graph.rateMap.put(exchangeRate, (Double.toString(fromWeight) + " " + Double.toString(toWeight)));
            graph.exchange[from][to] = Double.toString(fromWeight) + " " + Double.toString(toWeight);
            graph.exchange[to][from] = Double.toString(toWeight) + " " + Double.toString(fromWeight);
        }

        in.close();

        graph.print();
        graph.findInefficiency(outPath);
    }
}