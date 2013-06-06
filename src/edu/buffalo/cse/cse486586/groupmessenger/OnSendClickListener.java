package edu.buffalo.cse.cse486586.groupmessenger;

import java.util.Arrays;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class OnSendClickListener implements OnClickListener {

	private final TextView mTextView;
	private final String portStr;
	private final EditText usr_in;
	public OnSendClickListener(EditText user_input,TextView _tv, String Portstr) {
		usr_in=user_input;
		mTextView = _tv;
		portStr=Portstr;
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String outgoing_msg=null;
		outgoing_msg=usr_in.getText().toString();
		usr_in.setText("");
		Messageformat mf1=new Messageformat();
		Messageformat mf2=new Messageformat();
		mf1.msg_type="data";
		mf2.msg_type="data";
		mf1.Msg=outgoing_msg;
		mf2.Msg=outgoing_msg;
		if(portStr.equals("5554"))
		{
			mf1.Port=11112;
			mf2.Port=11116;
			mf1.from="AVD0";
			mf2.from="AVD0";
			SharedData.msg_vector[0]++;
		}
		else if(portStr.equals("5556"))
		{
			mf1.Port=11108;
			mf2.Port=11116;
			mf1.from="AVD1";
			mf2.from="AVD1";
			SharedData.msg_vector[1]++;
		}
		else if(portStr.equals("5558"))
		{
			mf1.Port=11108;
			mf2.Port=11112;
			mf1.from="AVD2";
			mf2.from="AVD2";
			SharedData.msg_vector[2]++;
		}
		for(int i=0;i<3;i++)
		{
			mf1.msg_vector[i]=SharedData.msg_vector[i];
			mf2.msg_vector[i]=SharedData.msg_vector[i];
		}
		mf1.msq_seq=mf1.from+SharedData.msg_seq;
		mf2.msq_seq=mf2.from+SharedData.msg_seq;
		SharedData.msg_seq++;
		MsgSender sender=new MsgSender();
		sender.message=mf1;
		sender.messagesender();
		sender.message=mf2;
		sender.messagesender();		
		if(SharedData.Iam.equals("AVD0"))
		{
			Messageformat mf_o1=new Messageformat();
			Messageformat mf_o2=new Messageformat();
			mf1.Order_seq=SharedData.s;
			mf1.order_rcved=true;
			SharedData.Holdback_queue.add(mf1);
			mf_o1.from="AVD0";
			mf_o1.msg_type="order";
			mf_o1.msq_seq=mf1.msq_seq;
			mf_o1.Order_seq=SharedData.s;
			mf_o1.Port=11112;
			mf_o2.from="AVD0";
			mf_o2.msg_type="order";
			mf_o2.msq_seq=mf1.msq_seq;
			mf_o2.Order_seq=SharedData.s;
			mf_o2.Port=11116;
			sender.message=mf_o1;
			sender.messagesender();
			sender.message=mf_o2;
			sender.messagesender();
			SharedData.s++;
		}
		else
		{
			SharedData.Holdback_queue.add(mf1);
		}
		//		new Client().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, outgoing_msg);
	}

}
