package com.example.statemachinetest;

import android.os.Message;
import android.util.Log;

import com.android.internal.util.State;
import com.android.internal.util.StateMachine;
import com.example.statemachinetest.MainActivity.Hsm2;

public class MySM extends StateMachine {
	private static final String TAG = "MySM";

	public static final int CMD_1 = 1;
	public static final int CMD_2 = 2;
	public static final int CMD_3 = 3;
	public static final int CMD_4 = 4;
	public static final int CMD_5 = 5;
	public static final int CMD_6 = 6;

	public static MySM makeMySM() {
		Log.d(TAG, "makeMySM E");
		MySM sm = new MySM("hsm2");
		sm.start();
		Log.d(TAG, "makeMySM X");
		return sm;
	}

	MySM(String name) {
		super(name);
		Log.d(TAG, "MySM E");

		// Add states, use indentation to show hierarchy
		addState(mS1);
		addState(mS2, mS1);
		addState(mS3);
		addState(mS4);

		// Set the initial state
		setInitialState(mS1);
		Log.d(TAG, "MySM X");
	}

	class S1 extends State {
		@Override
		public void enter() {
			Log.d(TAG, "mS1.enter");
		}

		@Override
		public boolean processMessage(Message message) {
			Log.d(TAG, "mS1.processMessage what=" + message.what);
			if (message.what == CMD_1) {
				// Transition to ourself to show that enter/exit is called
				transitionTo(mS2);
				return HANDLED;
			} else {
				// // // Let parent process all other messages
				// // return NOT_HANDLED;
				// // transitionTo(mS2);
				//
				// transitionToHaltingState();
				transitionToHaltingState();
				return HANDLED;
			}
		}

		@Override
		public void exit() {
			Log.d(TAG, "mS1.exit");
		}
	}

	class S2 extends State {
		@Override
		public void enter() {
			Log.d(TAG, "mS2.enter");
		}

		@Override
		public boolean processMessage(Message message) {
			boolean retVal;
			Log.d(TAG, "mS2.processMessage what=" + message.what);
			switch (message.what) {
			case (CMD_2):
				transitionTo(mS3);
				retVal = HANDLED;
				break;
			// case (CMD_3):
			// deferMessage(message);
			// transitionTo(mS3);
			// retVal = HANDLED;
			// break;
			default:
				retVal = NOT_HANDLED;
				break;
			}
			return retVal;
		}

		@Override
		public void exit() {
			Log.d(TAG, "mS2.exit");
		}
	}

	class S3 extends State {
		@Override
		public void enter() {
			Log.d(TAG, "mS3.enter");
			// sendMessage(obtainMessage(CMD_5));
		}

		@Override
		public boolean processMessage(Message message) {
			Log.d(TAG, "mS3.processMessage what=" + message.what);
			switch (message.what) {
			case (CMD_3):
				transitionTo(mS4);
				break;
			// case (CMD_4):
			// transitionTo(mS4);
			// break;
			// case (CMD_5):
			// transitionToHaltingState();
			// break;
			}
			return HANDLED;
		}

		@Override
		public void exit() {
			Log.d(TAG, "mS3.exit");
		}
	}

	class S4 extends State {
		@Override
		public void enter() {
			Log.d(TAG, "mS4.enter");
		}

		@Override
		public boolean processMessage(Message message) {
			boolean retVal;
			Log.d(TAG, "mS4.processMessage what=" + message.what);
			switch (message.what) {
			case CMD_4:
				// CMD_2 will arrive in mS2 before CMD_3
				// sendMessage(obtainMessage(CMD_1));
				// deferMessage(message);
				// transitionTo(mS1);
				retVal = HANDLED;
				break;
			default:
				// Any message we don't understand in this state invokes
				// unhandledMessage
				transitionTo(mS1);
				// sendMessage(obtainMessage(CMD_1));
				retVal = HANDLED;
				break;
			}
			return retVal;
		}

		@Override
		public void exit() {
			Log.d(TAG, "mS4.exit");
		}
	}

//	@Override
//	protected void halting() {
//		Log.d(TAG, "halting");
//		synchronized (this) {
//			this.notifyAll();
//		}
//	}

	@Override
	protected void onHalting() {
		Log.d(TAG, "onHalting");
		// TODO Auto-generated method stub
		super.onHalting();
		synchronized (this) {
		this.notifyAll();
	}
	}

	S1 mS1 = new S1();
	S2 mS2 = new S2();
	S3 mS3 = new S3();
	S4 mS4 = new S4();

	public static void doTest() {
		// TODO Auto-generated method stub
		Log.d(TAG, "doTest");
		MySM hsm = MySM.makeMySM();
		synchronized (hsm) {
			hsm.sendMessage(hsm.obtainMessage(Hsm2.CMD_1));
			hsm.sendMessage(hsm.obtainMessage(Hsm2.CMD_2));
			hsm.sendMessage(hsm.obtainMessage(Hsm2.CMD_3));
			hsm.sendMessage(hsm.obtainMessage(Hsm2.CMD_4));
			hsm.sendMessage(hsm.obtainMessage(Hsm2.CMD_5));
			hsm.sendMessage(hsm.obtainMessage(Hsm2.CMD_6));
			// try {
			// // wait for the messages to be handled
			// hsm.wait();
			// } catch (InterruptedException e) {
			// Log.e(TAG, "exception while waiting " + e.getMessage());
			// }
		}
	}
}
