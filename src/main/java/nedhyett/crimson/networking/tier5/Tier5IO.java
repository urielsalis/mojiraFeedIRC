package nedhyett.crimson.networking.tier5;

import nedhyett.crimson.logging.CrimsonLog;
import nedhyett.crimson.networking.tier4.Tier4Packet;
import nedhyett.crimson.threading.IProgressReportTask;
import nedhyett.crimson.utility.GenericUtils;
import nedhyett.crimson.utility.Serializer;
import nedhyett.crimson.utility.StringUtils;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * Created by ned on 27/02/2017.
 */
public class Tier5IO {

    public static Tier5Packet decompose(byte[] packetData, IProgressReportTask... progressListeners) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(packetData);
        DataInputStream dis = new DataInputStream(bais);
        return decompose(dis, progressListeners);
    }

    public static Tier5Packet decompose(DataInputStream dis, IProgressReportTask[] progressListeners) throws IOException, ClassNotFoundException {
        String packetClassName = dis.readUTF();
        Tier5Channel channel = new Tier5Channel().readChannelData(dis);
        System.out.println("Reading packet " + packetClassName + " from channel " + channel.name);
        try {
            Class.forName(packetClassName);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("VERY BAD! NOT GOOD PACKET!");
        }
        String checksumA = dis.readUTF();//uncompressed
        String checksumB = dis.readUTF();//compressed
        int bytes = dis.readInt();
        byte[] object = new byte[bytes]; //Allocate a buffer for the object
        dis.readFully(object); //Block until we get all the bytes the packet specified.
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] res = md.digest(object);
            StringBuilder sb = new StringBuilder();
            for(byte b1 : res) sb.append(Integer.toHexString(0xFF & b1));
            if(!Objects.equals(checksumB, sb.toString())) {
                throw new Tier5Exception("Packet failed pre-decompression checksum validation! (Was %s, should have been %s)", sb.toString(), checksumB);
            }
        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        object = GenericUtils.decompress(object);
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] res = md.digest(object);
            StringBuilder sb = new StringBuilder();
            for(byte b1 : res) sb.append(Integer.toHexString(0xFF & b1));
            if(!Objects.equals(checksumA, sb.toString())) {
                throw new Tier5Exception("Packet failed post-decompression checksum validation! (Was %s, should have been %s)", sb.toString(), checksumA);
            }
        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return (Tier5Packet) Serializer.deserialize(object);
    }

    public static byte[] compose(Tier5Packet packet, Tier5Channel channel, IProgressReportTask... progressListeners) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        compose(dos, packet, channel, progressListeners);
        return baos.toByteArray();
    }

    public static void compose(DataOutputStream dos, Tier5Packet packet, Tier5Channel channel, IProgressReportTask[] progressListeners) throws IOException {
        byte[] object = Serializer.serialize(packet); //Serialize the packet
        String checksumA, checksumB;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] res = md.digest(object);
            StringBuilder sb = new StringBuilder();
            for(byte b1 : res) sb.append(Integer.toHexString(0xFF & b1));
            checksumA = sb.toString();
        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
            checksumA = null;
        }
        object = GenericUtils.compress(object); //Compress the data
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] res = md.digest(object);
            StringBuilder sb = new StringBuilder();
            for(byte b1 : res) sb.append(Integer.toHexString(0xFF & b1));
            checksumB = sb.toString();
        } catch(NoSuchAlgorithmException e) {
            e.printStackTrace();
            checksumB = null;
        }

        dos.writeUTF(packet.getClass().getCanonicalName());
        channel.writeChannelData(dos); //Write the channel identifier
        dos.writeUTF(checksumA);
        dos.writeUTF(checksumB);
        dos.writeInt(object.length); //Write the number of bytes in this packet (need to find a way to fix this so we don't make a heartbleed-type bug)

        System.out.println("Sending " + packet.getClass().getCanonicalName());
        if(progressListeners.length > 0) {
            try {
                int idx = -1;
                while (idx++ < object.length) {
                    dos.write(object[idx]);
                    dos.flush();
                    for(IProgressReportTask progressListener : progressListeners) {
                        progressListener.call(idx, object.length);
                    }
                }
                for(IProgressReportTask progressListener : progressListeners) {
                    progressListener.call(1,1);
                }
            } catch (IndexOutOfBoundsException e) {

            } catch (Exception e) {
                CrimsonLog.warning("Failure during stream bridge!");
                CrimsonLog.warning(e);
            }
        } else {
            dos.write(object);
        }
        dos.flush(); //Flush the stream to make sure we wrote it.
    }

}
