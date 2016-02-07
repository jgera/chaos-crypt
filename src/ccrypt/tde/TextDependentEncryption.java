package ccrypt.tde;

import ccrypt.cmn.CoupledMapNetwork;
import ccrypt.cmn.Maps;
import ccrypt.cmn.NetworkState;

/*
    Class for performing Text Depenent Encryption
*/
public class TextDependentEncryption {

    // If set to true dynamic of the coupled map network
    // should be perturbated at ech time step
    private boolean perturb;
    // Coupled map network
    private CoupledMapNetwork cmn;
    // Text Dependent Encryption secret key
    private Key key;

    /*
	Creates and instance for TDE encryption/decryption
	using a given key k
    */
    public TextDependentEncryption(Key k){
	this(k, false);
    }

    /*
	Creates and instance for TDE encryption/decryption
	using a given key k.
	If the argument perturb is set to true then
	the coupled map network dynamic should be perturbed
	externally.
	By default the local dynamics is given by a logarithmic
	map
    */
    public TextDependentEncryption(Key k, boolean perturb){
	this(k, perturb, new Maps.Logarithmic(0.5));
    }

    /*
	Creates and instance for TDE encryption/decryption
	using a given key k, and allow to set the local dynamic
	If the argument perturb is set to true then
	the coupled map network dynamic should be perturbed
	externally.
    */
    public TextDependentEncryption(Key k, boolean perturb,
				   Maps.OneDimensionalMap map){
	key = k;
	this.perturb = perturb;
	// Set the coupled map network from the key
	// The key for TDE is the initial state of a coupled map
	// network and the coefficients of a coupling matrix
	cmn = new CoupledMapNetwork(k.getState(), k.getCoupling());
	cmn.setLocalDynamic(map);
    }

    /*
	Perform encryption. The resulting plaintext from the encryption
	method is a sequence of integer numbers.
	Each one of them represents the number of iterations until
	the corresponding symbol from the plaintext is generated by
	the coupled map networks dynamics.
	The input is an array containing the corresponding the plaintext
	as a sequence of bytes, and the output is an array containing
	the sequence of integers that encodes the plaintext 
    */
    public int[] encrypt(byte plaintext[]){

	int i = 0, iterations = 0;
	int ciphertext[] = new int[plaintext.length];

	// Load initial state for the coupled map network
	cmn.loadInitialState();

	// Start generating symbols with the coupled map networs
	while(i < plaintext.length){
	    cmn.iterate();
	    iterations++;

	    // If the generated symbol corresponds to the current
	    // plaintext symbol
	    if(binaryState(cmn.getState()) == plaintext[i]){
		// Store the encoding integer in the ciphertext integer
		// sequence
		ciphertext[i] = iterations;
		iterations = 0;
		i++;

		// If the coupled map network should be
		// externally perturbated then do it
		// The authors of TDE recommend using a perturbation
		// factor of -1
		if(perturb) cmn.perturbState(-1);
	    }
	}

	return ciphertext;
    }

    /*
	Perform decryption. This is basically the same
    */
    public byte [] decrypt(int ciphertext[]){
	
	byte plaintext[] = new byte[ciphertext.length];

	// Load the initial state of the coupled map network
	cmn.loadInitialState();

	// For each integer on the ciphertext sequence
	for(int i = 0 ; i < ciphertext.length ; i++){
	    // Iterate the coupled map network for
	    // the given number of satates
	    for(int j = 1 ; j <= ciphertext[i] ; j++)
		cmn.iterate();

	    // Convert the network state to a byte
	    plaintext[i]= binaryState(cmn.getState());

	    // If network has to be perturbated then do it
	    if(perturb) cmn.perturbState(-1);
	}

	return plaintext;
    }

    /*
	Converts the state of a coupled map network
	to a byte
    */
    private byte binaryState(NetworkState state){

	byte c = 0x0;
	
	// For each element of the network
	for(int i = 0 ; i < state.getSize(); i++){

	    // If its ith element is positive set
	    // the ith element of the byte to 1
	    // This threshold can be any value if
	    // the local dynamic is given by a logarithmic map.
	    // For other maps it is not necessary 0, but
	    // the behaviour of the map should be taken into account
	    // or there might be symbols forbidden by the dynamics
	    // of the coupled map network
	    // The authors of the TDE used this threashold
	    if(state.getElement(i) > 0)
		c |= (1 << i);
	}

	return c;
    }
}
