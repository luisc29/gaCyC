/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga;

import java.util.Arrays;

/**
 *
 * @author Luis Carlos
 */
public class Individual {
    double fitness;
    int[] tm;
    String tms;
    
    public Individual(double fitness, int[] tm){
        this.fitness = fitness;
        this.tm = tm;
        this.tms = Arrays.toString(tm);
    }
    
    public Individual(){
        this.fitness = 0;
    }
    
    
}
