package edu.buffalo.cse.cse486586.groupmessenger;

import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;

public class OnTestTwoClick implements OnClickListener{

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		new msg_send_task().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	private class msg_send_task extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... arg0) {
			Messageformat mf1=new Messageformat();
			Messageformat mf2=new Messageformat();
			mf1.msg_type="data";
			mf2.msg_type="data";
			mf1.test_msg=true;
			mf2.test_msg=true;
			if(SharedData.Iam.equals("AVD0"))
			{
				mf1.Port=11112;
				mf2.Port=11116;
				mf1.from="AVD0";
				mf1.Msg="AVD0:0";
				SharedData.msg_vector[0]++;
				mf2.from="AVD0";
				mf2.Msg="AVD0:0";
				mf1.msq_seq=mf1.from+SharedData.msg_seq;
				mf2.msq_seq=mf1.from+SharedData.msg_seq;
			}
			else if(SharedData.Iam.equals("AVD1"))
			{
				mf1.Port=11108;
				mf2.Port=11116;
				mf1.from="AVD1";
				mf1.Msg="AVD1:0";
				SharedData.msg_vector[1]++;
				mf2.from="AVD1";
				mf2.Msg="AVD1:0";
				mf1.msq_seq=mf1.from+SharedData.msg_seq;
				mf2.msq_seq=mf1.from+SharedData.msg_seq;
			}
			else if(SharedData.Iam.equals("AVD2"))
			{
				mf1.Port=11108;
				mf2.Port=11112;
				mf1.from="AVD2";
				mf1.Msg="AVD2:0";	
				SharedData.msg_vector[2]++;
				mf2.from="AVD2";
				mf2.Msg="AVD2:0";
				mf1.msq_seq=mf1.from+SharedData.msg_seq;
				mf2.msq_seq=mf1.from+SharedData.msg_seq;
			}
			for(int i=0;i<3;i++)
			{
				mf1.msg_vector[i]=SharedData.msg_vector[i];
				mf2.msg_vector[i]=SharedData.msg_vector[i];
			}
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
			return null;
		}
		
	}

}