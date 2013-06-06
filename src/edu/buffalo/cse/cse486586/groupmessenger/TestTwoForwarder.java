package edu.buffalo.cse.cse486586.groupmessenger;

import android.os.AsyncTask;

public class TestTwoForwarder {
	Messageformat msg;
	void messagesender()
	{
		new Client().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, msg);
	}
	private class Client extends AsyncTask<Messageformat,Void,Void>{

		@Override
		protected Void doInBackground(Messageformat... params) {
			Messageformat mf11=new Messageformat();
			Messageformat mf12=new Messageformat();
			Messageformat mf21=new Messageformat();
			Messageformat mf22=new Messageformat();
			mf11.msg_type="data";
			mf12.msg_type="data";
			mf21.msg_type="data";
			mf22.msg_type="data";
			if(SharedData.Iam.equals("AVD0"))
			{
				mf11.Port=11112;
				mf12.Port=11116;
				mf21.Port=11112;
				mf22.Port=11116;
				mf11.from="AVD0";
				mf12.from="AVD0";
				SharedData.msg_vector[0]++;
				for(int i=0;i<3;i++)
				{
					mf11.msg_vector[i]=SharedData.msg_vector[i];
					mf12.msg_vector[i]=SharedData.msg_vector[i];
				}
				mf21.from="AVD0";
				mf22.from="AVD0";
				SharedData.msg_vector[0]++;
				for(int i=0;i<3;i++)
				{
					mf21.msg_vector[i]=SharedData.msg_vector[i];
					mf22.msg_vector[i]=SharedData.msg_vector[i];
				}
				if(params[0].from.equals("AVD0"))
				{
					mf11.Msg="AVD0:1";
					mf12.Msg="AVD0:1";
					mf21.Msg="AVD0:2";
					mf22.Msg="AVD0:2";
				}
				else
				{
					mf11.Msg="AVD0:0";
					mf12.Msg="AVD0:0";
					mf21.Msg="AVD0:1";
					mf22.Msg="AVD0:1";
				}
			}
			else if(SharedData.Iam.equals("AVD1"))
			{
				mf11.Port=11108;
				mf12.Port=11116;
				mf21.Port=11108;
				mf22.Port=11116;
				mf11.from="AVD1";
				mf12.from="AVD1";
				SharedData.msg_vector[1]++;
				for(int i=0;i<3;i++)
				{
					mf11.msg_vector[i]=SharedData.msg_vector[i];
					mf12.msg_vector[i]=SharedData.msg_vector[i];
				}
				mf21.from="AVD1";
				mf22.from="AVD1";
				SharedData.msg_vector[1]++;
				for(int i=0;i<3;i++)
				{
					mf21.msg_vector[i]=SharedData.msg_vector[i];
					mf22.msg_vector[i]=SharedData.msg_vector[i];
				}
				if(params[0].from.equals("AVD1"))
				{
					mf11.Msg="AVD1:1";
					mf12.Msg="AVD1:1";
					mf21.Msg="AVD1:2";
					mf22.Msg="AVD1:2";
				}
				else
				{
					mf11.Msg="AVD1:0";
					mf12.Msg="AVD1:0";
					mf21.Msg="AVD1:1";
					mf22.Msg="AVD1:1";
				}
			}
			else if(SharedData.Iam.equals("AVD2"))
			{
				mf11.Port=11108;
				mf12.Port=11112;
				mf21.Port=11108;
				mf22.Port=11112;
				mf11.from="AVD2";
				mf12.from="AVD2";
				SharedData.msg_vector[2]++;
				for(int i=0;i<3;i++)
				{
					mf11.msg_vector[i]=SharedData.msg_vector[i];
					mf12.msg_vector[i]=SharedData.msg_vector[i];
				}
				mf21.from="AVD2";
				mf22.from="AVD2";
				SharedData.msg_vector[2]++;
				for(int i=0;i<3;i++)
				{
					mf21.msg_vector[i]=SharedData.msg_vector[i];
					mf22.msg_vector[i]=SharedData.msg_vector[i];
				}
				if(params[0].from.equals("AVD2"))
				{
					mf11.Msg="AVD2:1";
					mf12.Msg="AVD2:1";
					mf21.Msg="AVD2:2";
					mf22.Msg="AVD2:2";
				}
				else
				{
					mf11.Msg="AVD2:0";
					mf12.Msg="AVD2:0";
					mf21.Msg="AVD2:1";
					mf22.Msg="AVD2:1";
				}
			}
			mf11.msq_seq=mf11.from+SharedData.msg_seq;
			mf12.msq_seq=mf12.from+SharedData.msg_seq;
			SharedData.msg_seq++;
			mf21.msq_seq=mf21.from+SharedData.msg_seq;
			mf22.msq_seq=mf22.from+SharedData.msg_seq;
			SharedData.msg_seq++;
			MsgSender sender=new MsgSender();
			sender.message=mf11;
			sender.messagesender();
			sender.message=mf12;
			sender.messagesender();
			sender.message=mf21;
			sender.messagesender();
			sender.message=mf22;
			sender.messagesender();
			if(SharedData.Iam.equals("AVD0"))
			{
				Messageformat mf_o11=new Messageformat();
				Messageformat mf_o12=new Messageformat();
				Messageformat mf_o21=new Messageformat();
				Messageformat mf_o22=new Messageformat();
				mf11.Order_seq=SharedData.s;
				mf11.order_rcved=true;
				SharedData.Holdback_queue.add(mf11);
				mf_o11.from="AVD0";
				mf_o11.msg_type="order";
				mf_o11.msq_seq=mf11.msq_seq;
				mf_o11.Order_seq=SharedData.s;
				mf_o11.Port=11112;
				mf_o12.from="AVD0";
				mf_o12.msg_type="order";
				mf_o12.msq_seq=mf11.msq_seq;
				mf_o12.Order_seq=SharedData.s;
				mf_o12.Port=11116;
				sender.message=mf_o11;
				sender.messagesender();
				sender.message=mf_o12;
				sender.messagesender();
				SharedData.s++;
				mf21.Order_seq=SharedData.s;
				mf21.order_rcved=true;
				SharedData.Holdback_queue.add(mf21);
				mf_o21.from="AVD0";
				mf_o21.msg_type="order";
				mf_o21.msq_seq=mf21.msq_seq;
				mf_o21.Order_seq=SharedData.s;
				mf_o21.Port=11112;
				mf_o22.from="AVD0";
				mf_o22.msg_type="order";
				mf_o22.msq_seq=mf21.msq_seq;
				mf_o22.Order_seq=SharedData.s;
				mf_o22.Port=11116;
				sender.message=mf_o21;
				sender.messagesender();
				sender.message=mf_o22;
				sender.messagesender();
				SharedData.s++;
			}
			else
			{
				SharedData.Holdback_queue.add(mf11);
				SharedData.Holdback_queue.add(mf21);
			}
			return null;
		}
		
	}

}