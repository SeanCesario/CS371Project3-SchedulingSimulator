// exponential: takes one parameter, the average value, and returns a random number
// with an exponential distribution around that average
// this example is in C but should work in other languages with little fuss

#include <stdlib.h>
#include <math.h>
double exponential ( double expected ) {
    // inside the LOG you call a random number generator returning a FLOAT between 0 and 1
    // that's natural logarithm being called btw
    double nice = -expected * log ( (float)rand() / RAND_MAX );
    // in my code, all numbers are in millionths of a second, but I round all random numbers
    // to the nearest 100 microseconds (chop off the last two digits)
    // here's a simple way to do that with a float or double:
    nice *= 10000;
    nice = (int) nice;
    nice /= 10000;
    return ( nice );

}
