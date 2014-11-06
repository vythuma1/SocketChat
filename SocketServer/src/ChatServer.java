import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer {
	static int hostServer = 1234;
	static DataOutputStream os;
	static DataInputStream is;
	public static java.util.List<DataSocket> mDataSockets = new ArrayList<DataSocket>();

	public static void run() {
		try {
			ServerSocket ecoute;

			ecoute = new ServerSocket(hostServer);
			Socket service = null;
			System.out.println("Client khác kết nối...");
			while (true) {
				service = ecoute.accept();

				is = new DataInputStream(service.getInputStream());
				String name = is.readUTF().toString();
				mDataSockets.add(new DataSocket(name, service));

				String listOnline = "List User Online\n";
				for (int i = 0; i < mDataSockets.size(); i++) {
					if (!mDataSockets.get(i).getName().equals(name))
						listOnline += "- [" + mDataSockets.get(i).getName()
								+ "]\n";
				}
				os = new DataOutputStream(service.getOutputStream());
				os.writeUTF(service.getRemoteSocketAddress().toString());
				os.writeUTF(listOnline);
				System.out.println("Client --" + name + "-- đã kết nối: "
						+ service.getRemoteSocketAddress());

				// call a new thread to receive message
				ReceiveMessage mReceiveMessage = new ReceiveMessage(service);
				mReceiveMessage.start();
				//
				// // call a new thread to send message
				// SendMessage mSendMessage = new SendMessage(os);
				// mSendMessage.start();

			}
		} catch (IOException e) {
			System.out.print("Client kết nối bị lỗi");
		}
	}

	public static void main(String args[]) {
		run();
	}

}

class ReceiveMessage extends Thread {
	Socket service;

	ReceiveMessage(Socket service) {
		this.service = service;
	}

	public void run() {
		boolean flag = true; // you can change this flag's condition, to test if
								// the client disconects
		DataInputStream is;
		try {
			is = new DataInputStream(service.getInputStream());

			while (true) {

				String receiver = is.readUTF().toString();
				String content = is.readUTF().toString();
				System.out.println("Client[" + receiver + "]: " + content);
				ChatServer chatServer = new ChatServer();
				java.util.List<DataSocket> list = chatServer.mDataSockets;
				int index = 0;
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).getName().equals(receiver)) {
						index = i;
						break;
					}

				}
				Socket socketReceiver = list.get(index).getSocket();
				DataOutputStream mStream = new DataOutputStream(
						socketReceiver.getOutputStream());
				mStream.writeUTF(content);
			}
		} catch (IOException e) {

			System.out.println("Client ngắt kết nối");
		}
	}
}

// class SendMessage extends Thread {
//
// public static String read_string() {
// String read = "";
// try {
// read = new BufferedReader(new InputStreamReader(System.in), 1)
// .readLine();
// } catch (IOException ex) {
// System.out.println("error reading from the input stream!");
// }
// return read;
// }
//
// DataOutputStream os;
//
// SendMessage(DataOutputStream os) {
// this.os = os;
// }
//
// public void run() {
// boolean flag = true; // you can change this flag's condition, to test if
// // the client disconects
// try {
// while (true) {
// String str = read_string();
// os.writeUTF(str);
// }
// } catch (IOException e) {
// e.printStackTrace();
// }
// }
// }

