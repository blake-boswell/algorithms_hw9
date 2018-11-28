#include <iostream>
#include <fstream>
#include <vector>
#define WHITE 0
#define GREY 1
#define BLACK 2

using namespace std;

struct Node {
    int id;
    int color;
    float exchangeRate;
    Node() {
        this->id = -1;
        this->exchangeRate = -1.0;
        this->color = WHITE;
    }
    Node(int id, float exchangeRate) {
        this->id = id;
        this->exchangeRate = exchangeRate;
        this->color = WHITE;
    }
    void set(int id, float exchangeRate) {
        this->id = id;
        this->exchangeRate = exchangeRate;
        this->color = WHITE;
    }
};

struct Graph {
    int numProducts;
    vector< vector<Node> > adjacencyList;
    Graph(int numProducts) {
        for (int i = 0; i <= numProducts; i++) {
            adjacencyList.push_back(vector<Node>(0));
        }
    }
    void printAdjList() {
        cout << "Printing..." << endl;
        vector< vector<Node> >::iterator row;
        vector<Node>::iterator col;
        int count = 0;
        for (row = adjacencyList.begin(); row != adjacencyList.end(); row++) {
            cout << "[" << count << "] -> ";
            for (col = row->begin(); col != row->end(); col++) {
                cout << "[" << col->id << "] " << col->exchangeRate << " -> ";
            }
            cout << "\n";
            count++;
            
        }
        
    }
    void dfs() {
        vector<Node> stack;
        vector<float> profits;
        stack.push_back(adjacencyList[])
        for (int i = 1; i <= this->numProducts; i++) {
            for (int j = 0; j < adjacencyList[i].size(); j++) {
                stack.push_back(adjacencyList[i][j]);
            }
        }
    }
};



vector<int> findSequence(Graph graph) {
    vector<int> sequence;

    return sequence;
}

int main(int argc, char** argv) {
    // Perform a DFS to find a cycle
    // If cycle is found, check if there is profit

    fstream file;
    string filename = "input.txt";
    string outFile = "output.txt";
    if (argc > 1) {
        filename = argv[1];
    }
    if (argc > 2) {
        outFile = argv[2];
    }
    file.open(filename.c_str());
    // Read input
    if (file.is_open()) {
        
        int numProducts;
        file >> numProducts;
        Graph graph(numProducts);
        int productA, productB;
        float weightA, weightB;

        while (!file.eof()) {
            cout << "reading\n";
            file >> productA >> productB >> weightA >> weightB;
            float exchangeRate = weightB / weightA;
            Node node(productB, exchangeRate);
            graph.adjacencyList[productA].push_back(node);
        }

        file.close();
        graph.printAdjList();
        vector<int> sequence = findSequence(graph);
        cout << "Output written to " << outFile << endl;

    } else {
        cout << "Could not find file " << filename << ". Please enter the name of an input file as a parameter." << endl;
    }


    return 0;
}