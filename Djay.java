class DjayListener extends Listener {
    final static int BOTTOM, TOPLEFT, TOPRIGHT, NOTHING = 0,1,2,3;
    public void onInit(Controller controller) {
	System.out.println("Initialized");
    }

    public void onConnect(Controller controller) {
	System.out.println("Connected");
    }

    public void onDisconnect(Controller controller) {
	System.out.println("Disconnected");
    }
    
    int getArea(Vector pos) {
	if(pos.getZ()>30){
	    return BOTTOM;
	} elseif (pos.getX()<0) {
	    return TOPLEFT;
	} else {
	    return TOPRIGHT;
	}
    }

    public void onFrame(Controller controller) {
	// Get the most recent frame and report some basic information
	Frame frame = controller.frame();
	HandArray hands = frame.hands();
	long numHands = hands.size();
	int area = NOTHING; // area detects whether a hand is in BOTTOM, TOPLEFT, or TOPRIGHT;
	boolean present = false;


	if (numHands >= 1) {
	    present = true;
	    // Get the first hand
	    Hand hand = hands.get(0);

	    // Check if the hand has any fingers
	    FingerArray fingers = hand.fingers();
	    long numFingers = fingers.size();
	    if (numFingers >= 1) {
		// Calculate the hand's average finger tip position
		Vector pos = new Vector(0, 0, 0);
		for (int i = 0; i < numFingers; ++i) {
		    Finger finger = fingers.get(i);
		    Ray tip = finger.tip();
		    pos.setX(pos.getX() + tip.getPosition().getX());
		    pos.setY(pos.getY() + tip.getPosition().getY());
		    pos.setZ(pos.getZ() + tip.getPosition().getZ());
		}
		pos = new Vector(pos.getX()/numFingers, pos.getY()/numFingers, pos.getZ()/numFingers);
		area = getArea(pos);
		/*System.out.println("Hand has " + numFingers + " fingers with average tip position"
		  + " (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")");*/
	    }

	    // Check if the hand has a palm
	    Ray palmRay = hand.palm();
	    if (palmRay != null) {
		// Get the palm position and wrist direction
		Vector palm = palmRay.getPosition();
		Vector wrist = palmRay.getDirection();
		if (area == NOTHING) { //fingers not detected, get area of your hand
		    Vector palmPos = new Vector(palm.getX(), palm.getY(), palm.getZ());
		    area = getArea(palmPos);
		}
		/*System.out.println("Palm position ("
		  + palm.getX() + ", " + palm.getY() + ", " + palm.getZ() + ")");*/

		// Check if the hand has a normal vector
		Vector normal = hand.normal();
		if (normal != null) {
		    // Calculate the hand's pitch, roll, and yaw angles
		    double pitchAngle = Math.atan2(normal.getZ(), normal.getY()) * 180/Math.PI + 180;
		    double rollAngle = Math.atan2(normal.getX(), normal.getY()) * 180/Math.PI + 180;
		    double yawAngle = Math.atan2(wrist.getZ(), wrist.getX()) * 180/Math.PI - 90;
		    // Ensure the angles are between -180 and +180 degrees
		    if (pitchAngle > 180) pitchAngle -= 360;
		    if (rollAngle > 180) rollAngle -= 360;
		    if (yawAngle > 180) yawAngle -= 360;
		    /*System.out.println("Pitch: " + pitchAngle + " degrees,  "
		      + "roll: " + rollAngle + " degrees,  "
		      + "yaw: " + yawAngle + " degrees");*/
		}
	    }

	    // Check if the hand has a ball
	    Ball ball = hand.ball();
	    if (ball != null) {
		//System.out.println("Hand curvature radius: " + ball.getRadius() + " mm");
	    }
	}
    }
}

class Djay {
    

    public static void main(String[] args) {
	// Create a sample listener and assign it to a controller to receive events
	DjayListener listener = new DjayListener();
	Controller controller = new Controller(listener);

	// Keep this process running until Enter is pressed
	System.out.println("Press Enter to quit...");
	System.console().readLine();

	// The controller must be disposed of before the listener
	controller = null;
    }
}
