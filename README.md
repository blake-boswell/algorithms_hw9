# Algorithms HW 9
## Problem Statement
In our study of history at the University of Arkansas, we noticed that before
the money or currency was introduced, barter systems were used. For
instance, during a certain time window, one kg (kilogram) of corn can be
exchanged for 0.9 kg of wheat, 0.8 kg of wheat can be exchanged for 1.2 kg
millet (0.9 kg of wheat can be exchanged for 1.35 kg of millet), one kg millet
can be exchanged for 1.5 kg of sorghum, and one kg of sorghum can be exchanged
for 0.5 kg of corn. Observe that in this example, through a sequence
of transactions, one kg corn may be exchanged for 1.0125 kg of corn! That
is, 1 kg of corn gets 0.9 kg of wheat, which gets 1.35 kg of millet, which gets
2.025 kg of sorghum, which gets 1.0125 kg of corn. We notice the possibility
that in a barter system one may be able to generate a profit by simply going
through a sequence of exchanges! A barter system with this phenomenon is
called inefficient. Note that these conversion values may change over time
and in this example, not all the conversion values between agriculture products
are shown and not all the products are shown (for example, rice and barley).

As a student of algorithms, we would like to abstract a barter system as
follows. We use integer 1 to n to name the products of the system. If x kg
of product i can be exchanged for y kg of product j, we will have an entry
i j x y. If no market for or no one wants to exchange from product i to
product j then no such entry exists in the system. As we have learned many
algorithms and gained skills of develop them, we would like to challenge
ourselves to develop an efficient algorithm to determine if a given barter
system is inefficient or not. If the system is inefficient, we need to give a
sequence of exchanges that demonstrates a profit.
Argue the correctness of your algorithm, analyze the running time of your
algorithm for barter system efficiency analysis. Please do the same for the
algorithm that discovers a sequence of exchanges that results a profit if such
a sequence exists.

## I/O

Input file format for a barter system

n :n is the number of products and products are 1 to n
i j x y :i and j are products and x y kg floating point value
... :each unique i and j appear at most once
... :the quadruples end by EOF
1

Output file format for a barter system analysis

yes or no :yes for inefficient system, and no otherwise
i j x y :start of a profit sequence for an inefficient system
j k u v :
... :
p i w z :end of the profit sequence
one kg of i gets x (x > 1) kg of i from the above sequence

## Approach
The key to this program is to create an algorithm that can detect all simple cycles in a graph.
1. Search for cycle
2. Check cycle for inefficient profit
3. Write result

## Implementation
I implemented a backtracking approach that runs in O(V!) when there is a strongly connected graph with no inefficient cycles.
With more time, I could make the program more efficient either by pruning or coming up with a new algorithm to detect all simple cycles in a directed graph.