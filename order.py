#!/bin/python3

import math
import os
import random
import re
import sys

'''
The goal is reordering the numbers in the list so that they match their
index in the list - 1 (i.e. ordering the list with the minimmum number 
of swaps) (as counting starts at 0) (this adjustment is seen in the if
 condition: "if number selected is not equal to its index...). 

The chain of execution pursued here is moving one number (the first one 
in my example) to its correct position (i.e. the index of the same value
as the number) and moving the number that it replaces to its correct 
position and so on.

By using a helper value (temp), items are swapped to their correct
positions (the number at index i is swapped with the number at index:
:number being swapped minus one due to count starting at 0, i.e. the 
number sitting in its index).

Thanks to Python's tuples, a swap can be performed elegantly, in one line.

Every swap increments the 'swaps' value.

This way, a time complexity of O(n) (list of length n is parsed once) and
a space complexity of O(1) (the temp value) is achieved.
'''
def minimumSwaps(arr):
    swaps = 0
    i = 0
    while(i < len(arr)-1):
        if arr[i] != i+1:
            temp = arr[i]
            arr[i], arr[temp-1] = arr[temp-1], arr[i]
            swaps += 1
        else:
            i += 1
    return swaps

print (minimumSwaps([4, 3, 1, 2])) # should return 3 
print (minimumSwaps([1, 3, 5, 2, 4, 6, 7])) #should return 3 (WHY? Will upload walkthrough)
print (minimumSwaps([2, 31, 1, 38, 29, 5, 44, 6, 12, 18, 39, 9, 48, 49, 13, 
                    11, 7, 27, 14, 33, 50, 21, 46, 23, 15, 26, 8, 47, 40, 
                    3, 32, 22, 34, 42, 16, 41, 24, 10, 4, 28, 36, 30, 37, 
                    35, 20, 17, 45, 43, 25, 19])) #should return 46