// exponential-random: takes one parameter, the average value, and returns a random number
// with an exponential distribution around that average
// this example is in Java but should work in other languages with little translation

double exponential-random ( double expected ) {
    // take the natural log of (a random FLOAT between 0 and 1)
    // then multiply by the negative of the expected ("average") value passed in
    double nice = -expected * Math.log ( Math.random() );
    // in my code, all numbers are in millionths of a second, but I round all random numbers
    // to the nearest 100 microseconds (chop off the last two digits)
    // here's a simple way to do that with a float or double:
    nice *= 10000;
    nice = (int) nice;
    nice /= 10000;
    return ( nice );

}
