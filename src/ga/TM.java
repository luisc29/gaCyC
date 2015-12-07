/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ga;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.RandomAccessFile;

/**
 * These are the functions provided by Kuri to simulate a TM
 * The function TM.NewTape(params) receives a TM and tape in string and
 * returns the tape after the execution of the TM.
 * @author akuri
 */
public class TM {

    public static String NewTape(String TM, String Tape, int N, int P) {
        int Steps = 0;
        String sO;
        int Q = 0;											// Start in state Q=0
        int PtrTM;										// Pointer to TM
        int I, M;											// Input and Movement
        int LenTape = Tape.length();						// Size of tape
        String LeftTape = Tape.substring(0, P);				// Left tape minus last bit
        //                         0,1,..,P-1
        //                         \___P___/
        int LLT = LeftTape.length();						// Length of LeftTape
        String RighTape = Tape.substring(P + 1);			 	// Right tape minus first bit
        //                         P+1,..,eot
        int LRT = RighTape.length();						// Length of RighTape
        for (int i = 1; i <= N; i++) {
            I = Integer.parseInt(Tape.substring(P, P + 1));		// Input symbol in Tape
            PtrTM = Q * 16 + I * 8;								// Position in the TM
            sO = TM.substring(PtrTM, PtrTM + 1);					// Output symbol
            M = Integer.parseInt(TM.substring(PtrTM + 1, PtrTM + 2));	// Movement
            if (M == 0) {
                P++;										// Move RIGHT
                if (P == LenTape) {
                    System.out.println("\nRight limit of tape exceeded");
                    return "";
                }//endif
                LeftTape = LeftTape + sO;
                LLT++;
                Tape = LeftTape + RighTape;
                LRT--;
                RighTape = RighTape.substring(1);
            } else {	// M==1
                P--;										// Move LEFT
                if (P < 0) {
                    System.out.println("\nLeft limit of tape exceeded");
                    return "";
                }//endif
                RighTape = sO + RighTape;
                LRT++;
                Tape = LeftTape + RighTape;
                LLT--;
                LeftTape = LeftTape.substring(0, LLT);
            }//endif
            Q = 0;											// Next State
            for (int j = PtrTM + 2; j < PtrTM + 8; j++) {
                Q = Q * 2;
                if (TM.substring(j, j + 1).equals("1")) {
                    Q++;
                }
                //endif
            }//endFor
            if (Q == 63) {
                System.out.println("\n\nHALT state was reached");
                System.out.printf("%10.0f transitions were performed\n", (float) i);
                long ones = 0;
                for (int j = 0; j < Tape.length(); j++) {
                    if (Tape.substring(j, j + 1).equals("1")) {
                        ones++;
                    }
                    //endIf
                }//endFor
                System.out.println("The productivity of this machine is " + ones);
                return Tape;								// *** Processed Tape
            }//endif
            Steps++;
            if (Steps == 10000) {
                System.out.print("\b\b\b\b\b\b\b\b\b\b");
                System.out.print("\t" + i);
                Steps = 0;
            }//endIf
        }//endfor
        System.out.println("\nMaximum number of transitions was reached");
        return Tape;
    }//endOutTape
} //endClass

class FeedTM {

    static RandomAccessFile Datos;
    static BufferedReader TF;
    static BufferedReader Kbr;

    public static void main2(String[] args) throws Exception {
        Kbr = new BufferedReader(new InputStreamReader(System.in));
        String sResp;
        int i;
        boolean ask = false;
        while (true) {
            if (ask) {					// No preguntes la primera vez
                System.out.println("\nDesea leer otro archivo?");
                System.out.println("\"S\" para continuar; otro para terminar...)");
                sResp = Kbr.readLine().toUpperCase();
                if (!sResp.equals("S")) {
                    break;
                }
                //endIf
            } else {
                ask = true;				// Pregunta de la segunda en adelante
            }//endif
            System.out.println("Deme el nombre del archivo de datos que quiere leer:");
            String FName = Kbr.readLine().toUpperCase();
            try {
                Datos = new RandomAccessFile(new File(FName), "r");
            }//endTry
            catch (Exception e1) {
                System.out.println("No se encontro \"" + FName + "\"");
                continue;
            }//endCatch
            String Cinta = "", Car;
            while (true) {
                System.out.println("Los datos estan en binario-ASCII? (S/N)");
                sResp = Kbr.readLine().toUpperCase();
                if (sResp.equals("S") || sResp.equals("N")) {
                    break;
                }
                //endIf
            }//endWhile
            if (sResp.equals("N")) {
                System.out.println("Deme el nombre del archivo de salida de imagen numerica:");
                String FTarget = Kbr.readLine().toUpperCase();
                PrintStream Fps = new PrintStream(new FileOutputStream(new File(FTarget)));
                int BytesEnDatos = 0;
                byte X;
                /*
                 *	Averigua el tamaño del archivo en bytes
                 */
                while (true) {
                    Datos.seek(BytesEnDatos);
                    try {
                        X = Datos.readByte();
                    } catch (Exception e) {
                        break;
                    }
                    BytesEnDatos++;
                }//endWhile
                Datos.close();
                System.out.println("Se leyeron " + BytesEnDatos + " datos\n");
		//
		/*
                 *	Convierte en binario-ASCII
                 */
                int Y, T;
                Datos = new RandomAccessFile(new File(FName), "r");
                for (i = 0; i < BytesEnDatos; i++) {
                    Datos.seek(i);
                    Y = Datos.readByte();
                    T = Y;								// T <-- Número original
                    Car = "";
                    for (int j = 0; j < 8; j++) {
                        if (Y % 2 == 0) {
                            Car = "0" + Car;
                        } else {
                            Car = "1" + Car;
                        }
                        Y = Y / 2;
                    } // endFor
                    Cinta = Cinta + Car;
                    Fps.printf("%4.0f  ", (float) T, Car);	// Escribe cada uno de los bytes leidos
                    Fps.println();
                }//endFor
            } else {
                Datos.close();
                BufferedReader FCinta;
                FCinta = new BufferedReader(new InputStreamReader(new FileInputStream(new File(FName))));
                Cinta = FCinta.readLine();
            }//endIf
            PrintStream Tape = new PrintStream(new FileOutputStream(new File("Tape.txt")));
            Tape.println(Cinta);					// La imagen binaria esta en "Tape.txt"
            System.out.println("La imagen binaria-ASCII se halla en \"Tape.txt\"");
            int LongCinta = Cinta.length();			// Longitud LEÍDA de la cinta
            Datos.close();
            /*
             *  NewTape=UTM(TT,Tape,N,P)
             *              /\   |  | |
             Description of Turing's Machine in ASCII binary
             |  | |
             Input tape in ASCII binary
             | |
             Maximum number of transitions
             |
             Position of the Head at offset
					   	  
             Maximum 64 states (000000 - 111111)
             State 111111 is HALT

             ON OUTPUT:
             1) The processed tape if HALT
             2) Idem if N is exceeded
             3) Null tape if over/under flow occurs
             */
            String TTFN = "";
            String TT = "";
            int MTLen;
            while (true) {
                while (true) {
                    System.out.println("Deme el nombre del archivo con la Maquina de Turing:");
                    TTFN = Kbr.readLine().toUpperCase();
                    try {
                        TF = new BufferedReader(new InputStreamReader(new FileInputStream(new File(TTFN))));
                        System.out.println();
                        break;
                    }//endTry
                    catch (Exception e1) {
                        System.out.println("No se encontro \"" + TTFN + "\"");
                        continue;
                    }//endCatch
                }//endWhile
		/* 
                 *	LEE TODOS LOS BYTES DE LA MÁQUINA DE TURING
                 *
                 */
                boolean Forever = false, FF;
                TT = TF.readLine();
                MTLen = TT.length();
                System.out.println(MTLen + " bytes leidos del mapa de la MT");
                int iCar = 0;
                for (i = 0; i < MTLen; i++) {
                    Car = TT.substring(i, i + 1);
//				System.out.println(i+") : "+Car);
                    try {
                        iCar = Integer.parseInt(Car);
                        FF = false;
                    } catch (Exception e) {
                        FF = true;
                    }
                    if ((iCar != 0 && iCar != 1) || FF) {
                        System.out.println("Error en el formato de la Maquina de Turing");
                        System.out.println("Deben ser solamente \"0\" o \"1\"");
                        Forever = true;
                        break;		// Exit For
                    }//endIf
                }//endFor
                if (Forever) {
                    continue;
                }
                //endIf
                if (MTLen % 16 != 0) {
                    System.out.println("La longitud de la Maquina de Turing debe ser multiplo de 16");
                    continue;
                }//endIf
                break;				//Exit While
            }//endwhile
            int NumStates = MTLen / 16;
            int ix16, x0_I, x1_I, Estado;
            String x0_M, x1_M;
            System.out.println("Hay " + NumStates + " estados en la Maquina de Turing");
            System.out.println(" EA | O | M | SE || O | M | SE |");
            System.out.println(" -------------------------------");
            for (i = 0; i < NumStates; i++) {
                System.out.printf("%4.0f|", (float) i);
                ix16 = i * 16;
                x0_I = Integer.parseInt(TT.substring(ix16, ix16 + 1));
                x0_M = TT.substring(ix16 + 1, ix16 + 2);
                if (x0_M.equals("0")) {
                    x0_M = " R |";
                } else {
                    x0_M = " L |";
                }
                System.out.printf("%3.0f|" + x0_M, (float) x0_I);
                Estado = 0;
                for (int j = ix16 + 2; j < ix16 + 8; j++) {
                    Estado = Estado * 2;
                    if (TT.substring(j, j + 1).equals("1")) {
                        Estado++;
                    }
                    //endif
                }//endFor
                if (Estado == 63) {
                    System.out.print("   H||");
                } else {
                    System.out.printf("%4.0f||", (float) Estado);
                }
                //endif
                x1_I = Integer.parseInt(TT.substring(ix16 + 8, ix16 + 9));
                x1_M = TT.substring(ix16 + 9, ix16 + 10);
                if (x1_M.equals("0")) {
                    x1_M = " R |";
                } else {
                    x1_M = " L |";
                }
                System.out.printf("%3.0f|" + x1_M, (float) x1_I);
                Estado = 0;
                for (int j = ix16 + 10; j < ix16 + 16; j++) {
                    Estado = Estado * 2;
                    if (TT.substring(j, j + 1).equals("1")) {
                        Estado++;
                    }
                    //endif
                }//endFor
                if (Estado == 63) {
                    System.out.print("   H|\n");
                } else {
                    System.out.printf("%4.0f|\n", (float) Estado);
                }
                //endif
            }//endFor
		/*
             *	NÚMERO DE TRANSICIONES
             */
            int N = 0;
            while (true) {
                System.out.println("Deme el numero maximo de transiciones de la Maquina de Turing:");
                sResp = Kbr.readLine();
                try {
                    N = Integer.parseInt(sResp);
                    break;
                }//endTry
                catch (Exception e) {
                    System.out.println("Error de formato");
                    continue;
                }//endCatch
            }//endWhile
		/*
             *	LONGITUD DESEADA DE LA CINTA
             */
            int M = 0;
            while (true) {
                while (true) {
                    System.out.println("Deme el tamano deseado de cinta:");
                    sResp = Kbr.readLine();
                    try {
                        M = Integer.parseInt(sResp);
                        break;
                    }//endTry
                    catch (Exception e1) {
                        System.out.println("Error de formato");
                        continue;
                    }//endCatch
                }//endWhile
                if (M < LongCinta) {
                    System.out.println("El tamano especificado es menor que los datos");
                    continue;
                }//endIf
                break;
            }//endWhile
            System.out.println("La cinta se rellena con 0s a izq. y derecha...");
            int DifTamCinta = M - LongCinta;
            if (DifTamCinta % 2 != 0) {
                DifTamCinta++;			// Número par
            }
            DifTamCinta = DifTamCinta / 2;
            for (i = 0; i < DifTamCinta; i++) {
                Cinta = "0" + Cinta;	// 0s del lado izquierdo
            }
            for (i = 0; i < DifTamCinta; i++) {
                Cinta = Cinta + "0";	// 0s del lado derecho
            }		/*
             *	POSICIÓN DE LA CABEZA
             */

            int P = 0;
            while (true) {
                while (true) {
                    System.out.println("Deme la posicion de la cabeza en la cinta:");
                    System.out.println("\t(Entre \"0\" y \"" + M + "\")");
                    sResp = Kbr.readLine();
                    try {
                        P = Integer.parseInt(sResp);
                        break;
                    }//endTry
                    catch (Exception e1) {
                        System.out.println("Error de formato");
                        continue;
                    }//endCatch
                }//endFor
                if (P < 0 || P >= M) {
                    System.out.println("Cabeza en posicion erronea");
                    continue;
                }//endIf
                break;				// Exit While
            }//endWhile
            String NuevaCinta = TM.NewTape(TT, Cinta, N, P);
            PrintStream PProc = new PrintStream(new FileOutputStream(new File("Procesada.doc")));
            PProc.println(NuevaCinta);
            System.out.println("\nNueva cinta esta en \"Procesada.doc\"");
        }//endFor
    }//endMain
}//endClass

