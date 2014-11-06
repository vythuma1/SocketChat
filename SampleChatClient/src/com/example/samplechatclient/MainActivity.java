package com.example.samplechatclient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	private SendMessageThread thread;
	DataOutputStream os;
	String ipServer = "10.0.2.2";
	int postServer = 1234;

	Button btnConnect;
	EditText edtContent, edtName, edtChatWith;

	Socket s;
	TextView txtContent, txtIpClient;
	DataInputStream is;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		TextView txtIpServer = (TextView) findViewById(R.id.txtIpServer);
		txtIpServer.setText("Server: " + ipServer + ":" + postServer);
		txtIpClient = (TextView) findViewById(R.id.txtIpClient);
		txtContent = (TextView) findViewById(R.id.txtContent);

		edtContent = (EditText) findViewById(R.id.edtContent);
		edtName = (EditText) findViewById(R.id.edtName);
		edtChatWith = (EditText) findViewById(R.id.edtChatWith);

		btnConnect = (Button) findViewById(R.id.btnConnect);
		btnConnect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					s = new Socket(ipServer, postServer);
					os = new DataOutputStream(s.getOutputStream());
					is = new DataInputStream(s.getInputStream());
					os.writeUTF(edtName.getText().toString());
					txtIpClient.setText("Client (ME): "
							+ is.readUTF().toString());
					String listOnline = is.readUTF().toString();
					if (listOnline.length() < 18)
						txtContent.setText("Have no user online\n");
					else
						txtContent.setText(listOnline);
					receiveMessage();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		Button btnSubmit = (Button) findViewById(R.id.btnSubmit);
		btnSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				txtContent.append("ME: " + edtContent.getText().toString()
						+ "\n");
				try {
					os.writeUTF(edtChatWith.getText().toString());
					os.writeUTF(edtContent.getText().toString());
					edtContent.setText("");
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// thread = new SendMessageThread(os, edtContent.getText()
				// .toString());
				// thread.start();
				// edtContent.setText("");
			}
		});

	}

	private void receiveMessage() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					while (true) {
						final String text = is.readUTF().toString();
						Log.e("-----", text);
						txtContent.post(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								txtContent.append(edtChatWith.getText().toString()+": " + text + "\n");
							}
						});
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					txtContent.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							txtContent.append("Server is down");
						}
					});
				}
			}
		}).start();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			s.close();
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
