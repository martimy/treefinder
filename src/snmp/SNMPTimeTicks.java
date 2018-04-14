/*
 * SNMP Package
 *
 * Copyright (C) 2004, Jonathan Sevy <jsevy@mcs.drexel.edu>
 *
 * This is free software. Redistribution and use in source and binary forms, with
 * or without modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, this
 *     list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */


package snmp;


/**
*    SNMP datatype used to represent time value. Just extension of SNMPInteger.
*/


public class SNMPTimeTicks extends SNMPInteger
{

    public SNMPTimeTicks()
    {
        this(0);    // initialize value to 0
    }


    /**
    *    The long value is truncated to 32 bits for SNMP v2 compatibility.
    */
    public SNMPTimeTicks(long value)
    {
        // we truncate the long value to 32 bits for SNMP v2 compatibility
        super(value & 0x00000000FFFFFFFFL);

        tag = SNMPBERCodec.SNMPTIMETICKS;
    }


    protected SNMPTimeTicks(byte[] enc)
        throws SNMPBadValueException
    {
        super(enc);

        tag = SNMPBERCodec.SNMPTIMETICKS;
    }

    /**
    * returns readable formatted time (weeks, days, hours, minutes, seconds)
    * Added by Maen Artimy
    */
    public String toTimeString() {
		StringBuffer returnStringBuffer = new StringBuffer();

		// SNMP timeticks are in 100th of a sec

		long fsec = value.longValue() % 100;
		long remain = value.longValue() / 100;

		long sec = remain % 60;
		remain = remain / 60;

		long min = remain % 60;
		remain = remain / 60;

		long hr = remain % 24;
		remain = remain / 24;

		long day = remain % 7;
		long week = remain / 7;

        returnStringBuffer.append(week + " Week(s), " + day + " Day(s), " + hr + ":" + min + ":" + sec);

		return returnStringBuffer.toString();
	}


}