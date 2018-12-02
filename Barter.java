import java.util.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * numVertices: stores the number of vertices in the graph
 * rateMap: stores the map from exchange rate (weight2 / weight1) to weight1 weight2 for outputting results
 * exchangeRates: stores the exchange rate from vertex a to b as exchangeRate[a][b] (weightB / weightA)
 * adjacencyList: Representation of directed edges. Used for DFS in search of a cylce using back edge detection
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
     * Finds if there is an inefficiency in our goods exchange graph
     * Inefficiency: When you can trade product A for more of product A, making a profit
     * EX: .5 kg of rice for .75 kg of rice by trading rice for corn for wheat for rice
     * 
     * Running the function results in output written to the out file
     * 
     * DFS that can terminate early if cycle is found
     * O(V + E)
     * 
     * @param outPath
     */
    public void findInefficiency(String outPath) {
        // Perform DFS to look for a cycle
        boolean alreadyVisited[] = new boolean[this.numVertices + 1];
        for (int i = 0; i < this.numVertices + 1; i++) {
            alreadyVisited[i] = false;
        }

        Stack<Integer> stack = new Stack<Integer>();
        for (int i = 1; i < this.numVertices + 1; i++) {
            if (alreadyVisited[i] == false) {
                stack.push(i);
                boolean isInefficient = findCycle(stack, alreadyVisited, outPath);
                if (isInefficient) {
                    // Terminate early
                    return;
                }
            }
        }
        ArrayList<Integer> emptyList = new ArrayList<>();
        double profit = 0.0;
        this.writeResult(emptyList, profit, outPath);

        // If a cycle is found, check if the sequence produces a profit
    }

    public boolean hasBeenVisited(int vertex, boolean alreadyVisited[], Stack<Integer> stack) {
        return (alreadyVisited[vertex] == true || stack.contains(vertex) == true);
    }

    public boolean isBackEdge(int vertex, Stack<Integer> stack) {
        return stack.contains(vertex);
    }

    /**
     * Finds a cycle in the graph && tests if it is inefficient
     * If inefficient it will write the output
     * 
     * @param stack
     * @param alreadyVisited
     * @param outPath
     * @return
     */
    public boolean findCycle(Stack<Integer> stack, boolean alreadyVisited[], String outPath) {
        while (!stack.isEmpty()) {
            boolean searching = true;
            int current = stack.peek();
            Iterator<Integer> it = adjacencyList.get(current).iterator();
            while (it.hasNext() && searching) {
                int vertex = it.next();
                System.out.println("\tCHECKING: " + current + " -> " + vertex);
                // Detect the cycle
                if (isBackEdge(vertex, stack)) {
                    // Vertex already exists in the stack
                    int sequenceStart = stack.indexOf(vertex);
                    stack.push(vertex);
                    // Find if there is a profit
                    System.out.println("Checking for profit...");
                    for (int i = stack.size() - 1; i >= 0; i--) {
                        System.out.println(stack.get(i));
                    }

                    ArrayList<Integer> sequence = new ArrayList<>();
                    double profit = 1.0;

                    for (int i = sequenceStart; i < stack.size() - 1; i++) {
                        sequence.add(stack.get(i));
                        profit *= this.exchangeRates[stack.get(i)][stack.get(i + 1)];
                    }
                    sequence.add(stack.get(stack.size() - 1));

                    System.out.println("Sequence:");
                    for (int i = 0; i < sequence.size(); i++) {
                        System.out.print(sequence.get(i) + " ");
                    }
                    System.out.println();
                    
                    System.out.println("Profit: " + profit);
                    if (profit > 1.0) {
                        this.writeResult(sequence, profit, outPath);
                        return true;
                    }
                    stack.pop();
                }
                if (!hasBeenVisited(vertex, alreadyVisited, stack)) {
                    System.out.println("\tFirst visit for " + vertex);
                    searching = false;
                    stack.push(vertex);
                }
            }
            if (searching) {
                int vertex = stack.pop();
                alreadyVisited[vertex] = true;
            }
            for (int i = stack.size() - 1; i >= 0; i--) {
                System.out.println(stack.get(i));
            }
        }
        return false;
    }

    /**
     * Converts the sequence and profit into a formatted output to be written to the file in the path of outPath
     * 
     * @param sequence
     * @param profit
     * @param outPath
     */
    public void writeResult(ArrayList<Integer> sequence, double profit, String outPath) {
        try {
            PrintWriter writer = new PrintWriter(outPath, "UTF-8");
            if (sequence.size() > 0) {
                writer.println("yes");
                for (int i = 0; i < sequence.size() - 1; i++) {
                    int from = sequence.get(i);
                    int to = sequence.get(i + 1);
                    writer.println(from + " " + to + " " + this.rateMap.get(this.exchangeRates[from][to]));
                }
                int from = sequence.get(sequence.size() - 1);
                int to = sequence.get(0);
                writer.println(from + " " + to + " " + this.exchange[from][to]);
                writer.println("one kg of product " + sequence.get(0) + " gets " + profit + " kg of product " + sequence.get(0) + " from the above sequence.");
            } else {
                writer.println("no");
            }
            writer.close();
            
        } catch (IOException err) {
            err.printStackTrace();
        }
        
        

    }

    // public ArrayList<Integer> DFSUtil(int vertex, ArrayList<Integer> sequence) {

    // }

    // public void DFS() {
    //     ArrayList<Integer> sequence = new ArrayList<>();
    //     for (int i = 0; i < this.numVertices; i++) {
    //         DFSUtil(i, sequence);
    //     }
    // }
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