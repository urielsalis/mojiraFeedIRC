/*
 * The MIT License
 *
 * Copyright 2017 Ned Hyett.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package nedhyett.crimson.networking.tier4;

import nedhyett.crimson.logging.CrimsonLog;
import nedhyett.crimson.threading.IProgressReportTask;
import nedhyett.crimson.utility.GenericUtils;
import nedhyett.crimson.utility.Serializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Internal functions.
 *
 * @author Ned Hyett
 */
public class Tier4Utils {

	/**
	 * Read a packet from the input stream.
	 *
	 * @param in
	 *
	 * @return
	 *
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	static Tier4Packet readPacket(DataInputStream in) throws IOException, ClassNotFoundException {
		String channel = in.readUTF(); //Read the channel id (does nothing at the moment)
		String packetClassName = in.readUTF();
		System.out.println("Reading packet " + packetClassName + " from channel " + channel);
		try {
			Class.forName(packetClassName);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("VERY BAD! NOT GOOD PACKET!");
		}
		int length = in.readInt(); //Read the amount of bytes to expect
		byte[] b = new byte[length]; //Allocate a buffer for the object
		in.readFully(b); //Block until we get all the bytes the packet specified.
		b = GenericUtils.decompress(b); //Decompress the data
		return (Tier4Packet) Serializer.deserialize(b); //Reconstruct the packet
	}

	/**
	 * Write a packet to the output stream ignoring the channel.
	 *
	 * @param out
	 * @param packet
	 *
	 * @throws IOException
	 */
	static void writePacket(DataOutputStream out, Tier4Packet packet, IProgressReportTask... progressListeners) throws IOException {
		writePacket(out, packet, "*", progressListeners);
	}

	/**
	 * Write a packet to the output stream specifying the channel.
	 *
	 * @param out
	 * @param packet
	 * @param channel
	 *
	 * @throws IOException
	 */
	static void writePacket(DataOutputStream out, Tier4Packet packet, String channel, IProgressReportTask... progressListeners) throws IOException {
		byte[] object = Serializer.serialize(packet); //Serialize the packet
		object = GenericUtils.compress(object); //Compress the data
		out.writeUTF(channel); //Write the channel identifier
		out.writeUTF(packet.getClass().getCanonicalName());
		out.writeInt(object.length); //Write the number of bytes in this packet (need to find a way to fix this so we don't make a heartbleed-type bug)
		System.out.println("Sending " + packet.getClass().getCanonicalName());
		if(progressListeners.length > 0) {
			try {
				int idx = -1;
				while (idx++ < object.length) {
					out.write(object[idx]);
					out.flush();
					for(IProgressReportTask progressListener : progressListeners) {
						progressListener.call(idx, object.length);
					}
				}
				for(IProgressReportTask progressListener : progressListeners) {
					progressListener.call(1,1);
				}
				//System.out.println("Sent " + packet + " with " + progressListeners.length + " listeners...");
			} catch (IndexOutOfBoundsException e) {

			} catch (Exception e) {
				CrimsonLog.warning("Failure during stream bridge!");
				CrimsonLog.warning(e);
			}
		} else {
			out.write(object);
			//System.out.println("Sent " + packet + " without listeners...");
		}
		out.flush(); //Flush the stream to make sure we wrote it.
	}

}
