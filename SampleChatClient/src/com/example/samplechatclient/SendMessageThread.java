package com.example.samplechatclient;

import java.io.DataOutputStream;
import java.io.IOException;

public class SendMessageThread extends Thread {

	DataOutputStream os;
	String strContent = "";

	public SendMessageThread(DataOutputStream os, String strContent) {
		this.os = os;
		this.strContent = strContent;
	}

	public void run() {
		// boolean flag = true; // you can change this flag's condition, to test
		// if
		// the client disconects
		try {
			// while (true) {
			os.writeUTF(strContent);
			// }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
