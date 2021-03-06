/*======================================================================

 Copyright (C) 2009-2015. Mario Rincon-Nigro.

 This file is a part of Chaos-Crypt.

 This is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Chaos-Crypt is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Chaos-Crypt.  If not, see <http://www.gnu.org/licenses/>.

======================================================================*/

package ccrypt.cipher;

import ccrypt.map.CoupledMapNetwork;
import ccrypt.map.ChaoticMap;
import ccrypt.map.LogarithmicMap;
import ccrypt.map.Vector;

/**
 * Class for performing Text Dependent Encryption
 */
public class TextDependentCipher implements SymmetricCipher {

    // If set to true dynamic of the coupled map network
    // should be perturbed at each time step
    private boolean perturb;
    // Coupled map network
    private CoupledMapNetwork cmn;
    // Text Dependent Encryption secret key
    private TextDependentKey key;

    /**
     * Creates an and instance for TDE encryption/decryption
     * using a given key k. Uses a logarithmic map, and no external
     * perturbation.
     * 
     * @param key Private key.
     */
    public TextDependentCipher(TextDependentKey k) {
        this(k, false);
    }

    /**
     * Creates an and instance for TDE encryption/decryption
     * using a given key k, and whether state should be externally
     * perturbed. The perturbation flips the sign of the state at every
     * iteration.
     * 
     * @param key Private key.
     * @param perturb If set to true the CMN state is perturbed externally
     *        at every iteration. Otherwise states remains unperturbed.
     */
    public TextDependentCipher(TextDependentKey k, boolean perturb) {
        this(k, perturb, new LogarithmicMap(0.5));
    }

    /**
     * Creates an and instance of TDE encryption/decryption
     * using a given key k, and whether state should be externally
     * perturbed. The perturbation flips the sign of the state at every
     * iteration.
     * 
     * @param key Private key.
     * @param perturb If set to true the CMN state is perturbed externally
     *        at every iteration. Otherwise states remains unperturbed.
     * @param map Chaotic map to be used as local dynamic of the CMN.
     */
    public TextDependentCipher(TextDependentKey k, boolean perturb,
			       ChaoticMap map) {
        key = k;
        this.perturb = perturb;
        // Set the coupled map network from the key
        // The key for TDE is the initial state of a coupled map
        // network and the coefficients of a coupling matrix
        cmn = new CoupledMapNetwork(k.getState(), k.getCoupling());
        cmn.setLocalDynamic(map);
    }

    /**
     * Perform encryption. The resulting cipher-text from the encryption
     * method is a sequence of bytes, twice as large as the plain-text.
     * Each pair of them represents the number of iterations until
     * the corresponding symbol from the plain-text is generated by
     * the coupled map networks dynamics.
     *
     * @param plaintext An array containing the plain-text as a sequence
     *                  of bytes, and the output is an array containing.
     * @return The cipher-text. 
     */
    public byte[] encrypt(byte plaintext[]) {

        int i = 0, iterations = 0;
        byte ciphertext[] = new byte[plaintext.length << 1];

        // Set initial state for coupled map network from key
        cmn.setState(key.getState());

        // Start generating symbols with the coupled map network
        while(i < plaintext.length) {
            cmn.iterate();
            iterations++;

            // If the generated symbol corresponds to the current
            // plain-text symbol
            if(binaryState(cmn.getState()) == plaintext[i]) {
                // Store the encoding integer in the cipher-text integer
                // sequence
                ciphertext[i << 1] = (byte)(iterations & 0xFF);
                ciphertext[(i << 1) + 1] = (byte)((iterations >> 8) & 0xFF);

                iterations = 0;
                i++;

                // If the coupled map network should be
                // externally perturbed then do it
                // The authors of TDE recommend using a perturbation
                // factor of -1
                if(perturb) cmn.perturbState(-1.0);
            }
        }

        return ciphertext;
    }

    /**
     *	Perform decryption. This is basically the same
     */
    public byte [] decrypt(byte ciphertext[]) {

        byte plaintext[] = new byte[ciphertext.length >> 1];

        // Load the initial state of the coupled map network
        cmn.setState(key.getState());

        // For each integer on the cipher-text sequence
        for(int i = 0 ; i < ciphertext.length ; i += 2) {
            int lower = ciphertext[i] & 0xFF;
            int upper = ciphertext[i + 1] & 0xFF;
            int iterations = (upper << 8) | lower;

	    // Iterate the coupled map network for
            // the given number of states
	    cmn.iterate(iterations);

            // Convert the network state to a byte
            plaintext[i >> 1] = binaryState(cmn.getState());

            // If network has to be perturbed then do it
            if(perturb) cmn.perturbState(-1.0);
        }

        return plaintext;
    }

    /**
     * Converts the state of a coupled map network to a byte.
     *
     * @param state A vector state.
     * @return A byte obtained from the state.
     */
    private byte binaryState(Vector state) {
        byte c = 0x0;

        // For each element of the network
        for(int i = 0 ; i < state.getSize(); i++) {

            // If the i-th element is positive set the i-th element of
            // the byte to 1, otherwise keep it as 0.
            // This threshold can be any value if the local dynamic
            // is given by a logarithmic map.
            // For other maps it is not necessary 0, but the behavior
            // of the map should be taken into account or there might
            // be symbols forbidden by the dynamics of the coupled map
            // network.
            // The authors of the TDE used this threshold
            if(state.getElement(i) > 0.0)
                c |= (1 << i);
        }

        return c;
    }
}
