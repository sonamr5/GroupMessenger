package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.widget.EditText;
import android.widget.TextView;

public class GroupMessengerActivity extends Activity {
	int server_port=10000;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_messenger);

		TextView tv = (TextView) findViewById(R.id.textView1);
		tv.setMovementMethod(new ScrollingMovementMethod());
		findViewById(R.id.button1).setOnClickListener(
				new OnPTestClickListener(tv, getContentResolver()));
		findViewById(R.id.button2).setOnClickListener(
				new OnTestOneClick());
		findViewById(R.id.button3).setOnClickListener(
				new OnTestTwoClick());	
		try {
			ServerSocket server_socket=new ServerSocket(server_port);
			new Server().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, server_socket);
		} catch (IOException e) {
			{		
				e.printStackTrace();
			}
		}
		new msg_delivery().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		final EditText usr_input=(EditText) findViewById(R.id.editText1);
		TelephonyManager tel =(TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
		if(portStr.equals("5554"))
			SharedData.Iam="AVD0";
		else if (portStr.equals("5556"))
			SharedData.Iam="AVD1";
		else if( portStr.equals("5558"))
			SharedData.Iam="AVD2";
		findViewById(R.id.button4).setOnClickListener(
				new OnSendClickListener(usr_input,tv,portStr));
		/*		usr_input.setOnKeyListener(new OnKeyListener(){

			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				String outgoing_msg=null;
				if((arg2.getAction()==KeyEvent.ACTION_DOWN) && (arg1 == KeyEvent.KEYCODE_ENTER))
				{
					outgoing_msg=usr_input.getText().toString()+"\n";
					usr_input.setText("");
					new Client().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, outgoing_msg);
				}
				return false;
			}

		});*/

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
		return true;
	}
	private class msg_delivery extends AsyncTask<Void, String, Void>{
	    private static final String KEY_FIELD = "key";
	    private static final String VALUE_FIELD = "value";
	    private  Uri URI;
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try{
				ContentResolver cr=getContentResolver();
				URI = buildUri("content", "edu.buffalo.cse.cse486586.groupmessenger.provider");
				while(true)
				{
					Messageformat display;
					while((display=SharedData.Delivery_queue.poll())!=null)
					{
						publishProgress(display.Msg);
						if(display.test_msg)
						{
							TestTwoForwarder tf=new TestTwoForwarder();
							tf.msg=display;
							tf.messagesender();
						}
					}
					if(!SharedData.Holdback_queue.isEmpty())
					{
						for(int i =0;i<SharedData.Holdback_queue.size();i++)
						{
							if((SharedData.Holdback_queue.elementAt(i).order_rcved)&&(SharedData.Holdback_queue.elementAt(i).Order_seq==SharedData.global_count))
							{
								SharedData.Delivery_queue.put(SharedData.Holdback_queue.elementAt(i));
								Messageformat tmp=new Messageformat();
								tmp=SharedData.Holdback_queue.elementAt(i);
								ContentValues cv=new ContentValues();
								cv.put(KEY_FIELD,String.valueOf(SharedData.global_count));
								cv.put(VALUE_FIELD, tmp.Msg);
								cr.insert(URI, cv);
								SharedData.global_count=SharedData.Holdback_queue.elementAt(i).Order_seq+1;
								SharedData.Holdback_queue.removeElementAt(i);
								i--;
							}
						}
					}
				}
			}
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	    private Uri buildUri(String scheme, String authority) {
	        Uri.Builder uriBuilder = new Uri.Builder();
	        uriBuilder.authority(authority);
	        uriBuilder.scheme(scheme);
	        return uriBuilder.build();
	    }
		@Override
		protected void onProgressUpdate(String...string)
		{
			TextView msg_display = (TextView) findViewById(R.id.textView1);
			msg_display.setMovementMethod(new ScrollingMovementMethod());
			msg_display.append(string[0] + "\n");
			return;
		}

	}
	private class Server extends AsyncTask<ServerSocket,String,Void>{

		@Override
		protected Void doInBackground(ServerSocket... arg0) {
			Socket incoming_connection;
			Messageformat rcv_mf=new Messageformat();
			try {
				while(true)
				{
					incoming_connection=arg0[0].accept();
					ObjectInputStream in_buffer=new ObjectInputStream(incoming_connection.getInputStream());
					rcv_mf=(Messageformat)in_buffer.readObject();
					if(SharedData.Iam.equals("AVD0"))
					{
						Messageformat mf1=new Messageformat();
						Messageformat mf2=new Messageformat();
						if(rcv_mf.msg_type.equals("data"))
						{
							if(rcv_mf.from.equals("AVD1"))
							{
								if((rcv_mf.msg_vector[1]==SharedData.msg_vector[1]+1) && (rcv_mf.msg_vector[0]<=SharedData.msg_vector[0]) && (rcv_mf.msg_vector[2]<=SharedData.msg_vector[2]))
								{
									SharedData.msg_vector[1]++;
									rcv_mf.Order_seq=SharedData.s;
									rcv_mf.order_rcved=true;
									SharedData.Holdback_queue.add(rcv_mf);
									mf1.from="AVD0";
									mf1.msg_type="order";
									mf1.msq_seq=rcv_mf.msq_seq;
									mf1.Order_seq=SharedData.s;
									mf1.Port=11112;
									mf2.from="AVD0";
									mf2.msg_type="order";
									mf2.msq_seq=rcv_mf.msq_seq;
									mf2.Order_seq=SharedData.s;
									mf2.Port=11116;
									MsgSender sender=new MsgSender();
									sender.message=mf1;
									sender.messagesender();
									sender.message=mf2;
									sender.messagesender();
									SharedData.s++;
									update_vector();
								}
								else
								{
									int sum=0;
									if(rcv_mf.msg_vector[1]>SharedData.msg_vector[1]+1)
										sum=sum+(rcv_mf.msg_vector[1]-(SharedData.msg_vector[1]+1));
									if(rcv_mf.msg_vector[0]>SharedData.msg_vector[0])
										sum=sum+(rcv_mf.msg_vector[0]-SharedData.msg_vector[0]);
									if(rcv_mf.msg_vector[2]>SharedData.msg_vector[2])
										sum=sum+(rcv_mf.msg_vector[2]-SharedData.msg_vector[2]);
									mf1.from="AVD0";
									mf1.msg_type="order";
									mf1.msq_seq=rcv_mf.msq_seq;
									mf1.Order_seq=SharedData.s+sum;
									mf1.Port=11112;
									mf2.from="AVD0";
									mf2.msg_type="order";
									mf2.msq_seq=rcv_mf.msq_seq;
									mf2.Order_seq=SharedData.s+sum;
									mf2.Port=11116;
									rcv_mf.Order_seq=SharedData.s+sum;
									rcv_mf.order_rcved=true;
									SharedData.Causal_order.addElement(rcv_mf);
									MsgSender sender=new MsgSender();
									sender.message=mf1;
									sender.messagesender();
									sender.message=mf2;
									sender.messagesender();
								}
							}
							else if(rcv_mf.from.equals("AVD2"))
							{
								if((rcv_mf.msg_vector[2]==SharedData.msg_vector[2]+1) && (rcv_mf.msg_vector[0]<=SharedData.msg_vector[0]) && (rcv_mf.msg_vector[1]<=SharedData.msg_vector[1]))
								{
									SharedData.msg_vector[2]++;
									rcv_mf.Order_seq=SharedData.s;
									rcv_mf.order_rcved=true;
									SharedData.Holdback_queue.add(rcv_mf);
									//									publishProgress(rcv_mf.Msg);
									/*								for(int i=0;i<SharedData.Causal_order.size();i++)
								{
									if(SharedData.Causal_order.elementAt(i).from.equals("AVD2") && (SharedData.Causal_order.elementAt(i).msg_vector[2]==SharedData.msg_vector[2]+1))
									{
										SharedData.msg_vector[2]++;
										publishProgress(SharedData.Causal_order.elementAt(i).Msg);
										SharedData.Causal_order.removeElementAt(i);
										break;
									}
								}*/
									mf1.from="AVD0";
									mf1.msg_type="order";
									mf1.msq_seq=rcv_mf.msq_seq;
									mf1.Order_seq=SharedData.s;
									mf1.Port=11112;
									mf2.from="AVD0";
									mf2.msg_type="order";
									mf2.msq_seq=rcv_mf.msq_seq;
									mf2.Order_seq=SharedData.s;
									mf2.Port=11116;
									MsgSender sender=new MsgSender();
									sender.message=mf1;
									sender.messagesender();
									sender.message=mf2;
									sender.messagesender();
									SharedData.s++;
									update_vector();
								}
								else
								{
									int sum=0;
									if(rcv_mf.msg_vector[2]>SharedData.msg_vector[2]+1)
										sum=sum+(rcv_mf.msg_vector[2]-(SharedData.msg_vector[2]+1));
									if(rcv_mf.msg_vector[0]>SharedData.msg_vector[0])
										sum=sum+(rcv_mf.msg_vector[0]-SharedData.msg_vector[0]);
									if(rcv_mf.msg_vector[1]>SharedData.msg_vector[1])
										sum=sum+(rcv_mf.msg_vector[1]-SharedData.msg_vector[1]);
									mf1.from="AVD0";
									mf1.msg_type="order";
									mf1.msq_seq=rcv_mf.msq_seq;
									mf1.Order_seq=SharedData.s+sum;
									mf1.Port=11112;
									mf2.from="AVD0";
									mf2.msg_type="order";
									mf2.msq_seq=rcv_mf.msq_seq;
									mf2.Order_seq=SharedData.s+sum;
									mf2.Port=11116;
									rcv_mf.Order_seq=SharedData.s+sum;
									rcv_mf.order_rcved=true;
									SharedData.Causal_order.addElement(rcv_mf);
									MsgSender sender=new MsgSender();
									sender.message=mf1;
									sender.messagesender();
									sender.message=mf2;
									sender.messagesender();

								}
							}


						}
					}
					else if(SharedData.Iam.equals("AVD1"))
					{
						if(rcv_mf.from.equals("AVD0"))
						{
							if(rcv_mf.msg_type.equals("data"))
							{
								if((rcv_mf.msg_vector[0]==SharedData.msg_vector[0]+1) && (rcv_mf.msg_vector[1]<=SharedData.msg_vector[1]) && (rcv_mf.msg_vector[2]<=SharedData.msg_vector[2]))
								{
									SharedData.msg_vector[0]++;
									SharedData.Holdback_queue.add(rcv_mf);
									/*								for(int i=0;i<SharedData.Causal_order.size();i++)
								{
									if(SharedData.Causal_order.elementAt(i).from.equals("AVD0") && (SharedData.Causal_order.elementAt(i).msg_vector[0]==SharedData.msg_vector[0]+1))
									{
										SharedData.msg_vector[0]++;
										publishProgress(SharedData.Causal_order.elementAt(i).Msg);
										SharedData.Causal_order.removeElementAt(i);
										break;
									}
								}*/
									update_vector();
								}
								else
								{
									SharedData.Causal_order.addElement(rcv_mf);
								}
							}
							else
							{
								boolean flag=false;
								for(int i=0;i<SharedData.Holdback_queue.size();i++)
								{
									if(SharedData.Holdback_queue.elementAt(i).msq_seq.equals(rcv_mf.msq_seq))
									{
										Messageformat temp=SharedData.Holdback_queue.elementAt(i);
										temp.Order_seq=rcv_mf.Order_seq;
										temp.order_rcved=true;
										flag=true;
										break;
									}
								}
								if(!flag)
								{
									for(int i=0;i<SharedData.Holdback_queue.size();i++)
									{
										if(SharedData.Holdback_queue.elementAt(i).msq_seq.equals(rcv_mf.msq_seq))
										{
											Messageformat temp=SharedData.Holdback_queue.elementAt(i);
											temp.Order_seq=rcv_mf.Order_seq;
											temp.order_rcved=true;
										}
									}
								}
							}
						}
						else if(rcv_mf.from.equals("AVD2"))
						{
							if((rcv_mf.msg_vector[2]==SharedData.msg_vector[2]+1) && (rcv_mf.msg_vector[0]<=SharedData.msg_vector[0]) && (rcv_mf.msg_vector[1]<=SharedData.msg_vector[1]))
							{
								SharedData.msg_vector[2]++;
								SharedData.Holdback_queue.add(rcv_mf);
								/*								for(int i=0;i<SharedData.Causal_order.size();i++)
								{
									if(SharedData.Causal_order.elementAt(i).from.equals("AVD2") && (SharedData.Causal_order.elementAt(i).msg_vector[2]==SharedData.msg_vector[2]+1))
									{
										SharedData.msg_vector[2]++;
										publishProgress(SharedData.Causal_order.elementAt(i).Msg);
										SharedData.Causal_order.removeElementAt(i);
										break;
									}
								}*/
								update_vector();
							}
							else
							{
								SharedData.Causal_order.addElement(rcv_mf);
							}
						}
					}
					else if(SharedData.Iam.equals("AVD2"))
					{
						if(rcv_mf.from.equals("AVD0"))
						{
							if(rcv_mf.msg_type.equals("data"))
							{
								if((rcv_mf.msg_vector[0]==SharedData.msg_vector[0]+1) && (rcv_mf.msg_vector[1]<=SharedData.msg_vector[1]) && (rcv_mf.msg_vector[2]<=SharedData.msg_vector[2]))
								{
									SharedData.msg_vector[0]++;
									SharedData.Holdback_queue.add(rcv_mf);
									/*								for(int i=0;i<SharedData.Causal_order.size();i++)
								{
									if(SharedData.Causal_order.elementAt(i).from.equals("AVD0") && (SharedData.Causal_order.elementAt(i).msg_vector[0]==SharedData.msg_vector[0]+1))
									{
										SharedData.msg_vector[0]++;
										publishProgress(SharedData.Causal_order.elementAt(i).Msg);
										SharedData.Causal_order.removeElementAt(i);
										break;
									}
								}*/
									update_vector();
								}
								else
								{
									SharedData.Causal_order.addElement(rcv_mf);
								}
							}
							else
							{
								boolean flag=false;
								for(int i=0;i<SharedData.Holdback_queue.size();i++)
								{
									if(SharedData.Holdback_queue.elementAt(i).msq_seq.equals(rcv_mf.msq_seq))
									{
										Messageformat temp=SharedData.Holdback_queue.elementAt(i);
										temp.Order_seq=rcv_mf.Order_seq;
										temp.order_rcved=true;
										flag=true;
										break;
									}
								}
								if(!flag)
								{
									for(int i=0;i<SharedData.Holdback_queue.size();i++)
									{
										if(SharedData.Holdback_queue.elementAt(i).msq_seq.equals(rcv_mf.msq_seq))
										{
											Messageformat temp=SharedData.Holdback_queue.elementAt(i);
											temp.Order_seq=rcv_mf.Order_seq;
											temp.order_rcved=true;
										}
									}
								}
							}
						}
						else if(rcv_mf.from.equals("AVD1"))
						{
							if((rcv_mf.msg_vector[1]==SharedData.msg_vector[1]+1) && (rcv_mf.msg_vector[0]<=SharedData.msg_vector[0]) && (rcv_mf.msg_vector[2]<=SharedData.msg_vector[2]))
							{
								SharedData.msg_vector[1]++;
								SharedData.Holdback_queue.add(rcv_mf);
								/*								for(int i=0;i<SharedData.Causal_order.size();i++)
								{
									if(SharedData.Causal_order.elementAt(i).from.equals("AVD1") && (SharedData.Causal_order.elementAt(i).msg_vector[1]==SharedData.msg_vector[1]+1))
									{
										SharedData.msg_vector[1]++;
										publishProgress(SharedData.Causal_order.elementAt(i).Msg);
										SharedData.Causal_order.removeElementAt(i);
									}
								}*/
								update_vector();
							}
							else
							{
								SharedData.Causal_order.addElement(rcv_mf);
							}
						}
					}
					incoming_connection.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onProgressUpdate(String...string)
		{
			TextView msg_display = (TextView) findViewById(R.id.textView1);
			msg_display.setMovementMethod(new ScrollingMovementMethod());
			msg_display.append(string[0] + "\n");
			return;
		}
		void update_vector() throws InterruptedException
		{
			boolean flag=false;
			if(SharedData.Iam.equals("AVD0"))
			{
				for(int i=0;i<SharedData.Causal_order.size();i++)
				{
					if(SharedData.Causal_order.elementAt(i).from.equals("AVD1") && (SharedData.Causal_order.elementAt(i).msg_vector[1]==SharedData.msg_vector[1]+1))
					{
						SharedData.msg_vector[1]++;
						SharedData.Delivery_queue.put(SharedData.Causal_order.elementAt(i));
						SharedData.Causal_order.removeElementAt(i);
						SharedData.s++;
						flag=true;
						break;
					}
					else if(SharedData.Causal_order.elementAt(i).from.equals("AVD2") && (SharedData.Causal_order.elementAt(i).msg_vector[2]==SharedData.msg_vector[2]+1))
					{
						SharedData.msg_vector[2]++;
						SharedData.Delivery_queue.put(SharedData.Causal_order.elementAt(i));
						SharedData.Causal_order.removeElementAt(i);
						SharedData.s++;
						flag=true;
						break;
					}
				}

			}
			else if(SharedData.Iam.equals("AVD1"))
			{
				for(int i=0;i<SharedData.Causal_order.size();i++)
				{
					if(SharedData.Causal_order.elementAt(i).from.equals("AVD0") && (SharedData.Causal_order.elementAt(i).msg_vector[0]==SharedData.msg_vector[0]+1))
					{
						SharedData.msg_vector[0]++;
						SharedData.Delivery_queue.put(SharedData.Causal_order.elementAt(i));
						SharedData.Causal_order.removeElementAt(i);
						flag=true;
						break;
					}
					else if(SharedData.Causal_order.elementAt(i).from.equals("AVD2") && (SharedData.Causal_order.elementAt(i).msg_vector[2]==SharedData.msg_vector[2]+1))
					{
						SharedData.msg_vector[2]++;
						SharedData.Delivery_queue.put(SharedData.Causal_order.elementAt(i));
						SharedData.Causal_order.removeElementAt(i);
						flag=true;
						break;
					}
				}
			}
			else if(SharedData.Iam.equals("AVD2"))
			{
				for(int i=0;i<SharedData.Causal_order.size();i++)
				{
					if(SharedData.Causal_order.elementAt(i).from.equals("AVD0") && (SharedData.Causal_order.elementAt(i).msg_vector[0]==SharedData.msg_vector[0]+1))
					{
						SharedData.msg_vector[0]++;
						SharedData.Delivery_queue.put(SharedData.Causal_order.elementAt(i));
						SharedData.Causal_order.removeElementAt(i);
						flag=true;
						break;
					}
					else if(SharedData.Causal_order.elementAt(i).from.equals("AVD1") && (SharedData.Causal_order.elementAt(i).msg_vector[1]==SharedData.msg_vector[1]+1))
					{
						SharedData.msg_vector[1]++;
						SharedData.Delivery_queue.put(SharedData.Causal_order.elementAt(i));
						SharedData.Causal_order.removeElementAt(i);
						flag=true;
						break;
					}
				}
			}
			if(flag) update_vector();
		}

	}
}