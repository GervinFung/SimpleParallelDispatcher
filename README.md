Write a multithreaded program to simulate the operations of Sorter, Dispatcher A and
Dispatcher B in the scenario based on the following guidelines:

Sorter, Dispatcher A and Dispatcher B are handled by sperate thread.
An additional thread is used to simulate the arrival of item. This thread will generate an item
randomly (item A or B) at a random interval. (Use an appropriate interval range based on your
system so that the program will not execute too fast and you cannot monitor the output)
Each storage bag can store 10 items and there is unlimited supply of storage bag.
Sorter will wait for the arrival of item and then put it in to the storage bag accordingly. Item
will arrive at random interval.
Dispatcher will wait until the bag is full before it moves it to the conveyor. It will then wait for
the next bag to be loaded.
Each thread should print a message to indicate its current operation. Examples:
o Sorter may print messages such as

Sorter: To bag B
Sorter: waiting item...
Sorter: To bag A (9)
Sorter: waiting item...
Sorter: To bag A (10)
Sorter: Bag A full
Sorter: waiting item...


Thread that simulates arrival of item may print messages such as
Arrival: Item A
Arrival: Item A
Arrival: Item B

Note: the output from each thread may interleave.

1. All threads should run iteratively.
2. The program should be properly commented and meaningful name should be used for any
variable, function or method.
