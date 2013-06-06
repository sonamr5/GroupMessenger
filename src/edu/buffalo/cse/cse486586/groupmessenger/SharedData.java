package edu.buffalo.cse.cse486586.groupmessenger;

import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;

public class SharedData {
public static Vector<Messageformat> Causal_order=new Vector<Messageformat>(5,5);
public static int [] msg_vector={0,0,0};
public static String Iam=null;
public static Vector<Messageformat> Holdback_queue=new Vector<Messageformat>(5,5);
public static LinkedBlockingQueue<Messageformat> Delivery_queue=new LinkedBlockingQueue<Messageformat>();
public static int s=0;
public static int global_count=0;
public static int msg_seq=0;
}
