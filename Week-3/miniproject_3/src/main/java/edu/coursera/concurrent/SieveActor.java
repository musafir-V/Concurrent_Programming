package edu.coursera.concurrent;

import edu.rice.pcdp.Actor;

import static edu.rice.pcdp.PCDP.*;

/**
 * An actor-based implementation of the Sieve of Eratosthenes.
 *
 * TODO Fill in the empty SieveActorActor actor class below and use it from
 * countPrimes to determin the number of primes <= limit.
 */
public final class SieveActor extends Sieve {
    /**
     * {@inheritDoc}
     *
     * TODO Use the SieveActorActor class to calculate the number of primes <=
     * limit in parallel. You might consider how you can model the Sieve of
     * Eratosthenes as a pipeline of actors, each corresponding to a single
     * prime number.
     */
    @Override
    public int countPrimes(final int limit) {
        final SieveActorActor seiveActor = new SieveActorActor();
        finish(()->{
            for(int i=3;i<=limit;i+=2) {
                seiveActor.send(i);
            }
            seiveActor.send(0);
        });
        int totalPrime = 0;
        SieveActorActor iter = seiveActor;
        while(iter!=null) {
            totalPrime+=iter.getLocalPrimes();
            iter=iter.nextActor;
        }
        return totalPrime;
        // throw new UnsupportedOperationException();
    }

    /**
     * An actor class that helps implement the Sieve of Eratosthenes in
     * parallel.
     */
    public static final class SieveActorActor extends Actor {
        /**
         * Process a single message sent to this actor.
         *
         * TODO complete this method.
         *
         * @param msg Received message
         */
        static final int MAX = 1000;
        private SieveActorActor nextActor = null;
        private int[] localPrimes=new int[MAX];
        private int numLocalPrimes = 0;
        public int getLocalPrimes() {
            return this.numLocalPrimes;
        }
        @Override
        public void process(final Object msg) {
            final int candidate = (Integer) msg;
            if(candidate<=0) {
                if(nextActor != null) {
                    nextActor.send(msg);
                }
                return ;
            } 

            if(!this.isLocalPrime(candidate)) {
                return ;
            }

            if(numLocalPrimes < SieveActorActor.MAX) {
                this.localPrimes[this.numLocalPrimes++] = candidate;
                return ;
            }
            if(nextActor == null) {
                nextActor = new SieveActorActor();
            }
            nextActor.send(msg);
            // throw new UnsupportedOperationException();
        }
        boolean isLocalPrime(int candidate) {
            for(int iprime=0;iprime<numLocalPrimes;iprime++) {
                if(candidate % localPrimes[iprime] == 0)
                    return false;
                }
            return true;
        }
    }
}
