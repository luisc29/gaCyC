/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class GA {

    static String inputFilename = "input.txt";
    static int populationSize = 100;
    static int maxTransitions = 2000;
    static int maxGenerations = 200;
    static int tapeLength = 2000;
    static double pc = 0.95; // Crossover probability
    static double pm = 0.05; // Mutation probability
    static int individualLength = 1024;
    static String target;
    static HashSet<String> substrings;
    static double weights[];
    static String emptyTape;
    static long seed = 54321;
    static int fitnessFunction = 1;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        String input = "";
        do{
            try {
                run();
                System.out.println("Desea ejecutar de nuevo? \n (S)i / (N)o");
                
                input = console.readLine();
            } catch (IOException ex) {
                Logger.getLogger(GA.class.getName()).log(Level.SEVERE, null, ex);
            }
        }while(input.equalsIgnoreCase("S"));
             
    }
    
    public static void run(){
        // TODO code application logic here
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        
        try {
            loadConfigs();
            setup(console);
            
            Random random = new Random(seed);
             
            System.out.println("\nIniciando ejecución...\n");
             
            // Lee archivo de entrada y convierte a cadena binaria
            target = getTarget(inputFilename);
            
            System.out.println("Cadena objetivo: " + target + "\n");
            
            if (fitnessFunction == 2) {
                // Construye conjunto hash de las subcadenas contenidas en target (con longitud mayor a 1)
                substrings = buildSubstringHashSet(target);
                // Calcula pesos para función de fitness
                weights = calculateWeights();
            }
            
            // Construye cinta de ceros
            emptyTape = "";
            for (int i = 0; i < tapeLength; i++) {
                emptyTape += '0';
            }
            
            // 4) Construye población inicial
            Individual[] population = new Individual[populationSize + 1];
            for (int i = 0; i <= populationSize; i++) {
                population[i] = new Individual(individualLength, random);
                population[i].result = 
                        TM.NewTape(population[i].configuration, emptyTape, maxTransitions, tapeLength / 2);
                evaluateIndividual(population[i]);
            }
            
            // Ordena población (de mayor a menor por número matches y fitness)
            Arrays.sort(population);
            
            //printPopulation(population);
            
            // Ejecución de algoritmo genético
            
            int g = 0;
            double average;
            
            while (g < maxGenerations) {
                
                /*                
                System.out.println("Generación " + g + ":\t" + population[0].maxMatches + " matches (" + 
                        (100 * population[0].maxMatches / (double)target.length()) + "%) con aptitud " + population[0].fitness + ".");
                */
                
                System.out.println("Generacion " + g + ":\t" + population[0].maxMatches + " matches (" + 
                        (100 * population[0].maxMatches / (double)target.length()) + "%).");
                
                
                
                
                //System.out.println(average + "," + population[0].fitness);
                
                adjustFitness(population);
                sigmaTruncation(population);                
                
                // Calcula suma de fitness
                double sum = 0.0;
                for (int i = 0; i <= populationSize; i++) {
                    sum += population[i].fitness;
                }
                                
                // Generación de la nueva población
                Individual newPopulation[] = new Individual[populationSize + 1];
                for (int i = 0; i < populationSize; i+= 2) {
                    double p;
                    // Selección por ruleta
                    int parentIndex1 = -1;
                    p = random.nextDouble() * sum;
                    for (int j = 0; j <= populationSize; j++) {
                        p -= population[j].fitness;
                        if (p <= 0.0) {
                            parentIndex1 = j;
                            break;
                        }
                    }
                    int parentIndex2 = -1;
                    p = random.nextDouble() * sum;
                    for (int j = 0; j <= populationSize; j++) {
                        p -= population[j].fitness;
                        if (p <= 0.0) {
                            parentIndex2 = j;
                            break;
                        }
                    }

                    // Cruzamiento 
                    if (random.nextDouble() <= pc) {
                        int crossoverPoint = random.nextInt(individualLength);
                        newPopulation[i] = new Individual(population[parentIndex1], population[parentIndex2], crossoverPoint);
                        newPopulation[i + 1] = new Individual(population[parentIndex2], population[parentIndex1], crossoverPoint);
                    }
                    else {
                        newPopulation[i] = new Individual(population[parentIndex1]);
                        newPopulation[i + 1] = new Individual(population[parentIndex2]);
                    }

                    // Mutación
                    StringBuilder builder1 = new StringBuilder(newPopulation[i].configuration);
                    StringBuilder builder2 = new StringBuilder(newPopulation[i + 1].configuration);
                    for (int j = 0; j < individualLength; j++) {
                        
                        if (random.nextDouble() <= pm) { 
                            builder1.setCharAt(j, builder1.charAt(i) == '0' ? '1' : '0');
                        }
                        if (random.nextDouble() <= pm) {                            
                            builder2.setCharAt(j, builder2.charAt(i) == '0' ? '1' : '0');
                        }
                    }
                    
                    newPopulation[i].configuration = builder1.toString();
                    newPopulation[i + 1].configuration = builder2.toString();
                    
                }
                
                // Elitismo (se conserva el mejor individuo de la generación anterior)
                newPopulation[populationSize] = new Individual(population[0]);
                
                for (int i = 0; i <= populationSize; i++) {
                    newPopulation[i].result = 
                            TM.NewTape(newPopulation[i].configuration, emptyTape, maxTransitions, tapeLength / 2);
                    evaluateIndividual(newPopulation[i]);
                }
                
                Arrays.sort(newPopulation);
                
                // Copia nueva población a población original
                for (int i = 0; i <= populationSize; i++) {
                    population[i] = new Individual(newPopulation[i]);
                }
                
                //printPopulation(population);
                
                g++;
            }   
            
            
            
            System.out.println("\nResultado:\t" + population[0].maxMatches + " matches (" + 
                    (100 * population[0].maxMatches / (double)target.length()) + "%) con aptitud " + population[0].fitness + 
                    " despues de " + g + " generaciones.");
            
            int minStates = 64, temp;
            int maxMatches = population[0].maxMatches;
            int bestCandidate = 0;
            for (int i = 0; i <= populationSize; i++) {
                if (population[i].maxMatches == maxMatches) {
                    temp = TM.numberOfStates(population[i].configuration, emptyTape, maxTransitions, emptyTape.length() / 2);
                    if (temp < minStates) {
                        minStates = temp;
                        bestCandidate = i;
                    }
                }
                else {
                    break;
                }
            }
            
            System.out.println("Numero de estados: " + minStates);
            System.out.println("Complejidad de Kolmogorov: " + minStates * 16);
            System.out.println(target);
            System.out.println(population[bestCandidate].result.substring(
                    population[bestCandidate].position, population[bestCandidate].position + target.length()));
                    
            
            
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }   
    }
    
    public static void setup(BufferedReader console) throws IOException{
        
        printParams();
        
        System.out.println("¿Desea modificar algún parametro? \n (S)i / (N)o");
        String input = console.readLine();
        
        if (input.equalsIgnoreCase("S")) {
            String decision = "S";
            do{
                System.out.println("¿Qué parametro de configuración desea cambiar?:");
                int num = Integer.parseInt(console.readLine());
                System.out.println("¿Cual es el nuevo valor?");
                String val = console.readLine();
                switch(num){
                    case 1:
                        inputFilename = val;
                        break;
                    case 2:
                        populationSize = Integer.parseInt(val);
                        break;
                    case 3:
                        maxGenerations = Integer.parseInt(val);
                        break;
                    case 4:
                        maxTransitions = Integer.parseInt(val);
                        break;
                    case 5:
                        tapeLength = Integer.parseInt(val);
                        break;
                    case 6:
                        pc = Double.parseDouble(val);
                        break;
                    case 7:
                        pm = Double.parseDouble(val);
                        break;
                    case 8:
                        seed = Long.parseLong(val);
                        break;
                    case 9:
                        fitnessFunction = Integer.parseInt(val);
                        break;
                }
                printParams();
                System.out.println("¿Desea modificar algún parametro? \n (S)i / (N)o");
                decision = console.readLine();
                
            } 
            while(decision.equalsIgnoreCase("S"));
            
            if (populationSize % 2 == 1) {
                populationSize++;
            }
            
            saveConfigs();
        }
    }
    
    public static void printParams(){
        System.out.println("*********   Estimacion de la Complejidad de Kolmogorov mediante Algoritmos Geneticos   *********     \n");
        System.out.println("-- Parametros del programa --");
        System.out.print("1. Nombre del archivo de entrada: ");
        System.out.println(inputFilename);
        System.out.print("2. Tamaño de la poblacion: ");
        System.out.println(populationSize);
        System.out.print("3. Maximo numero de generaciones: ");
        System.out.println(maxGenerations);
        System.out.print("4. Numero maximo de transiciones: ");
        System.out.println(maxTransitions);
        System.out.print("5. Tamaño de la cinta: ");
        System.out.println(tapeLength);
        System.out.print("6. Probabilidad de cruza: ");
        System.out.println(pc);
        System.out.print("7. Probabilidad de mutacion: ");
        System.out.println(pm);
        System.out.print("8. Semilla para generación de numeros aleatorios: ");
        System.out.println(seed);
        System.out.print("9. Funcion de aptitud [1.Hamming 2.Suma Ponderada]: ");
        System.out.println(fitnessFunction);
    }
    
    public static String getTarget(String inputFilename) throws IOException {
       
        byte[] bytes = Files.readAllBytes(Paths.get(inputFilename));
        
        String temp = "";
        
        for (byte b : bytes)
        {
            int val = b;
            for (int i = 0; i < 8; i++)
            {
                temp += ((val & 128) == 0 ? '0' : '1');
                val <<= 1;
            }
        }

        return temp;
    }
    
    public static HashSet<String> buildSubstringHashSet(String target) {
        HashSet<String> hashSet = new HashSet<>();
        int length = target.length();
        for (int i = 0; i < length; i++) {
            for (int j = i; j < length; j++)
            {
                String temp = target.substring(i, j + 1);
                hashSet.add(temp);
            }
        }
          
        return hashSet;
    }

    public static void evaluateIndividual(Individual individual) {
        
        if (fitnessFunction == 1) {
            hamming(individual);
        }
        else if (fitnessFunction == 2) {
            weightedSum(individual);
        }
       
    }
    
    public static void weightedSum(Individual individual) {
        
        int n = individual.result.length();
        int occurrences[] = new int[target.length() + 1];
        for (int i = 0; i <= target.length(); i++) {
            occurrences[i] = 0;
        }
          
        individual.maxMatches = 0;
        for (int i = 0; i < n; i++) { 
            for (int j = Math.min(target.length(), n - i); j > 0; j--) {
                String temp = individual.result.substring(i, i + j);
                if (substrings.contains(temp)) {
                    occurrences[j]++;
                    if (j > individual.maxMatches) {
                        individual.maxMatches = j;
                    }
                    break;
                }
            }
        }
        
        individual.fitness = 0.0;
        for (int i = 0; i <= target.length(); i++) {
            individual.fitness += occurrences[i] * weights[i];
        } 
        
    }
    
    public static void hamming(Individual individual) {
        individual.fitness = -target.length();
        individual.maxMatches = 0;
        if (individual.result.length() > 0) {
            /*
            for (int p = individual.result.length() / 2 - target.length(); 
                    p <= individual.result.length() / 2 + target.length();
                    p++) {
            */
            for (int p = 0; p < individual.result.length() - target.length(); p++) {
                float tempFitness = -target.length();
                int tempMaxMatches = 0;
                for (int i = 0, j = p; i < target.length(); i++, j++) {
                    if (target.charAt(i) == individual.result.charAt(j)) {
                        tempMaxMatches += 1;
                        tempFitness += 1.0;
                    }
                }
                if (individual.fitness < tempFitness) {
                    individual.fitness = tempFitness;
                    individual.maxMatches = tempMaxMatches;
                    individual.position = p;
                }
            }
        }
    }
    
    public static double[] calculateWeights() {
        double phi[] = new double[target.length() + 1];

        /*
        phi[0] = 0.0;
        phi[1] = 0.0;
        phi[2] = 1e-20;
        for (int i = 3; i <= target.length(); i++) {
            phi[i] = (tapeLength - i + 3) * phi[i - 1];
        }
        */
        
        phi[0] = 0.0;
        double temp;
        for (int i = 1; i <= target.length(); i++) {
            temp = Math.exp(target.length() / 2.0 - i);
            phi[i] = 1.0 / (1.0 + temp);
        }
        
        /*
        for (int i = 0; i <= target.length(); i++) {
            System.out.println(phi[i]);
        }*/
        
        return phi;
    }
    
    public static void adjustFitness(Individual population[]) {
        
        int n = population.length;

        double min = Double.MAX_VALUE;
        double mean = 0.0;
        for (int i = 0; i < n; i++) {
            mean += Math.abs(population[i].fitness);
            if (population[i].fitness < min) {
                min = population[i].fitness;
            }
        }
        mean /= n;
        
        for (int i = 0; i < n; i++) {
            population[i].fitness += Math.abs(min) + mean;
        }
        
    }
    
    public static void sigmaTruncation(Individual population[]) {
        
        int n = population.length;
        
        double mean = 0.0;
        
        for (int i = 0; i < n; i++) {
            mean += population[i].fitness;
        }
        mean /= n;
        
        double sigma = 0.0;
        
        for (int i = 0; i < n; i++) {
            sigma += Math.pow(population[i].fitness - mean, 2.0);
        }
        sigma = Math.sqrt(sigma);
        
        if (sigma <= 1e-15) {
            // All individuals have the "same fitness"
            for (int i = 0; i < n; i++) {
                population[i].fitness = 1.0;
            }
        }
        else {
            for (int i = 0; i < n; i++) {
                population[i].fitness = Math.min(1.5,                         
                        1 + ((population[i].fitness - mean) / (2 * sigma)));
            }
        }
    }

    public static void printPopulation(Individual population[]) {
        System.out.println("Población:");
        int n = population.length;
        for (int i = 0; i < n; i++) {
            System.out.println(population[i].configuration.substring(0, 20) + 
                    "...\t" + population[i].maxMatches + "\t" + population[i].fitness);
        }
        System.out.println("\n");
    }
    
    public static void loadConfigs(){
        String file = "config.txt";
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            inputFilename = br.readLine().split("=")[1];
            populationSize = Integer.parseInt(br.readLine().split("=")[1]);
            maxTransitions = Integer.parseInt(br.readLine().split("=")[1]);
            maxGenerations = Integer.parseInt(br.readLine().split("=")[1]);
            tapeLength = Integer.parseInt(br.readLine().split("=")[1]);
            pc = Double.parseDouble(br.readLine().split("=")[1]);
            pm = Double.parseDouble(br.readLine().split("=")[1]);
            individualLength = Integer.parseInt(br.readLine().split("=")[1]);
            seed = Integer.parseInt(br.readLine().split("=")[1]);
            fitnessFunction = Integer.parseInt(br.readLine().split("=")[1]);
            
        }catch(Exception e){
                    
        }

    }
    
    public static void saveConfigs(){
        String fileName = "config.txt";

        try {
            FileWriter fileWriter = new FileWriter(fileName);

            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            bufferedWriter.write("inputFilename=" + inputFilename + "\n");
            bufferedWriter.newLine();
            bufferedWriter.write("populationSize=" + populationSize + "\n");
            bufferedWriter.newLine();
            bufferedWriter.write("maxTransations=" + maxTransitions + "\n");
            bufferedWriter.newLine();
            bufferedWriter.write("maxGenerations=" + maxGenerations + "\n");
            bufferedWriter.newLine();
            bufferedWriter.write("tapeLength=" + tapeLength + "\n");
            bufferedWriter.newLine();
            bufferedWriter.write("pc=" + pc + "\n");
            bufferedWriter.newLine();
            bufferedWriter.write("pm=" + pm + "\n");
            bufferedWriter.newLine();
            bufferedWriter.write("individualLength=" + individualLength + "\n");
            bufferedWriter.newLine();
            bufferedWriter.write("seed=" + seed + "\n");
            bufferedWriter.newLine();
            bufferedWriter.write("fitnessFunction=" + fitnessFunction + "\n");
            bufferedWriter.newLine();
           
            bufferedWriter.close();
        }
        catch(Exception ex) {
            
        }
    }

}
