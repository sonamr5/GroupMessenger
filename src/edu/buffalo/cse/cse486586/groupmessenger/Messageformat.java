package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.Serializable;

public class Messageformat implements Serializable {
String from;
String Msg;
Integer Port;
int msg_length;
int [] msg_vector={0,0,0};
String msg_type;
String msq_seq;
int Order_seq;
boolean order_rcved=false;
boolean test_msg=false;
}
