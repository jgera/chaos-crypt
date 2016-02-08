package ccrypt.tde;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;
import ccrypt.cmn.Vector;
import ccrypt.cmn.Matrix;

/**
 * Loader of a Text Dependent Encryption method key.
 */
public class KeyLoader {

	// The laoded key
	private Key key;

	/*
	For creating instances of a key loader.
	path is the path to the KEY file.
	The network associated to the key has the
	size indicated by the second argument
	 */
	public KeyLoader(String path, int size)
			throws FileNotFoundException, InputMismatchException {

		Scanner scan = null;

		double state[] = new double[size];
		double coupling[][] = new double[size][size];

		// Create an instance of a scanner for reading the file
		scan = new Scanner(new File(path));

		try {
			// Read the initial state of the network
			for(int i = 0 ; i < size ; i++)
				state[i] = scan.nextDouble();

			// Read the coupling coefficients
			for(int i = 0 ; i < size ; i++)
				for(int j = 0 ; j < size ; j++)
					coupling[i][j] = scan.nextDouble();

			// Create the instance of the key
			key = new Key(new Vector(state), new Matrix(coupling));

			scan.close();
		} catch (InputMismatchException e) {
			scan.close();

			throw e;
		}
	}

	public Key getKey(){
		return key;
	}
}
