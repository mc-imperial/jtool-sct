/**
 * 
 * Copyright (c) 2001-2010, Purdue University
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   * Neither the name of the Purdue University nor the
 *     names of its contributors may be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package immortal.persistentScope.transientScope;

/**
 * @author Filip Pizlo
 */

class Aircraft implements Comparable {
	/** The callsign. Currently, the only data we hold. */
	private final byte[] callsign;

	/** Construct with a callsign. */
	public Aircraft(final byte[] _callsign) {
		callsign = _callsign;
	}

	/** Construct a copy of an aircraft. */
	public Aircraft(final Aircraft _aircraft) {
		this(_aircraft.getCallsign());
	}

	/** Gives you the callsign. */
	public byte[] getCallsign() {
		return callsign;
	}

	/** Returns a valid hash code for this object. */
	public int hashCode() {
		int h = 0;

		for(int i=0; i<callsign.length; i++) {
			h += callsign[i];
		}

		return h;
	}

	/** Performs a comparison between this object and another. */
	public boolean equals(final Object other) {
		if (other == this) return true;
		else if (other instanceof Aircraft) {
			final byte[] cs = ((Aircraft) other).callsign;
			if (cs.length != callsign.length) return false;
			for (int i = 0; i < cs.length; i++)
				if (cs[i] != callsign[i]) return false;
			return true;
		} else return false;
	}

	/** Performs comparison with ordering taken into account. */
	public int compareTo(final Object _other) throws ClassCastException {
		final byte[] cs = ((Aircraft) _other).callsign;
		if (cs.length < callsign.length) return -1;
		if (cs.length > callsign.length) return +1;
		for (int i = 0; i < cs.length; i++)
			if (cs[i] < callsign[i]) return -1;
			else if (cs[i] > callsign[i]) return +1;
		return 0;
	}

	/** Returns a helpful description of this object. */
	public String toString() {
		return new String(callsign, 0, callsign.length);
	}
}
