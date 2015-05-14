package com.example.statemachinetest;

import com.android.internal.util.State;
import com.android.internal.util.StateMachine;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener{
	public static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button toTest = (Button) findViewById(R.id.button1);
		toTest.setOnClickListener(this);
		
//		testHelloWorld();		
//		testHsm1();
//		testHsm2();
		MySM.doTest();
	}


	@Override
	public void onClick(View v) {
		Log.d(TAG, "onClick");
//		testHelloWorld();		
//		testHsm1();
//		testHsm2();
	}

	
	void testHelloWorld() {
		Log.d(TAG, "testHelloWorld");
		HelloWorld hw = HelloWorld.makeHelloWorld();
		hw.sendMessage(hw.obtainMessage());
	}
	
	void testHsm1(){
		Log.d(TAG, "testHsm1");
		Hsm1 hsm = Hsm1.makeHsm1();
		synchronized(hsm) {
		     hsm.sendMessage(hsm.obtainMessage(Hsm1.CMD_1));
		     hsm.sendMessage(hsm.obtainMessage(Hsm1.CMD_2));
		     try {
		          // wait for the messages to be handled
		          hsm.wait();
		     } catch (InterruptedException e) {
		          Log.e(TAG, "exception while waiting " + e.getMessage());
		     }
		}
	}
	
	void testHsm2(){
		Log.d(TAG, "testHsm2");
		Hsm2 hsm = Hsm2.makeHsm2();
		synchronized(hsm) {
		     hsm.sendMessage(hsm.obtainMessage(Hsm2.CMD_1));
		     hsm.sendMessage(hsm.obtainMessage(Hsm2.CMD_2));
		     hsm.sendMessage(hsm.obtainMessage(Hsm2.CMD_3));
		     hsm.sendMessage(hsm.obtainMessage(Hsm2.CMD_4));
		     hsm.sendMessage(hsm.obtainMessage(Hsm2.CMD_5));
		     hsm.sendMessage(hsm.obtainMessage(Hsm2.CMD_6));
		     try {
		          // wait for the messages to be handled
		          hsm.wait();
		     } catch (InterruptedException e) {
		          Log.e(TAG, "exception while waiting " + e.getMessage());
		     }
		}
	}

	static class HelloWorld extends StateMachine {
		State1 mState1 = new State1();

		HelloWorld(String name) {
			super(name);
			addState(mState1);
			setInitialState(mState1);
		}

		public static HelloWorld makeHelloWorld() {
			Log.d(TAG, "makeHelloWorld");
			HelloWorld hw = new HelloWorld("hw");
			hw.start();
			return hw;
		}

		class State1 extends State {
			@Override
			public boolean processMessage(Message message) {
				Log.d(TAG, "Hello World");
				return HANDLED;
			}
		}
	}

	static class Hsm1 extends StateMachine {
		private static final String TAG = "hsm1";

		public static final int CMD_1 = 1;
		public static final int CMD_2 = 2;
		public static final int CMD_3 = 3;
		public static final int CMD_4 = 4;
		public static final int CMD_5 = 5;

		public static Hsm1 makeHsm1() {
			Log.d(TAG, "makeHsm1 E");
			Hsm1 sm = new Hsm1("hsm1");
			sm.start();
			Log.d(TAG, "makeHsm1 X");
			return sm;
		}

		Hsm1(String name) {
			super(name);
			Log.d(TAG, "ctor E");

			// Add states, use indentation to show hierarchy
			addState(mP1);
			addState(mS1, mP1);
			addState(mS2, mP1);
			addState(mP2);

			// Set the initial state
			setInitialState(mS1);
			Log.d(TAG, "ctor X");
		}

		class P1 extends State {
			@Override
			public void enter() {
				Log.d(TAG, "mP1.enter");
			}

			@Override
			public boolean processMessage(Message message) {
				boolean retVal;
				Log.d(TAG, "mP1.processMessage what=" + message.what);
				switch (message.what) {
				case CMD_2:
					// CMD_2 will arrive in mS2 before CMD_3
					sendMessage(obtainMessage(CMD_3));
					deferMessage(message);
					transitionTo(mS2);
					retVal = HANDLED;
					break;
				default:
					// Any message we don't understand in this state invokes
					// unhandledMessage
					retVal = NOT_HANDLED;
					break;
				}
				return retVal;
			}

			@Override
			public void exit() {
				Log.d(TAG, "mP1.exit");
			}
		}

		class S1 extends State {
			@Override
			public void enter() {
				Log.d(TAG, "mS1.enter");
			}

			@Override
			public boolean processMessage(Message message) {
				Log.d(TAG, "S1.processMessage what=" + message.what);
				if (message.what == CMD_1) {
					// Transition to ourself to show that enter/exit is called
					transitionTo(mS1);
					return HANDLED;
				} else {
					// Let parent process all other messages
					return NOT_HANDLED;
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
					sendMessage(obtainMessage(CMD_4));
					retVal = HANDLED;
					break;
				case (CMD_3):
					deferMessage(message);
					transitionTo(mP2);
					retVal = HANDLED;
					break;
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

		class P2 extends State {
			@Override
			public void enter() {
				Log.d(TAG, "mP2.enter");
				sendMessage(obtainMessage(CMD_5));
			}

			@Override
			public boolean processMessage(Message message) {
				Log.d(TAG, "P2.processMessage what=" + message.what);
				switch (message.what) {
				case (CMD_3):
					break;
				case (CMD_4):
					break;
				case (CMD_5):
					transitionToHaltingState();
					break;
				}
				return HANDLED;
			}

			@Override
			public void exit() {
				Log.d(TAG, "mP2.exit");
			}
		}

//		@Override
//		protected void halting() {
//			Log.d(TAG, "halting");
//			synchronized (this) {
//				this.notifyAll();
//			}
//		}

		P1 mP1 = new P1();
		@Override
		protected void onHalting() {
			Log.d(TAG, "onHalting");
			// TODO Auto-generated method stub
			super.onHalting();
		}
		S1 mS1 = new S1();
		S2 mS2 = new S2();
		P2 mP2 = new P2();
	}


	static class Hsm2 extends StateMachine {
		private static final String TAG = "Hsm2";

		public static final int CMD_1 = 1;
		public static final int CMD_2 = 2;
		public static final int CMD_3 = 3;
		public static final int CMD_4 = 4;
		public static final int CMD_5 = 5;
		public static final int CMD_6 = 6;
		public static Hsm2 makeHsm2() {
			Log.d(TAG, "makeHsm2 E");
			Hsm2 sm = new Hsm2("hsm2");
			sm.start();
			Log.d(TAG, "makeHsm2 X");
			return sm;
		}

		Hsm2(String name) {
			super(name);
			Log.d(TAG, "ctor E");

			// Add states, use indentation to show hierarchy			
			addState(mS1);
			addState(mS2);
			addState(mS3);
			addState(mS4);

			// Set the initial state
			setInitialState(mS1);
			Log.d(TAG, "ctor X");
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
//					sendMessage(obtainMessage(CMD_1));
//					deferMessage(message);
					transitionTo(mS1);
					retVal = HANDLED;
					break;
				default:
					// Any message we don't understand in this state invokes
					// unhandledMessage
//					transitionTo(mS1);
//					sendMessage(obtainMessage(CMD_1));
					transitionTo(mS2);
					retVal = NOT_HANDLED;
					break;
				}
				return retVal;
			}

			@Override
			public void exit() {
				Log.d(TAG, "mS4.exit");
			}
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
//					// Let parent process all other messages
//					return NOT_HANDLED;
//					transitionTo(mS2);
					
					transitionToHaltingState();
					return NOT_HANDLED;
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
				case (CMD_3):
					deferMessage(message);
					transitionTo(mS3);
					retVal = HANDLED;
					break;
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
//				sendMessage(obtainMessage(CMD_5));
			}

			@Override
			public boolean processMessage(Message message) {
				Log.d(TAG, "mS3.processMessage what=" + message.what);
				switch (message.what) {
				case (CMD_3):
					transitionTo(mS4);
					break;
				case (CMD_4):
					transitionTo(mS4);
					break;
				case (CMD_5):
					transitionToHaltingState();
					break;
				}
				return HANDLED;
			}

			@Override
			public void exit() {
				Log.d(TAG, "mS3.exit");
			}
		}

//		@Override
//		protected void halting() {
//			Log.d(TAG, "halting");
//			synchronized (this) {
//				this.notifyAll();
//			}
//		}
		
		@Override
		protected void onHalting() {
			Log.d(TAG, "onHalting");
			// TODO Auto-generated method stub
			super.onHalting();
		}
		
		S1 mS1 = new S1();
		S2 mS2 = new S2();
		S3 mS3 = new S3();
		S4 mS4 = new S4();
	}


	
}
