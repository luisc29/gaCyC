/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Luis Carlos
 */
public class Utils {

    public static int[] concatenateInt(int[] a, int[] b) {
        int aLen = a.length;
        int bLen = b.length;
        int[] c = new int[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }
    
    //Randomly, creates the initial population
    public static List<Individual> createRandomPopulation(int p, Random r){
        List<Individual> population = new ArrayList<>();
        int max = (int) (Math.pow(2, 1008)-1);
        for(int i = 0; i < p; i++){
            int[] tm = new int[max];
            for(int j = 0; j < tm.length; j++){
                int rand = r.nextBoolean() ? 1 : 0;
                tm[j] = rand;
            }
            Individual ind = new Individual(0,tm);
            population.add(ind);
        }
        return population;
    }
}
