package TP3;

public class TP3 {


/* the file is gonna function like this: input a txt file carte.txt with the following format
a 
b 
c 
d 
e 
f 
g 
h 
---
rue0 : a e 9 ;
rue1 : a c 5 ;
rue2 : a b 7 ;
rue3 : b c 4 ;
rue4 : c d 2 ;
rue5 : b f 10 ;
rue6 : e d 4 ;
rue7 : d f 3 ;
rue8 : e g 3 ;
rue9 : d h 7 ;
rue10 : f h 7 ;
rue11 : g h 4 ;
---
*/

// so in theory i'd just need to read the file, which would go something like this:
// store each line as a node
// encounter "---"
// the edges all have a semicolon at the end, we'll use that to move on to the next line
// start storing the edges, with the format "rue0 : a e 9 ;"
// rue0 is the name (string of chars) 
// a is the starting node 
// and e is the ending node
// this is only important in case we have two paths with the same weight and we need to choose one, 
// we will use the lexicographic order of the nodes, firstly, with the starting node and if need be, the ending node
// the last number is the weight of the edge which is obviously the most important
// and then we encounter another "---" and know to stop reading the file

/* our function will return a txt file arm.txt with the following format
a
b
c
d
e
f
g
h
rue1	a	c	5
rue3	b	c	4
rue4	c	d	2
rue6	d	e	4
rue7	d	f	3
rue8	e	g	3
rue11	g	h	4
---
25
*/

// so we'll just print the nodes in the order they were read
// then we'll print the edges one by one, in a new format, based on our output from Kruskal's algorithm
// to break, a "---" will be printed
// and finally we'll print the total weight of the ARM

// instead of getting into the technicalities of reading and writing files let's just implement a working Kruskal first
// first order of business is declaring all the bullshit cuz this is java

// we'll make a class called edge
// one called node (not even sure if we really need it.. will have to look at the kruskal implementation first)
// I don't think the graph really needs a representation its just a visual thing we can think of

// then we'll implement Kruskal's algorithm to make the ARM (idk english name or abbreviation)







}