import java.io.BufferedReader;
import java.io.FileReader;

public class Comparador {
    public static void main(String args[]) throws Exception {

        FileReader fr1 = new FileReader("filename.txt");
        FileReader fr2 = new FileReader("filename1.txt");

        BufferedReader bf1 = new BufferedReader(fr1);
        BufferedReader bf2 = new BufferedReader(fr2);

        String sCadena1 = bf1.readLine();
        String sCadena2 = bf2.readLine();
        boolean iguales = true;

        while ((sCadena1 != null) && (sCadena2 != null) && iguales) {

            if (!sCadena1.equals(sCadena2)) {
                iguales = false;
                System.out.println("la linea es distinta");
            }

            sCadena1 = bf1.readLine();
            sCadena2 = bf2.readLine();
        }

        if ((iguales) && (sCadena1 == null) && (sCadena2 == null)) {
            System.out.println("Los ficheros son iguales");
        } else {
            System.out.println("Los ficheros son diferentes");
        }
    }
}
