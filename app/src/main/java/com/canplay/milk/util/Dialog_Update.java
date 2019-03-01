package com.canplay.milk.util;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.canplay.medical.R;


/**
 * 客服对对话框
 */
public class Dialog_Update implements View.OnClickListener {

	private Context mContext;
	private TextView positive;
	private TextView navitive;
	private Dialog mDialog;
	private TextView txt_desc;
	private UpdateListener listener;

	public void setListener(UpdateListener listener) {
		this.listener = listener;
	}

	public interface UpdateListener{
		void updateChoose(Context context, int i);
	}
	public Dialog_Update(Context context) {
		this.mContext = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.gs_dialog_update, null);

		mDialog = new Dialog(context, R.style.dialog);
		mDialog.setContentView(view);

		txt_desc = (TextView) view.findViewById(R.id.txt_desc);

		positive = (TextView) view.findViewById(R.id.yes);
		navitive = (TextView) view.findViewById(R.id.no);
		positive.setOnClickListener(this);
		navitive.setOnClickListener(this);

	}


	public void show(String desc) {
		txt_desc.setText(desc);
		mDialog.show();
	}

	public void dismiss() {
		mDialog.dismiss();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.yes:
				if(listener != null){
					listener.updateChoose(mContext,UpLoadModel.UPDATE);
				}
				dismiss();
				break;
			case R.id.no:
				if(listener != null){
					listener.updateChoose(mContext,UpLoadModel.CANCEL);
				}
				dismiss();
				break;
		}
	}
}
