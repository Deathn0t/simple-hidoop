package application;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import formats.Format;
import formats.FormatReader;
import formats.FormatWriter;
import formats.KV;
import map.MapReduce;
import ordo.Job;

public class MyMapReduce implements MapReduce {
	private static final long serialVersionUID = 1L;

	// MapReduce program that computes word counts
	public void map(FormatReader reader, FormatWriter writer) {
		
		Map<String,Integer> hm = new HashMap<>();
		KV kv;
		while ((kv = reader.read()) != null) {
			StringTokenizer st = new StringTokenizer(kv.v);
			while (st.hasMoreTokens()) {
				String tok = st.nextToken();
				if (hm.containsKey(tok)) hm.put(tok, hm.get(tok).intValue()+1);
				else hm.put(tok, 1);
			}
		}
		for (String k : hm.keySet()) writer.write(new KV(k,hm.get(k).toString()));
	}
	
	public void reduce(FormatReader reader, FormatWriter writer) {
                Map<String,Integer> hm = new HashMap<>();
		KV kv;
		while ((kv = reader.read()) != null) {
			if (hm.containsKey(kv.k)) hm.put(kv.k, hm.get(kv.k)+Integer.parseInt(kv.v));
			else hm.put(kv.k, Integer.parseInt(kv.v));
		}
		for (String k : hm.keySet()) writer.write(new KV(k,hm.get(k).toString()));
	}
	
	public static void main(String args[]) {
		Job j = null;
		try {
			j = new Job();
		} catch (RemoteException e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("[] Job initialisé !");
        j.setInputFormat(Format.Type.LINE);
        j.setInputFname(args[0]);
        j.setOutputFname("res-"+args[0]);
        long t1 = System.currentTimeMillis();
		j.startJob(new MyMapReduce());
		long t2 = System.currentTimeMillis();
        System.out.println("[MyMapReduce] time in ms ="+(t2-t1));
        Count.main(args);
        System.exit(0);
		}
}
