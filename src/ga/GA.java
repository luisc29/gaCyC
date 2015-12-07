/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author equipo1cyc
 */
public class GA {
     static String fileName = "input.txt";
     static int numIndividuals = 80;
     static int numTransicions = 1000;
     static int tapeSize = 1000;
     static double probComb = 0.95;
     static double probMut = 0.05;
     static int numGenerations = 100;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
         try {
             setup(console);
         } catch (IOException ex) {
             Logger.getLogger(GA.class.getName()).log(Level.SEVERE, null, ex);
         }
    }
    
    public static void setup(BufferedReader console) throws IOException{
        System.out.println("*********   Complejidad de Kolmogorov con Algoritmos Geneticos   *********     \n\n");
        System.out.println("-- Parametros preestablecidos --");
        System.out.print("1. Nombre del archivo: ");
        System.out.println(fileName);
        System.out.print("2. Numero de individuos: ");
        System.out.println(numIndividuals);
        System.out.print("3. Numero de generaciones: ");
        System.out.println(numGenerations);
        System.out.print("4. Numero de transiciones: ");
        System.out.println(numTransicions);
        System.out.print("5. Tamaño de la cinta: ");
        System.out.println(tapeSize);
        System.out.print("6. Probabilidad de combinacion: ");
        System.out.println(probComb);
        System.out.print("7. Probabilidad de mutacion: ");
        System.out.println(probMut);
        
        System.out.println("Desea cambiar una configuracion? \n (S)i / (N)o");
        String input = console.readLine();
        
        if(input.equalsIgnoreCase("S")){
            String decision = "S";
            do{
                System.out.println("Que numero de configuracion desea cambiar?:");
                int num = Integer.parseInt(console.readLine());
                System.out.println("Cual es el nuevo valor?");
                String val = console.readLine();
                switch(num){
                    case 1:
                        fileName = val;
                        break;
                    case 2:
                        numIndividuals = Integer.parseInt(val);
                        break;
                    case 3:
                        numGenerations = Integer.parseInt(val);
                        break;
                    case 4:
                        numTransicions = Integer.parseInt(val);
                        break;
                    case 5:
                        tapeSize = Integer.parseInt(val);
                        break;
                    case 6:
                        probComb = Double.parseDouble(val);
                        break;
                    case 7:
                        probMut = Double.parseDouble(val);
                        break;
                }
                System.out.println("Desea cambiar una configuracion? \n (S)i / (N)o");
                decision = console.readLine();
            }while(decision.equalsIgnoreCase("s"));
        }
    }
    
}



/**
 * Runs a genetic algoritm g times
 */
class GeneticAlgorithmLauncher extends Thread{
    int generations;
    Canonical ga;
    
    public GeneticAlgorithmLauncher(int g, Canonical ga){
        this.generations = g;
        this.ga = ga;
    }
    
    @Override
    public void run(){
        int c = 0;
        while(!ga.isDone() || c < generations){
            ga.evaluate();
            ga.select();
            ga.crossover();
            ga.mutation();
        }
    }
}







