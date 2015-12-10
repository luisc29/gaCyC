/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga;

import java.util.Random;

/**
 *
 */
public class Individual implements Comparable {
    public String configuration;
    public String result;
    public int maxMatches;
    public double fitness;
    public int position;
    
    public Individual(int length, Random random) {
        
        StringBuilder builder = new StringBuilder(length);
        
        for (int i = 0; i < length; i++) {
            builder.append(random.nextDouble() < 0.5 ? '0' : '1');
        }

        this.configuration = builder.toString();
        this.result = "";
        this.maxMatches = 0;
        this.fitness = 0.0;
        this.position = -1;
    }
    
    public Individual(Individual o) {
        this.configuration = o.configuration;
        this.result = o.result;
        this.maxMatches = o.maxMatches;
        this.fitness = o.fitness;
        this.position = o.position;
    }
    
    public Individual(Individual parent1, Individual parent2, int crossPoint) {
        
        int length = parent1.configuration.length();
        
        StringBuilder builder = new StringBuilder(parent1.configuration);
        
        for (int i = crossPoint + 1; i < length; i++) {
            builder.setCharAt(i, parent2.configuration.charAt(i));
        }
        
        this.configuration = builder.toString();
        this.result = "";
        this.maxMatches = 0;
        this.fitness = 0.0;
    }

    @Override
    public int compareTo(Object o) {
        Individual other = (Individual) o;
        
        if (this.maxMatches > other.maxMatches) {
            return -1;
        }
        else if (this.maxMatches < other.maxMatches) {
            return 1;
        }
        else if (this.fitness > other.fitness) {
            return -1;
        }
        else if (this.fitness < other.fitness) {
            return 1;
        }
        return 0;
    }
}
