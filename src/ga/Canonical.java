/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Implementation of the canonical genetic algorithm
 */
public class Canonical{
    private int populationSize;
    private int generations;
    private double combinationProb;
    private double mutationProb;
    private int[] targetArray;
    private String target;
    private List<Individual> population;
    private boolean isDone;
    private Random randomizer;
    
    /**
     * 
     * @param g number of generations
     * @param p size of the population
     * @param combProb combination probability
     * @param mutProb mutation probability
     * @param t target string
     * @param isAscii true if target is in ASCII format
     * @param seed seed for the randomizer
     */
    public Canonical(int g, int p, double combProb, double mutProb, String t,
                        boolean isAscii, long seed){
        this.generations = g;
        this.populationSize = p;
        this.combinationProb = combProb;
        this.mutationProb = mutProb;
        this.target = t;
        if(isAscii){
            //convert ascii characters to int array
            
        }else{
            //convert string to int array
        }
        this.population = Utils.createRandomPopulation(populationSize, randomizer);
        randomizer = new Random(seed);
    }
    
    public void evaluate(){
        //TODO fitness function
       
    }
    
    public void select(){
        //TODO sigma truncation
    }
    
    public void crossover(){
        for(int i = 0; i < populationSize/2; i++){
            for(int j = populationSize/2; j < populationSize; j++){
                int prob = randomizer.nextInt(100);
                if(prob <= 100*combinationProb){
                    int[] ind_a = population.get(i).tm;
                    int[] ind_b = population.get(j).tm;
                    int slice = randomizer.nextInt(ind_a.length-1);
                    int[] n1 = Arrays.copyOfRange(ind_a, 0, slice);
                    int[] n2 = Arrays.copyOfRange(ind_b, slice, ind_b.length);
                    int[] n = Utils.concatenateInt(n1, n2);
                    population.add(new Individual(0, n));
                }
            }
        }
    }
    
    public void mutation(){
        for(int i = 0; i < population.size(); i++){
            int[] tm = population.get(i).tm;
            for(int j = 0; j < tm.length; j++){
                int prob = randomizer.nextInt(100);
                if(prob <= 100*mutationProb){
                    if(tm[j] == 0){
                        tm[j] = 1;
                    }else{
                        tm[j] = 0;
                    }
                }
            }
            population.get(i).tm = tm;
        }
    }
    
    //The algorithm can be stopped if an individual contains the target
    public boolean isDone(){
        return isDone;
    }
    
    
    
    @Override
    public Canonical clone(){
        try {
            return (Canonical)super.clone();
        } catch (CloneNotSupportedException ex) {
            return null;
        }
    }
    
}