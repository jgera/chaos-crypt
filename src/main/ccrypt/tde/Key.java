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

package ccrypt.tde;

import ccrypt.cmn.Matrix;
import ccrypt.cmn.Vector;

/**
 * Key for the Text Dependent Encryption method. Basically the aggregation
 * of a state vector and a coupling matrix of a coupled map network.
 */
public class Key{
    
    private Vector initialState;
    
    private Matrix couplingMatrix;
    
    /**
     * Create an instance of the key.
     *
     * @param state Vector representing the initial state of a coupled
     *        map network.
     * @param coupling Matrix representing the coupling of elements in
     *        a coupled map network. 
     */
    public Key(Vector state, Matrix coupling){
	
	initialState = state;
	couplingMatrix = coupling;
    }
    
    /**
     * Get the coupling matrix.
     *
     * @return The coupling matrix.
     */
    public Matrix getCoupling(){
	return couplingMatrix;
    }
    
    /**
     *	Get the initial state vector.
     *
     * @return The initial state vector.
     */
    public Vector getState(){
	return initialState;
    }
}
